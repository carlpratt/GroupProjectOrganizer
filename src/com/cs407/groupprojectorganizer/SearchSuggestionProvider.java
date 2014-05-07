package com.cs407.groupprojectorganizer;

import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.provider.SearchRecentSuggestions;

/**
 * This class helps to fill in the 'recent search suggestions' when searching
 * other app users
 */
public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider {
    static SearchRecentSuggestions getBridge(Context context) {

        return (new SearchRecentSuggestions(context, "com.cs407.groupprojectorganizer", DATABASE_MODE_QUERIES));

    }

    public SearchSuggestionProvider() {
        super();
        setupSuggestions("com.cs407.groupprojectorganizer", DATABASE_MODE_QUERIES);
    }
}
