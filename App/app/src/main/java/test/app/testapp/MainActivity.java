package test.app.testapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import client.Test;
import client.server.ServerHandler;

public class MainActivity extends AppCompatActivity {


    private TextView mDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new RetrieveFeedTask().execute();


    }

    class RetrieveFeedTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... urls)
        {
            mDisplay = findViewById(R.id.TextView1);
            Test.main(new String[] {}); //TODO remove this later
            return null;

        }
    }

    }
