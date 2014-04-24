package com.cs407.groupprojectorganizer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ShowProjectsActivity extends Activity {

    public static ArrayList<String> valuesTitles = new ArrayList<String>();
    public SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.projects_list);

        session =  new SessionManager(getApplicationContext());
        //get stuff from the data base
        ListView projectList = (ListView)findViewById(R.id.listView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.listpop, R.id.titleLine, valuesTitles);
        // Assign adapter to ListView
        projectList.setAdapter(adapter);

        projectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                ProjectView.position = position;
                Intent intent = new Intent(ShowProjectsActivity.this, ProjectView.class);

                startActivity(intent);
            }
        });
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

            case R.id.action_settings:
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
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
        }
    }
}
