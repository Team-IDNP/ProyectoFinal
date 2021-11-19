package com.example.residuosapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {
    private EditText name;
    private EditText last;
    private EditText mail;
    private EditText pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.name);
        last = findViewById(R.id.lastName);
        mail = findViewById(R.id.email);
        pass = findViewById(R.id.password);

    }

    public void registerAccount(View v){
        String email = mail.getText().toString();
        String password = pass.getText().toString();
        String names = name.getText().toString();
        String lastName = last.getText().toString();

        if(TextUtils.isEmpty(names)){
            name.setError("Ingrese nombres");
            name.requestFocus();
        }else if(TextUtils.isEmpty(lastName)){
            last.setError("Ingrese apellidos");
            last.requestFocus();
        }else if(TextUtils.isEmpty(email)){
            mail.setError("Ingrese correo");
            mail.requestFocus();
        }else if(TextUtils.isEmpty(password)){
            pass.setError("Ingrese contraseña");
            pass.requestFocus();
        }else{
            //Aquí Guardado de usuario y de datos de sedsion
            if(true){
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        }
    }

    public void loginScreen(View v){
        onBackPressed();
    }
}