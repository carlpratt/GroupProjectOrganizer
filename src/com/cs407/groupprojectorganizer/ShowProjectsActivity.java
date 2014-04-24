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

    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    private static String url_get_projects = "http://group-project-organizer.herokuapp.com/get_projects.php";

    HashMap<String, String> userDetails;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USER = "user";
    private static final String TAG_TITLE = "project_title";
    private static final String TAG_PID = "pid";
    private static final String TAG_DESC = "project_description";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session =  new SessionManager(getApplicationContext());
        userDetails = session.getUserDetails();

        if (isOnline()) {
            new GetProjects().execute();
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
     * This AsyncTask gets all projects associated with the current user when
     *  the ShowProjectsActivity is started
     */
    class GetProjects extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ShowProjectsActivity.this);
            pDialog.setMessage("Getting your projects...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            String uid = userDetails.get(SessionManager.KEY_UID);
            ShowProjectsActivity.valuesTitles.clear();
            ProjectView.project_title.clear();
            ProjectView.project_desc.clear();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("uid", uid));

            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_get_projects,
                    "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    ArrayList<String> pids = new ArrayList<String>();
                    JSONArray userArray = json.getJSONArray(TAG_USER);

                    for (int i = 0; i < userArray.length(); i++) {
                        JSONObject user = userArray.getJSONObject(i);
                        ShowProjectsActivity.valuesTitles.add(user.getString(TAG_TITLE));
                        pids.add(user.getString(TAG_PID));
                        ProjectView.project_desc.add(user.getString(TAG_DESC));
                        ProjectView.project_title.add(user.getString(TAG_TITLE));
                    }
                } else {
                    // Failed to get anything from database
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

        /**
         * Once the network operations are complete, this method
         *  populates the listview
         */
        protected void onPostExecute(String file_url) {

            if (pDialog != null) {
                pDialog.dismiss();
            }

            setContentView(R.layout.projects_list);

            ListView projectList = (ListView)findViewById(R.id.listView);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.listpop, R.id.titleLine, valuesTitles);
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
    }
}
