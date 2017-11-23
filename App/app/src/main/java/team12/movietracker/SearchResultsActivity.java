package team12.movietracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import client.pojo.Media;
import client.pojo.User;
import client.server.ServerHandler;

public class SearchResultsActivity extends HomeActivity implements RecyclerItemClickListener.OnRecyclerClickListener  {

    private String mUsername;
    private String mPassword;
    private SearchRecyclerViewAdapter mSearchRecyclerViewAdapter;



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
//        mSearchResults = (TextView) findViewById(R.id.searchResults);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String queryResult = sharedPreferences.getString(SEARCH_QUERY, "");
//        mSearchResults.setText(queryResult);


    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String queryResult = sharedPreferences.getString(SEARCH_QUERY, "");
        Media queryMedia = new Media();
        queryMedia.setTitle(queryResult); //anything set in this query Media will be used as a parameter in the search. producerId and year require exact matches, everything else will perform a "string contains" operation. Any parameter with a ';' will be discarded as safety against sql injection. This means you cannot search multiple actors or showtimes at once.
        List<Integer> currentMedia = ServerHandler.queryMedia(queryMedia);
        queryMedia.setActors(queryResult);
        List<Integer> currentMediaDirector = ServerHandler.queryMedia(queryMedia);
        currentMedia.addAll(currentMediaDirector);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mSearchRecyclerViewAdapter = new SearchRecyclerViewAdapter(this, new ArrayList<Integer>());
        recyclerView.setAdapter(mSearchRecyclerViewAdapter);
        mSearchRecyclerViewAdapter.loadNewData(currentMedia);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, this));
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, ShowDetailActivity.class);
        intent.putExtra("USER",mUsername);
        intent.putExtra("PASS",mPassword);
        intent.putExtra("MEDIAID",mSearchRecyclerViewAdapter.getSub(position));
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Intent intent = new Intent(this, ShowDetailActivity.class);
        intent.putExtra("USER",mUsername);
        intent.putExtra("PASS",mPassword);
        intent.putExtra("MEDIAID",mSearchRecyclerViewAdapter.getSub(position));
        startActivity(intent);

    }
}
