package com.harry.demooutfitterx.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.harry.demooutfitterx.User.User;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileUserViewActivity extends AppCompatActivity {
    public static final String TAG = "ProfileUserViewActivity";
    public static final int REQUEST_IMAGE = 100;

    private static final int AVATAR_GALLERY = 1;

    Uri imageUri;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mRef;
    StorageReference mSto, urlAvatar;

    TextView        txtUserName, txtActiveName, txtFollowing, txtFollower,txtPost;
    LinearLayout    linearFollower, linearFollowing, linearPost;
    RecyclerView    recyclerViewPost;

    CircularImageView avatar, changeAvatar;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user_view);

        mAuth           = FirebaseAuth.getInstance();
        mUser           = FirebaseAuth.getInstance().getCurrentUser();
        mRef            = FirebaseDatabase.getInstance().getReference();
        mSto            = FirebaseStorage.getInstance().getReference();

        urlAvatar       = mSto.child("Avatar").child(mUser.getUid());

        txtUserName     = findViewById(R.id.userNameProfileUserView);
        txtActiveName   = findViewById(R.id.activeNameProfileUserView);
        txtFollower     = findViewById(R.id.numberFollowerUserView);
        txtFollowing    = findViewById(R.id.numberFollowingUserView);
        txtPost         = findViewById(R.id.numberPostUserView);

        avatar          = findViewById(R.id.avatar);
        changeAvatar    = findViewById(R.id.changeAvatar);

        linearFollower  = findViewById(R.id.linearFollowerUserView);
        linearFollowing = findViewById(R.id.linearFollowingUserView);
        linearPost      = findViewById(R.id.linearPostUserView);

        recyclerViewPost= findViewById(R.id.recyclerPostUserView);

        //// CTRL + CLICK TO KNOW MORE :))
        loadUserData();

        ///  Change avatar
        changeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleChangingAvatar();
            }
        });

        // Clearing older images from cache directory
        // don't call this line if you want to choose multiple images in the same activity
        // call this once the bitmap(s) usage is over
//        ImagePickerActivity.clearCache(this);
    }

    /// Handle changing avatar
    void handleChangingAvatar() {
        /// User Dexter for simplify the request permission
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            /// If user have granted permissions then show the image picker options
                            showImagePickerOptions();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            /// Else show settings dialog
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    /// Showing image picker options
    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    /// Launch the Camera intent
    private void launchCameraIntent() {
        Intent intent = new Intent(ProfileUserViewActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    /// Launch the gallery intent
    private void launchGalleryIntent() {
        Intent intent = new Intent(ProfileUserViewActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileUserViewActivity.this);

        builder.setTitle("Grant Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");

        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                openSettings();
            }
        });

        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            /// Get avatar's URI
            Uri uri = data.getParcelableExtra("path");

            try {
                /// Get image bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                /// Upload to firebase
                uploadAvatar(bitmap);

                // loading profile image from local cache
                loadProfile(uri.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadProfile(String url) {
        Log.d(TAG, "Image cache path: " + url);
        Picasso.get().load(url).into(avatar);
    }

    // Navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    public void uploadAvatar(Bitmap bitmap) {
        //// PREPARE FOR PROGRESS DIALOG
        progressDialog = new ProgressDialog(ProfileUserViewActivity.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Uploading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        progressDialog.setCancelable(false);

        //// I DO NOT KNOW WHAT IS IT, IF YOU DELETE IT YOU WILL GET A BUG :))))
//        img.setDrawingCacheEnabled(true);
//        img.buildDrawingCache();
        //// Really ? =))))))))))

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream); //// YOU CAN EDIT QUALITY HERE
        byte[] data = byteArrayOutputStream.toByteArray();

        //// CREATE A LINK FOR IMAGE TO UPLOAD TO FIREBASE
        final StorageReference filepath = mSto.child("Avatar").child(mUser.getUid());

        //// PUSH IMAGE TO FIREBASE
        filepath.putBytes(data).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NotNull UploadTask.TaskSnapshot taskSnapshot) {
                //// SHOW YOU PROCESS OF UPLOADING
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.incrementProgressBy((int) progress);

                //// IF SUCCESS OF UPLOADING
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ProfileUserViewActivity.this,"Post successful", Toast.LENGTH_SHORT).show();

                //// UPDATE LINK OF AVATAR TO REALTIME DATABASE
                mRef.child("USERS").child(mUser.getUid()).child("image").setValue(mUser.getUid());

                //// DESTROY PROGRESS DIALOG
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileUserViewActivity.this,"Post Fail", Toast.LENGTH_SHORT).show();

                //// DESTROY PROGRESS DIALOG
                progressDialog.dismiss();
            }
        });
    }

    //// FILL USER INFO TO UI
    public void loadUserData(){
        mRef.child("USERS").child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /// GET USER'S DATA FROM FIREBASE
                User user = dataSnapshot.getValue(User.class);

                //// CLICK + CTRL TO KNOW :))) JUST KIDDING. SET AVATAR
                getUrlAvatar(user.getImage());

                //// SET NAME FOR USER
                txtUserName.setText(user.getName());
                txtActiveName.setText("Active Name: " + user.getInfoUser().getActiveName());

                //// SET NUMBER OF POST, FOLLOWER, FOLLOWING
                txtPost.setText(String.valueOf(user.getPostAndFollow().getPost()));
                txtFollower.setText(String.valueOf(user.getPostAndFollow().getFollower()));
                txtFollowing.setText(String.valueOf(user.getPostAndFollow().getFollowing()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: failed to get user's data");
            }
        });
    }

    /// Get avatar's URL
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

                //// IF INTERNET IS WEEK, AVATAR IS DEFAULT IMAGE
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
