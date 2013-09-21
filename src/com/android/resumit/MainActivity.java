package com.android.resumit;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;


public class MainActivity extends Activity {
	String username;
	String password;
	boolean authenticated = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String asdf;

        SSHMgr mgr = new SSHMgr();
        try {
			if(mgr.execute(this).get(5000, TimeUnit.MILLISECONDS))
				putConsole("Success!");
			else
				putConsole("Fail!");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			putConsole(e.getMessage());
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			putConsole(e.getMessage());
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			putConsole(e.getMessage());
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public class SSHMgr extends AsyncTask<Object, Void, Boolean>{
		public Boolean execSCP() {
			InputStream scpIn;
			OutputStream scpOut;
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
	        	
	        	//putConsole("||Keyfile OK||" + privateKey);
	        	
	        	//  /data/data/app.knode/files
				JSch ssh = new JSch();
				//ssh.addIdentity("eddawangS4", privateKey.getBytes(), publicKey.getBytes(), null);
				ssh.addIdentity("eddawangS4", privateKey.getBytes(), publicKey.getBytes(), null);
				JSch.setConfig("StrictHostKeyChecking", "no");
	        	//putConsole("Add Identity OK||");
	        	
	        	Session session = ssh.getSession("eddawangS4", "71.178.189.165", 272);
	        	session.connect();
	        	/*
	        	String t = getUserInfo() + "\n";
	        	byte[] coord = t.getBytes();
	        	FileOutputStream temp = openFileOutput("coords", 0);
	        	temp.write(coord);
	        	temp.flush();
	        	temp.close();
	        	
	        	putConsole("Session OK");
	        	
	        	Channel scp = session.openChannel("exec");
	        	((ChannelExec)scp).setCommand("scp -t coords");
	        	scp.connect();
	        	
	        	scpIn = scp.getInputStream();
	        	scpOut = scp.getOutputStream();
	        	
	        	scpOut.write(("C0644 "+ (coord.length) + " coords\n").getBytes());
	        	scpOut.flush();
	        	scpOut.write(coord);
	        	scpOut.write(0);
	        	scpOut.flush();
	        	if(checkAck(scpIn, (MainActivity)args[0])==0)
					if(scp.isConnected())
	                	putConsole("SCP Complete||"+coord.length);
	        	
	        	scpIn.close();
	        	scpOut.close();
	        	scp.disconnect();
	        	*/
	        	session.disconnect();
	        	//putConsole("Success!");
	
		        return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (JSchException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				//putConsole(e.getMessage());
			}
	        return false;
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			return execSCP();
		}
    }

	private int checkAck(InputStream in, MainActivity k) throws IOException{
	    int b=in.read();
	    // b may be 0 for success,
	    //          1 for error,
	    //          2 for fatal error,
	    //          -1
	    if(b==0) return b;
	    k.putConsole("IN");
	    if(b==-1) return b;
	    k.putConsole("Pass");

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
    
}
