package com.example.artcitytourapp.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;
import java.util.Objects;

import Usuario.VisitanteSingleton;

public class LoginActivity extends AppCompatActivity {
    EditText username, password;
    Button login, loginGoogle, loginFacebook;
    TextView createAccountTextView, forgotPassTextView;
    ProgressBar progressBar;

    FirebaseAuth fAuth;
    CallbackManager mCallbackManager;
    ActivityResultLauncher<Intent> startActivityForResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loaddata();
        startActivityForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d("Codigo", String.valueOf(result.getResultCode()));
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d("Existo", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try{
                            GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                            firebaseAuthWithGoogle(account);
                        }catch (Exception e){
                            new AlertDialog.Builder(getApplicationContext())
                                    .setTitle("Error")
                                    //.setMessage("" + e.getMessage())
                                    .setMessage("Google no pudo verificar sus credenciales")
                                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss())
                                    .show();
                        }
                    }
                });
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
                        new AlertDialog.Builder(getApplicationContext())
                                .setTitle("Error")
                                //.setMessage("" + task.getException())
                                .setMessage("El usuario no se ha podido verificar, revise los datos")
                                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss())
                                .show();
                    }
                }
            });
        });

        loginGoogle.setOnClickListener(view -> {
            loginGoogle();
        });

        loginFacebook.setOnClickListener(view -> {
            mCallbackManager = CallbackManager.Factory.create();

            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
            LoginManager.getInstance().registerCallback(mCallbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            handleFacebookAccessToken(loginResult.getAccessToken());
                        }
                        @Override
                        public void onCancel() {
                            new AlertDialog.Builder(getApplicationContext())
                                    .setTitle("Error")
                                    //.setMessage("" + e.getMessage())
                                    .setMessage("Facebook no pudo verificar sus credenciales")
                                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss())
                                    .show();
                        }
                        @Override
                        public void onError(FacebookException exception) {
                            new AlertDialog.Builder(getApplicationContext())
                                    .setTitle("Error")
                                    //.setMessage("" + e.getMessage())
                                    .setMessage("Facebook no pudo verificar sus credenciales")
                                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss())
                                    .show();
                        }
                    });
        });

        createAccountTextView.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        });
        forgotPassTextView.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), RecuperatePassActivity.class));
        });
    }

    private void loginGoogle(){
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult.launch(intent);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        fAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                String uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                String correo = Objects.requireNonNull(fAuth.getCurrentUser()).getEmail();

                Log.d("DATOSSSSS", "uid: " + uid + ", correo: " + correo);
                Log.d("DATOSSSSS", String.valueOf(Objects.requireNonNull(authResult.getAdditionalUserInfo()).isNewUser()));
                if (Objects.requireNonNull(authResult.getAdditionalUserInfo()).isNewUser()){
                    //register
                    VisitanteSingleton.CreateVisitante(uid, correo);
                }else{
                    //login
                    Log.d("user", uid);
                    VisitanteSingleton.LoginVisitante(uid);
                }
                // change to main
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new AlertDialog.Builder(getApplicationContext())
                        .setTitle("Error")
                        //.setMessage("" + task.getException())
                        .setMessage("El usuario no se ha podido verificar, revise que ya este registrado")
                        .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss())
                        .show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        fAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                String uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                String correo = Objects.requireNonNull(fAuth.getCurrentUser()).getEmail();

                Log.d("DATOSSSSS", "uid: " + uid + ", correo: " + correo);
                Log.d("DATOSSSSS", String.valueOf(Objects.requireNonNull(authResult.getAdditionalUserInfo()).isNewUser()));
                if (Objects.requireNonNull(authResult.getAdditionalUserInfo()).isNewUser()){
                    //register
                    VisitanteSingleton.CreateVisitante(uid, correo);
                }else{
                    //login
                    Log.d("user", uid);
                    VisitanteSingleton.LoginVisitante(uid);
                }
                // change to main
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new AlertDialog.Builder(getApplicationContext())
                        .setTitle("Error")
                        //.setMessage("" + task.getException())
                        .setMessage("El usuario no se ha podido verificar, revise que ya este registrado")
                        .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss())
                        .show();
            }
        });
    }
}