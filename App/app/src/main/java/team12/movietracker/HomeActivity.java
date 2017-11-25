package team12.movietracker;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
    private startNotifications mNotifications = null;
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
//                case R.id.navigation_favorites:
//                    Intent displayFavorites = new Intent(HomeActivity.this, FavoritesActivity.class);
//                    displayFavorites.putExtra("USER",mUsername);
//                    displayFavorites.putExtra("PASS",mPassword);
//                    startActivity(displayFavorites);
//                    return true;
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
//        Intent intent = new Intent(this, ShowDetailActivity.class);
//        intent.putExtra("USER",mUsername);
//        intent.putExtra("PASS",mPassword);
//        intent.putExtra("MEDIAID",mDetailRecyclerViewAdapter.getSub(position));
//        startActivity(intent);
//
    }

    @Override
    protected void onResume() {
        super.onResume();
//        ServerHandler.setupServerHandler();
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

        mNotifications = new startNotifications(subscriptions1);
        mNotifications.execute();


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
    public class startNotifications extends AsyncTask<Void, Void, Boolean>
    {
        private List<Integer> mSubs;
        public startNotifications(List<Integer> subs)
        {
            mSubs = subs;
        }
        @Override
        protected Boolean doInBackground(Void... voids)
        {
            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            boolean notificationONOFF = SP.getBoolean("notifications_new_message", false);
            String output = SP.getString("list_preference_1","0");
            output = output.toUpperCase();
            System.out.println("Made it1");
            if(output.contains("MINUTES"))
            {
                output = output.replace(" MINUTES","");
            }
            else if(output.contains("NONE"))
            {
                output = output.replace("NONE","0");
            }
            int delayValue = Integer.parseInt(output);
            System.out.println("Made it2");

            System.out.println("Made it3");


            ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();
            Media retrievedMedia = new Media();
            String tempShowTime;
            String tempShowArray[];
            String tempDividedArray[];
            String tempMonth;
            String tempTime[];
            String tempJ;
            int month;
            int day;
            int year;
            int hour;
            int minute;
            int alarmNumber = 0;
            boolean AM;
            for(int i : mSubs)
            {

                retrievedMedia = ServerHandler.retrieveMedia(i);
                try
                {
                    System.out.println("Made it Try"+i);
                    tempShowTime = retrievedMedia.getShowtimes();
                    System.out.println(tempShowTime);
                    tempShowArray = tempShowTime.split(";");
                    for(String j:tempShowArray)
                    {
                        tempJ = j.trim();
                        tempDividedArray = tempJ.split(" ");
                        System.out.println(j);
                        /*
                        Nov 17, 2017 11:00 PM
                        tempDividedArray[0] = Month
                        tempDividedArray[1] = Day
                        tempDividedArray[2] = Year
                        tempDividedArray[3] = Time
                        tempDividedArray[4] = AM/PM
                         */
                        tempMonth = tempDividedArray[0].toUpperCase();
                        System.out.println(tempDividedArray[0]);
                        System.out.println(tempDividedArray[1]);
                        System.out.println(tempDividedArray[2]);
                        System.out.println(tempDividedArray[3]);
                        System.out.println(tempDividedArray[4]);

                        switch(tempMonth) {
                            case "JAN":
                                month = 0;
                                break;
                            case "FEB":
                                month = 1;
                                break;
                            case "MAR":
                                month = 2;
                                break;
                            case "APR":
                                month = 3;
                                break;
                            case "MAY":
                                month = 4;
                                break;
                            case "JUN":
                                month = 5;
                                break;
                            case "JUL":
                                month = 6;
                                break;
                            case "AUG":
                                month = 7;
                                break;
                            case "SEP":
                                month = 8;
                                break;
                            case "OCT":
                                month = 9;
                                break;
                            case "NOV":
                                month = 10;
                                break;
                            case "DEC":
                                month = 11;
                                break;
                            default:
                                month = 0;
                                break;
                        }
                        tempDividedArray[1] = tempDividedArray[1].replace(",","");
                        day = Integer.parseInt(tempDividedArray[1]);
                        year = Integer.parseInt(tempDividedArray[2]);
                        tempTime = tempDividedArray[3].split(":");
                        hour = Integer.parseInt(tempTime[0]);
                        minute = Integer.parseInt(tempTime[1]);

                        minute = minute - delayValue;
                        if(minute < 0)
                        {
                            hour = hour -1;
                            minute = minute + 60;
                        }


                        if(tempDividedArray[4].contains("AM"))
                        {
                            AM = true;
                            if(hour == 12)
                            {
                                hour += 12;
                            }

                        }
                        else
                        {
                            AM = false;
                            if(hour != 12)
                            {
                                hour = hour + 12;

                            }
                        }




                        Intent notifyIntent = new Intent(HomeActivity.this,MyReceiver.class);
//                        if()
//                        notifyIntent.putExtra("DELAY", )

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DATE, day);
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 1);


//                        calendar.set(Calendar.MONTH, 10);
//                        calendar.set(Calendar.DATE, 25);
//                        calendar.set(Calendar.YEAR, 2017);
//                        calendar.set(Calendar.HOUR_OF_DAY, 11);
//                        calendar.set(Calendar.MINUTE, 52);
//                        calendar.set(Calendar.SECOND, 1);

                        Calendar cal2 = Calendar.getInstance();
                        System.out.println("Alarm set to "+calendar.getTimeInMillis() + " current time is: "+cal2.getTimeInMillis());
                        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                        if(cal2.getTimeInMillis()<=calendar.getTimeInMillis())
                        {
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmNumber, notifyIntent, 0);
                            manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                            alarmNumber++;
                            intentArray.add(pendingIntent);
                        }
                        else
                        {
                            if(tempShowArray[tempShowArray.length-1].equals(j))
                            {
                                ServerHandler.removeSubscription(i);

                            }
                        }



                        if(!notificationONOFF)
                        {
                            for(PendingIntent k: intentArray)
                            {
                                manager.cancel(k);
                            }
                        }
                    }



                }
                catch (NullPointerException e)
                {
                    System.out.println("Made it catch"+i);
                    Log.d("HomeActivityStartNotification", "Unable to get ShowTimes:" + e);
                }

            }
//            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                NotificationChannel notificationChannel = new NotificationChannel("my_notification_channel", "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);
//
//                // Configure the notification channel.
//                notificationChannel.setDescription("Channel description");
//                notificationChannel.enableLights(true);
//                notificationChannel.setLightColor(Color.RED);
//                notificationManager.createNotificationChannel(notificationChannel);
//            }
//
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(HomeActivity.this, "my_notification_channel")
//                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                    .setSmallIcon(R.drawable.movietrack)
//                    .setContentTitle("Content Title")
//                    .setContentText("Content Text");
//
//            notificationManager.notify(1, builder.build());
            return true;
        }
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
