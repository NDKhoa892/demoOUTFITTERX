package com.harry.demooutfitterx.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import com.harry.demooutfitterx.R;

public class NotificationActivity extends AppCompatActivity {

    Button btnSub, btnLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        btnSub = findViewById(R.id.btnSubscribe);
        btnLog = findViewById(R.id.btnLogToken);


        /// JUST TEST =)) DONT DELETE PLEASE :V
        /*/// CLICK BUTTON LOG TOKEN
        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // GET TOKEN
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(NotificationActivity.this, "GetInstance failed!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // Get new Instance ID token
                                String token = task.getResult().getToken();
                                Toast.makeText(NotificationActivity.this, "InstanceID Token: " + token, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });*/
    }
}
