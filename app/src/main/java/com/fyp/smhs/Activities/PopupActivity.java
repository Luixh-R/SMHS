package com.fyp.smhs.Activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;

import com.bumptech.glide.Glide;
import com.fyp.smhs.Database.AppDatabase;
import com.fyp.smhs.Database.Journal;
import com.fyp.smhs.Database.JournalDao;
import com.fyp.smhs.Fragments.HomeFragment;
import com.fyp.smhs.Models.Post;
import com.fyp.smhs.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PopupActivity extends AppCompatActivity {


    private static final String TAG = "DocSnippets";

    private AppBarConfiguration mAppBarConfiguration;

    private AppBarConfiguration rar;
    private static final int REQUESTCODE = 2;
    private static final int PReqCode =  2;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Dialog popAddPost ;
    ImageView popupUserImage,popupPostImage,popupAddBtn;
    TextView popupTitle,popupDescription;
    ProgressBar popupClickProgress;
    private Uri pickedImgUri = null;
    String imageDownloadLink;
    FirebaseFirestore firebaseFirestore;
    CollectionReference collectionReference;
    NavController navController;

    public static final String QUERY_MOOD_PARAMETER = "Home.QueryMood";
    public static final String NOTIFICATION_CHANNEL_ID = "Home.NotificationChan";

    private int mCurrentMood = 1;
    private int mCurrentMoodIntensity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_add_post);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        iniPopup();





    }

    private void iniPopup() {


        // ini popup widgets
        popupUserImage = findViewById(R.id.popup_user_image);
        popupPostImage = findViewById(R.id.popup_img);
        popupTitle = findViewById(R.id.popup_title);
        popupDescription = findViewById(R.id.popup_description);
        popupAddBtn = findViewById(R.id.popup_add);
        popupClickProgress = findViewById(R.id.popup_progressBar);

        // load Current user profile photo

        Glide.with(PopupActivity.this).load(R.drawable.default_user_photo).into(popupUserImage);

        // Add post click Listener

        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                popupAddBtn.setVisibility(View.INVISIBLE);
                popupClickProgress.setVisibility(View.VISIBLE);

                // we need to test all input fields (Title and description) and post image

                if(!popupTitle.getText().toString().isEmpty()
                        && !popupDescription.getText().toString().isEmpty()
                ) {

                    // add && pickedImgUri != null

                    //everything is okay no empty or null value
                    // TODO Create Post Object and add it to firebase database
                    // first we need to upload post Image
                    // access firebase storage
                    //  StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_images");
                    //StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());

                    //  imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {


                    if(currentUser.getPhotoUrl() != null){

                        Post post = new Post(popupTitle.getText().toString(),
                                popupDescription.getText().toString(),
                                imageDownloadLink,
                                currentUser.getUid(),
                                currentUser.getPhotoUrl().toString());

                        // Add post to firebase database

                        addPost(post);
                        // Add post to firebase database
                    }else {

                        Post post = new Post(popupTitle.getText().toString(),
                                popupDescription.getText().toString(),
                                imageDownloadLink,
                                currentUser.getUid(),
                                null);

                        // Add post to firebase database

                        addPost(post);
                        // Add post to firebase database
                    }


                }
                else{
                    showMessage("Please verify all input fields and choose Post Image");
                    popupAddBtn.setVisibility(View.VISIBLE);
                    popupClickProgress.setVisibility(View.INVISIBLE);

                }

                //   HomeFragment;

            }
        });


    }

    private void showMessage(String message) {

        Toast.makeText(PopupActivity.this, message, Toast.LENGTH_SHORT).show();

    }

    private void addPost(Post post) {


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        String documentId=db.collection("Posts").document().getId();
        post.setPostKey(documentId);


        db.collection("Posts")
                .add(post)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(popupDescription.getWindowToken(), 0);

                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        showMessage("Post Added successfully");
                        popupClickProgress.setVisibility(View.INVISIBLE);
                        popupTitle.setText("");
                        popupDescription.setText("");
                        popupAddBtn.setVisibility(View.VISIBLE);
                        finish();


                        //if you added fragment via layout xml
                        // HomeFragment fragment = (HomeFragment) fm.findFragmentById(R.id.nav_);
                        //fragment.Update();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

}