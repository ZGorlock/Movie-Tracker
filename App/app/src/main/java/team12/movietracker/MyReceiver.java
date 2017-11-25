package team12.movietracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

import static android.content.Context.NOTIFICATION_SERVICE;
import team12.movietracker.NotificationUtils.*;
;
/**
 * Created by estor on 11/25/2017.
 */

public class MyReceiver extends BroadcastReceiver {


    private NotificationUtils mNotificationUtils;

    @Override
    public void onReceive (Context context, Intent intent)
    {

//        int notifyID = 1;
//        String CHANNEL_ID = "my_channel_01";
//        CharSequence name = context.getString(R.string.channel_name);
//        int importantce = NotificationManager.IMPORTANCE_HIGH;
//        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importantce);
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,"id_product")
//                .setSmallIcon(R.drawable.movietrack) //your app icon
//                .setBadgeIconType(R.drawable.movietrack) //your app icon
//                .setChannelId("my_channel_01")
//                .setContentTitle("TITLE")
//                .setAutoCancel(true)
//                .setNumber(1)
//                .setColor(255)
//                .setContentText("GOD HELP US")
//                .setWhen(System.currentTimeMillis());
//        notificationManager.notify(1, notificationBuilder.build());


//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        String CHANNEL_ONE_ID = "team12.movietracker.ONE";
//        String CHANNEL_ONE_NAME = "Channel One";
//        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,CHANNEL_ONE_NAME,notificationManager.IMPORTANCE_HIGH);
//
//        notificationChannel.enableLights(true);
//        notificationChannel.setLightColor(Color.RED);
//        notificationChannel.setShowBadge(true);
//        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//        notificationManager.createNotificationChannel(notificationChannel);
//
//        Notification.Builder notification = new Notification.Builder(context, CHANNEL_ONE_ID)
//                .setContentTitle("FUCK TITTLES")
//                .setContentText("FUCK TEXT BODIES")
//                .setSmallIcon(R.drawable.movietrack)
//                .setAutoCancel(true);
//
//        notification.notify(1,notification );

        // The id of the channel.
//        String CHANNEL_ID = "my_channel_01";
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(context, CHANNEL_ID)
//                        .setSmallIcon(R.drawable.movietrack)
//                        .setContentTitle("My notification")
//                        .setContentText("Hello World!");
// Creates an explicit intent for an Activity in your app
//        Intent resultIntent = new Intent(context, HomeActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your app to the Home screen.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
// Adds the back stack for the Intent (but not the Intent itself)
//        stackBuilder.addParentStack(HomeActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//        mBuilder.setContentIntent(resultPendingIntent);
//        NotificationManager mNotificationManager =
//                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

// mNotificationId is a unique integer your app uses to identify the
// notification. For example, to cancel the notification, you can pass its ID
// number to NotificationManager.cancel().
//        mNotificationManager.notify(7776810, mBuilder.build());






//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "M_CH_TD");
//        MediaPlayer mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
//        mediaPlayer.start();

//                .setContentTitle("New MEssage")
//                .setContentText("Fuck this")
//                .setSmallIcon(R.drawable.movietrack)
//                .setChannelId(CHANNEL_ID)
//                .build();



//        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
////        mChannel.enableLights(true);
////        mChannel.setLightColor(Color.RED);
//        notificationManager.notify(1, notification);




//        builder.setSmallIcon(R.drawable.ic_notifications_black_24dp);
//        builder.setContentTitle("Notification Alert");
//        builder.setContentText("Fuck me this is dumb");
//        builder.setAutoCancel(true)
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setWhen(System.currentTimeMillis())
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("Alarm activated!")
//                .setContentText("This is my alarm")
//                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
//                .setContentInfo("Info");

//        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.createNotificationChannel(mChannel);
//        Intent receive = new Intent(context, MyIntentService.class);
//        context.startService(receive);


        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        String output = SP.getString("list_preference_1","0");
        String title = "Movie Time!";
        String body = output + " until your movie";

        if(output == "None")
        {
            body = "Your movie is starting now!";
        }

        mNotificationUtils = new NotificationUtils(context);
        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(body)) {
            Notification.Builder nb = mNotificationUtils.
                    getAndroidChannelNotification(title, body);

            mNotificationUtils.getManager().notify(101, nb.build());
        }
        System.out.println("Made it to notification");

    }
}
