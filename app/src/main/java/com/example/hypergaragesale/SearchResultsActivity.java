package com.example.hypergaragesale;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class SearchResultsActivity extends ListActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        handleIntent(getIntent());
    }

    // when an item of the list is clicked
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);

        Intent intent = new Intent(this,PostDetailsActivity.class);
        intent.putExtra("POSITION", (int)id);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            performSearch(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.browse_post_menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    private void performSearch(String query){

        //SqlDbHelper sqlDbHelper = new SqlDbHelper(this);

        PostsDbHelper mDbHelper = new PostsDbHelper(this);

        Cursor cursor = mDbHelper.getReadableDatabase().rawQuery("SELECT * FROM " + Posts.PostEntry.TABLE_NAME +
                      " WHERE upper(" + Posts.PostEntry.COLUMN_NAME_TITLE + ") like '%" + query.toUpperCase() + "%'", null);
        setListAdapter(new SimpleCursorAdapter(this, R.layout.row_layout, cursor,
                             new String[] {Posts.PostEntry.COLUMN_NAME_TITLE }, new int[]{R.id.searchText}));

    }
}
