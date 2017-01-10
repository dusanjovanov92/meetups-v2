package com.dusanjovanov.meetups3.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dusanjovanov.meetups3.R;

/**
 * Created by duca on 10/1/2017.
 */

public class GroupMeetingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_group_meetings,container,false);
        return fragment;
    }
}
