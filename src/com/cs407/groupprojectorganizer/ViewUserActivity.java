package com.cs407.groupprojectorganizer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

/**
 * This class populates the User Information of the Team Member that was clicked on
 * in the ProjectViewActivity
 */
public class ViewUserActivity extends Activity{


    private static String url_remove_team_member = "http://group-project-organizer.herokuapp.com/remove_team_member.php";

    public SessionManager session;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    HashMap<String, String> userDetails;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
//    public SessionManager session;
//    private ProgressDialog pDialog;
//    JSONParser jsonParser = new JSONParser();
//    HashMap<String, String> userDetails;

    public static int position;
    private static String pid;
    private static String uid;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
        session =  new SessionManager(getApplicationContext());
       userDetails = session.getUserDetails();


        setContentView(R.layout.project_user_view);

        TextView userName = (TextView) findViewById(R.id.projectUserTextViewTitle);
        TextView textName= (TextView) findViewById(R.id.userNameTextView);
        TextView textEmail = (TextView) findViewById(R.id.userEmailTextView);
        TextView textPhone = (TextView) findViewById(R.id.userPhoneTextView);
        TextView textFacebook = (TextView) findViewById(R.id.userFacebookTextView);
        TextView textGoogle = (TextView) findViewById(R.id.userGoogleTextView);
        boolean getOwner = false;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userName.setText(extras.getString("USER_NAME"));
            textName.setText(extras.getString("USER_NAME"));
            textEmail.setText(extras.getString("USER_EMAIL"));
            textPhone.setText(extras.getString("USER_PHONE"));
            textFacebook.setText(extras.getString("USER_FACEBOOK"));
            textGoogle.setText(extras.getString("USER_GOOGLE"));
            getOwner = extras.getBoolean("IS_OWNER");
            pid = extras.getString("PID");
            uid = extras.getString("UID");
        }
        if(!getOwner) {
            Button hide = (Button)findViewById(R.id.btnDeleteTeamMember);
            hide.setVisibility(View.GONE);

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.teammember, menu);
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

            case R.id.action_projects:
                Intent projectsIntent = new Intent(getApplicationContext(),ShowProjectsActivity.class);
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




//    /**
//     * Determines if android device has network access
//     */
//    private boolean isOnline() {
//        ConnectivityManager cm =
//                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
//            return true;
//        }
//        return false;
//    }



    class DeleteMember extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewUserActivity.this);
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
