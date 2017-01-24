package com.dusanjovanov.meetups3.adapters;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
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
 * Created by duca on 30/12/2016.
 */

public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.UserViewHolder>{

    private AppCompatActivity activity;
    private ArrayList<User> users;
    private InterfaceUtil.OnRowClickListener listener;

    public UsersRecyclerAdapter(AppCompatActivity activity,ArrayList<User> users,InterfaceUtil.OnRowClickListener listener) {
        this.activity = activity;
        this.users = users;
        this.listener = listener;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserViewHolder(activity.getLayoutInflater().inflate(R.layout.item_user_search,parent,false));
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.bindModel(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView ivProfileImage;
        private CircleImageView civProfileImage;
        private TextView txtDisplayName;

        public UserViewHolder(View itemView) {
            super(itemView);
            this.ivProfileImage = (ImageView) itemView.findViewById(R.id.iv_profile_image);
            this.civProfileImage = (CircleImageView) itemView.findViewById(R.id.civ_profile_image);
            this.txtDisplayName = (TextView) itemView.findViewById(R.id.txt_display_name);
            itemView.setOnClickListener(this);
        }

        void bindModel(User user){
            if(user.getPhotoUrl()==null){
                ivProfileImage.setImageDrawable(InterfaceUtil.getTextDrawable(user.getDisplayName()));
            }
            else{
                ivProfileImage.setVisibility(View.GONE);
                civProfileImage.setVisibility(View.VISIBLE);
                Picasso.with(activity).load(user.getPhotoUrl()).into(civProfileImage);
            }

            txtDisplayName.setText(user.getDisplayName());
        }

        @Override
        public void onClick(View view) {
            listener.onRowClick(users.get(getAdapterPosition()));
        }
    }
}
