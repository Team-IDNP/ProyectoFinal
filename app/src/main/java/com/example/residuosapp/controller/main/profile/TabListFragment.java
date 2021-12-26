package com.example.residuosapp.controller.main.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.residuosapp.R;

public class TabListFragment extends Fragment {

    public TabListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {







        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_list, container, false);





    }
}