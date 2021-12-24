package com.example.residuosapp.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.residuosapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private EditText mail;
    private EditText pass;

    private FirebaseAuth mAuth;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configurar login con Firebase
        // Configurar Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.OAuthKey))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        mail = findViewById(R.id.email);
        pass = findViewById(R.id.password);
    }

    // Verificar si ya se habia almacenado un usuario
    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            cargarMainActivity(currentUser);
        }
    }

    // Manejar el resultado de inicio de sesion con google
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Inicio de sesion exitoso, obtener token e iniciar sesion con firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Error al iniciar sesion
                Log.w(TAG, "Google sign in failed", e);
                // TODO: Toast con mensaje de error
                Toast.makeText(
                        LoginActivity.this,
                        "Error al iniciar sesion con Google.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    /**
     * Iniciar sesion en Firebase con el usuario de google. Si exitoso carga la actividad principal
     *
     * @param idToken Token resultado de iniciar sesion con google
     */
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Inicio de sesion exitoso
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        cargarMainActivity(user);
                    } else {
                        // Inicio de sesion fallido.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(
                                LoginActivity.this,
                                "Error al iniciar sesion con Google y Firebase.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    /**
     * Iniciar sesion con Google
     *
     * @param v boton
     */
    public void googleSignIn(View v) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Iniciar sesion con correo y contraseña
     *
     * @param v boton
     */
    public void login(View v) {
        String email = mail.getText().toString();
        String password = pass.getText().toString();

        // Validar campos
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

        // Iniciar sesion con firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Inicio de sesion exitoso
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        cargarMainActivity(user);
                    } else {
                        // Inicio de sesion fallido
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        String mensaje = "Error al iniciar sesion con correo y contraseña.";

                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            mensaje = "Error. El usuario no existe";
                        }

                        Toast.makeText(
                                LoginActivity.this,
                                mensaje,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    /**
     * Lanzar activity para registrar una cuenta nueva
     *
     * @param v boton
     */
    public void register(View v) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    /**
     * Cambia a la actividad principal despues del inicio de sesion
     *
     * @param user Usuario de firebase, despues de iniciar sesion
     */
    private void cargarMainActivity(FirebaseUser user) {
        // TODO: Almacenar en el modelo
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

}