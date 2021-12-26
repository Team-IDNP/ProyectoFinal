package com.example.residuosapp.controller.main.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.residuosapp.R;
import com.example.residuosapp.controller.LoginActivity;
import com.example.residuosapp.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DialogInformation extends DialogFragment {

    TextView dateCTV, emailTV;

    public static DialogInformation newInstance(){
        return new DialogInformation();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);
        /*setContentView(R.layout.dialog_profile_information);

        Button btnC = findViewById(R.id.btn_closeInformation);
        btnC.setOnClickListener(view -> finish());*/
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_profile_information, container, false);
        Button btnC = v.findViewById(R.id.btn_closeInformation);
        btnC.setOnClickListener(view -> dismiss());

        dateCTV = v.findViewById(R.id.date_create_user);
        emailTV = v.findViewById(R.id.email_info);
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        Date creationDate = new Date(Long.parseLong(u.getMetadata().getCreationTimestamp()+""));
        SimpleDateFormat formatDate = new SimpleDateFormat("MMMM");
        String dateS = formatDate.format(creationDate);
        dateCTV.setText("Registrado desde "+creationDate.getDay()+" de "+dateS+" del "+(creationDate.getYear()+1900));

        emailTV.setText(u.getEmail());



        return v;
    }

    public void cerrarSesion(View view) {
        FirebaseAuth.getInstance().signOut();
        Usuario.Companion.eliminar(requireActivity());
        Intent i = new Intent(requireActivity(), LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
}
