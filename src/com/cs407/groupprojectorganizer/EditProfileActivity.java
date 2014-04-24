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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditProfileActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    EditText inputEmail;
    EditText inputName;
    EditText inputPhone;
    EditText inputFacebook;
    EditText inputGoogle;

    // url to log in a user
    private static String url_update_profile = "http://group-project-organizer.herokuapp.com/update_profile.php";

    public SessionManager session;
    HashMap<String, String> userDetails;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USER = "user";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        session = new SessionManager(getApplicationContext());
        userDetails = session.getUserDetails();

        // Edit Text Fields
        inputName = (EditText) findViewById(R.id.nameEditText);
        inputEmail = (EditText) findViewById(R.id.emailEditText);
        inputPhone = (EditText) findViewById(R.id.phoneEditText);
        inputFacebook = (EditText) findViewById(R.id.facebookEditText);
        inputGoogle = (EditText) findViewById(R.id.googleEditText);

        inputName.setText(userDetails.get(SessionManager.KEY_NAME));
        inputEmail.setText(userDetails.get(SessionManager.KEY_EMAIL));
        inputPhone.setText(userDetails.get(SessionManager.KEY_PHONE));
        inputFacebook.setText(userDetails.get(SessionManager.KEY_FACEBOOK));
        inputGoogle.setText(userDetails.get(SessionManager.KEY_GOOGLE));
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

            case R.id.btnUpdateProfile:

                // Verify user is online
                if (isOnline()) {
                    new UpdateProfile().execute();

                    Toast.makeText(getApplicationContext(), "Profile successfully updated", Toast.LENGTH_SHORT).show();
                    finish();
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
     * Background Async Task to execute the login
     * */
    class UpdateProfile extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditProfileActivity.this);
            pDialog.setMessage("Updating Profile...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Performing login
         * */
        protected String doInBackground(String... args) {
            String uid = userDetails.get(SessionManager.KEY_UID);
            String name = inputName.getText().toString();
            String email = inputEmail.getText().toString();
            String phone = inputPhone.getText().toString();
            String facebook = inputFacebook.getText().toString();
            String google = inputGoogle.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("uid", uid));
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("phone", phone));
            params.add(new BasicNameValuePair("facebook", facebook));
            params.add(new BasicNameValuePair("google", google));

            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_update_profile,
                    "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                    // Update the session information
                    session.createSession(uid, name, email, phone, facebook, google);

                } else {
                    // failed to log in user
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Incorrect inputEmail and password",
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
            if (pDialog != null) {
                pDialog.dismiss();
            }
        }

    }
}