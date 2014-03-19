package com.cs407.groupprojectorganizer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    EditText inputEmail;
    EditText inputPassword;

    // url to log in a user
    private static String url_login = "http://group-project-organizer.herokuapp.com/login.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Edit Text
        inputEmail = (EditText) findViewById(R.id.loginEmail);
        inputPassword = (EditText) findViewById(R.id.loginPassword);

        // Create button
        Button btnLogin = (Button) findViewById(R.id.btnLogin);

        // button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new user in background thread
                new PerformLogin().execute();
            }
        });
    }

    /**
     * Background Async Task to execute the login
     * */
    class PerformLogin extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Signing In...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Performing login
         * */
        protected String doInBackground(String... args) {
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("password", password));

            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_login,
                    "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully logged in user
                    Intent i = new Intent(getApplicationContext(), ShowProjectsActivity.class);
                    startActivity(i);

                    // closing this screen
                    finish();
                } else {
                    // failed to log in user
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
