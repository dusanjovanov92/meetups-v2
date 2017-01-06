package com.dusanjovanov.meetups3.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dusanjovanov.meetups3.R;
import com.dusanjovanov.meetups3.adapters.HomeRecyclerAdapter;
import com.dusanjovanov.meetups3.models.ContactRequest;
import com.dusanjovanov.meetups3.models.GroupUserRequest;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.rest.ApiClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by duca on 30/12/2016.
 */

public class HomeFragment extends Fragment {

    private static final String TAG = "TagHomeFragment";
    private TextView txtContactRequestsNum;
    private TextView txtGroupRequestsNum;
    private RecyclerView rvHome;
    private HomeRecyclerAdapter adapter;
    private ArrayList<ContactRequest> contactRequests = new ArrayList<>();
    private ArrayList<GroupUserRequest> groupUserRequests = new ArrayList<>();
    private User currentUser;
    private Context context;
    private boolean refreshDisplay = false;

    public HomeFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        adapter = new HomeRecyclerAdapter(context,contactRequests, groupUserRequests);
        Bundle args = getArguments();
        if(args!=null){
            currentUser = (User) args.getSerializable("user");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_home,container,false);
        txtContactRequestsNum = (TextView) fragment.findViewById(R.id.txt_contact_requests_number);
        txtGroupRequestsNum = (TextView) fragment.findViewById(R.id.txt_group_requests_number);
        rvHome = (RecyclerView) fragment.findViewById(R.id.rv_home);
        rvHome.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvHome.setLayoutManager(layoutManager);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getContactRequests();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshDisplay = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(isVisibleToUser){
            if(refreshDisplay){
                getContactRequests();
                getGroupRequests();
            }
        }
    }

    private void getContactRequests(){
        Call<ArrayList<ContactRequest>> call = ApiClient.getApi().getContactRequests(currentUser.getId());
        call.enqueue(new Callback<ArrayList<ContactRequest>>() {
            @Override
            public void onResponse(Call<ArrayList<ContactRequest>> call, Response<ArrayList<ContactRequest>> response) {
                if(response.isSuccessful()){
                    Log.d(TAG,response.raw().toString());
                    Log.d(TAG,String.valueOf(response.body().size()));
                    contactRequests.clear();
                    int responseSize = response.body().size();
                    txtContactRequestsNum.setText(String.valueOf(responseSize));
                    contactRequests.addAll(response.body());
                    adapter.notifyDataSetChanged();

                }
                else{

                }
            }

            @Override
            public void onFailure(Call<ArrayList<ContactRequest>> call, Throwable t) {

            }
        });
    }

    private void getGroupRequests(){
        Call<ArrayList<GroupUserRequest>> call = ApiClient.getApi().getGroupUserRequests(currentUser.getId());
        call.enqueue(new Callback<ArrayList<GroupUserRequest>>() {
            @Override
            public void onResponse(Call<ArrayList<GroupUserRequest>> call, Response<ArrayList<GroupUserRequest>> response) {
                if(response.isSuccessful()){
                    Log.d(TAG,response.raw().toString());
                    Log.d(TAG,String.valueOf(response.body().size()));
                    groupUserRequests.clear();
                    int responseSize = response.body().size();
                    txtGroupRequestsNum.setText(String.valueOf(responseSize));
                    groupUserRequests.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
                else{

                }
            }

            @Override
            public void onFailure(Call<ArrayList<GroupUserRequest>> call, Throwable t) {

            }
        });
    }
}
