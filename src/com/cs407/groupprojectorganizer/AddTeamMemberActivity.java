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
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddTeamMemberActivity extends Activity {

    private static String url_add_team_member = "http://group-project-organizer.herokuapp.com/add_team_member.php";
    private ProgressDialog pDialog;
    private JSONParser jsonParser = new JSONParser();

    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_PID = "pid";

    private EditText name;
    private EditText email;

    private String pid; // pid of project we want to add someone to

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_team_member);

        name = (EditText) findViewById(R.id.addPersonName);
        email = (EditText) findViewById(R.id.addPersonEmail);

        Intent intent = getIntent();
        pid = intent.getStringExtra(TAG_PID);
    }

    public void onButtonClick(View view){

        switch (view.getId()){

            case R.id.btnSearchAndAddGroupMember:

                if (isOnline()){
                    new AddPersonToProject().execute();
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
     * Background Async Task to add new team member to project
     * */
    class AddPersonToProject extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddTeamMemberActivity.this);
            pDialog.setMessage("Searching for User...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_PID, pid));
            //params.add(new BasicNameValuePair(TAG_NAME, name.toString()));
            params.add(new BasicNameValuePair(TAG_EMAIL, email.getText().toString()));

            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_add_team_member,
                    "POST", params);

            Log.d("Create Response", json.toString());

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            if (pDialog != null) {
                pDialog.dismiss();
            }

            Intent intent = new Intent(getApplicationContext(), ProjectViewActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
