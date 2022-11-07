package com.example.artcitytourapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.artcitytourapp.R;
import com.google.firebase.auth.FirebaseAuth;

@Deprecated
public class ChangePassActivity extends AppCompatActivity {
    EditText password, confirmPassword;
    Button changePass;
    TextView backTextView;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        loaddata();
    }

    void loaddata() {
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        changePass = findViewById(R.id.changePass);
        backTextView = findViewById(R.id.backTextView);

        fAuth = FirebaseAuth.getInstance();
        changePass.setOnClickListener(view -> {
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
        });
        backTextView.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        });
    }
}