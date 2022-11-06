package com.example.artcitytourapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.artcitytourapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import Usuario.VisitanteSingleton;

public class LoginActivity extends AppCompatActivity {
    EditText username, password;
    Button login, loginGoogle, loginFacebook;
    TextView createAccountTextView, forgotPassTextView;
    ProgressBar progressBar;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loaddata();
    }

    void loaddata(){
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        loginGoogle = findViewById(R.id.loginGoogle);
        loginFacebook = findViewById(R.id.loginFacebook);
        createAccountTextView = findViewById(R.id.createAccountTextView);
        forgotPassTextView = findViewById(R.id.forgotPassTextView);
        progressBar = findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() != null){
            VisitanteSingleton.LoginVisitante(fAuth.getCurrentUser().getUid());
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        login.setOnClickListener(view -> {
            String correo = username.getText().toString().trim();
            String pass = password.getText().toString().trim();
            if (TextUtils.isEmpty(correo)){
                username.setError("El correo es requerido");
                return;
            }
            if (TextUtils.isEmpty(pass)){
                password.setError("La contraseña es requerida");
                return;
            }

            // correct data
            progressBar.setVisibility(View.VISIBLE);
            fAuth.signInWithEmailAndPassword(correo, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Log.d("user", fAuth.getCurrentUser().getUid());
                        VisitanteSingleton.LoginVisitante(fAuth.getCurrentUser().getUid());
                        // change to main
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }else{
                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Error")
                                //.setMessage("" + task.getException())
                                .setMessage("El usuario no se ha podido verificar, revise los datos")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .show();
                    }
                }
            });
        });
        loginGoogle.setOnClickListener(view -> {
            // todo
        });
        loginFacebook.setOnClickListener(view -> {
            // todo
        });
        createAccountTextView.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        });
        forgotPassTextView.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), RecuperatePassActivity.class));
        });
    }
}