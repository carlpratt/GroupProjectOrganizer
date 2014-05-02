package com.cs407.groupprojectorganizer;


import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.HashMap;

public class AddTeamMemberActivity extends ListActivity {
    public SessionManager session;

    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    HashMap<String, String> userDetails;

    public static int position;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_team_member);///May have to do after populating list with data

        //Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //doMySearch(query);
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

    //Actual search operation
    public String doMySearch() {

        //If data comes from SQLite database query, can apply results to a ListView using
        //a CursorAdapter. If it comes in another format, can use extension of BaseAdapter.


        return "string";
    }






//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        handleIntent(getIntent());
//
//    }
//
//    public void onNewIntent(Intent intent) {
//        setIntent(intent);
//        handleIntent(intent);
//
//    }
//
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        // call detail activity for clicked entry
//
//    }
//
//    private void handleIntent(Intent intent) {
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//            doSearch(query);
//        }
//    }
//
//    private void doSearch(String queryStr) {
//        //get a cursor, prepare ListAdapter, and set it
//
//    }

}
