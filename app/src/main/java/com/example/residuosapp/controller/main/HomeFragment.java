package com.example.residuosapp.controller.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.residuosapp.R;
import com.example.residuosapp.controller.main.home.MapFragment;
import com.example.residuosapp.controller.main.home.StatisticsFragment;


public class HomeFragment extends Fragment {

    private MapFragment mapFragment;
    private StatisticsFragment statisticsFragment;
    private Button botonCambioSubFragment;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar layout
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        mapFragment = new MapFragment();
        statisticsFragment = new StatisticsFragment();

        FragmentManager fM = getActivity().getSupportFragmentManager();
        fM.beginTransaction()
                .add(R.id.content_fragment, statisticsFragment)
                .add(R.id.content_fragment, mapFragment)
                .hide(statisticsFragment)
                .commit();

        botonCambioSubFragment = v.findViewById(R.id.btn_change);
        botonCambioSubFragment.setOnClickListener(v1 -> {
            // Si actualmente se muestra el frament Statistics, cambiar a Map
            if (botonCambioSubFragment.getText().equals("Ver estadisticas")) {
                fM.beginTransaction()
                        .hide(mapFragment)
                        .show(statisticsFragment)
                        .commit();
                botonCambioSubFragment.setText("Ver mapa");
                TextView toolbarT = getActivity().findViewById(R.id.toolbar_title);
                toolbarT.setText("Estadisticas de alertas");
            }
            // Sino, cambiar de Map a Statistics
            else {
                fM.beginTransaction()
                        .hide(statisticsFragment)
                        .show(mapFragment)
                        .commit();
                botonCambioSubFragment.setText("Ver estadisticas");
                TextView toolbarT = getActivity().findViewById(R.id.toolbar_title);
                toolbarT.setText("Mapa de alertas");
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}