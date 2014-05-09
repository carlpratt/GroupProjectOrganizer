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

    public static ArrayList<Project> projects = new ArrayList<Project>();

    //ArrayList of Users to hold each user's attributes
    private ArrayList<AppUser> users = new ArrayList<AppUser>();
    //ArrayList of Users currently in project
    private ArrayList<String> inProject = new ArrayList<String>();


    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    private static String url_delete_project = "http://group-project-organizer.herokuapp.com/delete_project.php";

    private static String url_get_users_in_project = "http://group-project-organizer.herokuapp.com/get_users_in_project.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USERS = "users";
    private static final String TAG_UID = "uid";
    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_FACEBOOK = "facebook";
    private static final String TAG_GOOGLE = "google";

    SessionManager session;

    private String pid;

    HashMap<String, String> userDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
              pid = extras.getString("PID");
        }

        //setContentView(R.layout.project_view);

        session = new SessionManager(getApplicationContext());
        userDetails = session.getUserDetails();

        //Start the AsyncTask to populate the ListView before showing on screen
        if (isOnline()) {
            new GetProjectUsers().execute();
        }

        //store the project's pid
        pid = projects.get(position).getPid();

        // Only a project owner can delete a project
        if (!projects.get(position).getProjOwner().equals(session.getUserDetails().get(SessionManager.KEY_UID))){
//            Button deleteProjectButton = (Button) findViewById(R.id.btnDeleteProject);
//            deleteProjectButton.setVisibility(View.INVISIBLE);
//
//            Button addTeamMemberButton = (Button) findViewById(R.id.btnAddTeamMember);
//            addTeamMemberButton.setVisibility(View.INVISIBLE);
            System.out.println(" ");
            System.out.println("NO, THIS IS NOT THE PROJECT OWNER- MAKE BUTTON INVISIBLE");
            System.out.println(" ");
        }
    }

    /**
     * Ensures that if the user pushes the 'back' button, the next screen will be ShowProjectsActivity
     */
    @Override
    public void onBackPressed() {//NOT WORKING
        System.out.println("***************onBackPressed() METHOD");
        super.onBackPressed();
    }

    public void onButtonClick(View view){

        switch (view.getId()) {

            case R.id.btnDeleteProject:


            if (isOnline()) {

                new DeleteProject().execute();

                projects.remove(position);

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

                    i.putStringArrayListExtra("CURRENT_MEMBERS", inProject);
                    i.putExtra("PID",pid);
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
        Queries the Database and gathers necessary information
         */
        protected String doInBackground(String... args) {

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

                    //Goes through each JSON-team-member and stores them as 'AppUser' objects
                    for (int i = 0; i < projectUsers.length(); i++) {
                        JSONObject temp = projectUsers.getJSONObject(i);

                        AppUser tempUser = new AppUser(temp.getString(TAG_UID),temp.getString(TAG_NAME),
                                temp.getString(TAG_EMAIL), temp.getString(TAG_PHONE),temp.getString(TAG_FACEBOOK),
                                temp.getString(TAG_GOOGLE), i);

                        //stores all 'AppUser' objects in 'users'
                        users.add(tempUser);
                        //stores all project-users's ID's for AddTeamMemberActivityBase class
                        inProject.add(temp.getString(TAG_UID));

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


            proj.setText(projects.get(position).getProjTitle());
            desc.setText(projects.get(position).getProjDescription());
            if(projects.get(position).getProjOwner().equals(userDetails.get(SessionManager.KEY_UID))){////////////not working
                own.setText("*Owner*");
            }
            //setContentView(R.layout.project_view);

            //ArrayList to hold items that will populate the ListView
            ArrayList<String> items = new ArrayList<String>();

            //Add all user's names to 'items'
            for (int i = 0; i < users.size(); i++) {
                items.add(users.get(i).getName());
            }

            //create list of the project members and populate the ListView with 'items'
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
                public void onItemClick(AdapterView<?> parent, View view, int clickPosition, long id) {

                    Intent intent = new Intent(ProjectViewActivity.this, ViewUserActivity.class);

                    intent.putExtra("USER_NAME", users.get(clickPosition).getName());
                    intent.putExtra("USER_EMAIL", users.get(clickPosition).getEmail());
                    intent.putExtra("USER_PHONE", users.get(clickPosition).getPhone());
                    intent.putExtra("USER_FACEBOOK", users.get(clickPosition).getFacebook());
                    intent.putExtra("USER_GOOGLE", users.get(clickPosition).getGoogle());

                    intent.putExtra("USER_UID", users.get(clickPosition).getUid());
                    intent.putExtra("PROJECT_PID", projects.get(position).getPid());

                    startActivity(intent);
                }
            });

        }
    }
}

