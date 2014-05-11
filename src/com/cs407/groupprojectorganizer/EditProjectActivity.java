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

public class EditProjectActivity extends Activity {
    // Progress Dialog
    private ProgressDialog pDialog;

    private int pos = ProjectViewActivity.position;

    JSONParser jsonParser = new JSONParser();
    EditText inputTitle;
    EditText inputDescription;

    //url to edit the project
    private static String url_edit_project = "http://group-project-organizer.herokuapp.com/edit_project.php";

    public SessionManager session;
    HashMap<String, String> userDetails;

    //JSON Node Names
    private static final String TAG_SUCCESS = "success";
    //Project's pid
    private String pid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_project);

        //Get project's pid
        Bundle extras = getIntent().getExtras();
        if(extras != null)
            pid = extras.getString("PID");

        session = new SessionManager(getApplicationContext());
        userDetails = session.getUserDetails();

        //Edit Text Fields
        inputTitle = (EditText) findViewById(R.id.titleEditText);
        inputDescription = (EditText) findViewById(R.id.descriptionEditText);

        //Set Text Fields

    }

    // Makes sure program doesn't crash from pDialog not being dismissed during
    // the background async task
    @Override
    public void onPause(){
        super.onPause();

        if (pDialog != null){
            pDialog.dismiss();
        }
        pDialog = null;
    }

    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdateProject:

                //Verify user is online
                if (isOnline()) {
                    new UpdateProject().execute();
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
     * Background AsyncTask to execute the Project update
     */
    class UpdateProject extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditProjectActivity.this);
            pDialog.setMessage("Updating Project...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            String title = inputTitle.getText().toString();
            String description = inputDescription.getText().toString();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("pid", pid));
            params.add(new BasicNameValuePair("title", title));
            params.add(new BasicNameValuePair("description", description));

            //getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_edit_project,
                    "POST", params);

            //check log cat for response
            Log.d("Create Response", json.toString());

            //check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                    //update the project info
//                    ProjectViewActivity.projects.get(ProjectViewActivity.position)
//                            .setProjTitle(title);
//                    ProjectViewActivity.projects.get(ProjectViewActivity.position)
//                            .setProjDescription(description);
//                    ProjectViewActivity.project_title.get(pos) = title;
                    ProjectViewActivity.projects.get(pos).setProjTitle(title);
                    ProjectViewActivity.projects.get(pos).setProjDescription(description);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            //dismiss dialog once done
            if (pDialog != null)
                pDialog.dismiss();

            Intent i = new Intent(getApplicationContext(), ProjectViewActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }

    }

}

