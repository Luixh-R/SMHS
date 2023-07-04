package com.fyp.smhs.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.fyp.smhs.Models.Utility.LoadingDialog;
import com.fyp.smhs.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class EmailChangeFragment extends Fragment {

    private com.fyp.smhs.databinding.FragmentEmailChangeBinding binding;
    private LoadingDialog loadingDialog;
    private Button button1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = com.fyp.smhs.databinding.FragmentEmailChangeBinding.inflate(inflater, container, false);
        loadingDialog = new LoadingDialog(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        button1 = view.findViewById(R.id.btnUpdateEmail);

        button1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = binding.edtUEmail.getText().toString().trim();
                if (email.isEmpty()) {
                    binding.edtUEmail.setError("Email is required");
                    binding.edtUEmail.requestFocus();
                } else {
                    loadingDialog.startLoading();

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                                Map<String, Object> map = new HashMap<>();
                                map.put("email", email);
                                databaseReference.updateChildren(map);
                                loadingDialog.stopLoading();

                                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                                Fragment someFragment = new SettingsFragment();
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.contentprofile, someFragment ); // give your fragment container id in first parameter
                                //transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                                transaction.commit();


                            } else {
                                loadingDialog.stopLoading();
                                Log.d("TAG", "onComplete: " + task.getException());
                                Toast.makeText(requireContext(), "Error : " + task.getException(), Toast.LENGTH_SHORT).show();

                                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                                Fragment someFragment = new SettingsFragment();
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.contentprofile, someFragment ); // give your fragment container id in first parameter
                                //transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                                transaction.commit();
                            }
                            }
                        });
                    ;
                }

            };

        });




    }}