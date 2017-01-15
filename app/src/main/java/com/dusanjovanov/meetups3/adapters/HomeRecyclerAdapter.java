package com.dusanjovanov.meetups3.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dusanjovanov.meetups3.R;
import com.dusanjovanov.meetups3.models.ContactRequest;
import com.dusanjovanov.meetups3.models.Group;
import com.dusanjovanov.meetups3.models.GroupRequest;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.rest.ApiClient;
import com.dusanjovanov.meetups3.util.InterfaceUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by duca on 6/1/2017.
 */

public class HomeRecyclerAdapter extends RecyclerView.Adapter {

    public static final String TAG = "HRecAdapter";
    private Context context;
    private List<ContactRequest> contactRequests;
    private List<GroupRequest> groupRequests;
    private User currentUser;

    public HomeRecyclerAdapter(Context context, List<ContactRequest> contactRequests, List<GroupRequest> groupRequests, User currentUser) {
        this.context = context;
        this.contactRequests = contactRequests;
        this.groupRequests = groupRequests;
        this.currentUser = currentUser;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (viewType == R.id.header) {
            return new InterfaceUtil.HeaderHolder(inflater.inflate(R.layout.item_header, parent, false));
        }
        else if(viewType == R.id.no_results){
            return new InterfaceUtil.NoResultsHolder(inflater.inflate(R.layout.item_no_results,parent,false));
        }
        else {
            return new RowHolder(inflater.inflate(R.layout.item_request, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int contactRequestsSize = contactRequests.size();
        int groupRequestsSize = groupRequests.size();

        if (holder instanceof InterfaceUtil.HeaderHolder) {
            String header = null;
            if (position == 0) {
                header = "Zahtevi za kontakt";
            }
            else{
                header = "Zahtevi za učlanjenje u grupu";
            }
            ((InterfaceUtil.HeaderHolder) holder).bindHeader(header);
        }
        else if(holder instanceof InterfaceUtil.NoResultsHolder){
            if(position==1){
                ((InterfaceUtil.NoResultsHolder)holder).bind("Nemate nijedan zahtev za kontakt trenutno");
            }
            else{
                ((InterfaceUtil.NoResultsHolder)holder).bind("Nemate nijedan zahtev za učlanjenje u grupu trenutno");
            }
        }
        else {
            if(position>=1 && position<=contactRequestsSize){
                ((RowHolder)holder).bindContactRequest(contactRequests.get(position-1));
            }
            else{
                ((RowHolder)holder).bindGroupRequest(groupRequests.get(position-2-(contactRequestsSize==0?1:contactRequestsSize)));
            }
        }
    }

    @Override
    public int getItemCount() {
        int contactRequestsSize = contactRequests.size();
        int groupRequestsSize = groupRequests.size();

        return (contactRequestsSize ==0 ? 1: contactRequestsSize) + (groupRequestsSize ==0 ? 1 : groupRequestsSize) + 2;
    }

    @Override
    public int getItemViewType(int position) {
        int contactRequestsSize = contactRequests.size();
        int groupRequestsSize = groupRequests.size();

        if (position == 0 || position == (contactRequestsSize==0 ? 1: contactRequestsSize) + 1) {
            return R.id.header;
        }
        else if((position==1 && contactRequestsSize==0) || (position== (contactRequestsSize==0?1:contactRequestsSize)+2 && groupRequestsSize==0)){
            return R.id.no_results;
        }
        else {
            return R.id.row;
        }
    }

    private class RowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivProfileImage;
        private CircleImageView civProfileImage;
        private TextView txtDisplayName;
        private ImageButton btnAccept, btnReject;


        public RowHolder(View itemView) {
            super(itemView);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.iv_profile_image);
            civProfileImage = (CircleImageView) itemView.findViewById(R.id.civ_profile_image);
            txtDisplayName = (TextView) itemView.findViewById(R.id.txt_display_name);
            btnAccept = (ImageButton) itemView.findViewById(R.id.btn_accept);
            btnReject = (ImageButton) itemView.findViewById(R.id.btn_reject);

            btnAccept.setOnClickListener(this);
            btnReject.setOnClickListener(this);
        }

        void bindContactRequest(ContactRequest model) {

            if (model.getUser().getPhotoUrl() == null) {
                ivProfileImage.setImageDrawable(InterfaceUtil.getTextDrawable(model.getUser().getDisplayName()));
            } else {
                ivProfileImage.setVisibility(View.GONE);
                civProfileImage.setVisibility(View.VISIBLE);
                Picasso.with(context).load(model.getUser().getPhotoUrl()).into(civProfileImage);
            }

            txtDisplayName.setText(model.getUser().getDisplayName());
        }

        void bindGroupRequest(GroupRequest model){
            ivProfileImage.setImageDrawable(InterfaceUtil.getTextDrawable(model.getGroup().getName()));

            txtDisplayName.setText(model.getGroup().getName());
        }


        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_accept:
                    acceptRequest(getAdapterPosition());
                    break;
                case R.id.btn_reject:
                    rejectRequest(getAdapterPosition());
                    break;
            }
        }
    }

    private void acceptRequest(final int adapterPosition) {

        final User sendingUser = contactRequests.get(adapterPosition - 1).getUser();
        Call<Void> call = ApiClient.getApi().addToContacts(currentUser.getId(), sendingUser.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Korisnik " + sendingUser.getDisplayName() + " je dodat u kontakte.", Toast.LENGTH_SHORT).show();
                    contactRequests.remove(adapterPosition - 1);
                    notifyItemRemoved(adapterPosition);
                    notifyDataSetChanged();
                } else {

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    private void rejectRequest(final int adapterPosition) {
        final int contactRequestsSize = contactRequests.size();

        if(adapterPosition>contactRequests.size()){
            Group group = groupRequests.get(adapterPosition-2-(contactRequestsSize==0?1:contactRequestsSize)).getGroup();
            Call<Void> call = ApiClient.getApi().deleteMemberRequest(group.getId(),currentUser.getId());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful()){
                        Toast.makeText(context, "Zahtev je obrisan.", Toast.LENGTH_SHORT).show();
                        groupRequests.remove(adapterPosition-2-(contactRequestsSize==0?1:contactRequestsSize));
                        notifyItemRemoved(adapterPosition);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
        }
        else{
            final User sendingUser = contactRequests.get(adapterPosition - 1).getUser();
            Call<Void> call = ApiClient.getApi().deleteContactRequest(currentUser.getId(), sendingUser.getId());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Zahtev je obrisan.", Toast.LENGTH_SHORT).show();
                        contactRequests.remove(adapterPosition - 1);
                        notifyItemRemoved(adapterPosition);
                    }
                    else {

                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
        }

    }


}
