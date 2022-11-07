package com.example.artcitytourapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.artcitytourapp.R;
import com.example.artcitytourapp.activities.LoginActivity;
import com.example.artcitytourapp.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.Objects;

import Usuario.VisitanteSingleton;

public class UserProfileFragment extends Fragment {
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        loaddata();
        return view;
    }
    void loaddata(){
        final EditText username = view.findViewById(R.id.username);
        final EditText password = view.findViewById(R.id.pass);
        final EditText confirmPassword = view.findViewById(R.id.confirmPass);
        final Button saveUsername = view.findViewById(R.id.saveUsername);
        final Button savePass = view.findViewById(R.id.savePass);
        final Button logoutBtn = view.findViewById(R.id.logoutBtn);
        final FirebaseAuth fAuth = FirebaseAuth.getInstance();

        username.setText(VisitanteSingleton.getInstance().getNombre());
        saveUsername.setOnClickListener(view -> {
            String user = username.getText().toString().trim();
            if (TextUtils.isEmpty(user)){
                username.setError("El nombre es requerido");
                return;
            }
            if(user.equals(VisitanteSingleton.getInstance().getNombre())){
                username.setError("El nombre debe ser diferente al actual");
                return;
            }
            VisitanteSingleton.getInstance().updateUsername(user, view);
        });

        savePass.setOnClickListener(view -> {
            String pass = password.getText().toString().trim();
            String confirmPass = confirmPassword.getText().toString().trim();
            if (TextUtils.isEmpty(pass)){
                password.setError("La contraseña es requerida");
                return;
            }
            if (pass.length() < 6){
                password.setError("La contraseña tiene que tener 6+ caracteres");
                return;
            }
            if (TextUtils.isEmpty(confirmPass)){
                confirmPassword.setError("La confirmacion es requerida");
            }
            if (!confirmPass.equals(pass)){
                confirmPassword.setError("La contraseña no coincide");
            }
            Objects.requireNonNull(fAuth.getCurrentUser()).updatePassword(pass);
        });

        logoutBtn.setOnClickListener(view -> {
            fAuth.signOut();
            MainActivity.main.finish();
            startActivity(new Intent(view.getContext(), LoginActivity.class));
        });

    }
}