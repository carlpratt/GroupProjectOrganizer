package com.cs407.groupprojectorganizer;

import android.app.SearchManager;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import java.util.ArrayList;
import java.util.List;

public class SearchInterface extends AddTeamMemberActivityBase {

    @Override
    ListAdapter makeMeAnAdapter(Intent intent) {
        ListAdapter adapter = null;
        System.out.println("RIGHT NOW I'M IN THE SearchInterface CLASS!!!!!!!!!! - makeMeAnAdapter()");

        if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            ArrayList<String> results = searchItems(query);

            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, results);//Creating the new ListView of the searched item
            setTitle("Search : " + query);
        }

        return(adapter);
    }

    private ArrayList<String> searchItems(String query) {

        System.out.println("RIGHT NOW I'M IN THE SearchInterface CLASS!!!!!!!!!!!! - searchItems()");

//        SearchSuggestionProvider
//                .getBridge(this)
//                .saveRecentQuery(query, null);

        ArrayList<String> results = new ArrayList<String>();

        for (String item : items) {
            if (item.indexOf(query) > -1) {
                results.add(item);
            }
        }
        return(results);
    }

}
