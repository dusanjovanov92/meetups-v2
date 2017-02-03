package com.dusanjovanov.meetups3.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dusanjovanov.meetups3.MeetingActivity;
import com.dusanjovanov.meetups3.R;
import com.dusanjovanov.meetups3.adapters.MeetingsRecyclerAdapter;
import com.dusanjovanov.meetups3.decorations.HorizontalDividerItemDecoration;
import com.dusanjovanov.meetups3.models.Group;
import com.dusanjovanov.meetups3.models.Meeting;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.rest.ApiClient;
import com.dusanjovanov.meetups3.rest.CustomDeserializer;
import com.dusanjovanov.meetups3.util.ConstantsUtil;
import com.dusanjovanov.meetups3.util.InterfaceUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by duca on 10/1/2017.
 */

public class GroupMeetingsFragment extends Fragment implements InterfaceUtil.OnRowClickListener {

    public static final String TAG = "GMFrag";
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
            currentUser = (User) args.getSerializable(ConstantsUtil.EXTRA_CURRENT_USER);
            group = (Group) args.getSerializable(ConstantsUtil.EXTRA_GROUP);
        }
        adapter = new MeetingsRecyclerAdapter(context,meetings,this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_group_meetings,container,false);
        rvMeetings = (RecyclerView) fragment.findViewById(R.id.rv_meetings);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvMeetings.setLayoutManager(layoutManager);
        HorizontalDividerItemDecoration decoration =
                new HorizontalDividerItemDecoration(ResourcesCompat.getDrawable(getResources(),R.drawable.item_divider,null),false);
        rvMeetings.addItemDecoration(decoration);
        rvMeetings.setAdapter(adapter);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
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
                Type type = new TypeToken<ArrayList<Meeting>>(){}.getType();

                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(type,new CustomDeserializer<>())
                        .create();

                List<Meeting> meetings = gson.fromJson(response.body(),type);
                GroupMeetingsFragment.this.meetings.clear();
                GroupMeetingsFragment.this.meetings.addAll(meetings);
                adapter.notifyDataSetChanged();
            }

            @Override
             public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    @Override
    public void onRowClick(Serializable serializable) {
        Intent intent = new Intent(context, MeetingActivity.class);
        intent.putExtra(ConstantsUtil.EXTRA_ACTION,TAG);
        intent.putExtra(ConstantsUtil.EXTRA_GROUP,group);
        intent.putExtra(ConstantsUtil.EXTRA_CURRENT_USER,currentUser);
        intent.putExtra(ConstantsUtil.EXTRA_MEETING,serializable);
        startActivity(intent);
    }
}
