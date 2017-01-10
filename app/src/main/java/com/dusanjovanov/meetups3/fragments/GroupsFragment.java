package com.dusanjovanov.meetups3.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dusanjovanov.meetups3.GroupActivity;
import com.dusanjovanov.meetups3.R;
import com.dusanjovanov.meetups3.adapters.GroupsRecyclerAdapter;
import com.dusanjovanov.meetups3.models.Group;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.rest.ApiClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by duca on 30/12/2016.
 */

public class GroupsFragment extends Fragment implements GroupsRecyclerAdapter.RowClickListener{

    public static final String TAG = "TagGroupsFragment";
    private RecyclerView rvGroups;
    private TextView txtNoGroups;
    private GroupsRecyclerAdapter adapter;
    private ArrayList<Group> groups = new ArrayList<>();
    private Context context;
    private User currentUser;
    private boolean updateData = false;

    public GroupsFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Bundle args = getArguments();
        if(args!=null){
            currentUser = (User) args.getSerializable("user");
        }
        adapter = new GroupsRecyclerAdapter(groups,context,currentUser,this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_groups,container,false);
        rvGroups = (RecyclerView) fragment.findViewById(R.id.rv_groups);
        rvGroups.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(context,2);
        rvGroups.setLayoutManager(layoutManager);
        GroupsRecyclerAdapter.SpacesItemDecoration itemDecoration = new GroupsRecyclerAdapter.SpacesItemDecoration(10);
        rvGroups.addItemDecoration(itemDecoration);
        txtNoGroups = (TextView) fragment.findViewById(R.id.txt_no_groups);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getGroups();
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
                getGroups();
            }
        }
    }

    private void getGroups(){
        Call<ArrayList<Group>> call = ApiClient.getApi().getGroups(currentUser.getId());
        call.enqueue(new Callback<ArrayList<Group>>() {
            @Override
            public void onResponse(Call<ArrayList<Group>> call, Response<ArrayList<Group>> response) {
                if(response.isSuccessful()){
                    Log.d(TAG,response.raw().toString());
                    groups.clear();
                    if(response.body().size()<1){
                        txtNoGroups.setVisibility(View.VISIBLE);
                    }
                    else{
                        txtNoGroups.setVisibility(View.GONE);
                        groups.addAll(response.body());
                        adapter.notifyDataSetChanged();
                    }
                }
                else{

                }
            }

            @Override
            public void onFailure(Call<ArrayList<Group>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onRowClick(Group group) {
        Intent intent = new Intent(context, GroupActivity.class);
        intent.putExtra("action", GroupsFragment.TAG);
        intent.putExtra("current_user",currentUser);
        intent.putExtra("group",group);
        startActivity(intent);
    }
}
