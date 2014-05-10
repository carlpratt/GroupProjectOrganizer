package com.cs407.groupprojectorganizer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewUserInProjectActivity extends Activity {

    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_FACEBOOK = "facebook";
    private static final String TAG_GOOGLE = "google";

    private TextView name;
    private TextView email;
    private TextView phone;
    private TextView facebook;
    private TextView google;

    private static String url_remove_team_member = "http://group-project-organizer.herokuapp.com/remove_team_member.php";

    public SessionManager session;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    HashMap<String, String> userDetails;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";


    public static int position;
    private static String pid;
    private static String uid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);

        session = new SessionManager(getApplicationContext());

        name = (TextView) findViewById(R.id.displayName);
        email = (TextView) findViewById(R.id.displayEmail);
        phone = (TextView) findViewById(R.id.displayPhone);
        facebook = (TextView) findViewById(R.id.displayFacebook);
        google = (TextView) findViewById(R.id.displayGoogle);

        Intent intent = getIntent();

        name.setText(intent.getStringExtra(TAG_NAME));
        email.setText(intent.getStringExtra(TAG_EMAIL));
        phone.setText(intent.getStringExtra(TAG_PHONE));
        facebook.setText(intent.getStringExtra(TAG_FACEBOOK));
        google.setText(intent.getStringExtra(TAG_GOOGLE));

        Linkify.addLinks(facebook, Linkify.ALL);
        Linkify.addLinks(google, Linkify.ALL);

        boolean getOwner = intent.getBooleanExtra("IS_OWNER", false);

        if(!getOwner) {
            Button hide = (Button)findViewById(R.id.btnDeleteTeamMember);
            hide.setVisibility(View.GONE);
        }

        pid = intent.getStringExtra("PID");
        uid = intent.getStringExtra("UID");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.teammember, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

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
                Intent projectsIntent = new Intent(getApplicationContext(), ShowProjectsActivity.class);
                startActivity(projectsIntent);
                return true;
            case R.id.action_projectview:
                Intent in = new Intent(getApplicationContext(), ProjectViewActivity.class);
                startActivity(in);
        }
        return false;
    }

    public void onButtonClick(View v){
        switch (v.getId()){
            case R.id.btnDeleteTeamMember:
                //need to run deleteteam memeber php
                new DeleteMember().execute();
                Intent in = new Intent(getApplicationContext(), ProjectViewActivity.class);
                in.putExtra("PID", pid);
                startActivity(in);
                break;
        }
    }

    class DeleteMember extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewUserInProjectActivity.this);
            pDialog.setMessage("Deleting Team Member");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {


            // Building Parameters
            //Puts the user by his 'id' in this list- only has 1 element- the current user
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("uid", uid));
            params.add(new BasicNameValuePair("pid", pid));

            // getting JSON Object
            //php request for database
            JSONObject json = jsonParser.makeHttpRequest(url_remove_team_member,
                    "POST", params);

            // check log cat for response
            Log.d("delete", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                String message = json.getString(TAG_MESSAGE);
                if (success == 1) {

                } else {
                    // Failed to get anything from database
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Could not delete the team member",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * Once the network operations are complete, this method
         *  populates the listview
         */
        protected void onPostExecute(String file_url) {

            if (pDialog != null) {
                pDialog.dismiss();
            }

        }
    }
}