package com.example.socialapp.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialapp.BleepsActivity;
import com.example.socialapp.R;
import com.example.socialapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LoginActivity.isUserRegistered()) {
            startActivity(new Intent(this, BleepsActivity.class));
            finish();
        }
        setContentView(R.layout.activity_register);

        setup();
    }

    @SuppressLint("CheckResult")
    public void setup() {
        TextView registerEmailTextView = findViewById(R.id.registerEmailTextView);
        TextView registerNameTextView = findViewById(R.id.registerNameTextView);
        TextView registerNickTextView = findViewById(R.id.registerNickTextView);
        Button registerButton = findViewById(R.id.registerFinalButton);
        CircularImageView registerImageView = findViewById(R.id.registerImageView);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            registerEmailTextView.setText(user.getEmail());
        }

        registerImageView.setOnClickListener(v -> {
            startActivityForResult(Intent.createChooser(new Intent()
                    .setType("image/*")
                    .setAction(Intent.ACTION_GET_CONTENT)),
            );
        });

        registerButton.setOnClickListener(v -> {
            if (registerEmailTextView.getText() != ""
                    && registerNameTextView.getText() != ""
                    && registerNickTextView.getText() != "") {

                DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users");
                usersReference.child(user.getUid()).setValue(new User(user.getUid(),
                        registerNameTextView.getText().toString(),
                        registerNickTextView.getText().toString(),
                        "a")
                );
                nextActivity();
            } else {
                Toast.makeText(this,
                        "Todos los campos son obligatorios",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void nextActivity() {
        startActivity(new Intent(this, BleepsActivity.class));
        finish();
    }
}