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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    private JSONParser jsonParser = new JSONParser();
    private EditText inputName;
    private EditText inputEmail;
    private EditText inputPassword;

    private String name;
    private String email;
    private String password;

    // url to create new user
    private static String url_create_user = "http://group-project-organizer.herokuapp.com/register.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // Edit Text
        inputName = (EditText) findViewById(R.id.registerName);
        inputEmail = (EditText) findViewById(R.id.registerEmail);
        inputPassword = (EditText) findViewById(R.id.registerPassword);
    }

    public void onButtonClick(View v){

        switch (v.getId()){

            case R.id.btnRegister:
                name = inputName.getText().toString();
                email = inputEmail.getText().toString();
                password = encryptPassword(inputPassword.getText().toString());

                // Verify user is online
                if (isOnline()) {
                    if (verifyInput(name, email, password)) {
                        new CreateNewUser().execute();
                    }
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
     * Verifies user input to protect against null values and sql injection attacks.
     * @param name
     * @param email
     * @param password
     * @return
     */
    private boolean verifyInput(String name, String email, String password){

        if (name == null || name.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Name can not be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
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
        if (name.contains(";") || email.contains(";") || password.contains(";")){
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
     * Background Async Task to Create new user
     * */
    class CreateNewUser extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setMessage("Creating Account...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating user
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("password", password));

            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_create_user,
                    "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created user
                    Intent i = new Intent(getApplicationContext(), ShowProjectsActivity.class);
                    startActivity(i);

                    // closing this screen
                    finish();
                } else {
                    // failed to create user
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Registration unsuccessful",
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