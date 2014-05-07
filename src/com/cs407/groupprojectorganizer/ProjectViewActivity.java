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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProjectViewActivity extends Activity {


    public static int position;
    //Project Attributes

    public static ArrayList<String> project_title = new ArrayList<String>();
    public static ArrayList<String> project_desc = new ArrayList<String>();
    public static ArrayList<String> pids = new ArrayList<String>();
    public static ArrayList<String> pOwner = new ArrayList<String>();

    //User Attributes
    private ArrayList<String> uids = new ArrayList<String>();
    private ArrayList<String> name = new ArrayList<String>();
    private ArrayList<String> email = new ArrayList<String>();
    private ArrayList<String> phone = new ArrayList<String>();
    private ArrayList<String> facebook = new ArrayList<String>();
    private ArrayList<String> google = new ArrayList<String>();


    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    private static String url_delete_project = "http://group-project-organizer.herokuapp.com/delete_project.php";

    private static String url_get_users_in_project = "http://group-project-organizer.herokuapp.com/get_users_in_project.php";

    public static JSONObject selected;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    //private static final String TAG_MESSAGE = "message";
    private static final String TAG_USERS = "users";
    private static final String TAG_UID = "uid";
    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_FACEBOOK = "facebook";
    private static final String TAG_GOOGLE = "google";


    SessionManager session;

    private String u_email;
    private String pid;
    HashMap<String, String> userDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.project_view);

        session = new SessionManager(getApplicationContext());
        userDetails = session.getUserDetails();

        //Start the AsyncTask to populate the ListView before showing on screen
        if (isOnline()) {
            new GetProjectUsers().execute();
        }

        //store the project's pid
        pid = pids.get(position);
        //store the user's email
        //u_email = email.get(ViewUserActivity.position);/////////////////////////////////


        // Only a project owner can delete a project
        if (!pOwner.get(position).equals(session.getUserDetails().get(SessionManager.KEY_UID))){
            Button deleteProjectButton = (Button) findViewById(R.id.btnDeleteProject);
            deleteProjectButton.setVisibility(View.INVISIBLE);

            Button addTeamMemberButton = (Button) findViewById(R.id.btnAddTeamMember);
            addTeamMemberButton.setVisibility(View.INVISIBLE);
        }
    }

    public void onButtonClick(View view){

        switch (view.getId()) {

            case R.id.btnDeleteProject:


            if (isOnline()) {

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

                    Intent i = new Intent(getApplicationContext(), AddTeamMemberActivity.class);
                    i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("PID", pid);
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

        /*
        Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ProjectViewActivity.this);
            pDialog.setMessage("Getting Project Team Members...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /*
        Queries the Database and gathers necesssary information
         */
        protected String doInBackground(String... args) {//////////////////////Not always getting the right information
            //String pid = userDetails.get(SessionManager.KEY_UID);

            //Build parameters associated to user
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("pid", pid));

            //getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_get_users_in_project,
                    "POST", params);

            //check log cat for response
            Log.d("Create Response", json.toString());

            //check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                    //creates array of team members associated with the project
                    JSONArray projectUsers = json.getJSONArray(TAG_USERS);
                    uids.clear();
                    name.clear();
                    email.clear();
                    phone.clear();
                    facebook.clear();
                    google.clear();

                    //Goes through each user and stores their information in ArrayLists
                    for (int i = 0; i < projectUsers.length(); i++) {
                        JSONObject temp = projectUsers.getJSONObject(i);

                        uids.add(temp.getString(TAG_UID));
                        name.add(temp.getString(TAG_NAME));
                        email.add(temp.getString(TAG_EMAIL));
                        phone.add(temp.getString(TAG_PHONE));
                        facebook.add(temp.getString(TAG_FACEBOOK));
                        google.add(temp.getString(TAG_GOOGLE));
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

            setContentView(R.layout.project_view);

            TextView proj = (TextView)findViewById(R.id.project_name_textview);
            TextView desc = (TextView)findViewById(R.id.project_description_edit_text);
            TextView own = (TextView)findViewById(R.id.textview_owner);

            proj.setText(project_title.get(position));
            desc.setText(project_desc.get(position));
            if(pOwner.get(position) == userDetails.get(SessionManager.KEY_UID)){
                own.setText("*Owner*");
            }

            ArrayList<String> items = new ArrayList<String>();


            for (int i = 0; i < name.size(); i++) {
                items.add(name.get(i));
            }

            for (int j = 0; j < name.size(); j++) {
                System.out.println(name.get(j));
            }

            //create list of the project members and populate the ListView with them
            ListView usersList = (ListView)findViewById(R.id.list_project_members);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.listpop,
                    R.id.titleLine, items);
            usersList.setAdapter(adapter);

            if (pDialog != null) {
                pDialog.dismiss();
            }

            //Handle click events on Users
            usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ViewUserActivity.position = position;

                    Intent intent = new Intent(ProjectViewActivity.this, ViewUserActivity.class);

                    intent.putExtra("USER_NAME", name.get(position));
                    intent.putExtra("USER_EMAIL", email.get(position));
                    intent.putExtra("USER_PHONE", phone.get(position));
                    intent.putExtra("USER_FACEBOOK", facebook.get(position));
                    intent.putExtra("USER_GOOGLE", google.get(position));


                    startActivity(intent);
                }
            });

        }
    }
}

