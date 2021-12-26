package com.example.residuosapp.controller.main.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.residuosapp.R;
import com.example.residuosapp.model.Alert;
import com.example.residuosapp.model.Departamento;
import com.example.residuosapp.model.Marcador;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapFragment extends Fragment {

    private GoogleMap mapG;
    ArrayList<Marcador> marcadores;
    public static boolean a = true;

    public MapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapView);



        marcadores = Marcador.Companion.getMarcadores();


        mapFragment.getMapAsync(googleMap -> {
            mapG = googleMap;
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference myRef = db.getReference("alerts");
            // Read from the database
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mapG.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Alert u = ds.getValue(Alert.class);
                        if (u != null) {
                            String lat =  u.getUbiLat();
                            String lon = u.getUbiLong();
                            LatLng sydney = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                            mapG.addMarker(new MarkerOptions()
                                    .position(sydney)
                                    .title("Marker"));
                            mapG.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        }
                    }
                    mapG.animateCamera(CameraUpdateFactory.zoomTo(5));
                    mapG.getUiSettings().setZoomControlsEnabled(true);


                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                    99);

                        }
                    }

                    mapG.setMyLocationEnabled(true);
                    mapG.getUiSettings().setMyLocationButtonEnabled(true);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        TextView toolbarT = getActivity().findViewById(R.id.toolbar_title);
        toolbarT.setText("Mapa de alertas");
    }
}