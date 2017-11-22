package team12.movietracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import client.pojo.User;
import client.server.ServerHandler;

public class SearchResultsActivity extends HomeActivity {

    private String mUsername;
    private String mPassword;


    private TextView mSearchResults;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUsername = getIntent().getStringExtra("USER");
        mPassword = getIntent().getStringExtra("PASS");
        User user = ServerHandler.validateUser(mUsername, mPassword);
        mSearchResults = (TextView) findViewById(R.id.searchResults);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String queryResult = sharedPreferences.getString(SEARCH_QUERY, "");
        mSearchResults.setText(queryResult);

    }
}
