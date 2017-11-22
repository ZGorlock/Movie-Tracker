package team12.movietracker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import client.pojo.User;
import client.server.ServerHandler;


//TODO NEED TO FINISH THIS TO DISPLAY THE DETAILS OF THE FAVORITE SHOW/MOVIE or w.e
public class ShowDetailActivity extends AppCompatActivity {

    private String mUsername;
    private String mPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mUsername = getIntent().getStringExtra("USER");
        mPassword = getIntent().getStringExtra("PASS");
        User user = ServerHandler.validateUser(mUsername, mPassword);




    }
}
