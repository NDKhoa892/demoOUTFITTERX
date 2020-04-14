package com.harry.demooutfitterx.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.harry.demooutfitterx.R;
import com.harry.demooutfitterx.Settings.SettingsActivity;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    Button btnSignout, btnSettings;

    /// FIREBASE
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSettings = findViewById(R.id.btnSettings);
        btnSignout = findViewById(R.id.btnSignout);

        /// GET FIREBASE AUTH
        mAuth = FirebaseAuth.getInstance();

        //CLICK BUTTON SETTINGS
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /// OPEN SETTINGS ACTIVITY
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        /// CLICK BUTTON SIGN OUT
        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /// CTRL + CLICK TO KNOW =))))))
                signOut();
            }
        });

        //// Use for get user's token, don't delete please
        /*FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        Log.d(TAG, token);
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });*/
    }

    /// JUST OPEN ANOTHER ACTIVITY
    private void updateUI(Class Activity) {
        startActivity(new Intent(this, Activity));
    }

    /// HANDLE SIGN OUT
    private void signOut() {
        /// BUILD ALERT DIALOG
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        /// SET TITLE
        alertDialogBuilder.setTitle("Do you want to Sign Out ?");

        /// SET CONTENT
        alertDialogBuilder
                .setMessage("Click yes to Sign Out!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                /// SIGN OUT
                                mAuth.signOut();

                                /// UPDATE UI
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                finish();
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        /// SHOW
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
