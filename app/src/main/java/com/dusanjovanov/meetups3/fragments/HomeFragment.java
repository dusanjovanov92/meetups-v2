package com.dusanjovanov.meetups3.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dusanjovanov.meetups3.R;
import com.dusanjovanov.meetups3.adapters.HomeRecyclerAdapter;
import com.dusanjovanov.meetups3.models.ChatMessage;
import com.dusanjovanov.meetups3.models.ContactRequest;
import com.dusanjovanov.meetups3.models.GroupRequest;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.rest.ApiClient;
import com.dusanjovanov.meetups3.rest.CustomDeserializer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by duca on 30/12/2016.
 */

public class HomeFragment extends Fragment implements HomeRecyclerAdapter.OnRowClickListener{

    private static final String TAG = "TagHomeFragment";
    private TextView txtContactRequestsNum;
    private RecyclerView rvHome;
    private HomeRecyclerAdapter adapter;
    private List<ContactRequest> contactRequests = new ArrayList<>();
    private List<GroupRequest> groupRequests = new ArrayList<>();
    private User currentUser;
    private Context context;
    private boolean refreshDisplay = false;
    private DatabaseReference dbRef;

    public HomeFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Bundle args = getArguments();
        if(args!=null){
            currentUser = (User) args.getSerializable("user");
        }
        adapter = new HomeRecyclerAdapter(context,contactRequests,groupRequests,this);
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_home,container,false);
        txtContactRequestsNum = (TextView) fragment.findViewById(R.id.txt_contact_requests_number);
        rvHome = (RecyclerView) fragment.findViewById(R.id.rv_home);
        rvHome.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvHome.setLayoutManager(layoutManager);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getRequests();
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
                getRequests();
            }
        }
    }

    private void getRequests(){
        Call<String> call = ApiClient.getApi().getRequests(currentUser.getId());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    Type typeCR = new TypeToken<ArrayList<ContactRequest>>(){}.getType();
                    Type typeGR = new TypeToken<ArrayList<GroupRequest>>(){}.getType();

                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(typeCR,new CustomDeserializer<>("contactRequests"))
                            .registerTypeAdapter(typeGR,new CustomDeserializer<>("groupRequests"))
                            .create();

                    List<ContactRequest> contactRequestsGson = gson.fromJson(response.body(),typeCR);
                    List<GroupRequest> groupRequestsGson = gson.fromJson(response.body(),typeGR);

                    contactRequests.clear();
                    contactRequests.addAll(contactRequestsGson);
                    groupRequests.clear();
                    groupRequests.addAll(groupRequestsGson);
                    adapter.notifyDataSetChanged();
                    txtContactRequestsNum.setText(String.valueOf(contactRequests.size()));
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }


    @Override
    public void onContactRequestAcceptClick(final ContactRequest request, final int adapterPosition) {
        ChatMessage message = new ChatMessage("a","a","a",1);
        String firebaseNode = dbRef.child("chat").push().getKey();
        dbRef.child("chat").child(firebaseNode).push().setValue(message);

        Call<Void> call = ApiClient.getApi().addToContacts(currentUser.getId(),request.getUser().getId(),firebaseNode);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(context, "Korisnik "+request.getUser().getDisplayName()+ " je dodat u kontakte",
                            Toast.LENGTH_SHORT).show();
                    contactRequests.remove(adapterPosition-1);
                    adapter.notifyItemRemoved(adapterPosition);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    @Override
    public void onContactRequestRejectClick(ContactRequest request, final int adapterPosition) {
        Call<Void> call = ApiClient.getApi().deleteContactRequest(currentUser.getId(),request.getUser().getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(context, "Zahtev je obrisan", Toast.LENGTH_SHORT).show();
                    contactRequests.remove(adapterPosition-1);
                    adapter.notifyItemRemoved(adapterPosition);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    @Override
    public void onGroupRequestAcceptClick(final GroupRequest request, final int adapterPosition) {
        Call<Void> call = ApiClient.getApi().addMember(request.getGroup().getId(),currentUser.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(context, "Postali ste član grupe "+request.getGroup().getName(), Toast.LENGTH_SHORT).show();
                    groupRequests.remove(adapterPosition-2-(contactRequests.size()==0?1:contactRequests.size()));
                    adapter.notifyItemRemoved(adapterPosition);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    @Override
    public void onGroupRequestRejectClick(GroupRequest request, final int adapterPosition) {
        Call<Void> call = ApiClient.getApi().deleteMemberRequest(request.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(context, "Zahtev je obrisan", Toast.LENGTH_SHORT).show();
                    groupRequests.remove(adapterPosition-2-(contactRequests.size()==0?1:contactRequests.size()));
                    adapter.notifyItemRemoved(adapterPosition);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}
