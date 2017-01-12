package com.dusanjovanov.meetups3.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dusanjovanov.meetups3.ChatActivity;
import com.dusanjovanov.meetups3.R;
import com.dusanjovanov.meetups3.adapters.ContactsRecyclerAdapter;
import com.dusanjovanov.meetups3.decorations.HorizontalDividerItemDecoration;
import com.dusanjovanov.meetups3.models.Contact;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.rest.ApiClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by duca on 30/12/2016.
 */

public class ContactsFragment extends Fragment implements ContactsRecyclerAdapter.RowClickListener{

    public static final String TAG = "TagContactsFragment";
    private RecyclerView rvContacts;
    private TextView txtNoResults;
    private ContactsRecyclerAdapter adapter;
    private ArrayList<Contact> contacts = new ArrayList<>();
    private Context context;
    private User currentUser;
    private boolean updateData = false;

    public ContactsFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        adapter = new ContactsRecyclerAdapter(context,contacts,this);
        Bundle args = getArguments();
        if(args!=null){
            currentUser = (User) args.getSerializable("user");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_contacts,container,false);
        txtNoResults = (TextView) fragment.findViewById(R.id.txt_no_results);
        rvContacts = (RecyclerView) fragment.findViewById(R.id.rv_contacts);
        rvContacts.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvContacts.setLayoutManager(layoutManager);
        HorizontalDividerItemDecoration decoration =
                new HorizontalDividerItemDecoration(ResourcesCompat.getDrawable(getResources(),R.drawable.item_divider,null),false);
        rvContacts.addItemDecoration(decoration);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getContacts();
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
                getContacts();
            }
        }
    }

    private void getContacts(){
        Call<ArrayList<Contact>> call = ApiClient.getApi().getContacts(currentUser.getId());
        call.enqueue(new Callback<ArrayList<Contact>>() {
            @Override
            public void onResponse(Call<ArrayList<Contact>> call, Response<ArrayList<Contact>> response) {
                if(response.isSuccessful()){
                    Log.d(TAG,response.raw().toString());
                    contacts.clear();
                    if(response.body().size()<1){
                        txtNoResults.setVisibility(View.VISIBLE);
                    }
                    else{
                        txtNoResults.setVisibility(View.GONE);
                        contacts.addAll(response.body());
                        adapter.notifyDataSetChanged();
                    }
                }
                else{

                }
            }

            @Override
            public void onFailure(Call<ArrayList<Contact>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(Contact contact) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("action",TAG);
        intent.putExtra("user",currentUser);
        intent.putExtra("contact",contact);
        startActivity(intent);
    }
}
