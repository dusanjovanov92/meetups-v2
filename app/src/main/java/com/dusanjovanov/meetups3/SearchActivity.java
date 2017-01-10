package com.dusanjovanov.meetups3;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dusanjovanov.meetups3.adapters.UsersRecyclerAdapter;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.rest.ApiClient;
import com.dusanjovanov.meetups3.util.NetworkUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class SearchActivity extends AppCompatActivity {

    public static final String TAG = "tagSearchActivity";

    private ArrayList<User> users = new ArrayList<>();
    private UsersRecyclerAdapter adapter;
    private TextView txtNoResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar appBar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(appBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        SearchView searchView = (SearchView) findViewById(R.id.search);
        searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        RecyclerView rvSearchResults = (RecyclerView) findViewById(R.id.rv_search_results);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UsersRecyclerAdapter(this,users);
        rvSearchResults.setAdapter(adapter);
        txtNoResults = (TextView) findViewById(R.id.txt_no_results);

    }

    private void search(String query) {
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
                    Log.e(TAG,"Server error");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<User>> call, Throwable t) {
                if(!NetworkUtil.isOnline(SearchActivity.this)){

                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
