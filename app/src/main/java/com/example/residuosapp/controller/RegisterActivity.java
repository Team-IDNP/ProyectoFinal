package com.example.residuosapp.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.residuosapp.R;
import com.example.residuosapp.model.Usuario;
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

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.name);
        last = findViewById(R.id.lastName);
        mail = findViewById(R.id.email);
        pass = findViewById(R.id.password);
    }

    // Revisar si un usuario ya inicio sesion
    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            cargarMainActivity(currentUser);
        }
    }

    /**
     * Registra un usuario con los valores de los edittext
     *
     * @param v boton
     */
    public void registerAccount(View v) {
        String email = mail.getText().toString();
        String password = pass.getText().toString();
        String names = name.getText().toString();
        String lastName = last.getText().toString();

        // Validar datos
        if (TextUtils.isEmpty(names)) {
            name.setError("Ingrese nombres");
            name.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(lastName)) {
            last.setError("Ingrese apellidos");
            last.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            mail.setError("Ingrese correo");
            mail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            pass.setError("Ingrese contraseña");
            pass.requestFocus();
            return;
        }

        // Intentar guardar usuario en Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Exitoso
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        cargarMainActivity(user);
                    } else {
                        // Error
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(
                                RegisterActivity.this,
                                "Error al crear cuenta.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    /**
     * Carga la actividad principal
     *
     * @param user Usuario de firebase
     */
    private void cargarMainActivity(FirebaseUser user) {
        Usuario.Companion.set(
                this,
                user.getDisplayName(),
                user.getEmail(),
                user.getPhotoUrl()
        );
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
    }

    /**
     * Regresar a la actividad de inicio de sesion
     *
     * @param v boton
     */
    public void loginScreen(View v) {
        onBackPressed();
    }

}
