package com.cs407.groupprojectorganizer;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onButtonClick(View v){
		
		switch(v.getId()){
			
			case R.id.login_login:
				setContentView(R.layout.projects);
				break;
			
			case R.id.login_create:
				setContentView(R.layout.create_account);
				break;
			
			case R.id.create_account:
				setContentView(R.layout.projects);
				break;
				
		}
	}

}
