package com.cs407.groupprojectorganizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CreateProjectActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_project);
    }

    public void onButtonClick(View view){

        switch (view.getId()){
            case R.id.btnCreateNewProject:
                finish();
                break;
        }
    }
}
