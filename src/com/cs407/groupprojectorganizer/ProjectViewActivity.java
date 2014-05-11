package com.cs407.groupprojectorganizer;

import android.app.ListActivity;
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

public class ProjectViewActivity extends ListActivity {


    public static int position;

    public static ArrayList<Project> projects = new ArrayList<Project>();

//    public static ArrayList<String> project_title = new ArrayList<String>();
//    public static ArrayList<String> project_desc = new ArrayList<String>();
//    public static ArrayList<String> pids = new ArrayList<String>();
//    public static ArrayList<String> pOwner = new ArrayList<String>();

    private static final String TAG_USERS = "users";
    private static final String TAG_UID = "uid";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_FACEBOOK = "facebook";
    private static final String TAG_GOOGLE = "google";

    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    private static String url_delete_project = "http://group-project-organizer.herokuapp.com/delete_project.php";
    private static String url_get_users_in_project = "http://group-project-organizer.herokuapp.com/get_users_in_project.php";

    private ArrayList<HashMap<String, String>> usersList = new ArrayList<HashMap<String, String>>(); // Users for adapter

    private ListAdapter adapter; // List adapter
    SessionManager session;

    String pid;
    HashMap<String, String> userDetails;

    private boolean owner = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_view);

//        for (int i = 0; i < project_title.size(); i++) {
//            System.out.println(project_title.get(i));
//        }

        session = new SessionManager(getApplicationContext());
        userDetails = session.getUserDetails();
        TextView proj = (TextView)findViewById(R.id.project_name_textview);
        TextView desc = (TextView)findViewById(R.id.project_description_edit_text);
        TextView own = (TextView)findViewById(R.id.textview_owner);


//        proj.setText(project_title.get(position));
//        desc.setText(project_desc.get(position));
        proj.setText(projects.get(position).getProjTitle());
        desc.setText(projects.get(position).getProjDescription());
//        if(pOwner.get(position) == userDetails.get(SessionManager.KEY_UID)){
//            own.setText("*Owner*");
//        }
        if(projects.get(position).getProjOwner().equals(userDetails.get(SessionManager.KEY_UID))) {
            own.setText("*Owner*");
        }

//        pid = pids.get(position);
        pid = projects.get(position).getPid();

        // Only a project owner can delete a project
//        if (!pOwner.get(position).equals(session.getUserDetails().get(SessionManager.KEY_UID))){
        if (!projects.get(position).getProjOwner().equals(session.getUserDetails().get(SessionManager.KEY_UID))) {
            Button deleteProjectButton = (Button) findViewById(R.id.btnDeleteProject);
            deleteProjectButton.setVisibility(View.GONE);
            Button editProjectButton = (Button) findViewById(R.id.btnEditProject);
            editProjectButton.setVisibility(View.GONE);
            owner = false;
        }

        // Grab all of the users in the project so we can populate the list
        if (isOnline()) {
            new GetUsersInProject().execute();
        }
    }

    public void onButtonClick(View view){

        switch (view.getId()){
            case R.id.btnDeleteProject:

                if (isOnline()){

                    new DeleteProject().execute();

//                    project_desc.remove(position);
//                    project_title.remove(position);
//                    pids.remove(position);
//                    pOwner.remove(position);
                    projects.remove(position);
                }
                break;

            case R.id.btnAddTeamMember:

                if (isOnline()){
                    Intent intent = new Intent(getApplicationContext(), AddTeamMemberActivity.class);
                    intent.putExtra(TAG_PID, pid);
                    startActivity(intent);
                }
                break;

            case R.id.btnEditProject:

                if (isOnline()) {
                    Intent i = new Intent(getApplicationContext(), EditProjectActivity.class);
                    i.putExtra("PID", pid);
                    startActivity(i);
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.projectview, menu);
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
            case R.id.action_refresh:
                new GetUsersInProject().execute();
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
     * Shows the users in this project in a list
     */
    private void setUsersListAdapter(){

        adapter = new SimpleAdapter(ProjectViewActivity.this, usersList, R.layout.user_in_project_list,
                new String[] {TAG_NAME},
                new int[] {R.id.userInProjectName});

        setListAdapter(adapter);

        ListView usersInProjectListView = getListView();
        usersInProjectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {
                Intent intent = new Intent(getApplicationContext(), ViewUserInProjectActivity.class);
                intent.putExtra(TAG_NAME, usersList.get(position).get(TAG_NAME));
                intent.putExtra(TAG_EMAIL, usersList.get(position).get(TAG_EMAIL));
                intent.putExtra(TAG_PHONE, usersList.get(position).get(TAG_PHONE));
                intent.putExtra(TAG_FACEBOOK, usersList.get(position).get(TAG_FACEBOOK));
                intent.putExtra(TAG_GOOGLE, usersList.get(position).get(TAG_GOOGLE));

                intent.putExtra("IS_OWNER",owner);
                intent.putExtra("PID", pid);
                intent.putExtra("UID", usersList.get(position).get(TAG_UID));

                startActivity(intent);
            }
        });
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
            params.add(new BasicNameValuePair(TAG_PID, pid));

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
     * Background Async Task to fetch all users in a project
     * */
    class GetUsersInProject extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ProjectViewActivity.this);
            pDialog.setMessage("Fetching Project Info...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_PID, pid));

            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_get_users_in_project,
                    "POST", params);

            Log.d("pid of selected project", pid);

            try{
                // check log cat for response
                Log.d("Create Response", json.toString());

                JSONArray userArray = json.getJSONArray(TAG_USERS);

                for (int i = 0; i < userArray.length(); i++) {

                    JSONObject user = userArray.getJSONObject(i);

                    HashMap<String, String> map = new HashMap<String, String>();

                    // If user is not us, add them to the display list
                    if (!user.get(TAG_UID).toString().equals(session.getUserDetails().get(SessionManager.KEY_UID))){
                        map.put(TAG_UID, user.get(TAG_UID).toString());
                        map.put(TAG_NAME, user.get(TAG_NAME).toString());
                        map.put(TAG_EMAIL, user.get(TAG_EMAIL).toString());
                        map.put(TAG_PHONE, user.get(TAG_PHONE).toString());
                        map.put(TAG_FACEBOOK, user.get(TAG_FACEBOOK).toString());
                        map.put(TAG_GOOGLE, user.get(TAG_GOOGLE).toString());

                        usersList.add(map);
                    }
                }
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

            setUsersListAdapter();
        }
    }
}