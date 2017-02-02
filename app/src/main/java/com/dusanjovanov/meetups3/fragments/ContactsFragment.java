package com.dusanjovanov.meetups3.fragments;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.dusanjovanov.meetups3.ChatActivity;
import com.dusanjovanov.meetups3.R;
import com.dusanjovanov.meetups3.adapters.ContactsRecyclerAdapter;
import com.dusanjovanov.meetups3.decorations.HorizontalDividerItemDecoration;
import com.dusanjovanov.meetups3.models.Contact;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.rest.ApiClient;
import com.dusanjovanov.meetups3.util.FirebaseUtil;
import com.dusanjovanov.meetups3.util.InterfaceUtil;

import java.io.Serializable;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by duca on 30/12/2016.
 */

public class ContactsFragment extends Fragment implements ContactsRecyclerAdapter.OnRowClickListener {

    public static final String TAG = "TagContactsFragment";
    private RecyclerView rvContacts;
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
        adapter = new ContactsRecyclerAdapter(context,contacts);
        adapter.setOnRowClickListener(this);
        Bundle args = getArguments();
        if(args!=null){
            currentUser = (User) args.getSerializable("user");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_contacts,container,false);
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
                    contacts.clear();
                    contacts.addAll(response.body());
                    adapter.notifyDataSetChanged();
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
    public void onRowClick(Serializable serializable) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("action",TAG);
        intent.putExtra("user",currentUser);
        intent.putExtra("contact",serializable);
        startActivity(intent);
    }

    @Override
    public void onClickDelete(final Contact contact, final int adapterPosition) {
        InterfaceUtil.showYesNoDialog(context,
                "Da li ste sigurni da hoćete da obrišete ovaj kontakt?",
                "Da",
                "Otkaži",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteContact(contact,adapterPosition);
                    }
                });
    }

    private void deleteContact(final Contact contact, final int adapterPosition){
        Call<Void> call = ApiClient.getApi().deleteContact(currentUser.getId(),contact.getUser().getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(context, "Kontakt je obrisan", Toast.LENGTH_SHORT).show();
                    contacts.remove(adapterPosition);
                    adapter.notifyItemRemoved(adapterPosition);
                    adapter.notifyDataSetChanged();
                    FirebaseUtil.deleteChatNodes(currentUser.getId(),contact.getUser().getId(),contact.getFirebaseNode());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }
}
