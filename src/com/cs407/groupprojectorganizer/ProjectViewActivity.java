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
import android.widget.TextView;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProjectViewActivity extends Activity {


    public static int position;
    public static ArrayList<String> project_title = new ArrayList<String>();
    public static ArrayList<String> project_desc = new ArrayList<String>();
    public static ArrayList<String> pids = new ArrayList<String>();
    public static ArrayList<String> pOwner = new ArrayList<String>();

    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    private static String url_delete_project = "http://group-project-organizer.herokuapp.com/delete_project.php";

    SessionManager session;

    String pid;
    HashMap<String, String> userDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_view);

        session = new SessionManager(getApplicationContext());
        userDetails = session.getUserDetails();
        TextView proj = (TextView)findViewById(R.id.project_name_textview);
        TextView desc = (TextView)findViewById(R.id.project_description_edit_text);
        TextView own = (TextView)findViewById(R.id.textview_owner);

        Log.d("here",project_desc.toString());
        Log.d("here",project_title.toString());
        Log.d("here",pids.toString());

        proj.setText(project_title.get(position));
        desc.setText(project_desc.get(position));
        if(pOwner.get(position) == userDetails.get(SessionManager.KEY_UID)){
            own.setText("*Owner*");
        }
        pid = pids.get(position);

        Log.d("pid set in onCreate", pid);
    }

    public void onButtonClick(View view){

        switch (view.getId()){
            case R.id.btnDeleteProject:

                if (isOnline()){
                    
                    new DeleteProject().execute();
                    project_desc.remove(position);
                    project_title.remove(position);
                    pids.remove(position);
                    pOwner.remove(position);

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
}
