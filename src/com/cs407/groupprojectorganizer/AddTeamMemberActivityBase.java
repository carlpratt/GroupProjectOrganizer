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

    //ArrayLists
    private ArrayList<String> uids = new ArrayList<String>();
    private ArrayList<String> name = new ArrayList<String>();
    private ArrayList<String> email = new ArrayList<String>();
    private ArrayList<String> phone = new ArrayList<String>();
    private ArrayList<String> facebook = new ArrayList<String>();
    private ArrayList<String> google = new ArrayList<String>();

    private ArrayList<String> prompt = new ArrayList<String>();
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

    private ArrayList<AppUser> allUsers = new ArrayList<AppUser>();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.add_team_member);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            pid = extras.getString("PID");
            CURRENT_MEMBERS = extras.getStringArrayList("CURRENT_MEMBERS");/////////////for preventing adding twice?
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
        Toast.makeText(this, "Position in this list: " + position, Toast.LENGTH_SHORT).show();//////////

        //Find the selected user's position in the entire list
        for (int i = 0; i < allUsers.size(); i++) {
            if (thisName == allUsers.get(i).getName()) {
                pos = i;
            }
        }

        Toast.makeText(this, "Position in entire list: " + pos, Toast.LENGTH_SHORT).show();/////////////

        //store that user's uid
        uid = allUsers.get(pos).getUid();
        eee = allUsers.get(pos).getEmail();
        new addTeamMember().execute();

        Intent intent = new Intent(AddTeamMemberActivityBase.this, ProjectViewActivity.class);
        intent.putExtra("PID",pid);
        startActivity(intent);

//        //Find the selected user's position in the entire list
//        boolean show = false;
//        for (int i = 0; i < name.size(); i++) {
//            if (CURRENT_MEMBERS.contains(thisName)) {
//                show = true;
//            }
////            } else if (thisName == name.get(i)) {
//            else {
//                pos = i;
//
//                //store that user's uid
//                uid = uids.get(pos);
//
//                eee = email.get(pos);
//                new addTeamMember().execute();
//
//                Intent intent = new Intent(AddTeamMemberActivityBase.this, ProjectViewActivity.class);
//                intent.putExtra("PID",pid);
//                startActivity(intent);
//            }
//
//
//        }
//        if (show)
//            Toast.makeText(this, "Already a Team Member", Toast.LENGTH_SHORT).show();

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

                        AppUser tempUser = new AppUser(temp.getString(TAG_UID),temp.getString(TAG_NAME),
                                temp.getString(TAG_EMAIL), temp.getString(TAG_PHONE),temp.getString(TAG_FACEBOOK),
                                temp.getString(TAG_GOOGLE), i);

                        //stores all 'AppUser' objecets in 'allUsers'
                        allUsers.add(tempUser);

//                        //add the info to the ArrayLists
//                        uids.add(temp.getString(TAG_UID));
//                        name.add(temp.getString(TAG_NAME));
//                        email.add(temp.getString(TAG_EMAIL));
//
//                        if (temp.getString(TAG_PHONE) != null)
//                            phone.add(temp.getString(TAG_PHONE));
//                        if (temp.getString(TAG_FACEBOOK) != null)
//                            facebook.add(temp.getString(TAG_FACEBOOK));
//                        if (temp.getString(TAG_GOOGLE) != null)
//                            google.add(temp.getString(TAG_GOOGLE));
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

            for (int i = 0; i < allUsers.size(); i++)
                items.add(allUsers.get(i).getName());

            selection = (TextView)findViewById(R.id.selection);
            setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
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
            params.add(new BasicNameValuePair("uid", uid));

            Log.d("uid of added user", uid);

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
