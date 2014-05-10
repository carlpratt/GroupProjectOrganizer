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



public class PromptView extends Activity {



    private static String project_title;
    private static String project_desc;
    private static String pid;
    private static String pOwner;

    private static final String TAG_USERS = "users";


    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();


    private static String url_accept_project = "http://group-project-organizer.herokuapp.com/accept_project.php";
    private static String url_decline_project = "http://group-project-organizer.herokuapp.com/decline_project.php";

    private ArrayList<HashMap<String, String>> usersList = new ArrayList<HashMap<String, String>>(); // Users for adapter

    SessionManager session;


    HashMap<String, String> userDetails;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prompt_view);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            project_title = extras.getString("NAME");
            pid = extras.getString("PID");
            project_desc = extras.getString("DESC");
            pOwner = extras.getString("OWNER");
        }


        session = new SessionManager(getApplicationContext());
        userDetails = session.getUserDetails();
        TextView proj = (TextView) findViewById(R.id.accept_project_title);
        TextView desc = (TextView) findViewById(R.id.accept_project_description);
        TextView own = (TextView) findViewById(R.id.accept_owner);

        proj.setText(project_title);
        desc.setText(project_desc);
        own.setText("Owner: " + pOwner);



    }

    public void onButtonClick(View view){

        switch (view.getId()){
            case R.id.btnAcceptProject:

                if (isOnline()){

                    new AcceptProject().execute();

                }
                break;

            case R.id.btnDecline:

                if (isOnline()){
                   new DeclineProject().execute();


                }
        }
    }
//TODO
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.projectview, menu);
        return true;
    }

//TODO
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

            case R.id.action_projects:
                Intent projectsIntent = new Intent(getApplicationContext(),ShowProjectsActivity.class);
                startActivity(projectsIntent);
                return true;
        }
        return false;
    }

    /**
     * Determines if android device has network access
     */
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }



    /**
     * Background Async Task to delete a project
     * */
    class DeclineProject extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PromptView.this);
            pDialog.setMessage("Declining...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            String uid = userDetails.get(SessionManager.KEY_UID);
            params.add(new BasicNameValuePair("pid", pid));
            params.add(new BasicNameValuePair("uid", uid));


            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_decline_project,
                    "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            if (pDialog != null) {
                pDialog.dismiss();
            }

            Intent intent = new Intent(getApplicationContext(),PromptApproval.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }


    /**
     * Background Async Task to fetch all users in a project
     * */
    class AcceptProject extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PromptView.this);
            pDialog.setMessage("Joining Project...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            String uid = userDetails.get(SessionManager.KEY_UID);

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("uid", uid));
            params.add(new BasicNameValuePair("pid", pid));

            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_accept_project,
                    "POST", params);



            try{
                // check log cat for response
                Log.d("Create Response", json.toString());

                JSONArray userArray = json.getJSONArray(TAG_USERS);



            }
            catch (JSONException e){

            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            if (pDialog != null) {
                pDialog.dismiss();
            }
            Intent intent = new Intent(getApplicationContext(),PromptApproval.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }
    }
}