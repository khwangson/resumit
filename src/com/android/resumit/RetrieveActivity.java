package com.android.resumit;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.android.resumit.RetrieveActivity.SSHMgr;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class RetrieveActivity extends Activity {
	String username = "testtestest";
	String password;
	String transferUser="testtestest";
	boolean authenticated = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve);
        String asdf;

        SSHMgr mgr = new SSHMgr();
        try {
			if(mgr.execute(this).get(5000, TimeUnit.MILLISECONDS)){
				////putConsole("Success!");
			}else{}
				////putConsole("Fail!");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//putConsole("Error IE"+e.getMessage());
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//putConsole("Error EE"+e.getMessage());
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//putConsole("Error TE"+e.getMessage());
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.retrieve, menu);
        return true;
    }
    
    public class SSHMgr extends AsyncTask<Object, Void, Boolean>{
		public Boolean execSCP(RetrieveActivity act) {
			InputStream scpIn;
			OutputStream scpOut;
		    FileOutputStream fos=null;
	        try {
				
				BufferedReader keyReader = new BufferedReader(new InputStreamReader(getAssets().open("id_rsa")));
	
				String privateKey = "";
				String publicKey = "";
				int ch;
				while((ch=keyReader.read())!=-1){
					privateKey += (char)ch;
				}
	        	keyReader.close();
	        	
	        	/*
	        	keyReader = new BufferedReader(new InputStreamReader(getAssets().open("id_dsa_knode.pub")));
				while((ch=keyReader.read())!=-1){
					publicKey += (char)ch;
				}
	        	keyReader.close();
	        	*/
	        	
	        	////putConsole("||Keyfile OK||" + privateKey);
	        	
	        	//  /data/data/app.knode/files
				JSch ssh = new JSch();
				//ssh.addIdentity("eddawangS4", privateKey.getBytes(), publicKey.getBytes(), null);
				ssh.addIdentity("eddawangS4", privateKey.getBytes(), publicKey.getBytes(), null);
				JSch.setConfig("StrictHostKeyChecking", "no");
	        	////putConsole("Add Identity OK||");
	        	
	        	Session session = ssh.getSession("eddawangS4", "71.178.189.165", 272);
	        	session.connect();
	        	
	        	/*
	        	String t = username + "\n";
	        	byte[] txt = t.getBytes();
	        	FileOutputStream temp = openFileOutput("auth", 0);
	        	temp.write(txt);
	        	temp.flush();
	        	temp.close();*/
	        	
	        	////putConsole("Session OK");
	        	
	        	Channel scp = session.openChannel("exec");
	        	((ChannelExec)scp).setCommand("scp -f resume/"+transferUser+"/"+"resume.pdf");
	        	scp.connect();

	        	scpIn = scp.getInputStream();
	        	scpOut = scp.getOutputStream();

	        	////putConsole("Connected");
	        	//scpOut.write(("C0644 "+ (txt.length) + " auth\n").getBytes());
	        	//scpOut.flush();
	        	//scpOut.write(txt);
	        	scpOut.write(0);
	        	scpOut.flush();
	        	/*
	        	if(checkAck(scpIn, act)==0)
					if(scp.isConnected())
	                	//putConsole("SCP Complete||"+txt.length);
	        	*/

	            byte[] buf=new byte[1024];
	        	while(true){
	        		int c=checkAck(scpIn,act);
        	        if(c!='C'){
	        		  break;
	        		}
	        	 
        	        // read '0644 '
        	        scpIn.read(buf, 0, 5);
        	 
        	        long filesize=0L;
        	        while(true){
        	          if(scpIn.read(buf, 0, 1)<0){
        	            // error
        	            break; 
        	          }
        	          if(buf[0]==' ')break;
        	          filesize=filesize*10L+(long)(buf[0]-'0');
        	        }
        	 
        	        String file=null;
        	        for(int i=0;;i++){
        	        	scpIn.read(buf, i, 1);
        	          if(buf[i]==(byte)0x0a){
        	            file=new String(buf, 0, i);
        	            break;
        	          }
        	        }

        	    	//System.out.println("filesize="+filesize+", file="+file);
        	     
        	            // send '\0'
        	            buf[0]=0; scpOut.write(buf, 0, 1); scpOut.flush();
        	     
        	            ////putConsole("Reading File..");
        	            // read a content of lfile
        	            fos=openFileOutput(transferUser+"_Resume",0);
        	            int foo;
        	            while(true){
        	              if(buf.length<filesize) foo=buf.length;
        	              else foo=(int)filesize;
        	              foo=scpIn.read(buf, 0, foo);
        	              if(foo<0){
        	                // error 
        	                break;
        	              }
        	              fos.write(buf, 0, foo);
        	              filesize-=foo;
        	              if(filesize==0L) break;
        	            }
        	            fos.flush();
        	            fos.close();
        	            fos=null;
        	            
        	            ////putConsole("Complete....");
        	     
        	    	if(checkAck(scpIn,act)!=0){
        	    		//putConsole("ERROR");
        	    		return false;
        	    	}
        	     
        	            // send '\0'
        	            buf[0]=0; scpOut.write(buf, 0, 1); scpOut.flush();
    	          }
	        	scpIn.close();
	        	scpOut.close();
	        	scp.disconnect();
	        	
	        	session.disconnect();
	
		        return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				//putConsole("IOERROR"+e.getMessage());
			} catch (JSchException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				//putConsole(e.getMessage());
			}
	        return false;
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			return execSCP((RetrieveActivity)params[0]);
		}
    }

	private int checkAck(InputStream in, RetrieveActivity k) throws IOException{
	    int b=in.read();
	    // b may be 0 for success,
	    //          1 for error,
	    //          2 for fatal error,
	    //          -1
	    if(b==0) return b;
	    //k.//putConsole("IN");
	    if(b==-1) return b;
	    //k.//putConsole("Pass");

	    if(b==1 || b==2){
	      StringBuffer sb=new StringBuffer();
	      int c;
	      do {
			c=in.read();
			sb.append((char)c);
	      }
	      while(c!='\n');
	      if(b==1){ // error
	    	  k.putConsole(sb.toString());
	      }
	      if(b==2){ // fatal error
	    	  k.putConsole(sb.toString());
	      }

	    }
	    return b;
	}


	public void putConsole(String string) {
		// TODO Auto-generated method stub

		((TextView) findViewById(R.id.textView1)).setText(string);
	}
	
	public void setAuthenticationStatus(boolean val){
		authenticated = val;
	}
	
	public String getUsername(){
		return username;
	}
	
	public void gotoUpload(View v){
		Intent asdf = new Intent(this,UploadActivity.class);
		startActivity(asdf);
	}
    
}

