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
import com.fyp.smhs.databinding.FragmentPasswordChangeBinding;
import com.github.florent37.shapeofview.shapes.ArcView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class PasswordChangeFragment extends Fragment {

    private FragmentPasswordChangeBinding binding;
    private LoadingDialog loadingDialog;
    private String password;
    private Button button1;
    private ArcView arcView;
    private FirebaseAuth mAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPasswordChangeBinding.inflate(inflater, container, false);
        loadingDialog = new LoadingDialog(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        button1 = view.findViewById(R.id.btnUpdatePassword);
        mAuth = FirebaseAuth.getInstance();

        button1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (areFieldReady()) {
                    loadingDialog.startLoading();

                    FirebaseUser user = mAuth.getCurrentUser();

                    user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                loadingDialog.stopLoading();
                                Toast.makeText(requireContext(), "Password is updated", Toast.LENGTH_SHORT).show();

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
                }

            };

        });



    }


    private boolean areFieldReady() {


        password = binding.edtUPassword.getText().toString().trim();

        boolean flag = false;
        View requestView = null;

        if (password.isEmpty()) {
            binding.edtUPassword.setError("Field is required");
            flag = true;
            requestView = binding.edtUPassword;
        } else if (password.length() < 8) {
            binding.edtUPassword.setError("Minimum 8 characters");
            flag = true;
            requestView = binding.edtUPassword;
        }

        if (flag) {
            requestView.requestFocus();
            return false;
        } else {
            return true;
        }

    }
}