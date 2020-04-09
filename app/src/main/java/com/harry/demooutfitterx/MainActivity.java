package com.harry.demooutfitterx;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Button btnSignout;

    /// FIREBASE
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignout = findViewById(R.id.btnSignout);

        /// GET FIREBASE AUTH
        mAuth = FirebaseAuth.getInstance();

        /// CLICK BUTTON SIGN OUT
        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /// CTRL + CLICK TO KNOW =))))))
                signOut();
            }
        });
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
