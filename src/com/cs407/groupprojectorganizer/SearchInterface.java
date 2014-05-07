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

        if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            List<String> results = searchItems(query);

            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, results);
            setTitle("Search : " + query);
        }

        return(adapter);
    }

    private List<String> searchItems(String query) {

//        SearchSuggestionProvider
//                .getBridge(this)
//                .saveRecentQuery(query, null);

        List<String> results = new ArrayList<String>();

        for (String item : items) {
            if (item.indexOf(query) > -1) {
                results.add(item);
            }
        }
        return(results);
    }

}
