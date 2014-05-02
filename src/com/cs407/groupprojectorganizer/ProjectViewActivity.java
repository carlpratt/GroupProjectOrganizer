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
import android.view.View;
import android.widget.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProjectViewActivity extends Activity {


    public static int position;

    //Project
    public static ArrayList<String> project_title = new ArrayList<String>();
    public static ArrayList<String> project_desc = new ArrayList<String>();
    public static ArrayList<String> pids = new ArrayList<String>();
    public static ArrayList<String> pOwner = new ArrayList<String>();

    public static ArrayList<String> project_uids = new ArrayList<String>();///////////////////need to get from DB////NEED TO ADD TO WHEN ADD_USER BUTTON USED


    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    private static String url_delete_project = "http://group-project-organizer.herokuapp.com/delete_project.php";
    private static String url_get_project_users = "http://group-project-organizer.herokuapp.com/get_project_users.php";


    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USER = "user";
    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_FACEBOOK = "facebook";
    private static final String TAG_GOOGLE = "google";


    SessionManager session;

    String pid;
    HashMap<String, String> userDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.project_view);///////Moved to the AsyncTask getProjectUsers

        session = new SessionManager(getApplicationContext());
        userDetails = session.getUserDetails();

        //Start the AsyncTask to populate the ListView before showing on screen
        if (isOnline()) {
            new GetProjectUsers().execute();
        }

        //Need to put this in the AsyncTask?
        TextView proj = (TextView)findViewById(R.id.project_name_textview);
        TextView desc = (TextView)findViewById(R.id.project_description_edit_text);
        TextView own = (TextView)findViewById(R.id.textview_owner);

        proj.setText(project_title.get(position));
        desc.setText(project_desc.get(position));
        if(pOwner.get(position) == userDetails.get(SessionManager.KEY_UID)){
            own.setText("*Owner*");
        }

        pid = pids.get(position);

        // Only a project owner can delete a project
        if (!pOwner.get(position).equals(session.getUserDetails().get(SessionManager.KEY_UID))){
            Button deleteProjectButton = (Button) findViewById(R.id.btnDeleteProject);
            deleteProjectButton.setVisibility(View.INVISIBLE);
        }
    }

    public void onButtonClick(View view){

        switch (view.getId()){
            //Button to delete a project is pressed
            case R.id.btnDeleteProject:

                if (isOnline()){

                    new DeleteProject().execute();

                    project_desc.remove(position);
                    project_title.remove(position);
                    pids.remove(position);
                    pOwner.remove(position);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Network connection required to do this", Toast.LENGTH_SHORT).show();
                }

                break;

            //Button to add a team member is pressed
            case R.id.btnAddTeamMember:

                if (isOnline()) {

                    Intent i = new Intent (getApplicationContext(), AddTeamMemberActivity.class);

                    startActivity(i);
                    break;

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Network connection required to do this", Toast.LENGTH_SHORT).show();
                }
                break;
        }
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
    class DeleteProject extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ProjectViewActivity.this);
            pDialog.setMessage("Deleting Project...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("pid", pid));

            Log.d("pid of deleted project", pid);

            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_delete_project,
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

            Intent intent = new Intent(getApplicationContext(),ShowProjectsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    /**
     * Background Async Task to get all team members associated with the current project in view when the
     * ProjectViewActivity is started
     */
    class GetProjectUsers extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ProjectViewActivity.this);
            pDialog.setMessage("Getting Project Team Members...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            String uid = userDetails.get(SessionManager.KEY_UID);

            //Build parameters associated to user
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("uid", uid));

            //getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_get_project_users, "POST", params);///PHP CODE

            //check log cat for response
            Log.d("Create Response", json.toString());

            //check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                    /*
                        NEED AN ARRAY OF USERS ASSOCIATED WITH THE CURRENT PROJECT
                        EACH ITEM IN THE LIST WILL HAVE THE USER'S NAME, AND WILL NEED
                        THE INFORMATION ASSOCIATED WITH THE UID TO FORMAT THE NEXT SCREEN (project_user_view)

                        NEED TO USE THE USER_TO_PROJECTS TABLE?
                     */

                    JSONArray projectUsers = json.getJSONArray(TAG_USER);
                    ViewUserActivity.name.clear();
                    ViewUserActivity.email.clear();
                    ViewUserActivity.phone.clear();
                    ViewUserActivity.facebook.clear();
                    ViewUserActivity.google.clear();

                    for (int i = 0; i < projectUsers.length(); i++) {
                        JSONObject temp = projectUsers.getJSONObject(i);

                        project_uids.add(temp.getString(TAG_USER));
                        ViewUserActivity.name.add(temp.getString(TAG_NAME));
                        ViewUserActivity.email.add(temp.getString(TAG_EMAIL));
                        ViewUserActivity.phone.add(temp.getString(TAG_PHONE));
                        ViewUserActivity.facebook.add(temp.getString(TAG_FACEBOOK));
                        ViewUserActivity.google.add(temp.getString(TAG_GOOGLE));
                    }

                } else {
                    // Failed to get anything from database
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "No Team Members were found",
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
         * Populate the ListView with the Project's associated users
         */
        protected void onPostExecute(String file_url) {

            if (pDialog != null) {
                pDialog.dismiss();
            }

            setContentView(R.layout.project_view);

            ArrayList<String> items = new ArrayList<String>();

            for (int i = 0; i < project_uids.size(); i++) {
                items.add(ViewUserActivity.name.get(i));
            }

            //create list of the project members and populate the ListView with them
            ListView usersList = (ListView)findViewById(R.id.list_project_members);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.listpop,
                    R.id.titleLine, items);
            usersList.setAdapter(adapter);

            //Handle click events on Users
            usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ViewUserActivity.position = position;
                    Intent intent = new Intent(ProjectViewActivity.this, ViewUserActivity.class);
                    startActivity(intent);
                }
            });

        }
    }
}