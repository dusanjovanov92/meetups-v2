package com.dusanjovanov.meetups3.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dusanjovanov.meetups3.R;
import com.dusanjovanov.meetups3.models.ContactRequest;
import com.dusanjovanov.meetups3.models.GroupUserRequest;
import com.dusanjovanov.meetups3.util.InterfaceUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by duca on 6/1/2017.
 */

public class HomeRecyclerAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<ContactRequest> contactRequests;
    private ArrayList<GroupUserRequest> groupUserRequests;

    public HomeRecyclerAdapter(Context context, ArrayList<ContactRequest> contactRequests, ArrayList<GroupUserRequest> groupUserRequests) {
        this.context = context;
        this.contactRequests = contactRequests;
        this.groupUserRequests = groupUserRequests;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(viewType==R.id.header){
            return new HeaderHolder(inflater.inflate(R.layout.item_home_header,parent,false));
        }
        else{
            return new RowHolder(inflater.inflate(R.layout.item_request,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderHolder){
            String header = null;
            if(position==0){
                header = "Contact requests";
            }
            else{
                header = "Group requests";
            }
            ((HeaderHolder) holder).bindHeader(header);
        }
        else{
            if(contactRequests.size()!=0){
                ((RowHolder)holder).bindContactRequest(contactRequests.get(position-1));
            }
        }
    }

    @Override
    public int getItemCount() {
        return contactRequests.size()+2;
    }

    @Override
    public int getItemViewType(int position) {
        int contactRequestsSize = contactRequests.size();

        if(position==0 || position == contactRequestsSize+1){
            return R.id.header;
        }
        else{
            return R.id.row;
        }
    }

    private class HeaderHolder extends RecyclerView.ViewHolder {

        private TextView txtHeader;

        public HeaderHolder(View itemView) {
            super(itemView);
            txtHeader = (TextView) itemView.findViewById(R.id.txt_header);
        }

        void bindHeader(String header){
            txtHeader.setText(header);
        }
    }

    private class RowHolder extends RecyclerView.ViewHolder {

        private ImageView ivProfileImage;
        private CircleImageView civProfileImage;
        private TextView txtDisplayName;

        public RowHolder(View itemView) {
            super(itemView);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.iv_profile_image);
            civProfileImage = (CircleImageView) itemView.findViewById(R.id.civ_profile_image);
            txtDisplayName = (TextView) itemView.findViewById(R.id.txt_display_name);
        }

        void bindContactRequest(ContactRequest model) {

            if (model.getUser().getPhotoUrl() == null) {
                ivProfileImage.setImageDrawable(InterfaceUtil.getTextDrawable(model.getUser().getDisplayName()));
            }
            else {
                ivProfileImage.setVisibility(View.GONE);
                civProfileImage.setVisibility(View.VISIBLE);
                Picasso.with(context).load(model.getUser().getPhotoUrl()).into(civProfileImage);
            }

            txtDisplayName.setText(model.getUser().getDisplayName());
        }
    }
}
