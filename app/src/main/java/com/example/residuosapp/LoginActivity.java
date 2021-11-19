package com.example.residuosapp;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private EditText mail;
    private EditText pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mail = findViewById(R.id.email);
        pass = findViewById(R.id.password);
    }

    public void login(View v){
        String email = mail.getText().toString();
        String password = pass.getText().toString();

        if(TextUtils.isEmpty(email)){
            mail.setError("Ingrese correo");
            mail.requestFocus();
        }else if(TextUtils.isEmpty(password)){
            pass.setError("Ingrese contraseña");
            pass.requestFocus();
        }else{
            //Aquí cerificación
            if(true){
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        }
    }

    public void register(View v){
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
}