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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ShowProjectsActivity extends Activity {

    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();


    private ArrayList<String> valuesTitles = new ArrayList<String>();



    private static String url_get_projects = "http://group-project-organizer.herokuapp.com/get_projects.php";
    SessionManager session;

    HashMap<String, String> userDetails;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USER = "user";
    private static final String TAG_TITLE = "project_title";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.projects_list);


        session = new SessionManager(getApplicationContext());
        userDetails = session.getUserDetails();


        //get stuff from the data base
        ListView projectList = (ListView)findViewById(R.id.listView);

        //doInBackground();
        Log.d("mymy","here");

        new getData().execute();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.listpop, R.id.titleLine);
        // Assign adapter to ListView
        projectList.setAdapter(adapter);




        for(int y = 0; y < valuesTitles.size();y++) {
            Log.d("should", valuesTitles.get(y).toString());
        }
        adapter.addAll(valuesTitles);



        projectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                //String itemValue = (String) projectList.getItemAtPosition(position);
               //when the user clicks on one of the list items.



            }

        });
    }




    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }




    public class getData extends AsyncTask<String, String, String> {



        /**
         * Performing login
         */
        protected String doInBackground(String... args) {
            String uid = userDetails.get(SessionManager.KEY_UID);


            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("uid", uid));
           Log.d("checking", params.toString());

            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_get_projects,
                    "POST", params);



            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                Log.d("thishouse","jjj");
                if (success == 1) {

                    JSONArray userArray = json.getJSONArray(TAG_USER);

                    for(int i = 0; i < userArray.length();i++) {
                        JSONObject user = userArray.getJSONObject(i);
                        Log.d("ggggg",userArray.getString(i));
                        valuesTitles.add(user.getString(TAG_TITLE));

                        //Log.d("heyo", valuesTitles.get(i));
                    }

                    Log.d("hereiam","coolcool");

                   // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.listpop, R.id.titleLine, valuesTitles);

                    // Assign adapter to ListView
                   // projectList.setAdapter(adapter);

                    // Update the session information
                    //session.createSession(uid, name, email, phone, facebook, google);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "found",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                } else {
                    // failed to log in user
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "No projects were found",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
        protected void onPostExecute(String file_url) {

            if (pDialog != null) {
                pDialog.dismiss();
            }
        }
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

            case R.id.btnEditProfile:
                Intent editProfileIntent = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(editProfileIntent);
                break;


        }
    }
}
