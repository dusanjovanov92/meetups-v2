package com.dusanjovanov.meetups3.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dusanjovanov.meetups3.R;
import com.dusanjovanov.meetups3.models.ContactRequest;
import com.dusanjovanov.meetups3.models.GroupRequest;
import com.dusanjovanov.meetups3.util.InterfaceUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by duca on 6/1/2017.
 */

public class HomeRecyclerAdapter extends RecyclerView.Adapter {

    public static final String TAG = "HRecAdapter";
    private Context context;
    private List<ContactRequest> contactRequests;
    private List<GroupRequest> groupRequests;
    private OnRowClickListener listener;

    public interface OnRowClickListener{
        void onContactRequestAcceptClick(ContactRequest request,int adapterPosition);
        void onContactRequestRejectClick(ContactRequest request,int adapterPosition);
        void onGroupRequestAcceptClick(GroupRequest request,int adapterPosition);
        void onGroupRequestRejectClick(GroupRequest request,int adapterPosition);
    }

    public HomeRecyclerAdapter(Context context, List<ContactRequest> contactRequests, List<GroupRequest> groupRequests,
                               HomeRecyclerAdapter.OnRowClickListener listener) {
        this.context = context;
        this.contactRequests = contactRequests;
        this.groupRequests = groupRequests;
        this.listener = listener;
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


        RowHolder(View itemView) {
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
            final int contactRequestsSize = contactRequests.size();
            switch (view.getId()) {
                case R.id.btn_accept:
                    if(getAdapterPosition()>contactRequests.size()){
                        GroupRequest request = groupRequests.get(getAdapterPosition()-2-(contactRequestsSize==0?1:contactRequestsSize));
                        listener.onGroupRequestAcceptClick(request,getAdapterPosition());
                    }
                    else{
                        ContactRequest request = contactRequests.get(getAdapterPosition() - 1);
                        listener.onContactRequestAcceptClick(request,getAdapterPosition());
                    }
                    break;
                case R.id.btn_reject:
                    if(getAdapterPosition()>contactRequests.size()) {
                        GroupRequest request = groupRequests.get(getAdapterPosition()-2-(contactRequestsSize==0?1:contactRequestsSize));
                        listener.onGroupRequestRejectClick(request,getAdapterPosition());
                    }
                    else{
                        ContactRequest request = contactRequests.get(getAdapterPosition() - 1);
                        listener.onContactRequestRejectClick(request,getAdapterPosition());
                    }
                    break;
            }
        }
    }

}
