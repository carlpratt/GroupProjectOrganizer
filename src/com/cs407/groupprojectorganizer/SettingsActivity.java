package com.cs407.groupprojectorganizer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.service.textservice.SpellCheckerService;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SettingsActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    CheckBox inputDiscover;
    CheckBox inputPrompt;
    private boolean discover;
    private boolean prompt;
    private boolean settingsChanged = false;
    // url to log in a user
    private static String url_settings_push = "http://group-project-organizer.herokuapp.com/settings_push.php";

    public SessionManager session;
    HashMap<String, String> userDetails;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    // private static final String TAG_USER = "user";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);


        session = new SessionManager(getApplicationContext());
        userDetails = session.getUserDetails();

        inputDiscover = (CheckBox)findViewById(R.id.checkBox1);
        inputPrompt = (CheckBox)findViewById(R.id.checkBox2);

        if(userDetails.get(SessionManager.KEY_DISCOVER).equals("1")){
            inputDiscover.setChecked(true);
        }else{
            inputDiscover.setChecked(false);
        }

        if(userDetails.get((SessionManager.KEY_PROMPT)).equals("1")){
            inputPrompt.setChecked(true);
        }else{
            inputPrompt.setChecked(false);
        }




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

    public void onButtonClick(View v){

        switch (v.getId()){

            case R.id.btnSettings_done:


                //Edit the integer fields
                //inputDiscover = (CheckBox)findViewById(R.id.checkBox1);
                //inputPrompt = (CheckBox)findViewById(R.id.checkBox2);
                if(isOnline()) {
                    if (inputPrompt.isChecked()) {
                        prompt = true;
                    }
                    if (inputDiscover.isChecked()) {
                        discover = true;
                    }
                    if (discover && userDetails.get(SessionManager.KEY_DISCOVER).equals("1") && prompt && userDetails.get(SessionManager.KEY_PROMPT).equals("1")) {
                        //do nothing
                    } else if (!discover && userDetails.get(SessionManager.KEY_DISCOVER).equals(("0")) && !prompt && userDetails.get(SessionManager.KEY_PROMPT).equals("0")) {
                        //do nothing
                    } else if (discover && userDetails.get(SessionManager.KEY_DISCOVER).equals("1") && !prompt && userDetails.get(SessionManager.KEY_PROMPT).equals("0")) {
                        //do nothing
                    } else if (!discover && userDetails.get(SessionManager.KEY_DISCOVER).equals("0") && prompt && userDetails.get(SessionManager.KEY_PROMPT).equals("1")) {

                    } else {
                        //write to database if there was setting changes
                        new SettingsPush().execute();
                        settingsChanged = true;
                    }

                    if (!settingsChanged) {
                        Toast.makeText(getApplicationContext(), "Settings have not changed", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), "Network connection required to do this", Toast.LENGTH_SHORT).show();
                }

                break;
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
    /**
     * Background Async Task to execute the login
     * */
    class SettingsPush extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SettingsActivity.this);
            pDialog.setMessage("Updating Settings...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Performing login
         * */
        protected String doInBackground(String... args) {
            String uid = userDetails.get(SessionManager.KEY_UID);
            String discoverable = "0";
            String prompt_approval = "0";

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            if(discover == true){
                discoverable = "1";
                params.add(new BasicNameValuePair("discoverable",discoverable));
                Log.d("discoverable checked",params.toString());
            }else{

                params.add(new BasicNameValuePair("discoverable",discoverable));
                Log.d("discoverable not checked", params.toString());
            }
            if(prompt == true){
                prompt_approval = "1";
                params.add(new BasicNameValuePair("prompt_approval",prompt_approval));
                Log.d("prompt_approval is checked", params.toString());
            }else{
                params.add(new BasicNameValuePair("prompt_approval", prompt_approval));
                Log.d("prompt_approval is not checked", params.toString());
            }
            params.add(new BasicNameValuePair("uid", uid));
            Log.d("uid",params.toString());

            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_settings_push,
                    "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    session.settingsSession(discoverable, prompt_approval);
                    // Update the session information

                } else {
                    // failed to log in user
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Didn't update the Database correctly",
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
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            if(settingsChanged){
                Toast.makeText(getApplicationContext(), "Settings successfully updated", Toast.LENGTH_SHORT).show();
            }



            if (pDialog != null) {
                pDialog.dismiss();
            }
        }

    }
}