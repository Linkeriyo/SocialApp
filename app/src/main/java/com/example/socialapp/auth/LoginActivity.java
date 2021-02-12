package com.example.socialapp.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socialapp.BleepsActivity;
import com.example.socialapp.R;
import com.example.socialapp.data.AppData;
import com.example.socialapp.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    private static final String TAG = "LoginActivity";
    private FirebaseAuth auth;
    private GoogleSignInOptions gSignInOptions;
    private GoogleSignInClient gSignInClient;
    private TextView emailTextView, passwordTextView;
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            readyForNextActivity();
        }

        gSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        setContentView(R.layout.activity_login);
        setup();
    }

    private void setup() {
        Button registerButton = findViewById(R.id.registerButton);
        Button loginButton = findViewById(R.id.loginButton);
        Button googleLoginButton = findViewById(R.id.googleLoginButton);
        emailTextView = findViewById(R.id.emailTextView);
        passwordTextView = findViewById(R.id.passwordTextView);

        registerButton.setOnClickListener(v -> {
            if (!emailTextView.getText().toString().isEmpty()
                    && !passwordTextView.getText().toString().isEmpty()) {

                auth.createUserWithEmailAndPassword(
                        emailTextView.getText().toString(),
                        passwordTextView.getText().toString()
                ).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(
                                this,
                                "Se ha registrado satisfactoriamente.",
                                Toast.LENGTH_SHORT
                        ).show();
                        readyForNextActivity();
                    } else {
                        Toast.makeText(
                                this,
                                "Error al registrarse.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            } else {
                Toast.makeText(
                        this,
                        "Los campos no deben estar vacíos.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        loginButton.setOnClickListener(v -> {

            if (!emailTextView.getText().toString().isEmpty()
                    && !passwordTextView.getText().toString().isEmpty()) {

                auth.signInWithEmailAndPassword(
                        emailTextView.getText().toString(),
                        passwordTextView.getText().toString()
                ).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(
                                this,
                                "Se ha iniciado sesión satisfactoriamente.",
                                Toast.LENGTH_SHORT
                        ).show();
                        readyForNextActivity();
                    } else {
                        Toast.makeText(
                                this,
                                "Error al iniciar sesión.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            }
        });

        googleLoginButton.setOnClickListener(v -> {
            gSignInClient = GoogleSignIn.getClient(this, gSignInOptions);
            startActivityForResult(gSignInClient.getSignInIntent(), RC_SIGN_IN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = (GoogleSignInAccount) task.getResult(ApiException.class);
                assert account != null;
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
                Thread.sleep(2000);
                Toast.makeText(
                        this,
                        "Se ha iniciado sesión satisfactoriamente.",
                        Toast.LENGTH_SHORT
                ).show();


                readyForNextActivity();
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(
                        this,
                        "Error al iniciar sesión",
                        Toast.LENGTH_SHORT
                ).show();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "signInWithCredential:success");
            } else {
                Log.w(TAG, "signInWithCredential:failure", task.getException());
            }
        });
    }

    private void readyForNextActivity() {
        if (isUserRegistered()) {
            startActivity(new Intent(context, BleepsActivity.class));
        } else {
            startActivity(new Intent(context, RegisterActivity.class));
        }

        finish();
    }

    public static boolean isUserRegistered() {
        String userId = FirebaseAuth.getInstance().getUid();

        for (User user : AppData.userList) {
            if (user.userId.equals(userId)) {
                return true;
            }
        }
        return false;
    }
}