package com.harry.demooutfitterx.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harry.demooutfitterx.R;
import com.harry.demooutfitterx.User.User;

public class LoginActivity extends AppCompatActivity {
    Button btnLog, btnReg;
    EditText txtEmail, txtPass;

    /// FIREBASE
    FirebaseAuth mAuth;
    DatabaseReference mRef;
    FirebaseUser mUser;

    /// DIALOG
    ProgressDialog mDialog;

    @Override
    protected void onStart() {
        super.onStart();

        //// GET FIREBASE AUTH
        mAuth = FirebaseAuth.getInstance();

        //// CHECK IF THERE ALREADY HAVE HAD A USER
        if (mAuth.getCurrentUser() != null) updateUIAndFinish(MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLog = findViewById(R.id.btnLog);
        btnReg = findViewById(R.id.btnReg);
        txtEmail = findViewById(R.id.txtEmail);
        txtPass = findViewById(R.id.txtPass);

        mDialog = new ProgressDialog(this);

        //// CLICK LOGIN BUTTON
        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /// CTRL + CLICK TO KNOW =))
                signIn();
            }
        });

        //// CLICK REGISTER BUTTON
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    /// HANDLE SIGNIN ACTION
    private void signIn() {
        final String email, pass;

        email = txtEmail.getText().toString().trim();
        pass = txtPass.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(LoginActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(pass)) {
            Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }

        /// SHOW DIALOG
        mDialog.setMessage("Login please wait...");
        mDialog.setIndeterminate(true);
        mDialog.show();

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                /// DISMISS DIALOG
                mDialog.dismiss();

                if (task.isSuccessful()) updateUIAndFinish(MainActivity.class);
                else Toast.makeText(LoginActivity.this, "Login not Successful", Toast.LENGTH_LONG).show();
            }
        });

    }

    /// CHANGE ACTIVITY AND FINISH THIS
    private void updateUIAndFinish(Class activity) {
        startActivity(new Intent(LoginActivity.this, activity));
        finish();
    }

}
