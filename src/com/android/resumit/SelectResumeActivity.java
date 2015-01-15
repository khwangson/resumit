package com.android.resumit;

import android.os.Bundle;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.view.Menu;

public class SelectResumeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent downloadsIntent = new Intent();
		downloadsIntent.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
		startActivity(downloadsIntent);
		
		Intent returnIntent = new Intent();
		
		
		String result = downloadsIntent.ACTION_GET_CONTENT;
		System.out.println(result);
		 returnIntent.putExtra("result",result);
		 setResult(RESULT_OK,returnIntent);     
		 finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_resume, menu);
		return true;
	}

}
