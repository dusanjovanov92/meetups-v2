package com.dusanjovanov.meetups3.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dusanjovanov.meetups3.GroupActivity;
import com.dusanjovanov.meetups3.R;
import com.dusanjovanov.meetups3.adapters.GroupsRecyclerAdapter;
import com.dusanjovanov.meetups3.models.Group;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.rest.ApiClient;
import com.dusanjovanov.meetups3.util.InterfaceUtil;

import java.io.Serializable;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by duca on 30/12/2016.
 */

public class GroupsFragment extends Fragment implements InterfaceUtil.OnRowClickListener {

    public static final String TAG = "TagGroupsFragment";
    private RecyclerView rvGroups;
    private GroupsRecyclerAdapter adapter;
    private ArrayList<Group> groups = new ArrayList<>();
    private FloatingActionButton fabCreateGroup;
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
        fabCreateGroup = (FloatingActionButton) fragment.findViewById(R.id.fab_create_group);
        fabCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Napravi grupu")
                        .setPositiveButton("Napravi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Dialog dialog = (Dialog)dialogInterface;
                                EditText edtName = (EditText) dialog.findViewById(R.id.edt);
                                createGroup(edtName.getText().toString(),currentUser.getId());
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("Otkaži", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                final EditText input = new EditText(context);
                input.setId(R.id.edt);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                input.setHint("Ime grupe");
                builder.setView(input);

                builder.create().show();
            }
        });
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
                    groups.addAll(response.body());
                    adapter.notifyDataSetChanged();
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
    public void onRowClick(Serializable serializable) {
        Intent intent = new Intent(context, GroupActivity.class);
        intent.putExtra("action", GroupsFragment.TAG);
        intent.putExtra("current_user",currentUser);
        intent.putExtra("group",serializable);
        startActivity(intent);
    }

    private void createGroup(final String name, int admin){
        Call<Void> call = ApiClient.getApi().createGroup(name,admin);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(context, "Grupa "+name+" je napravljena", Toast.LENGTH_SHORT).show();
                    getGroups();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}
