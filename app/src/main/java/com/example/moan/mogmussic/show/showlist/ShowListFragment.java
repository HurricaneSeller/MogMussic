package com.example.moan.mogmussic.show.showlist;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moan.mogmussic.R;

/**
 * A simple {@link Fragment} subclass.
 */
// TODO: 11/27/18 if click list song !!!!!!!!
public class ShowListFragment extends Fragment {


    public ShowListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_list, container, false);
    }

}
