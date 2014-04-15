package com.cs407.groupprojectorganizer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
    boolean discover;
    boolean prompt;
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
                inputDiscover = (CheckBox)findViewById(R.id.checkBox1);
                inputPrompt = (CheckBox)findViewById(R.id.checkBox2);

                if(inputPrompt.isChecked() == true){
                    discover = true;
                }
                if(inputDiscover.isChecked() == true ){
                    prompt = true;
                }
                
                new SettingsPush().execute();



                finish();

                break;
        }
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


            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            if(discover == true){
                params.add(new BasicNameValuePair("discoverable","1"));
                Log.d("discoverable checked",params.toString());
            }else{
                params.add(new BasicNameValuePair("discoverable","0"));
                Log.d("discoverable not checked", params.toString());
            }
            if(prompt == true){
                params.add(new BasicNameValuePair("prompt_approval","1"));
                Log.d("prompt_approval is checked", params.toString());
            }else{
                params.add(new BasicNameValuePair("prompt_approval", "0"));
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

                    // Update the session information
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "heyheyhey",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

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
            Toast.makeText(getApplicationContext(), "Profile successfully updated", Toast.LENGTH_SHORT).show();
            if (pDialog != null) {
                pDialog.dismiss();
            }
        }

    }
}
