package com.example.residuosapp.controller.main.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.residuosapp.R;
import com.example.residuosapp.model.Alert;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import com.example.residuosapp.model.Usuario;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TabMapFragment extends Fragment {

    GoogleMap mapTab;

    FirebaseDatabase db;


    public TabMapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        db = FirebaseDatabase.getInstance();


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.tabMapView);
        mapFragment.getMapAsync(googleMap -> {
            mapTab = googleMap;

            //Aquí se añadirian los marcadores de usuario
            //end
        });

        Usuario datosActual=Usuario.Companion.get(this.getActivity());
        String correoActual=datosActual.getCorreo();


        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference("alerts");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mapTab.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Alert u = ds.getValue(Alert.class);
                    if (u != null && u.getUsuarioId().equals(correoActual)) {
                        String lat =  u.getUbiLat();
                        String lon = u.getUbiLong();
                        LatLng sydney = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                        mapTab.addMarker(new MarkerOptions()
                                .position(sydney)
                                .title("Marker"));
                        mapTab.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    }
                }
                mapTab.animateCamera(CameraUpdateFactory.zoomTo(5));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        return v;
    }
}