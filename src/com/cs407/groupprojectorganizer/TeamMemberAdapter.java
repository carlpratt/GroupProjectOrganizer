package com.cs407.groupprojectorganizer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * This class describes how to populate the ListView for the ProjectView Activity with
 * each custom list item
 */
public class TeamMemberAdapter extends ArrayAdapter<ListModelTeamMember> {

    private Context context;
    private List<ListModelTeamMember> objects;


    public TeamMemberAdapter(Context context, int resource, List<ListModelTeamMember> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    //as user scrolls, uses this method to display each Team Member
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get reference to team member in list so it knows which data to present
        ListModelTeamMember teamMember = objects.get(position);

        //Create the view object- instance of the layout created for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        //create the view object
        View view = inflater.inflate(R.layout.item_project_view_team_member, null);

        //get the image
        ImageView image = (ImageView) view.findViewById(R.id.team_member_picture);
        image.setImageResource(R.drawable.ic_launcher);//NEEDS TO BE CHANGED FOR USER'S PICTURE

        //get the text
        TextView tv = (TextView) view.findViewById(R.id.team_member_name);
        tv.setText(teamMember.getName());//TEAM MEMBER CLASS


        //return super.getView(position, convertView, parent);
        return view;
    }

}
