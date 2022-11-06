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

public class RegisterActivity extends AppCompatActivity {
    EditText username, password, confirmPassword;
    Button register;
    TextView backTextView;
    ProgressBar progressBar;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loaddata();
    }

    void loaddata() {
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        register = findViewById(R.id.register);
        backTextView = findViewById(R.id.backTextView);
        progressBar = findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        register.setOnClickListener(view -> {
            String correo = username.getText().toString().trim();
            String pass = password.getText().toString().trim();
            String confirmPass = confirmPassword.getText().toString().trim();
            if (TextUtils.isEmpty(correo)){
                username.setError("El correo es requerido");
                return;
            }
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

            // correct data
            progressBar.setVisibility(View.VISIBLE);
            fAuth.createUserWithEmailAndPassword(correo, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Log.d("user", fAuth.getCurrentUser().getUid());
                        VisitanteSingleton.CreateVisitante(fAuth.getCurrentUser().getUid(), fAuth.getCurrentUser().getEmail());
                        // change to main
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }else{
                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Error")
                                //.setMessage("" + task.getException())
                                .setMessage("El usuario no se ha podido registrar, ya que el correo ya ha sido utilizado")
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
    }
}