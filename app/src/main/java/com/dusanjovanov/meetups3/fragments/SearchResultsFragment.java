package com.dusanjovanov.meetups3.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dusanjovanov.meetups3.R;
import com.dusanjovanov.meetups3.adapters.UsersRecyclerAdapter;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.rest.ApiClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by duca on 1/1/2017.
 */

public class SearchResultsFragment extends Fragment {

    private ArrayList<User> users = new ArrayList<>();
    private UsersRecyclerAdapter adapter;
    private TextView txtNoResults;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search,container,false);
    }

    public void search(String query) {
        Call<ArrayList<User>> call = ApiClient.getApi().searchUsers(query);
        call.enqueue(new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(Call<ArrayList<User>> call, retrofit2.Response<ArrayList<User>> response) {
                if(response.isSuccessful()){
                    users.clear();
                    if(response.body().size()<1){
                        txtNoResults.setVisibility(View.VISIBLE);
                    }
                    else{
                        txtNoResults.setVisibility(View.GONE);
                        users.addAll(response.body());
                    }
                    adapter.notifyDataSetChanged();
                }
                else{
                    Log.e("asd","Server error");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<User>> call, Throwable t) {

            }
        });

    }
}
