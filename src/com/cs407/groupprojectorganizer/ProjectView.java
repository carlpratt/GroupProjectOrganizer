package com.cs407.groupprojectorganizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by jmetzler on 4/23/2014.
 */

public class ProjectView extends Activity {

    public static int position;
    public static ArrayList<String> project_title = new ArrayList<String>();
    public static ArrayList<String> project_desc = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_view);


        TextView proj = (TextView)findViewById(R.id.project_name_textview);
        TextView desc = (TextView)findViewById(R.id.project_description_edit_text);

        proj.setText(project_title.get(position));
        desc.setText(project_desc.get(position));



    }
}
