package com.dusanjovanov.meetups3.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dusanjovanov.meetups3.R;

/**
 * Created by duca on 30/12/2016.
 */

public class HomeFragment extends Fragment {

    private TextView txtContactRequestsNum;
    private TextView txtGroupRequestsNum;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_home,container,false);
        txtContactRequestsNum = (TextView) fragment.findViewById(R.id.txt_contact_requests_number);
        txtGroupRequestsNum = (TextView) fragment.findViewById(R.id.txt_group_requests_number);
        return fragment;
    }
}
