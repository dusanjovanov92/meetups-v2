package com.dusanjovanov.meetups3;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.util.InterfaceUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchActivity extends AppCompatActivity {

    public static final String TAG = "tagSearchActivity";

    private RecyclerView rvSearchResults;
    private ArrayList<User> users = new ArrayList<>();
    private RecyclerView.Adapter<UserViewHolder> adapter;
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

        rvSearchResults = (RecyclerView) findViewById(R.id.rv_search_results);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerView.Adapter<UserViewHolder>() {

            @Override
            public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new UserViewHolder(getLayoutInflater().inflate(R.layout.item_user_search,parent,false));
            }

            @Override
            public void onBindViewHolder(UserViewHolder holder, int position) {
                holder.bindModel(users.get(position));
            }

            @Override
            public int getItemCount() {
                return users.size();
            }
        };
        rvSearchResults.setAdapter(adapter);
        txtNoResults = (TextView) findViewById(R.id.txt_no_results);

    }

    class UserViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivProfileImage;
        private CircleImageView civProfileImage;
        private TextView txtDisplayName;

        public UserViewHolder(View itemView) {
            super(itemView);
            this.ivProfileImage = (ImageView) itemView.findViewById(R.id.iv_profile_image);
            this.civProfileImage = (CircleImageView) itemView.findViewById(R.id.civ_profile_image);
            this.txtDisplayName = (TextView) itemView.findViewById(R.id.txt_display_name);
        }

        void bindModel(User user){
            if(user.getPhotoUrl()==null){
                ivProfileImage.setImageDrawable(InterfaceUtil.getTextDrawable(user.getDisplayName()));
            }
            else{
                ivProfileImage.setVisibility(View.GONE);
                civProfileImage.setVisibility(View.VISIBLE);
                Picasso.with(SearchActivity.this).load(user.getPhotoUrl()).into(civProfileImage);
            }

            txtDisplayName.setText(user.getDisplayName());
        }
    }

    private void search(String query) {
        DataRequest<User[]> request = new DataRequest<>(
                "/users/search/" + query,
                User[].class,
                new DataRequest.ResponseListener<User[]>() {
                    @Override
                    public void onResponse(User[] response) {
                        users.clear();
                        if(response.length<1){
                            txtNoResults.setVisibility(View.VISIBLE);
                        }
                        else{
                            txtNoResults.setVisibility(View.GONE);
                            users.addAll(Arrays.asList(response));
                        }
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        VolleyHandler.getInstance(this).addToRequestQueue(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setIconifiedByDefault(false);
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
