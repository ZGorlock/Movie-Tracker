package team12.movietracker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import client.pojo.User;
import client.server.ServerHandler;

public class HelpActivity extends AppCompatActivity {

    private String mUsername;
    private String mPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_help);
        TextView test1 = (TextView)findViewById(R.id.Contents);
        test1.setText(Html.fromHtml(getString(R.string.help_Contents)));

        TextView test2 = (TextView)findViewById(R.id.Started);
        test2.setText(Html.fromHtml(getString(R.string.help_GettingStarted)));

        TextView test3 = (TextView)findViewById(R.id.favoriting);
        test3.setText(Html.fromHtml(getString(R.string.help_Favoriting)));

        TextView test4 = (TextView)findViewById(R.id.Searching);
        test4.setText(Html.fromHtml(getString(R.string.help_Searching)));

        TextView test5 = (TextView)findViewById(R.id.Suggestions);
        test5.setText(Html.fromHtml(getString(R.string.help_Suggestions)));

        TextView test6 = (TextView)findViewById(R.id.Notifications);
        test6.setText(Html.fromHtml(getString(R.string.help_Notifications)));


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUsername = getIntent().getStringExtra("USER");
        mPassword = getIntent().getStringExtra("PASS");
        User user = ServerHandler.validateUser(mUsername, mPassword);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
