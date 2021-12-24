package com.example.residuosapp.controller.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.residuosapp.controller.main.profile.ProfileInformationActivity;
import com.example.residuosapp.R;
import com.example.residuosapp.controller.main.profile.TabFragmentAdapter;
import com.google.android.material.tabs.TabLayout;

public class ProfileFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    TabFragmentAdapter adapter;

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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        Button moreI = v.findViewById(R.id.btn_viewInformation);
        moreI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInformationProfile(v);
            }
        });


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
        toolbarT.setText("Michael Jordan");
    }

    public void openInformationProfile(View v) {
        // TODO: Cambiar a Fragment
        Intent intent = new Intent(getActivity(), ProfileInformationActivity.class);
        startActivity(intent);
    }

}
