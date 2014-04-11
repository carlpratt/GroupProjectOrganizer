package com.cs407.groupprojectorganizer;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ShowProjectsActivity extends Activity {

    SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.projects_list);

        session = new SessionManager(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()){

            case R.id.action_edit_profile:
                Intent editProfileIntent = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(editProfileIntent);
                return true;

            case R.id.action_logout:
                session.logoutUser();
                return true;
        }
        return false;
    }

    public void onButtonClick(View v){
        switch (v.getId()){
            case R.id.btnAddNewProject:
                Intent i = new Intent(getApplicationContext(), CreateProjectActivity.class);
                startActivity(i);
                break;

            case R.id.btnEditProfile:
                Intent editProfileIntent = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(editProfileIntent);
                break;
        }
    }
}
