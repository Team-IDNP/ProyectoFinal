package com.example.residuosapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "REGISTER_ACTIVITY";
    private EditText name;
    private EditText last;
    private EditText mail;
    private EditText pass;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.name);
        last = findViewById(R.id.lastName);
        mail = findViewById(R.id.email);
        pass = findViewById(R.id.password);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

        }
    }

    public void registerAccount(View v) {
        String email = mail.getText().toString();
        String password = pass.getText().toString();
        String names = name.getText().toString();
        String lastName = last.getText().toString();

        if (TextUtils.isEmpty(names)) {
            name.setError("Ingrese nombres");
            name.requestFocus();
        } else if (TextUtils.isEmpty(lastName)) {
            last.setError("Ingrese apellidos");
            last.requestFocus();
        } else if (TextUtils.isEmpty(email)) {
            mail.setError("Ingrese correo");
            mail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            pass.setError("Ingrese contraseña");
            pass.requestFocus();
        } else {
            //Aquí Guardado de usuario y de datos de sesion
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    });
        }
    }

    private void updateUI(FirebaseUser user) {
        // TODO: hacer otras cosas
        if (user != null)
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
    }

    public void loginScreen(View v) {
        onBackPressed();
    }
}