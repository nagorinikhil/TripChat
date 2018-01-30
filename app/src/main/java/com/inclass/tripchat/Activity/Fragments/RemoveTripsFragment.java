package com.inclass.tripchat.Activity.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inclass.tripchat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RemoveTripsFragment extends Fragment {


    public RemoveTripsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_remove_trips, container, false);
    }

}
