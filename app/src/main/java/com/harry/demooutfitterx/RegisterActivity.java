package com.harry.demooutfitterx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    Button btnReg;
    EditText txtName, txtEmail, txtPass;

    ProgressDialog mDialog;

    ///   FIREBASE
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnReg = findViewById(R.id.btnReg);
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPass = findViewById(R.id.txtPass);

        mDialog = new ProgressDialog(this);

        ///  GET FIREBASE AUTH
        mAuth = FirebaseAuth.getInstance();

        ///  CLICK BUTTON REGISTER
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleRegister();
            }
        });
    }

    //// HANDLE REGISTER ACTION
    private void handleRegister() {
        final String name, email, pass, ava;

        ///  GET NAME, EMAIL, PASS...
        name = txtName.getText().toString();
        email = txtEmail.getText().toString();
        pass = txtPass.getText().toString();
        ava = "https://firebasestorage.googleapis.com/v0/b/demooutfitterx-86f8e.appspot.com/o/Avatar%2Fman.png?alt=media&token=5ff017bb-aebd-47ab-aca6-1eb090b8be6f";

        ////  CHECK IF SOMETHING IS EMPTY :v
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(RegisterActivity.this, "Enter Name", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(RegisterActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        } else if (pass.length() < 6) {
            Toast.makeText(RegisterActivity.this, "Password must be greater than 6 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        ///   SHOW DIALOG
        mDialog.setMessage("Creating user please wait...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        /// REALTIME DATABASE
        mDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                /// DISMISS DIALOG
                mDialog.dismiss();

                if (task.isSuccessful()) {
                    /// CTRL + CLICK to know =))))
                    sendEmailVerification();

                    /// GET USER ID
                    String userId = task.getResult().getUser().getUid();

                    /// PUSH ON REALTIME DATABASE
                    mDatabase.child(userId).setValue(new User(name, email, ava, userId));

                    /// FINISH ACTIVITY
                    mAuth.signOut();
                    finish();
                } else Toast.makeText(RegisterActivity.this, "Error while creating user", Toast.LENGTH_LONG).show();
            }
        });
    }

    /// SEND EMAIL VERIFICATION
    private void sendEmailVerification() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Check your Email for verification", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Can't send Email for verification\nPlease try again", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
