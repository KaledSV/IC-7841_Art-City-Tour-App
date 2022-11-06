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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import Usuario.VisitanteSingleton;

public class RecuperatePassActivity extends AppCompatActivity {
    EditText username;
    Button recuperateBtn;
    TextView backTextView;

    FirebaseAuth fAuth;

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

        fAuth = FirebaseAuth.getInstance();
        recuperateBtn.setOnClickListener(view -> {
            String correo = username.getText().toString().trim();
            if (TextUtils.isEmpty(correo)){
                username.setError("El correo es requerido");
                return;
            }
            fAuth.sendPasswordResetEmail(correo).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Correo enviada")
                            //.setMessage("" + task.getException())
                            .setMessage("El correo de recuperacion ha sido enviado al correo: " + correo)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Error")
                            .setMessage("El correo no se ha podido enviar, verifique que la direccion de correo es correcta")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                }
            });
        });
        backTextView.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        });
    }
}
