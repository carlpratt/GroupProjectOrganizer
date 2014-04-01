package com.cs407.groupprojectorganizer;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
    private static String url_login = "http://group-project-organizer.herokuapp.com/update_profile.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USER = "user";
    private static final String TAG_UID = "uid";
    private static final String TAG_EMAIL = "inputEmail";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);


        // Edit Text Fields
        inputEmail = (EditText) findViewById(R.id.emailEditText);
        inputName = (EditText) findViewById(R.id.nameEditText);
        inputPhone = (EditText) findViewById(R.id.phoneEditText);
        inputFacebook = (EditText) findViewById(R.id.facebookEditText);
        inputGoogle = (EditText) findViewById(R.id.googleEditText);
    }

    public void onButtonClick(View v){

        switch (v.getId()){

            case R.id.btnUpdateProfile:
                new UpdateProfile().execute();
                finish();
                break;
        }
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
            String email = inputEmail.getText().toString();
            String name = inputName.getText().toString();
            String phone = inputPhone.getText().toString();
            String facebook = inputFacebook.getText().toString();
            String google = inputGoogle.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("phone", phone));
            params.add(new BasicNameValuePair("facebook", facebook));
            params.add(new BasicNameValuePair("google", google));

            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_login,
                    "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                    // Selects the user data
                    JSONArray userArray = json.getJSONArray(TAG_USER);
                    JSONObject user = userArray.getJSONObject(0);

                    // Open user's projects list page
                    Intent i = new Intent(getApplicationContext(), ShowProjectsActivity.class);
                    startActivity(i);

                    // closing this screen
                    finish();
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
            pDialog.dismiss();
        }

    }

}
