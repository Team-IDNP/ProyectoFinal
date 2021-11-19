package com.example.residuosapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private MapFragment map;
    private StatisticsFragment stat;


    private String mParam1;
    private String mParam2;
    private GoogleMap mapGoogle;
    private MapView mapV;
    private Button but;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        map = new MapFragment();
        stat = new StatisticsFragment();

        FragmentManager fM = getActivity().getSupportFragmentManager();
        fM.beginTransaction()
                .add(R.id.content_fragment, stat)
                .add(R.id.content_fragment, map)
                .hide(stat).commit();

        but = v.findViewById(R.id.btn_change);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(but.getText().equals("Ver estadisticas")){
                    fM.beginTransaction()
                            .hide(map)
                            .show(stat)
                            .commit();
                    but.setText("Ver mapa");
                    TextView toolbarT = getActivity().findViewById(R.id.toolbar_title);
                    toolbarT.setText("Estadisticas de alertas");

                }else{
                    fM.beginTransaction()
                            .hide(stat)
                            .show(map)
                            .commit();
                    but.setText("Ver estadisticas");
                    TextView toolbarT = getActivity().findViewById(R.id.toolbar_title);
                    toolbarT.setText("Mapa de alertas");
                }

            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*TextView toolbarT = getActivity().findViewById(R.id.toolbar_title);
        toolbarT.setText("Mapa de alertas");*/
    }

}