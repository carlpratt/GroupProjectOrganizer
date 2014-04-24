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
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;




    public class Startup extends Activity {

        private ProgressDialog pDialog;

        JSONParser jsonParser = new JSONParser();

        private static String url_get_projects = "http://group-project-organizer.herokuapp.com/get_projects.php";
        SessionManager session;

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
            setContentView(R.layout.loading);


            session = new SessionManager(getApplicationContext());
            userDetails = session.getUserDetails();

            if (isOnline()) {
                new getData().execute();

                Toast.makeText(getApplicationContext(), "Projects found", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(),
                        "Network connection required to get projects", Toast.LENGTH_SHORT).show();

                Intent in = new Intent(getApplicationContext(),ShowProjectsActivity.class);
                startActivity(in);
            }


        }




        private boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            }
            return false;
        }




        public class getData extends AsyncTask<String, String, String> {


            /**
             * Performing login
             */
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


                        Intent i = new Intent(getApplicationContext(), ShowProjectsActivity.class);
                        startActivity(i);
                    } else {
                        // failed to log in user
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

            protected void onPostExecute(String file_url) {

                if (pDialog != null) {
                    pDialog.dismiss();
                }
            }
        }
    }



