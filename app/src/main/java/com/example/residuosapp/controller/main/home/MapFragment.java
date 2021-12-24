package com.example.residuosapp.controller.main.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.residuosapp.R;
import com.example.residuosapp.model.Marcador;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapFragment extends Fragment {

    private GoogleMap mapG;

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

        ArrayList<Marcador> marcadores = Marcador.Companion.getMarcadores();

        mapFragment.getMapAsync(googleMap -> {
            mapG = googleMap;
            for (Marcador m: marcadores) {
                MarkerOptions marcador = new MarkerOptions();
                marcador.position(new LatLng(m.getLatitud(), m.getLongitud()));
                mapG.addMarker(marcador);
            }
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