package com.harry.demooutfitterx;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileGuestViewActivity extends AppCompatActivity {
    public static final String TAG = "ProfileGuestViewActivity";

    Uri imageUri;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mref;
    StorageReference msto, urlAvatar;

    TextView txtUserName, txtActiveName, txtFollowing, txtFollower,txtPost;
    LinearLayout linearFollower, linearFollowing, linearPost;
    RecyclerView recyclerViewPost;
    CircleImageView avatar;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_guest_view);

        mAuth           = FirebaseAuth.getInstance();
        mref            = FirebaseDatabase.getInstance().getReference();
        msto            = FirebaseStorage.getInstance().getReference();

        txtUserName     = findViewById(R.id.userNameProfileGuestView);
        txtActiveName   = findViewById(R.id.activeNameProfileGuestView);
        txtFollower     = findViewById(R.id.numberFollowerGuestView);
        txtFollowing    = findViewById(R.id.numberFollowingGuestView);
        txtPost         = findViewById(R.id.numberPostGuestView);

        avatar          = findViewById(R.id.avtProfileGuestView);

        linearFollower  = findViewById(R.id.linearFollowerGuestView);
        linearFollowing = findViewById(R.id.linearFollowingGuestView);
        linearPost      = findViewById(R.id.linearPostGuestView);

        recyclerViewPost= findViewById(R.id.recyclerPostGuestView);
    }
}
