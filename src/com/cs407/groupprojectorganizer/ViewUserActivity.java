package com.cs407.groupprojectorganizer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;

/**
 * This class populates the User Information of the Team Member that was clicked on
 * in the ProjectViewActivity
 */
public class ViewUserActivity extends Activity{

//    public SessionManager session;
//    private ProgressDialog pDialog;
//    JSONParser jsonParser = new JSONParser();
//    HashMap<String, String> userDetails;

    public static int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
//        session =  new SessionManager(getApplicationContext());
//        userDetails = session.getUserDetails();


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
        }

    }
//
//    /**
//     * Determines if android device has network access
//     */
//    private boolean isOnline() {
//        ConnectivityManager cm =
//                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
//            return true;
//        }
//        return false;
//    }


}
