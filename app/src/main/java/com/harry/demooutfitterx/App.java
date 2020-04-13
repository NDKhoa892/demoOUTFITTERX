/*
* Use this java file for initialize when open app
* Mentioned in Manifests
* */

package com.harry.demooutfitterx;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //TODO: init firebaseAuth hear

        /// BIG BRAIN TIME =))
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        /// Check if android version is Oreo or upper to show notification via NotificationChannel
        /// For each channel, you can set the and auditory behavior that is applied to all notifications in that channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /// Create default channel, pikachu sound
            buildChannel(
                    getString(R.string.CHANNEL_DEFAULT_ID),
                    getString(R.string.CHANNEL_DEFAULT_NAME),
                    Uri.parse("android.resource://" + getPackageName() + '/' + R.raw.pikachuuuuuuu)
            );

            /// Mario power up sound
            buildChannel(
                    getString(R.string.CHANNEL_MARIO_ID),
                    getString(R.string.CHANNEL_DEFAULT_NAME),
                    Uri.parse("android.resource://" + getPackageName() + '/' + R.raw.mario_power_up)
            );

            /// Message tone sound
            buildChannel(
                    getString(R.string.CHANNEL_MESSAGE_ID),
                    getString(R.string.CHANNEL_DEFAULT_NAME),
                    Uri.parse("android.resource://" + getPackageName() + '/' + R.raw.message_tone)
            );

            /// Sneeze sound
            buildChannel(
                    getString(R.string.CHANNEL_SNEEZE_ID),
                    getString(R.string.CHANNEL_DEFAULT_NAME),
                    Uri.parse("android.resource://" + getPackageName() + '/' + R.raw.sneeze)
            );

            /// Tuturu sound
            buildChannel(
                    getString(R.string.CHANNEL_TUTURU_ID),
                    getString(R.string.CHANNEL_DEFAULT_NAME),
                    Uri.parse("android.resource://" + getPackageName() + '/' + R.raw.tuturu)
            );

            ////////// Delete other Notification channel, just leave user selected sound =))

            /// SharedPreference time =))
            SharedPreferences sharedPreferences = getSharedPreferences(
                    getString(R.string.PREFERENCE_FILE_KEY),
                    Context.MODE_PRIVATE
            );

            /// Get index of saved channel ID
            int savedChannelID = sharedPreferences.getInt(getString(R.string.RINGTONE_PREFERENCE_KEY), 0);
            /// Get array of channel ID in arrays.xml
            final String[] channelID = getResources().getStringArray(R.array.channelID);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            /// Delete other notification channel
            for (int i = 0; i < 5; ++i)
                if (i != savedChannelID) notificationManager.deleteNotificationChannel(channelID[i]);
        }
    }

    private void buildChannel(String channelID, String channelName, Uri soundURI) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /// Set channel's ID, name, and importance
            NotificationChannel notificationChannel = new NotificationChannel(
                    channelID,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            );

            /// Set default sound for channel
            notificationChannel.setSound(soundURI, null);

            // Register the channel with the system
            //CAUTION: You can't change the importance or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

}
