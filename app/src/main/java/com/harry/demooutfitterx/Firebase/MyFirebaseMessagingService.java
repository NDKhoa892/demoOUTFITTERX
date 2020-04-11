/// USE FOR HANDLE MESSAGING SERVICE
/// MENTION IN MANIFESTS

package com.harry.demooutfitterx.Firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.harry.demooutfitterx.Activity.MainActivity;
import com.harry.demooutfitterx.R;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        /// Handle when receive notification via data event
        if (remoteMessage.getData().size() > 0) {
            showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("message"));
        }

        /// Handle when receive notification
        if (remoteMessage.getNotification() != null) {
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    /// Method for custom design notification
    private RemoteViews getCustomDesign(String title, String message) {
        /// Declare remoteView as notification view
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.design_notification);

        // Read to know =))
        remoteViews.setTextViewText(R.id.txtTitle, title);
        remoteViews.setTextViewText(R.id.txtMess, message);
        remoteViews.setImageViewResource(R.id.imgLogo, R.drawable.logo);

        return remoteViews;
    }

    /// Method for show notification
    public void showNotification(String title, String message) {
        /// Set intent to show when click notification
        Intent intent = new Intent(this, MainActivity.class);

        /// Chanel ID, use for android Oreo and upper
        String channelID = "OUTFITTERX_channel";

        /// addFlag, to know more: https://medium.com/@janishar.ali/saurabh-patel-b6282e2ceef3
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        /// pendingIntent will grant NotificationManager permission to execute
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        /// Set notification's sound
        /// TODO: add list of notification's sound
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        /// Create a Notification Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setSmallIcon(R.drawable.logo) /// Set notification's icon
                .setSound(uri) /// Set sound
                .setAutoCancel(true) /// Allow sound to auto cancel
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}) /// Set sound vibration
                .setOnlyAlertOnce(true) /// Only alert Once
                .setContentIntent(pendingIntent) /// Set intent it will show when click notification
                .setContent(getCustomDesign(title, message)); /// Set the design of notification

        /// Manage notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        /// Check if android version is Oreo or upper to show notification via NotificationChannel
        /// For each channel, you can set the and auditory behavior that is applied to all notifications in that channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /// Set channel's ID, name, and importance
            NotificationChannel notificationChannel = new NotificationChannel(channelID, "OUTFITTERX", notificationManager.IMPORTANCE_HIGH);

            /// Set sound for channel
            notificationChannel.setSound(uri, null);

            /// Create channel
            notificationManager.createNotificationChannel(notificationChannel);
        }

        /// Show the notification
        notificationManager.notify(0, builder.build());
    }
}
