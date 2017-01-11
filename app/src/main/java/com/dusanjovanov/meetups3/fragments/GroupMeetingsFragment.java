package com.dusanjovanov.meetups3.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dusanjovanov.meetups3.R;
import com.dusanjovanov.meetups3.adapters.MeetingsRecyclerAdapter;
import com.dusanjovanov.meetups3.models.Group;
import com.dusanjovanov.meetups3.models.Meeting;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.rest.ApiClient;
import com.dusanjovanov.meetups3.util.UserUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by duca on 10/1/2017.
 */

public class GroupMeetingsFragment extends Fragment {

    private Group group;
    private User currentUser;
    private Context context;
    private boolean updateData = false;
    private RecyclerView rvMeetings;
    private MeetingsRecyclerAdapter adapter;
    private ArrayList<Meeting> meetings = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Bundle args = getArguments();
        if(args!=null){
            currentUser = (User) args.getSerializable(UserUtil.EXTRA_CURRENT_USER);
            group = (Group) args.getSerializable(UserUtil.EXTRA_GROUP);
        }
        adapter = new MeetingsRecyclerAdapter(context,meetings);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_group_meetings,container,false);
        rvMeetings = (RecyclerView) fragment.findViewById(R.id.rv_meetings);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvMeetings.setLayoutManager(layoutManager);
        rvMeetings.setAdapter(adapter);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateData = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(isVisibleToUser){
            if(updateData){
                getData();
            }
        }
    }

    private void getData(){
        Call<String> call = ApiClient.getApi().getGroupMeetings(group.getId());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<Meeting>>(){}.getType();
                ArrayList<Meeting> meetings = gson.fromJson(response.body(),type);
                if(meetings.size()==0){

                }
                else{
                    GroupMeetingsFragment.this.meetings.clear();
                    GroupMeetingsFragment.this.meetings.addAll(meetings);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

}
