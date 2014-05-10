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
import android.widget.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class populates the User Information of the Team Member that was clicked on
 * in the ProjectViewActivity
 */
public class ViewUserActivity extends Activity{

    private static String url_remove_team_member = "http://group-project-organizer.herokuapp.com/remove_team_member.php";

    private static final String TAG_SUCCESS = "success";

    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;

    private String uid;
    private String pid;
    private boolean owner;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.project_user_view);

        TextView userName = (TextView) findViewById(R.id.projectUserTextViewTitle);
        TextView textName= (TextView) findViewById(R.id.userNameTextView);
        TextView textEmail = (TextView) findViewById(R.id.userEmailTextView);
        TextView textPhone = (TextView) findViewById(R.id.userPhoneTextView);
        TextView textFacebook = (TextView) findViewById(R.id.userFacebookTextView);
        TextView textGoogle = (TextView) findViewById(R.id.userGoogleTextView);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userName.setText(extras.getString("USER_NAME"));
            textName.setText(extras.getString("USER_NAME"));
            textEmail.setText(extras.getString("USER_EMAIL"));
            textPhone.setText(extras.getString("USER_PHONE"));
            textFacebook.setText(extras.getString("USER_FACEBOOK"));
            textGoogle.setText(extras.getString("USER_GOOGLE"));

            uid = extras.getString("USER_UID");
            pid = extras.getString("PROJECT_PID");
            owner = extras.getBoolean("PROJECT_OWNER");
        }
        if (!owner){
            Button btn = (Button)findViewById(R.id.remove_team_member_button);
            btn.setVisibility(View.GONE);
        }



    }

    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.remove_team_member_button:

                if (isOnline()) {
                    new RemoveTeamMember().execute();
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

    class RemoveTeamMember extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewUserActivity.this);
            pDialog.setMessage("Removing team member from project...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            //Building parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("pid", pid));
            params.add(new BasicNameValuePair("uid", uid));

            //getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_remove_team_member,
                    "POST", params);

            //check log cat for response
            Log.d("Create Response", json.toString());

            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success != 1)
                    Toast.makeText(getApplicationContext(), "Team Member removal- unsuccessful",
                            Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {

            if (pDialog != null)
                pDialog.dismiss();

            Intent intent = new Intent(getApplicationContext(), ProjectViewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ViewUserActivity.this.finishActivity(0);
            startActivity(intent);

        }
    }


}
