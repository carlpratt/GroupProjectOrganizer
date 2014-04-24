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
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateProjectActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    EditText inputProjectName;
    EditText inputProjectDescription;

    // url to create a new project
    private static String url_create_project = "http://group-project-organizer.herokuapp.com/create_project.php";

    public SessionManager session;
    HashMap<String, String> userDetails;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_project);

        session = new SessionManager(getApplicationContext());
        userDetails = session.getUserDetails();

        // Edit Text Fields
        inputProjectName = (EditText) findViewById(R.id.projectTitleEditText);

        inputProjectDescription = (EditText) findViewById(R.id.projectDescriptionEditText);

    }

    @Override
    public void onPause(){
        super.onPause();

        if (pDialog != null){
            pDialog.dismiss();
        }

        pDialog = null;
    }

    public void onButtonClick(View v){

        switch (v.getId()){

            case R.id.btnCreateNewProject:

                // Verify user is online
                if (isOnline()) {
                    new CreateProject().execute();
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
     * Background Async Task to create a new project
     * */
    class CreateProject extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CreateProjectActivity.this);
            pDialog.setMessage("Creating Project...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            String uid = userDetails.get(SessionManager.KEY_UID);
            String projectTitle = inputProjectName.getText().toString();
            String projectDescription = inputProjectDescription.getText().toString();

            projectTitle = projectTitle.replace("'","''");
            projectDescription = projectDescription.replace("'","''");

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("project_title", projectTitle));
            params.add(new BasicNameValuePair("project_description", projectDescription));
            params.add(new BasicNameValuePair("project_owner", uid));


            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_create_project,
                    "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                } else {
                    Toast.makeText(getApplicationContext(), "Project was not created", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            if (pDialog != null) {
                pDialog.dismiss();
            }

            Intent in = new Intent(getApplicationContext(),ShowProjectsActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(in);
        }

    }
}
