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
import android.widget.TextView;

import com.example.artcitytourapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import Usuario.VisitanteSingleton;

public class RecuperatePassActivity extends AppCompatActivity {
    EditText username;
    Button recuperateBtn;
    TextView backTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperate_pass);

        loaddata();
    }

    void loaddata() {
        username = findViewById(R.id.username);
        recuperateBtn = findViewById(R.id.recuperateBtn);
        backTextView = findViewById(R.id.backTextView);

        recuperateBtn.setOnClickListener(view -> {
            String correo = username.getText().toString().trim();
            if (TextUtils.isEmpty(correo)){
                username.setError("El correo es requerido");
                return;
            }
            // todo mandar correo
        });
        backTextView.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        });
    }
}
