package com.fyp.smhs.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.smhs.Activities.Home;
import com.fyp.smhs.Activities.NotepadEntry;
import com.fyp.smhs.Activities.PopupActivity;
import com.fyp.smhs.Adapters.CommentAdapter;
import com.fyp.smhs.Adapters.PostAdapter;
import com.fyp.smhs.Models.Comment;
import com.fyp.smhs.Models.Post;
import com.fyp.smhs.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "DocSnippets";
    RecyclerView postRecyclerView;
    PostAdapter postAdapter;
    FirebaseFirestore firebaseFirestore;
    DatabaseReference databaseReference;
    CollectionReference collectionReference;
    List<Post> postList;

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

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }





        // getActivity().getSupportActionBar().setTitle("Home");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View fragmentView = inflater.inflate(R.layout.fragment_home2, container, false);
        postRecyclerView = fragmentView.findViewById(R.id.postRV);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        postRecyclerView.setHasFixedSize(true);
        firebaseFirestore = firebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("Posts");


     //   FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        // Inflate the layout for this fragment
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        FloatingActionButton fab = view.findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Add code for creating a journal entry
                Intent intent = new Intent(view.getContext(), PopupActivity.class);
                startActivityForResult(intent, 0x0);
            }
        });







    }





    public void Update(){

        CollectionReference postRef = firebaseFirestore.collection("Posts");
        postRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            for (DocumentChange dc : snapshot.getDocumentChanges()) {
                switch (dc.getType()) {
                    case ADDED:

                        postList = new ArrayList<>();
                        for (DocumentSnapshot document : snapshot.getDocuments()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());


                            //  for (DataSnapshot postsnap: task.getChildren()) {

                            Post post = document.toObject(Post.class);
                            postList.add(post);

                            //  }

                        }
                        postAdapter = new PostAdapter(getActivity(), postList);
                        postRecyclerView.setAdapter(postAdapter);

                        // handle added documents...
                        break;
                    case MODIFIED:
                        // handle modified documents...
                        break;
                    case REMOVED:
                        // handle removed documents...
                        break;


                }
            }
        });






    }

        public void UpdateOld() {


            // Get List Posts from the database
            firebaseFirestore.collection("Posts")
                    // .whereEqualTo("capital", true)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                postList = new ArrayList<>();
                                for (DocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());


                                    //  for (DataSnapshot postsnap: task.getChildren()) {

                                    Post post = document.toObject(Post.class);
                                    postList.add(post);

                                    //  }

                                }
                                postAdapter = new PostAdapter(getActivity(),postList);
                                postRecyclerView.setAdapter(postAdapter);
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });







        }




    @Override
    public void onStart() {
        super.onStart();


        CollectionReference postRef = firebaseFirestore.collection("Posts");
        postRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            for (DocumentChange dc : snapshot.getDocumentChanges()) {
                switch (dc.getType()) {
                    case ADDED:

                        postList = new ArrayList<>();
                        for (DocumentSnapshot document : snapshot.getDocuments()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());


                            //  for (DataSnapshot postsnap: task.getChildren()) {

                            Post post = document.toObject(Post.class);
                            postList.add(post);

                            //  }

                        }
                        postAdapter = new PostAdapter(getActivity(), postList);
                        postRecyclerView.setAdapter(postAdapter);

                        // handle added documents...
                        break;
                    case MODIFIED:
                        // handle modified documents...
                        break;
                    case REMOVED:
                        // handle removed documents...
                        break;


                }
            }
        });


    }



}


