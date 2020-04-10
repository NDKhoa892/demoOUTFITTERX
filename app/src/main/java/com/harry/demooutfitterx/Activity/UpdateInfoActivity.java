package com.harry.demooutfitterx.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.harry.demooutfitterx.InfoUser;
import com.harry.demooutfitterx.R;
import com.shawnlin.numberpicker.NumberPicker;

public class UpdateInfoActivity extends AppCompatActivity {
    NumberPicker heightPicker, agePicker, weightPicker ;
    EditText edtActiveName, edtAdress;
    CheckBox deliveryCheck;
    RadioGroup radioSexGroup;
    RadioButton radioSexBtn;
    Button btnNext;

    String activeName, adress, gender;
    long age, weight, height;
    boolean deliveryBoolean;

    DatabaseReference mRef;
    FirebaseUser mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateinfo);

        mRef          = FirebaseDatabase.getInstance().getReference();
        mUser         = FirebaseAuth.getInstance().getCurrentUser();;

        heightPicker  = findViewById(R.id.heightpicker);
        agePicker     = findViewById(R.id.agepicker);
        weightPicker  = findViewById(R.id.weightpicker);

        radioSexGroup = findViewById(R.id.radioSex);

        edtActiveName = findViewById(R.id.edtActiveName);
        edtAdress     = findViewById(R.id.edtAdress);

        deliveryCheck = findViewById(R.id.checkDelivery);
        btnNext       = findViewById(R.id.nextBtnInfo);

        ///  Number Piker library https://github.com/ShawnLin013/NumberPicker
        ///Set max min for height picker
        heightPicker.setMaxValue(300);
        heightPicker.setMinValue(1);

        ///Set max min for age pikcer
        agePicker.setMaxValue(120);
        agePicker.setMinValue(0);

        ///Set max min for weight pikcer
        weightPicker.setMaxValue(200);
        weightPicker.setMinValue(1);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtActiveName.getText().toString().isEmpty())
                {
                    Toast.makeText(UpdateInfoActivity.this, "Enter Name", Toast.LENGTH_SHORT).show();
                    return;

                } else if (edtAdress.getText().toString().isEmpty()){
                    Toast.makeText(UpdateInfoActivity.this, "Enter Adress", Toast.LENGTH_SHORT).show();
                    return;

                    /// ID = -1 is no radio button are checked
                } else if (radioSexGroup.getCheckedRadioButtonId() == -1){
                    Toast.makeText(UpdateInfoActivity.this, "Choose Gender", Toast.LENGTH_SHORT).show();
                    return;

                } else {
                    /// GET ID OF CHECKED BTN RADIO
                    int selectedID = radioSexGroup.getCheckedRadioButtonId();
                    radioSexBtn = findViewById(selectedID);

                    ////  GET ALL VALUE FROM SCREEN
                    activeName = edtActiveName.getText().toString();
                    adress     = edtAdress.getText().toString();
                    gender     = radioSexBtn.getText().toString();
                    age        = agePicker.getValue();
                    weight     = weightPicker.getValue();
                    height     = heightPicker.getValue();
                    deliveryBoolean = deliveryCheck.isChecked();

                    /// PUT ALL VALUE INTO 1 CLASS
                    InfoUser infoUser = new InfoUser(activeName,adress,gender,age,weight,height,deliveryBoolean);

                    /// PUSH DATA TO FIREBASE
                    mRef.child("USERS").child(mUser.getUid()).child("USERSINFO").setValue(infoUser);

                    /// CHANGE ACTIVITY
                    startActivity(new Intent(UpdateInfoActivity.this, MainActivity.class));

                }
            }
        });

    }

}
