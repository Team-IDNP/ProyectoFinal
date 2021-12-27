package com.example.residuosapp.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.AndroidException;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.residuosapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    AlertDialog AD;
    boolean forResult = false;

    final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!isGrantedPermissions()) {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 150);
        }else{
            startAplication();
        }

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.e("afd", ""+requestCode);
        switch (requestCode){
            case 98:
                Log.e("de frgament", grantResults.length+""+ Arrays.toString(grantResults));
                break;

            case 100:
                Log.e("de frgament", grantResults.length+" "+ Arrays.toString(grantResults));
                break;
            case 99:
                Log.e("de frgament", grantResults.length+"  "+ Arrays.toString(grantResults));
                break;
            case 150:
                if(isGrantedPermissions()){
                    startAplication();
                }else{
                    createDialogPermission();
                    AD.show();
                }
                break;

        }

    }

    private boolean isGrantedPermissions() {
        return ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void createDialogPermission() {
        //Mostramos diálogo
        AlertDialog.Builder ADBuilder = new AlertDialog.Builder(MainActivity.this);
        ADBuilder.setCancelable(false);
        ADBuilder.setMessage(Html.fromHtml("<p>Se necesitan los permisos de:</p>\n" +
                "<p><strong>C&aacute;mara:</strong> Para tomar fotos de las evidencias</p>\n" +
                "<p><strong>Ubicaci&oacute;n:</strong> Para localizarlo en el mapa</p>\n" +
                "<p><strong>Archivos:</strong> Para guardar las im&aacute;genes</p>"));
        ADBuilder.setNeutralButton("Ir a configuracion", (dialogInterface, i) -> {
            Intent set = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            //set.addCategory(Intent.CATEGORY_DEFAULT);
            set.setData(Uri.parse("package:" + getPackageName()));
            set.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //set.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            //set.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(set);
            forResult = true;
        });

        AD = ADBuilder.create();
    }

    public void startAplication(){
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_mapAlert,
                R.id.navigation_addAlert,
                R.id.navigation_notifications
        ).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(forResult){
            if(!isGrantedPermissions()){
                AD.show();
            }else{
                startAplication();
                AD.dismiss();
                forResult = false;
            }
        }
    }
}