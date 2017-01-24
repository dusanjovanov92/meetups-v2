package com.dusanjovanov.meetups3.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dusanjovanov.meetups3.R;
import com.dusanjovanov.meetups3.models.ChatMessage;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.rest.ApiClient;
import com.dusanjovanov.meetups3.util.ConstantsUtil;
import com.dusanjovanov.meetups3.util.InterfaceUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

/**
 * Created by duca on 24/1/2017.
 */

public class UserProfileDialog extends DialogFragment {

    public static final String TAG = "TagUsrProDialog";

    public static final int NOTHING = 0;
    public static final int PENDING = 1;
    public static final int REQUEST = 2;
    public static final int CONTACT = 3;

    private User currentUser;
    private User user;
    private Context context;
    private int relationship;
    private DatabaseReference dbRef;

    private ImageButton ibExit;
    private ImageView ivProfileImage;
    private CircleImageView civProfileImage;
    private TextView txtDisplayName;
    private TextView txtEmail;
    private Button btnAddContact;
    private TextView txtPendingRequest;
    private Button btnAcceptRequest;

    public static UserProfileDialog getInstance(User currentUser,User user){
        UserProfileDialog dialog = new UserProfileDialog();
        Bundle args = new Bundle();
        args.putSerializable(ConstantsUtil.EXTRA_CURRENT_USER,currentUser);
        args.putSerializable("user",user);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if(getArguments()!=null){
            currentUser = (User) getArguments().getSerializable(ConstantsUtil.EXTRA_CURRENT_USER);
            user = (User) getArguments().getSerializable("user");
        }
        getRelationship();
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.dialog_user_profile,container,false);
        ibExit = (ImageButton) dialog.findViewById(R.id.btn_exit);
        ibExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        ivProfileImage = (ImageView) dialog.findViewById(R.id.iv_profile_image);
        civProfileImage = (CircleImageView) dialog.findViewById(R.id.civ_profile_image);

        if(user.getPhotoUrl()==null){
            ivProfileImage.setImageDrawable(InterfaceUtil.getTextDrawable(user.getDisplayName()));
        }
        else{
            ivProfileImage.setVisibility(GONE);
            civProfileImage.setVisibility(View.VISIBLE);
            Picasso.with(context).load(user.getPhotoUrl()).into(civProfileImage);
        }

        txtDisplayName = (TextView) dialog.findViewById(R.id.txt_display_name);

        txtDisplayName.setText(user.getDisplayName());

        txtEmail = (TextView) dialog.findViewById(R.id.txt_email);

        txtEmail.setText(user.getEmail());

        btnAddContact = (Button) dialog.findViewById(R.id.btn_add_contact);
        txtPendingRequest = (TextView) dialog.findViewById(R.id.txt_pending_request);
        btnAcceptRequest = (Button) dialog.findViewById(R.id.btn_accept);

        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendContactRequest();
            }
        });

        btnAcceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptRequest();
            }
        });

        return dialog;
    }

    private void sendContactRequest(){
        Call<Void> call = ApiClient.getApi().sendContactRequest(currentUser.getId(),user.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(context, "Zahtev za kontakt je poslat", Toast.LENGTH_SHORT).show();
                    getRelationship();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    private void acceptRequest(){
        ChatMessage message = new ChatMessage("a","a","a",1);
        String firebaseNode = dbRef.child("chat").push().getKey();
        dbRef.child("chat").child(firebaseNode).push().setValue(message);

        final User user = this.user;
        Call<Void> call = ApiClient.getApi().addToContacts(currentUser.getId(),user.getId(),firebaseNode);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(context, "Korisnik "+user.getDisplayName()+"je dodat u kontakte", Toast.LENGTH_SHORT).show();
                    getRelationship();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    private void getRelationship(){
        Call<String> call = ApiClient.getApi().getRelationship(currentUser.getId(),user.getId());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){

                    Gson gson = new Gson();

                    relationship = gson.fromJson(response.body(),int.class);

                    btnAddContact.setVisibility(GONE);
                    txtPendingRequest.setVisibility(GONE);
                    btnAcceptRequest.setVisibility(GONE);

                    switch (relationship){
                        case NOTHING:
                            btnAddContact.setVisibility(View.VISIBLE);
                            break;
                        case PENDING:
                            txtPendingRequest.setVisibility(View.VISIBLE);
                            break;
                        case REQUEST:
                            btnAcceptRequest.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
}
