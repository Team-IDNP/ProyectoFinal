package com.example.residuosapp.controller.main.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.residuosapp.R;
import com.example.residuosapp.model.Alert;
import com.example.residuosapp.model.AlertAdapter;
import com.example.residuosapp.model.Distrito;
import com.example.residuosapp.model.Usuario;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class TabListFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Alert> listAlert;
    AlertAdapter alertAdapter;
    public TabListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_tab_list, container, false);
        recyclerView = v.findViewById(R.id.recycler_alert);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listAlert = new ArrayList<>();
        alertAdapter = new AlertAdapter(getContext(), listAlert);
        recyclerView.setAdapter(alertAdapter);


        Usuario datosActual=Usuario.Companion.get(this.getActivity());
        String correoActual=datosActual.getCorreo();


        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference("alerts");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {


            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listAlert.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Alert u = ds.getValue(Alert.class);
                    u.setId(ds.getKey());
                    if (u != null && u.getUsuarioId().equals(correoActual)) {
                        Log.e("cant", u.getDistritoId()+"");
                        listAlert.add(u);
                    }
                }
                Collections.sort(listAlert, Alert.DateAZComparator);
                alertAdapter.notifyDataSetChanged();

                //Distrito
                db.getReference("distrito").addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int cant = (int) snapshot.getChildrenCount();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Distrito s = ds.getValue(Distrito.class);
                            assert s != null;
                            s.setId(ds.getKey());
                            Log.e("cant", listAlert.get(0).getDistritoId()+"");
                            for (Alert a : listAlert){
                                if(a.getDistritoId().equals(s.getId())){
                                    a.lugar = s.getName();
                                }
                            }
                        }
                        alertAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("PRUEBA", ": "+error);
                    }
                });








            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return v;





    }
}