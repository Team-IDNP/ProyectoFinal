package com.example.residuosapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.residuosapp.controller.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileInformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_information);

        Button btnC = findViewById(R.id.btn_closeInformation);
        btnC.setOnClickListener(view -> finish());
    }

    public void cerrarSesion(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
}
