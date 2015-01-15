package com.android.resumit;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.android.resumit.UploadActivity.SSHMgr;


import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class UploadActivity extends Activity {

	String username;
	
	String resumePath="Folder/";
	String resumeName="asdf.txt";
	boolean authenticated = false;
	static final int CHOOSE_FILE = 1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        String asdf;
 
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.upload, menu);
        return true;
    }
    
    public void upload(View view){
    	   Intent prev_intent= getIntent(); 
           username = prev_intent.getStringExtra("Username");
           EditText chosenFile = (EditText)findViewById(R.id.userFile);
       	String filename = chosenFile.getText().toString();
       	
       	resumePath = filename.substring(0,filename.indexOf('/')+1).trim();
       	resumeName = filename.substring(filename.indexOf('/')+1).trim();
           chosenFile.setText(filename);
           
           SSHMgr mgr = new SSHMgr();
           try {
   			if(mgr.execute(this).get(5000, TimeUnit.MILLISECONDS)){
   				
   				
   				
   			}else{}
   				////putConsole("Fail!");
   		} catch (InterruptedException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   			//putConsole("Error"+e.getMessage());
   		} catch (ExecutionException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   			//putConsole("Error"+e.getMessage());
   		} catch (TimeoutException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   			//putConsole("Error"+e.getMessage());
   		}
    }
    
    public void retrieve(View view){
    	
    	Intent retrieveIntent = new Intent(this, RetrieveActivity.class);
    	startActivity(retrieveIntent);
    	
    	
    }
    
//    public void browse(View view){
//    	
// 
//    	Intent intent = new Intent(this,SelectResumeActivity.class);
//		
//        startActivityForResult(intent,CHOOSE_FILE);
//        
//        SSHMgr mgr = new SSHMgr();
//        try {
//			if(mgr.execute(this).get(5000, TimeUnit.MILLISECONDS)){
//				
//				
//				
//			}else{}
//				////putConsole("Fail!");
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			//putConsole("Error"+e.getMessage());
//		} catch (ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			//putConsole("Error"+e.getMessage());
//		} catch (TimeoutException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			//putConsole("Error"+e.getMessage());
//		}
//    	
//    	
//    }
//    
//    
//    @Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//    	// Check which request we're responding to
//	    if (requestCode == CHOOSE_FILE) {
//	        // Make sure the request was successful
//	        if (resultCode == RESULT_OK) {
//	        	String result = data.getDataString();
//	        	System.out.println(result);
//	        	resumePath = result;
//	        	
//	        	EditText chosenFile = (EditText)findViewById(R.id.fileChosen);
//	        	chosenFile.setText(resumePath);
//	           
//	        }
//	    }
//	}
    
    
    public class SSHMgr extends AsyncTask<Object, Void, Boolean>{
		public Boolean execSCP(UploadActivity act) {
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
	        	
	        	////putConsole("||Keyfile OK||" + privateKey);
	        	
	        	//  /data/data/app.knode/files
				JSch ssh = new JSch();
				//ssh.addIdentity("eddawangS4", privateKey.getBytes(), publicKey.getBytes(), null);
				ssh.addIdentity("eddawangS4", privateKey.getBytes(), publicKey.getBytes(), null);
				JSch.setConfig("StrictHostKeyChecking", "no");
	        	////putConsole("Add Identity OK||");
	        	
	        	Session session = ssh.getSession("eddawangS4", "71.178.189.165", 272);
	        	session.connect();

	        	File tt = Environment.getExternalStoragePublicDirectory(resumePath);
	        	File ff = new File(tt,resumeName);
	        	//putConsole("File OK "+ff.getAbsolutePath());
	        	FileInputStream temp2 = new FileInputStream(ff);
	        	byte[] txt = new byte[(int) ff.length()];
	        	temp2.read(txt);
	        	temp2.close();
	        	//putConsole("Read OK");
	        	
	        	FileOutputStream temp = openFileOutput("resume", 0);
	        	temp.write(txt);
	        	temp.flush();
	        	temp.close();
	        	
	        	//putConsole("Session OK");
	        	
	        	Channel scp = session.openChannel("exec");
	        	((ChannelExec)scp).setCommand("scp -t resume/"+username);
	        	scp.connect();
	        	
	        	scpIn = scp.getInputStream();
	        	scpOut = scp.getOutputStream();
	        	
	        	scpOut.write(("C0644 "+ (txt.length) + " resume.pdf\n").getBytes());
	        	scpOut.flush();
	        	scpOut.write(txt);
	        	scpOut.write(0);
	        	scpOut.flush();
	        	
	        	if(checkAck(scpIn, act)==0)
					if(scp.isConnected())
	                	//putConsole("SCP Complete||"+txt.length);
	        	
	        	scpIn.close();
	        	scpOut.close();
	        	scp.disconnect();
	        	
	        	session.disconnect();
	        	//putConsole("Success!");
	
		        return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (JSchException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				////putConsole(e.getMessage());
			}
	        return false;
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			return execSCP((UploadActivity)params[0]);
		}
    }

	private int checkAck(InputStream in, UploadActivity k) throws IOException{
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
	
	public String getUsername(){
		return username;
	}
	
	public void gotoRetrieve(View v){
		Intent asdf = new Intent(this,RetrieveActivity.class);
		startActivity(asdf);
	}
    
}
