package team12.movietracker;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import client.pojo.Media;
import client.pojo.User;
import client.server.ServerHandler;

//TODO: Setup Links to Help, Settings, and Suggestions
//TODO: Setup Search

public class HomeActivity extends AppCompatActivity implements RecyclerItemClickListener.OnRecyclerClickListener {
    private SearchView mSearchView;
    static final String SEARCH_QUERY = "";
    private String mUsername;
    private String mPassword;
    private DetailRecyclerViewAdapter mDetailRecyclerViewAdapter;
    private displayHomeBrowse mdHBTask = null;
    private boolean firstCreate = true;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_help:
                    Intent displayHelp = new Intent(HomeActivity.this, HelpActivity.class);
                    displayHelp.putExtra("USER",mUsername);
                    displayHelp.putExtra("PASS",mPassword);
                    startActivity(displayHelp);
                    return true;
                case R.id.navigation_suggestions:
                    Intent displaySuggestions = new Intent(HomeActivity.this, SuggestionsActivity.class);
                    displaySuggestions.putExtra("USER",mUsername);
                    displaySuggestions.putExtra("PASS",mPassword);
                    startActivity(displaySuggestions);
                    return true;
                case R.id.navigation_favorites:
                    Intent displayFavorites = new Intent(HomeActivity.this, FavoritesActivity.class);
                    displayFavorites.putExtra("USER",mUsername);
                    displayFavorites.putExtra("PASS",mPassword);
                    startActivity(displayFavorites);
                    return true;
            }
            return false;
        }
    };

    @Override
    public void supportInvalidateOptionsMenu() {
        super.supportInvalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        mSearchView.setSearchableInfo(searchableInfo);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                sharedPreferences.edit().putString(SEARCH_QUERY, query).apply();
                Intent displaySearch = new Intent(HomeActivity.this,SearchResultsActivity.class);
                displaySearch.putExtra("USER",mUsername);
                displaySearch.putExtra("PASS",mPassword);
                startActivity(displaySearch);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.displaySettings)
        {
            Intent displaySettings = new Intent(HomeActivity.this, SettingsActivity.class);
            displaySettings.putExtra("USER",mUsername);
            displaySettings.putExtra("PASS",mPassword);
            startActivity(displaySettings);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, ShowDetailActivity.class);
        intent.putExtra("USER",mUsername);
        intent.putExtra("PASS",mPassword);
        intent.putExtra("MEDIAID",mDetailRecyclerViewAdapter.getSub(position));
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Intent intent = new Intent(this, ShowDetailActivity.class);
        intent.putExtra("USER",mUsername);
        intent.putExtra("PASS",mPassword);
        intent.putExtra("MEDIAID",mDetailRecyclerViewAdapter.getSub(position));
        startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        ServerHandler.setupServerHandler();
        if(firstCreate)
        {
            mUsername = getIntent().getStringExtra("USER");
            System.out.println(mUsername);
            mPassword = getIntent().getStringExtra("PASS");
            System.out.println(mPassword);
        }
        else
        {
            System.out.println(mUsername);
            System.out.println(mPassword);
        }



        String token = ServerHandler.authorizeUser(mUsername, mPassword);
        List<Integer> subscriptions1 = ServerHandler.getSubscriptions();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDetailRecyclerViewAdapter = new DetailRecyclerViewAdapter(this, new ArrayList<Integer>());
        recyclerView.setAdapter(mDetailRecyclerViewAdapter);
        mDetailRecyclerViewAdapter.loadNewData(subscriptions1);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, this));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mUsername = getIntent().getStringExtra("USER");
        mPassword = getIntent().getStringExtra("PASS");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        firstCreate = false;
//        mdHBTask.execute(); MAYBE TRY TO GET THIS TO ANOTHER THREAD THAN MAIN
//        ServerHandler.setupServerHandler();
//        String token = ServerHandler.authorizeUser(mUsername, mPassword);
//        List<Integer> subscriptions1 = ServerHandler.getSubscriptions();
//
//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        mDetailRecyclerViewAdapter = new DetailRecyclerViewAdapter(this, new ArrayList<Integer>());
//        recyclerView.setAdapter(mDetailRecyclerViewAdapter);
//        mDetailRecyclerViewAdapter.loadNewData(subscriptions1);
//
//        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, this));










    }
    public class displayHomeBrowse extends AsyncTask<Void, Void, Boolean> {


        public displayHomeBrowse()
        {

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
            System.out.println("Here");

            ServerHandler.setupServerHandler();
            System.out.println("Here");
            String token = ServerHandler.authorizeUser(mUsername, mPassword);
            System.out.println("Here");
            List<Integer> subscriptions1 = ServerHandler.getSubscriptions();
            System.out.println("Here");
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            System.out.println("Here");

            recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
            System.out.println("Here");


            mDetailRecyclerViewAdapter = new DetailRecyclerViewAdapter(HomeActivity.this, new ArrayList<Integer>());
            System.out.println("Here");

            recyclerView.setAdapter(mDetailRecyclerViewAdapter);
            System.out.println("Here");

            mDetailRecyclerViewAdapter.loadNewData(subscriptions1);
            System.out.println("Here");


            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(HomeActivity.this, recyclerView, HomeActivity.this));
            System.out.println("Here");

            return true;
        }
    }

}
