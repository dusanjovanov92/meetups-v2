package com.dusanjovanov.meetups3;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.dusanjovanov.meetups3.adapters.UsersRecyclerAdapter;
import com.dusanjovanov.meetups3.dialogs.UserProfileDialog;
import com.dusanjovanov.meetups3.models.Group;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.rest.ApiClient;
import com.dusanjovanov.meetups3.util.InterfaceUtil;
import com.dusanjovanov.meetups3.util.NetworkUtil;

import java.io.Serializable;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dusanjovanov.meetups3.util.ConstantsUtil.EXTRA_ACTION;
import static com.dusanjovanov.meetups3.util.ConstantsUtil.EXTRA_CURRENT_USER;
import static com.dusanjovanov.meetups3.util.ConstantsUtil.EXTRA_GROUP;

public class SearchActivity extends AppCompatActivity implements InterfaceUtil.OnRowClickListener {

    public static final String TAG = "tagSearchActivity";

    private ArrayList<User> users = new ArrayList<>();
    private UsersRecyclerAdapter adapter;
    private TextView txtNoResults;
    private User currentUser;
    private Group group;
    private String intentAction = "action";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        handleIntent();

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
        adapter = new UsersRecyclerAdapter(this, users, this);
        rvSearchResults.setAdapter(adapter);
        txtNoResults = (TextView) findViewById(R.id.txt_no_results);

    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            intentAction = intent.getStringExtra(EXTRA_ACTION);

            if (intentAction.equals(MainScreenActivity.TAG)) {
                currentUser = (User) intent.getSerializableExtra(EXTRA_CURRENT_USER);
            }
            else if (intentAction.equals(GroupActivity.TAG)) {
                group = (Group) intent.getSerializableExtra(EXTRA_GROUP);
            }
        }

    }

    private void search(String query) {
        Call<ArrayList<User>> call = ApiClient.getApi().searchUsers(query);
        call.enqueue(new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(Call<ArrayList<User>> call, retrofit2.Response<ArrayList<User>> response) {
                if (response.isSuccessful()) {
                    users.clear();
                    if (response.body().size() < 1) {
                        txtNoResults.setVisibility(View.VISIBLE);
                    } else {
                        txtNoResults.setVisibility(View.GONE);
                        users.addAll(response.body());
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Server error");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<User>> call, Throwable t) {
                if (!NetworkUtil.isOnline(SearchActivity.this)) {

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

    @Override
    public void onRowClick(final Serializable serializable) {
        if (intentAction.equals(MainScreenActivity.TAG)) {
            UserProfileDialog dialog = UserProfileDialog.getInstance(currentUser, (User) serializable);
            dialog.show(getSupportFragmentManager(), "user_profile");
        }
        else if (intentAction.equals(GroupActivity.TAG)) {
            InterfaceUtil.showYesNoDialog(
                    this,
                    "Da li želite da dodate ovog korisnika u grupu?",
                    "Da",
                    "Otkaži",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            sendMemberRequest((User)serializable);
                        }
                    });
        }

    }

    private void sendMemberRequest(User user){
        Call<Integer> call = ApiClient.getApi().sendMemberRequest(group.getId(),user.getId());
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()){
                    switch (response.body()){
                        case 1:
                            Toast.makeText(SearchActivity.this, "Korisniku je poslat zahtev za učlanjenje", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(SearchActivity.this, "Korisniku je već poslat zahtev za učlanjenje", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            Toast.makeText(SearchActivity.this, "Korisnik je već član grupe", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
    }
}
