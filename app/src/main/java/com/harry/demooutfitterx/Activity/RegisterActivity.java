package com.harry.demooutfitterx.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.harry.demooutfitterx.R;
import com.harry.demooutfitterx.User.InfoUser;
import com.harry.demooutfitterx.User.User;
import com.shawnlin.numberpicker.NumberPicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    Button btnReg;
    EditText txtName, txtEmail, txtPass, edtActiveName, edtAdress;
    NumberPicker heightPicker, agePicker, weightPicker ;
    CheckBox deliveryCheck;
    RadioGroup radioSexGroup;
    RadioButton radioSexBtn;

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

        ///  USER'S INFO
        heightPicker = findViewById(R.id.heightPicker);
        agePicker = findViewById(R.id.agePicker);
        weightPicker = findViewById(R.id.weightPicker);
        radioSexGroup = findViewById(R.id.radioSex);
        edtActiveName = findViewById(R.id.edtActiveName);
        edtAdress = findViewById(R.id.edtAdress);
        deliveryCheck = findViewById(R.id.checkDelivery);

        ///  Number Piker library https://github.com/ShawnLin013/NumberPicker
        ///  Set max min for height picker
        heightPicker.setMaxValue(300);
        heightPicker.setMinValue(1);

        ///  Set max min for age pikcer
        agePicker.setMaxValue(120);
        agePicker.setMinValue(0);

        ///  Set max min for weight pikcer
        weightPicker.setMaxValue(200);
        weightPicker.setMinValue(1);

        ///  DIALOG
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
        final String name, email, pass, ava, activeName, address, gender;
        final long age, weight, height;
        final boolean deliveryBoolean;
        final int selectedID;

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
        } else if (edtActiveName.getText().toString().isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Enter Name", Toast.LENGTH_SHORT).show();
            return;
        } else if (edtAdress.getText().toString().isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Enter Adress", Toast.LENGTH_SHORT).show();
            return;

            /// ID = -1 is no radio button are checked
        } else if (radioSexGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(RegisterActivity.this, "Choose Gender", Toast.LENGTH_SHORT).show();
            return;
        }

        /// GET ID OF CHECKED BTN RADIO
        selectedID = radioSexGroup.getCheckedRadioButtonId();
        radioSexBtn = findViewById(selectedID);

        ///  GET USER'S INFO
        activeName = edtActiveName.getText().toString();
        address = edtAdress.getText().toString();
        gender = radioSexBtn.getText().toString();
        age = agePicker.getValue();
        weight = weightPicker.getValue();
        height = heightPicker.getValue();
        deliveryBoolean = deliveryCheck.isChecked();

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
                    mDatabase.child(userId).setValue(new User(name, email, ava, userId, new InfoUser(activeName, address, gender, age, weight, height, deliveryBoolean)));

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
