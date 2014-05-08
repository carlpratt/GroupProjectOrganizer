package com.cs407.groupprojectorganizer;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

abstract public class AddTeamMemberActivityBase extends ListActivity {

    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();

    private static String url_get_all_users = "http://group-project-organizer.herokuapp.com/get_all_users.php";
    private static String url_add_team_member = "http://group-project-organizer.herokuapp.com/add_team_member.php";

    //JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USERS = "users";
    private static final String TAG_UID = "uid";
    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_FACEBOOK = "facebook";
    private static final String TAG_GOOGLE = "google";
    private static final String TAG_PROMPT = "prompt";

    private ArrayList<AppUser> allUsers = new ArrayList<AppUser>(); //ArrayList of all <AppUser> in database
    private ArrayList<AppUser> projectUsers = new ArrayList<AppUser>(); //ArrayList of all <AppUser> in project
    private ArrayList<String> CURRENT_MEMBERS;
    private String uid;
    private String eee;
    private String pid;
    private int pos;

    SessionManager session;
    HashMap<String, String> userDetails;

    abstract ListAdapter makeMeAnAdapter(Intent intent);
    private static final int SEARCH_ID = Menu.FIRST+1;
    TextView selection;
    ArrayList<String> items = new ArrayList<String>();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.add_team_member);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            pid = extras.getString("PID");
            CURRENT_MEMBERS = extras.getStringArrayList("CURRENT_MEMBERS");
        }

        session = new SessionManager(getApplicationContext());
        userDetails = session.getUserDetails();

        //Start the AsyncTask to populate the ListView
        if (isOnline()) {
            new getAllUsers().execute();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        ListAdapter adapter = makeMeAnAdapter(intent);
        if (adapter == null)
            finish();
        else
            setListAdapter(adapter);
    }

    public void onListItemClick(ListView parent, View v, int position, long id) {

        String thisName = parent.getAdapter().getItem(position).toString();
        selection.setText(thisName);

        //Find selected user's position in list of projectUsers
        for (int i = 0; i < projectUsers.size(); i++) {
            if (thisName.equals(allUsers.get(i).getName()))
                pos = i;
        }

        //store user's uid and email
        uid = projectUsers.get(pos).getUid();
        eee = projectUsers.get(pos).getEmail();

        //addTeamMember
        new addTeamMember().execute();

        //get back to ProjectViewActivity
        Intent intent = new Intent(AddTeamMemberActivityBase.this, ProjectViewActivity.class);
        intent.putExtra("PID", pid);/////////why?
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, SEARCH_ID, Menu.NONE, "Search").setIcon(android.R.drawable.ic_menu_search);

        return(super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case SEARCH_ID:
                onSearchRequested();
                return(true);
        }
        return(super.onOptionsItemSelected(item));
    }

    /**
     * Ensures that if the user pushes the 'back' button, the next screen will be ShowProjectsActivity
     */
    @Override////////////////////////////////NOT WORKING
    public void onBackPressed() {
        System.out.println("***************onBackPressed() METHOD");
        super.onBackPressed();
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
     * Background AsyncTask to get all app users for the search interface
     */
    class getAllUsers extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddTeamMemberActivityBase.this);
            pDialog.setMessage("Gathering app users...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            String uid = userDetails.get(SessionManager.KEY_UID);

            //Build parameters associated to user
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("uid", uid));

            //getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_get_all_users, "POST", params);

            //check log cat for response
            Log.d("Create Response", json.toString());

            //check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                    //create an array of all users of the app
                    JSONArray allAppUsers = json.getJSONArray(TAG_USERS);

                    //Goes through each JSON-app-user and stores them as 'AppUser' objects
                    for (int i = 0; i < allAppUsers.length(); i++) {

                        //store the current 'User' object
                        JSONObject temp = allAppUsers.getJSONObject(i);

                        AppUser tempUser = new AppUser(temp.getString(TAG_UID), temp.getString(TAG_NAME),
                                temp.getString(TAG_EMAIL), temp.getString(TAG_PHONE), temp.getString(TAG_FACEBOOK),
                                temp.getString(TAG_GOOGLE), i);

                        if (!CURRENT_MEMBERS.contains(temp.getString(TAG_UID)))
                            projectUsers.add(tempUser);

                        allUsers.add(tempUser);
                    }

                } else {
                    //Failed to get anything from the database
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "No App Users were found",
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
         * Populate the ListView with all the users
         */
        protected void onPostExecute(String file_url) {

            for (int i = 0; i < projectUsers.size(); i++)
                items.add(projectUsers.get(i).getName());

            selection = (TextView)findViewById(R.id.selection);
            setDefaultKeyMode(DEFAULT_KEYS_DISABLE);//was DEFAULT_KEYS_SEARCH_LOCAL
            onNewIntent(getIntent());

            if (pDialog != null)
                pDialog.dismiss();
        }
    }

    /**
     * Background AsyncTask to add a user to a Project
     */
    class addTeamMember extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddTeamMemberActivityBase.this);
            pDialog.setMessage("Adding Team Member...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            //Building parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("email",eee));
            params.add(new BasicNameValuePair("pid", pid));
            //params.add(new BasicNameValuePair("uid", uid));

            //Log.d("uid of added user", uid);

            //getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_add_team_member,
                    "POST", params);

            //check log cat for response
            Log.d("Create Response", json.toString());

            return null;
        }

        protected void onPostExecute(String file_url) {
            //dismiss the dialog once done
            if(pDialog != null) {
                pDialog.dismiss();
            }
        }
    }

}
