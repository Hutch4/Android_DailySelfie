package coursera.coursework.dailyselfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;
import java.text.DateFormat;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class SelfieNotification extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 1;
    private static final String TAG = "SelfieNotificaion";

    // Notification Action Elements
    private Intent mNotificationIntent;
    private PendingIntent mContentIntent;

    // Notification Text Elements
    private final CharSequence tickerText = "Selfie Request";
    private final CharSequence contentTitle = "Daily Selfie";
    private final CharSequence contentText = "Time to take another selfie...";
    private final long[] mVibratePattern = { 0, 200, 200, 300 };

    public void onReceive(Context context, Intent intent) {
        // The Intent to be used when the user clicks on the Notification View
        mNotificationIntent = new Intent(context, SelfieListActivity.class);
        mNotificationIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        // The PendingIntent that wraps the underlying Intent
        mContentIntent = PendingIntent.getActivity(context, 0,
                mNotificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Build the Notification
        Notification.Builder notificationBuilder = new Notification.Builder(
                context).setTicker(tickerText)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setAutoCancel(true).setContentTitle(contentTitle)
                .setContentText(contentText).setContentIntent(mContentIntent)
                .setVibrate(mVibratePattern);

        // Get the NotificationManager
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // Pass the Notification to the NotificationManager:
        mNotificationManager.notify(NOTIFICATION_ID,
                notificationBuilder.build());

        // Log occurence of notify() call
        Log.i(TAG, "Sending notification at:"
                + DateFormat.getDateTimeInstance().format(new Date()));

    }
}
