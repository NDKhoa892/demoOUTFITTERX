package com.harry.demooutfitterx.Settings;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.harry.demooutfitterx.R;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String TAG = "SettingsFragment";

    private PreferenceCategory notificationGroup;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        ///// Notification settings preference
        SwitchPreferenceCompat switchNoti = findPreference("switchNotification");

        notificationGroup = findPreference("notificationGroup");

        Preference listNotiSound = findPreference("listNotificationSound");
        SwitchPreferenceCompat topicOrder = findPreference(getString(R.string.TOPIC_ORDER_KEY));
        SwitchPreferenceCompat topicChats = findPreference(getString(R.string.TOPIC_CHATS_KEY));
        SwitchPreferenceCompat topicPromotions = findPreference(getString(R.string.TOPIC_PROMOTIONS_KEY));

        ///////// [BEGIN  INITIALIZE]

        /// Get settings data
        /// To know more: https://developer.android.com/guide/topics/ui/settings/use-saved-values
        SharedPreferences settingsData = PreferenceManager.getDefaultSharedPreferences(getContext());

        /// Set visible for list of notification's sound depend on switchNotification's condition
        if (settingsData.getBoolean(getString(R.string.NOTIFICATION_SWITCH_PREF), true)) {
            notificationGroup.setVisible(true);
            Log.w(TAG, "onSharedPreferenceChanged: TRUE");
        } else {
            notificationGroup.setVisible(false);
            Log.w(TAG, "onSharedPreferenceChanged: FALSE");
        }

        ///////// [END INITIALIZE]

        /// Set visible for list of notification's when change switchNotification's condition
        switchNoti.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((boolean) newValue) {
                    notificationGroup.setVisible(true);
                    Log.w(TAG, "Push notification: TRUE");
                } else {
                    notificationGroup.setVisible(false);
                    Log.w(TAG, "Push notification: FALSE");
                }

                return true;
            }
        });

        /// Click sound settings
        listNotiSound.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                /// Click to know =))
                handleRingtoneSettings();
                return true;
            }
        });

        ///////            Topics settings
        /// Topic Order update
        topicOrder.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((Boolean) newValue) {
                    handleTopicSubscribe(getString(R.string.TOPIC_ORDER_KEY));
                } else {
                    handleTopicUnsubscribe(getString(R.string.TOPIC_ORDER_KEY));
                }

                return true;
            }
        });

        /// Topics Chats
        topicChats.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((Boolean) newValue) {
                    handleTopicSubscribe(getString(R.string.TOPIC_CHATS_KEY));
                } else {
                    handleTopicUnsubscribe(getString(R.string.TOPIC_CHATS_KEY));
                }

                return true;
            }
        });

        /// Topics promotions
        topicPromotions.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((Boolean) newValue) {
                    handleTopicSubscribe(getString(R.string.TOPIC_PROMOTIONS_KEY));
                } else {
                    handleTopicUnsubscribe(getString(R.string.TOPIC_PROMOTIONS_KEY));
                }

                return true;
            }
        });
    }

    /// Variables for handleRingtoneSettings()
    private int savedPos, newPos;
    private SharedPreferences sharedPreferences;

    /// Use for show Ringtone's list dialog and handle change ringtone
    private void handleRingtoneSettings() {
        /// Array of sounds' name
        final String[] displayName = getActivity().getResources().getStringArray(R.array.soundsName);
        /// Array of Channels' ID
        final String[] channelID = getActivity().getResources().getStringArray(R.array.channelID);
        /// Array of sounds' raw ID
        final int[] rawID = {R.raw.pikachuuuuuuu, R.raw.mario_power_up, R.raw.message_tone, R.raw.sneeze, R.raw.tuturu};

        /// Declare AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        /// SharedPreference time =))
        /// Get saved ID of sound which user has chosen
        sharedPreferences = getContext().getSharedPreferences(
                getString(R.string.PREFERENCE_FILE_KEY),
                Context.MODE_PRIVATE
        );

        savedPos = newPos = sharedPreferences.getInt(getString(R.string.RINGTONE_PREFERENCE_KEY), 0);

        /// Creating dialog's properties
        builder.setTitle("Select your notification's sound")
                .setSingleChoiceItems(displayName, savedPos, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.w(TAG, "onClick: " + i);

                        /// Get URI of tapped sound
                        Uri uri = Uri.parse("android.resource://" + getContext().getPackageName() + '/' + rawID[i]);

                        /// Play sound
                        MediaPlayer mediaPlayer=MediaPlayer.create(getContext(), uri);
                        mediaPlayer.start();

                        /// Update position
                        newPos = i;
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /// Check if user actually changed the notification's sound
                        if (newPos != savedPos) {
                            Log.w(TAG, "User selected: " + newPos);

                            /// Check user's android version
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                /// get Notification Manager
                                NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);

                                /// Delete old notification channel
                                notificationManager.deleteNotificationChannel(channelID[savedPos]);

                                /// Create new
                                /// Actual, just a fake notification declare as a chosen ones
                                NotificationChannel notificationChannel = new NotificationChannel(
                                        channelID[newPos],
                                        getString(R.string.CHANNEL_DEFAULT_NAME),
                                        NotificationManager.IMPORTANCE_HIGH
                                );

                                // Register the channel with the system
                                notificationManager.createNotificationChannel(notificationChannel);
                            }

                            /// Change ringtonePreference data
                            sharedPreferences.edit().putInt(getString(R.string.RINGTONE_PREFERENCE_KEY), newPos).apply();

                            /// Toast to inform user
                            Toast.makeText(
                                    getContext(),
                                    "Notification's sound changed to: " + displayName[newPos],
                                    Toast.LENGTH_LONG).show();
                        }

                        /// Dismiss dialog
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /// Just dismiss
                        dialogInterface.dismiss();
                    }
                });

        /// Show dialog
        builder.create().show();
    }

    /// Handle when subscribe to a topic
    private void handleTopicSubscribe(final String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful())
                            Log.e(TAG, "Subscribe topic " + topic + " failed");

                        Log.w(TAG, "Subscribe topic " + topic + " successfully");
                    }
                });
    }

    /// Handle when unsubscribe to a topic
    private void handleTopicUnsubscribe(final String topic) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful())
                            Log.e(TAG, "Unsubscribe topic " + topic + " failed");

                        Log.w(TAG, "Unsubscribe topic " + topic + " successfully");
                    }
                });
    }
}
