package com.dusanjovanov.meetups3.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dusanjovanov.meetups3.R;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.util.InterfaceUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by duca on 11/1/2017.
 */

public class MembersRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private ArrayList<User> members;
    private LayoutInflater layoutInflater;
    private User admin;

    public MembersRecyclerAdapter(Context context, ArrayList<User> members,User admin) {
        this.context = context;
        this.members = members;
        this.admin = admin;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==R.id.header){
            return new HeaderHolder(layoutInflater.inflate(R.layout.item_header,parent,false));
        }
        else{
            return new RowHolder(layoutInflater.inflate(R.layout.item_member,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderHolder){
            ((HeaderHolder) holder).bindModel("Članovi");
        }
        else{
            if(members.size()!=0){
                ((RowHolder)holder).bindModel(members.get(position-1));
            }
        }
    }

    @Override
    public int getItemCount() {
        return members.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return R.id.header;
        }
        else{
            return R.id.row;
        }
    }

    private class HeaderHolder extends RecyclerView.ViewHolder{

        private TextView txtHeader;

        HeaderHolder(View itemView) {
            super(itemView);
            txtHeader = (TextView) itemView.findViewById(R.id.txt_header);
        }

        void bindModel(String header){
            txtHeader.setText(header);
        }
    }

    private class RowHolder extends RecyclerView.ViewHolder{

        private ImageView ivProfileImage;
        private CircleImageView civProfileImage;
        private TextView txtDisplayName;
        private ImageView ivAdmin;

        RowHolder(View itemView) {
            super(itemView);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.iv_profile_image);
            civProfileImage = (CircleImageView) itemView.findViewById(R.id.civ_profile_image);
            txtDisplayName = (TextView) itemView.findViewById(R.id.txt_display_name);
            ivAdmin = (ImageView) itemView.findViewById(R.id.iv_admin);
        }

        void bindModel(User model){
            if(model.getPhotoUrl()==null){
                ivProfileImage.setImageDrawable(InterfaceUtil.getTextDrawable(model.getDisplayName()));
            }
            else{
                ivProfileImage.setVisibility(View.GONE);
                civProfileImage.setVisibility(View.VISIBLE);
                Picasso.with(context).load(model.getPhotoUrl()).into(civProfileImage);
            }

            txtDisplayName.setText(model.getDisplayName());

            if(model.getId()==admin.getId()){
                ivAdmin.setVisibility(View.VISIBLE);
                ivAdmin.setImageDrawable(InterfaceUtil.getTextDrawable("A"));
            }
        }
    }
}
