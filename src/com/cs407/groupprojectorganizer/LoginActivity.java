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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that handles login operations
 */

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
    private static final String TAG_USER = "user";
    private static final String TAG_UID = "uid";
    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_FACEBOOK = "facebook";
    private static final String TAG_GOOGLE = "google";

    public SessionManager session;

    private String email;
    private String password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking if user is logged in before preparing activity.
        session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn()){
            Intent i = new Intent(getApplicationContext(), Startup.class);
            startActivity(i);
        }

        setContentView(R.layout.login);

        // Edit Text
        inputEmail = (EditText) findViewById(R.id.loginEmail);
        inputPassword = (EditText) findViewById(R.id.loginPassword);
    }

    public void onButtonClick(View v){

        switch (v.getId()){

            case R.id.btnLogin:
                email = inputEmail.getText().toString();
                password = encryptPassword(inputPassword.getText().toString());

                // Verify user is online
                if (isOnline()) {
                    if (verifyInput(email, password)) {
                        new PerformLogin().execute();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Network connection required to do this", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btnLinkToRegisterScreen:
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);

                startActivity(i);
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
     * Verifies user input to protect against null values and sql injection attacks.
     * @param email
     * @param password
     * @return
     */
    private boolean verifyInput(String email, String password){

        if (email == null || email.isEmpty()){
            Toast.makeText(getApplicationContext(), "Email can not be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password == null || password.isEmpty()){
            Toast.makeText(getApplicationContext(), "Password can not be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!email.contains("@")){
            Toast.makeText(getApplicationContext(), "Email must contain a '@'", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (email.contains(";") || password.contains(";")){
            Toast.makeText(getApplicationContext(), "No field may contain a ';'", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Handles password encryption
     * @param password
     * @return
     */
    private String encryptPassword(String password){

        String hashedPassword = "";
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));
            hashedPassword = byteArrayToHexString(crypt.digest());
        } catch (NoSuchAlgorithmException e){

        } catch (UnsupportedEncodingException e){

        }

        return hashedPassword;
    }

    private static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (int i=0; i < b.length; i++) {
            result +=
                    Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
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

                    // Selects the user data
                    JSONArray userArray = json.getJSONArray(TAG_USER);
                    JSONObject user = userArray.getJSONObject(0);

                    // Store the session data.
                    session.createSession(user.getString(TAG_UID), user.getString(TAG_NAME),
                            user.getString(TAG_EMAIL), user.getString(TAG_PHONE),
                            user.getString(TAG_FACEBOOK), user.getString(TAG_GOOGLE));

                    // Open user's projects list page
                    Intent i = new Intent(getApplicationContext(), Startup.class);
                    startActivity(i);

                    // closing this screen
                    finish();
                } else {
                    // failed to log in user
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Incorrect email or password",
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
