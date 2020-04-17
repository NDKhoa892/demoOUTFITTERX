package com.harry.demooutfitterx.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.harry.demooutfitterx.R;
import com.harry.demooutfitterx.User.InfoUser;
import com.harry.demooutfitterx.User.PostAndFollow;
import com.harry.demooutfitterx.User.User;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileUserViewActivity extends AppCompatActivity {
    public static final String TAG = "ProfileUserViewActivity";

    Uri imageUri;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mref;
    StorageReference msto, urlAvatar;

    private static final int AVATAR_GALLERY = 1;

    TextView        txtUserName, txtActiveName, txtFollowing, txtFollower,txtPost;
    LinearLayout    linearFollower, linearFollowing, linearPost;
    RecyclerView    recyclerViewPost;
    CircleImageView avatar;

    ProgressDialog progressDialog;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user_view);

        mAuth           = FirebaseAuth.getInstance();
        mUser           = FirebaseAuth.getInstance().getCurrentUser();
        mref            = FirebaseDatabase.getInstance().getReference();
        msto            = FirebaseStorage.getInstance().getReference();

        urlAvatar       = msto.child("Avatar").child(mUser.getUid());

        txtUserName     = findViewById(R.id.userNameProfileUserView);
        txtActiveName   = findViewById(R.id.activeNameProfileUserView);
        txtFollower     = findViewById(R.id.numberFollowerUserView);
        txtFollowing    = findViewById(R.id.numberFollowingUserView);
        txtPost         = findViewById(R.id.numberPostUserView);

        avatar          = findViewById(R.id.avtProfileUserView);

        linearFollower  = findViewById(R.id.linearFollowerUserView);
        linearFollowing = findViewById(R.id.linearFollowingUserView);
        linearPost      = findViewById(R.id.linearPostUserView);

        recyclerViewPost= findViewById(R.id.recyclerPostUserView);

        //// CTRL + CLICK TO KNOW MORE :))
        getData();

        //// CHANGE AVATAR
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                ProfileUserViewActivity.this.startActivityForResult(intent, AVATAR_GALLERY);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //// CHECK TO SURE THAT DATA NOT NULL
        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            //// IF YOU CLICK OUSIDE DIALOG CHOOSE PICTURE, THIS WILL HAPPEN
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();

                //// LEAD TO ALL PICTURE FOR USER TO CHOOSE
            } else if (requestCode == AVATAR_GALLERY){
                //// PUT IMAGE FROM GALLERY TO AVATAR IMAGEVIEW
                imageUri = data.getData();
                Picasso.get().load(imageUri.toString()).into(avatar);

                //// UPLOAD IMAGE TO FIREBASE
                try {
                    uploadImage(avatar);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void uploadImage( ImageView img ) throws IOException {
        //// PREPARE FOR PROGRESSDIALOG
        progressDialog = new ProgressDialog(ProfileUserViewActivity.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Uploading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        progressDialog.setCancelable(false);

        //// I DO NOT KNOW WHAT IS IT, IF YOU DELETE IT YOU WILL GET A BUG :))))
        img.setDrawingCacheEnabled(true);
        img.buildDrawingCache();

        //// CHANGE IMAGE TO BITMAP
        @SuppressLint({"NewApi", "LocalSuppress"}) Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(ProfileUserViewActivity.this).getContentResolver(), imageUri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream); //// YOU CAN EDIT QUALITY HERE
        byte[] data = byteArrayOutputStream.toByteArray();

        //// CREATE A LINK FOR IMAGE TO UPLOAD TO FIREBASE
        final StorageReference filepath = msto.child("Avatar").child(mUser.getUid());

        //// PUSH IMAGE TO FIREBASE
        filepath.putBytes(data).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                //// SHOW YOU PROCESS OF UPLOADING
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.incrementProgressBy((int) progress);

                //// IF SUCCESS OF UPLOADING
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ProfileUserViewActivity.this,"Post successful", Toast.LENGTH_SHORT).show();

                //// UPDATE LINK OF AVATAR TO DATABASE
                mref.child("USERS").child(mUser.getUid()).child("image").setValue(mUser.getUid());

                //// DESTROY PROCESSDIALOG
                progressDialog.dismiss();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileUserViewActivity.this,"Post Fail", Toast.LENGTH_SHORT).show();

                //// DESTROY PROCESSDIALOG
                progressDialog.dismiss();

            }
        });

    }

    //// FILL USER INFO TO UI
    public void getData(){
        mref.child("USERS").child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /// GET DATA FROM FIREBASE
                User            user     = dataSnapshot.getValue(User.class);
                InfoUser        infoUser = dataSnapshot.child("infoUser").getValue(InfoUser.class);
                PostAndFollow   postAndFollow = dataSnapshot.child("postAndFollow").getValue(PostAndFollow.class);

                //// CLICK + CTRL TO KNOW :))) JUST KIDING. SET AVATAR
                getUrlAvatar(user.getImage());

                //// SET NAME FOR USER
                txtUserName.setText(user.getName());
                txtActiveName.setText("Active Name: " + infoUser.getActiveName());

                //// SET NUMBER OF POST, FLWER, FLWING
                txtPost.setText(String.valueOf(postAndFollow.getPost()));
                txtFollower.setText(String.valueOf(postAndFollow.getFollower()));
                txtFollowing.setText(String.valueOf(postAndFollow.getFollowing()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getUrlAvatar(final String uriAvatar){
        // Got the download URL
        urlAvatar.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //// IF USER ALREADY HAD IMAGE FOR AVATAR
                if (mUser.getUid().equals(uriAvatar)) {
                    Picasso.get().load(String.valueOf(uri)).into(avatar);

                //// IF USER HAD NOT UPLOADED AVATAR
                } else {
                    Picasso.get().load(uriAvatar).into(avatar);
                }

                //// IF INTERNET IS WEEK, AVARTAR IS DEFAULT IMAGE
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                String avaFailure =  "https://firebasestorage.googleapis.com/v0/b/demooutfitterx-86f8e.appspot.com/o/Avatar%2Fman.png?alt=media&token=5ff017bb-aebd-47ab-aca6-1eb090b8be6f";
                Picasso.get().load(avaFailure).into(avatar);
                Log.w(TAG, "onFailure: ");
            }
        });
    }

}
