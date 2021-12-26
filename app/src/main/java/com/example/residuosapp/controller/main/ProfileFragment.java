package com.example.residuosapp.controller.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.residuosapp.controller.main.profile.DialogInformation;
import com.example.residuosapp.R;
import com.example.residuosapp.controller.main.profile.TabFragmentAdapter;
import com.example.residuosapp.model.Usuario;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    TabFragmentAdapter adapter;
    TextView dateCTV;

    private Usuario usuario;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        usuario = Usuario.Companion.get(requireActivity());

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        Button moreI = v.findViewById(R.id.btn_viewInformation);
        moreI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInformationProfile(v);
            }
        });
        dateCTV = v.findViewById(R.id.date_create_user);
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        Date creationDate = new Date(Long.parseLong(u.getMetadata().getCreationTimestamp()+""));
        SimpleDateFormat formatDate = new SimpleDateFormat("MMMM");
        String dateS = formatDate.format(creationDate);
        dateCTV.setText("Registrado desde "+creationDate.getDay()+" de "+dateS+" del "+(creationDate.getYear()+1900));



        tabLayout = v.findViewById(R.id.tab_layout);
        viewPager2 = v.findViewById(R.id.view_pager);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        adapter = new TabFragmentAdapter(fm, getLifecycle());
        viewPager2.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("Mapa").setIcon(R.drawable.ic_earth));
        tabLayout.addTab(tabLayout.newTab().setText("Lista").setIcon(R.drawable.ic_list));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        viewPager2.setUserInputEnabled(false);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        TextView toolbarT = getActivity().findViewById(R.id.toolbar_title);
        String nombreUsuario = usuario.getNombre();
        toolbarT.setText(nombreUsuario == null ? "Usuario" : nombreUsuario);
    }

    public void openInformationProfile(View v) {
        DialogFragment df = DialogInformation.newInstance();
        df.show(requireActivity().getSupportFragmentManager(), "dialog");
    }

}
