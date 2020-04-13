/*
USE FOR HANDLE MESSAGING SERVICE
MENTION IN MANIFESTS
*/

package com.harry.demooutfitterx.Firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.harry.demooutfitterx.Activity.MainActivity;
import com.harry.demooutfitterx.R;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingService";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        /// Get settings data
        /// To know more: https://developer.android.com/guide/topics/ui/settings/use-saved-values
        SharedPreferences settingsData = PreferenceManager.getDefaultSharedPreferences(this);

        /// Check notification settings
        /// If user want to receive notifications
        if (settingsData.getBoolean(getString(R.string.NOTIFICATION_SWITCH_PREF), true)) {
            /// Handle when receive notification via data event
            if (remoteMessage.getData().size() > 0) {
                showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("message"));
            }

            /// Handle when receive notification
            if (remoteMessage.getNotification() != null) {
                showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
            }
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

        /// addFlag, to know more: https://medium.com/@janishar.ali/saurabh-patel-b6282e2ceef3
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        /// pendingIntent will grant NotificationManager permission to execute
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        /// SharedPreference time =))
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.PREFERENCE_FILE_KEY),
                Context.MODE_PRIVATE
        );

        /// Get index of saved channel ID
        int savedChannelID = sharedPreferences.getInt(getString(R.string.RINGTONE_PREFERENCE_KEY), 0);
        /// Get array of channel ID in arrays.xml
        final String[] channelID = getResources().getStringArray(R.array.channelID);
        /// Create array raw ID of notification sound
        final int[] rawID = {R.raw.pikachuuuuuuu, R.raw.mario_power_up, R.raw.message_tone, R.raw.sneeze, R.raw.tuturu};

        /// Create a Notification Builder
        /// To know more: https://developer.android.com/training/notify-user/build-notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID[savedChannelID])
                .setSmallIcon(R.drawable.logo) /// Set notification's icon
                .setSound(Uri.parse("android.resource://" + getPackageName() + '/' + rawID[savedChannelID])) /// Set sound
                .setAutoCancel(true) /// Automatically remove notification when user taps it
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}) /// Set sound vibration
                .setOnlyAlertOnce(true) /// Only alert Once
                .setContentIntent(pendingIntent) /// Set intent it will show when click notification
                .setContent(getCustomDesign(title, message)); /// Set the design of notification

        /// Show
        notificationManager.notify(0, builder.build());
    }
}