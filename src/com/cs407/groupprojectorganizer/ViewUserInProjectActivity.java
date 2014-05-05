package com.cs407.groupprojectorganizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

public class ViewUserInProjectActivity extends Activity{

    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_FACEBOOK = "facebook";
    private static final String TAG_GOOGLE = "google";

    private TextView name;
    private TextView email;
    private TextView phone;
    private TextView facebook;
    private TextView google;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);

        name = (TextView) findViewById(R.id.displayName);
        email = (TextView) findViewById(R.id.displayEmail);
        phone = (TextView) findViewById(R.id.displayPhone);
        facebook = (TextView) findViewById(R.id.displayFacebook);
        google = (TextView) findViewById(R.id.displayGoogle);

        Intent intent = getIntent();

        name.setText(intent.getStringExtra(TAG_NAME));
        email.setText(intent.getStringExtra(TAG_EMAIL));
        phone.setText(intent.getStringExtra(TAG_PHONE));
        facebook.setText(intent.getStringExtra(TAG_FACEBOOK));
        google.setText(intent.getStringExtra(TAG_GOOGLE));

        Linkify.addLinks(facebook, Linkify.ALL);
        Linkify.addLinks(google, Linkify.ALL);
    }
}
