package team12.movietracker;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import client.pojo.Media;
import client.pojo.User;
import client.server.ServerHandler;
import team12.movietracker.adapters.ImagesAdapter;
import team12.movietracker.utils.Common;

//TODO: Setup Links to Help, Settings, and Suggestions
//TODO: Setup Search

public class HomeActivity extends AppCompatActivity {

    private SearchView mSearchView;
    static final String SEARCH_QUERY = "";

    //region View object References
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    //endregion

    //region Other Complex objects Variables
    private final String TAG = getClass().getSimpleName();
    private Context context;
    private ImagesAdapter mAdapter;
    private List<Media> mediaList;
    //endregion

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_help:
                    Intent displayHelp = new Intent(HomeActivity.this, HelpActivity.class);
                    startActivity(displayHelp);
                    return true;
                case R.id.navigation_suggestions:
                    Intent displaySuggestions = new Intent(HomeActivity.this, SuggestionsActivity.class);
                    startActivity(displaySuggestions);
                    return true;
                case R.id.navigation_favorites:
                    Intent displayFavorites = new Intent(HomeActivity.this, FavoritesActivity.class);
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
            startActivity(displaySettings);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preStart();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
      //  initData();
        initViews();
        new MediaAddTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }//end of mehtod onCreate....

    private void preStart() {
        setContentView(R.layout.activity_home);
        context = this;
    }//end of preStart....

    private void initData() {
        ServerHandler.setupServerHandler();




        String testUser = "tmp" + ((int)(Math.random() * 10000) + 1);
        String testProducer = "producer" + ((int)(Math.random() * 10000) + 1);
        String testPass = "password";



        //register the producer on the server

        //usernames, first names, and lastnames <= 32 character
        //emails <= 64 characters
        //Log.d("TEST:", "Calling .registerUser");
        boolean registered = ServerHandler.registerUser(testProducer, testPass, "email@email.com", "Mr", "Producer", true);

        if (registered) {
            System.out.println("Producer: " + testProducer + " registered with id: " + ServerHandler.userId);
        } else {
            System.out.println("Producer: " + testProducer + " could not be registered");
            return;
        }


        //essentially logging in, making sure the credentials are good and returning a User object

        User user = ServerHandler.validateUser(testProducer, testPass);
        if (user == null) {
            return;
        }
        System.out.println("Producer data: " + user.toString());


        //authorize producer, get an auth token allowing them to make changes to the database, the auth token is required for some endpoints

        String token = ServerHandler.authorizeUser(testProducer, testPass);
        System.out.println("Producer authorized with auth token: " + token);
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.unnamed_image);
        Media media1 = new Media();
        media1.setTitle("The Show");
        media1.setType("Show"); //you can make the types whatever you want as long as they are consistent
        //media1.setProducerId(); no need to set this, the server takes care of it
        media1.setDescription("This is the TV Show.");
        media1.setGenre("Drama");
        media1.setActors("Lionard Deprapio; Jecca Simons; Kristofar Wakin;"); //you can deliminate these entries any way you would like, just handle it in the client application. It will be stored and retrieved from the database exactly as you save it.
        //media1.setImage(new File("resources/unnamed.png"));
        media1.setImageBitmap(imageBitmap);
        media1.setShowtimes("Nov 17, 2017 11:00 PM; Nov 19, 217 08:00 PM;"); //same with the format and delimination of these
        media1.setRating("R");
        media1.setYear(2017);

        ServerHandler.addMedia(media1);

        media1.setTitle("The other Show");
        media1.setDescription("This is the other TV Show.");
        media1.setShowtimes("Dec 1, 2017 01:00 PM;");

        ServerHandler.addMedia(media1);

        media1.setTitle("The Movie");
        media1.setType("Movie");
        media1.setDescription("This is the movie.");
        media1.setShowtimes("Dec 7, 2017 01:00 PM;");

        ServerHandler.addMedia(media1);

        media1.setTitle("The other Movie");
        media1.setDescription("This is the other Movie.");
        media1.setShowtimes("Dec 3, 2017 01:00 PM;");

        ServerHandler.addMedia(media1);
        //query the current media by this producer

        Media queryMedia = new Media();
        queryMedia.setProducerId(ServerHandler.userId); //anything set in this query Media will be used as a parameter in the search. producerId and year require exact matches, everything else will perform a "string contains" operation. Any parameter with a ';' will be discarded as safety against sql injection. This means you cannot search multiple actors or showtimes at once.
        List<Integer> currentMedia = ServerHandler.queryMedia(queryMedia);
        for (int i : currentMedia) {
            System.out.println("Query returned Media: " + i);
        }


        //retrieve a media

        Media retrievedMedia = ServerHandler.retrieveMedia(currentMedia.get(0)); //retrieve the first media in the list, this will return a usable Media entity. This will also download the media's image to the images/ folder, but only the first time. You can get the image from the image field in the entity.
        System.out.println(retrievedMedia.toString());


        Log.d("","");
    }//end of initData....

    private void initViews() {
        initRecyclerView();
    }//end of initViews....

    //initializing RecyclerView....
    public void initRecyclerView() {
        try {
            mRecyclerView = (RecyclerView) findViewById(R.id.rv_imageList);
            mLayoutManager = new LinearLayoutManager(context);
            mRecyclerView.setLayoutManager(mLayoutManager);
            //mAdapter=new ImagesAdapter(context);
            mRecyclerView.setAdapter(mAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//end of initRecyclerView.....

    public class MediaAddTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            initData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}//end of class HomeActivity....
