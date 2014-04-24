package com.cs407.groupprojectorganizer;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.HashMap;
import java.util.List;


public class ProjectViewActivity extends ListActivity {

    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    SessionManager session;

    //Needed for CustomList
    private static final int REQUEST_CODE = 100;
    List<ListModelTeamMember> teamMemberList;//Need an ArrayList of instances of each Team Member

    HashMap<String, String> userDetails;


    //JSON node names?


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_view);

        session = new SessionManager(getApplicationContext());
        userDetails = session.getUserDetails();

        //Brings up the custom listview
        TeamMemberAdapter adapter = new TeamMemberAdapter(this, R.layout.item_project_view_team_member, teamMemberList);
        setListAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String
        }
    }

    //If "Add Team Member" button clicked
    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddTeamMember:
                Intent i = new Intent(getApplicationContext(), AddTeamMemberActivity.class);///TO DO
                startActivityForResult(AddTeamMemberActivity);
                break;
        }
    }




}
