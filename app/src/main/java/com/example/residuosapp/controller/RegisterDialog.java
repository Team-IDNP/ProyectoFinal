package com.example.residuosapp.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.residuosapp.R;
import com.example.residuosapp.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Executor;

public class RegisterDialog extends DialogFragment {
    private static final String TAG = "REGISTER_ACTIVITY";
    private EditText name;
    private EditText last;
    private EditText mail;
    private EditText pass;
    Button bClose;
    Button ret;
    Button bRegister;


    private FirebaseAuth mAuth;
    FirebaseDatabase db;

    public static RegisterDialog newInstance() {
        return new RegisterDialog();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_register, container, false);
        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        name = v.findViewById(R.id.name);
        last = v.findViewById(R.id.lastName);
        mail = v.findViewById(R.id.email);
        pass = v.findViewById(R.id.password);
        bClose = v.findViewById(R.id.btn_close);
        bClose.setOnClickListener(view -> dismiss());
        ret = v.findViewById(R.id.button_return);
        ret.setOnClickListener(view -> dismiss());
        bRegister = v.findViewById(R.id.button_register);
        bRegister.setOnClickListener(view -> registerAccount());
        return v;
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
     * @param
     */
    public void registerAccount() {
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
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Exitoso
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user == null) {
                            throw new RuntimeException("Usuario de Firebase es null a pesar " +
                                    "de crearse correctamente");
                        }
                        // Actualizar el nombre del usuario
                        UserProfileChangeRequest res = new UserProfileChangeRequest.Builder()
                                .setDisplayName(names)
                                .build();
                        user.updateProfile(res);

                        cargarMainActivity(user);
                    } else {
                        // Error
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(
                                requireActivity(),
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
                requireActivity(),
                user.getDisplayName(),
                user.getEmail(),
                user.getPhotoUrl()
        );
        startActivity(new Intent(requireActivity(), MainActivity.class));
    }

}
