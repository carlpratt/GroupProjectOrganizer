package com.cs407.groupprojectorganizer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.service.textservice.SpellCheckerService;
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


public class ViewUserActivity extends Activity{

    public SessionManager session;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    HashMap<String, String> userDetails;

    public static int position;

    //Database Fields
    public static ArrayList<String> name = new ArrayList<String>();
    public static ArrayList<String> email = new ArrayList<String>();
    public static ArrayList<String> phone = new ArrayList<String>();
    public static ArrayList<String> facebook = new ArrayList<String>();
    public static ArrayList<String> google = new ArrayList<String>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.project_user_view);

        session =  new SessionManager(getApplicationContext());
        userDetails = session.getUserDetails();

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


}
