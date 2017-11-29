package team12.movietracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import client.pojo.Media;
import client.pojo.User;
import client.server.ServerHandler;


public class ShowDetailActivity extends AppCompatActivity {

    private String mUsername;
    private String mPassword;
    private Integer mMediaID;
    private TextView mTitle;
    private ImageView mImage;
    private TextView mDescription;
    private TextView mActor;
    private TextView mGenre;
    private TextView mShowTimes;
    private TextView mRating;
    private TextView mYear;
    private Button mFavoriteAddRemove;
    private boolean FavoriteAdd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUsername = getIntent().getStringExtra("USER");
        mPassword = getIntent().getStringExtra("PASS");
        mMediaID = getIntent().getIntExtra("MEDIAID",0);
        User user = ServerHandler.validateUser(mUsername, mPassword);
        String token = ServerHandler.authorizeUser(mUsername, mPassword);


        mTitle = (TextView) findViewById(R.id.textViewTitle);
        mImage = (ImageView) findViewById(R.id.imageViewPoster);
        mDescription = (TextView) findViewById(R.id.textViewDescription);
        mActor = (TextView) findViewById(R.id.textViewActors);
        mGenre = (TextView) findViewById(R.id.textViewGenre);
        mShowTimes = (TextView) findViewById(R.id.textViewShowTimes);
        mRating = (TextView) findViewById(R.id.textViewRating);
        mYear = (TextView) findViewById(R.id.textViewYear);


//        ServerHandler.setupServerHandler();
        Media retrievedMedia = new Media();
        retrievedMedia = ServerHandler.retrieveMedia(mMediaID,this);
        try{
            mTitle.setText(retrievedMedia.getTitle());

        }
        catch (Exception e)
        {
            mTitle.setText("N/A");

        }
        try{
            String filePath = retrievedMedia.getImage().getPath();
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            mImage.setImageBitmap(bitmap);
        }
        catch (Exception e)
        {
            mImage.setImageResource(R.drawable.ic_image_black_48dp);
        }

        try{
            mDescription.setText(retrievedMedia.getDescription());

        }
        catch (Exception e)
        {
            mDescription.setText("N/A");

        }
        try{
            mActor.setText(retrievedMedia.getActors());

        }
        catch (Exception e)
        {
            mActor.setText("N/A");

        }
        try{
            mGenre.setText(retrievedMedia.getGenre());

        }
        catch (Exception e)
        {
            mGenre.setText("N/A");

        }
        try{
            mShowTimes.setText(retrievedMedia.getShowtimes());

        }
        catch (Exception e)
        {
            mShowTimes.setText("N/A");

        }
        try{
            mRating.setText(retrievedMedia.getRating());

        }
        catch (Exception e)
        {
            mRating.setText("N/A");

        }
        try{
            mYear.setText(String.valueOf(retrievedMedia.getYear()));

        }
        catch (Exception e)
        {
            mYear.setText("N/A");

        }


        mFavoriteAddRemove = (Button) findViewById(R.id.buttonAddDeleteFav);
        List<Integer> subscriptions = ServerHandler.getSubscriptions();
        if(!subscriptions.contains(mMediaID))
        {
            mFavoriteAddRemove.setText(R.string.favorite_add);
            FavoriteAdd = true;
        }
        else
        {
            mFavoriteAddRemove.setText(R.string.favorite_remove);
            FavoriteAdd = false;
        }

        mFavoriteAddRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FavoriteAdd)
                {
                    ServerHandler.addSubscription(mMediaID);
                    mFavoriteAddRemove.setText(R.string.favorite_remove);
                    FavoriteAdd = false;

                }
                else
                {
                    ServerHandler.removeSubscription(mMediaID);
                    mFavoriteAddRemove.setText(R.string.favorite_add);
                    FavoriteAdd = true;

                }
            }
        });


    }
}
