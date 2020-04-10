package com.harry.demooutfitterx.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
        if (mAuth.getCurrentUser() != null) {
            if (checkFirstTime())
            {
                updateUI(MainActivity.class);
            } else updateUI(UpdateInfoActivity.class);
        }
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

                if (task.isSuccessful()) {
                    /// CTRL + CLICK TO KNOW =))
                    if (checkFirstTime() == true)
                    {
                        /// CTRL + CLICK TO KNOW =))
                        updateUI(MainActivity.class);
                    } else updateUI(UpdateInfoActivity.class);

                } else Toast.makeText(LoginActivity.this, "Login not Successful", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void updateUI(Class activity ) {
        /// CHANGE ACTIVITY
        startActivity(new Intent(LoginActivity.this, activity));
        finish();
    }

    /// CHECK IS THIS FIRST TIME
    public boolean checkFirstTime(){
         final Boolean[] check = new Boolean[1];
         check[0] = false;

        /// GET USER AND REF
        mRef =FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        /// Get Age info from current user
        if (!mUser.isAnonymous())
        mRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /// IF IT IS NOT FIRST TIME : FALSE, ELSE TRUE
                if (dataSnapshot.child("Age").exists()){
                    check[0] = true;
                } else check[0] = false;
            }

            @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
        });

        return check[0];
    }

}
