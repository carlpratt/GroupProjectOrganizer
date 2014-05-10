package com.cs407.groupprojectorganizer;

import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

/**
 * This class defines the abstract class AddTeamMemberActivityBase and helps set up
 * the search interface
 */
public class AddTeamMemberActivity extends AddTeamMemberActivityBase {

    @Override
    ListAdapter makeMeAnAdapter(Intent intent) {
        return(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));
    }

}
