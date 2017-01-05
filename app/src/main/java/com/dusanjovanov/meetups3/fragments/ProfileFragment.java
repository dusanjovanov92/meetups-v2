package com.dusanjovanov.meetups3.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dusanjovanov.meetups3.R;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.util.InterfaceUtil;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by duca on 5/1/2017.
 */

public class ProfileFragment extends Fragment{

    private ImageView ivProfileImage;
    private CircleImageView civProfileImage;
    private TextView txtDisplayName;
    private User currentUser;
    private Context context;

    public ProfileFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Bundle args = getArguments();
        if(args!=null){
            currentUser = (User) args.getSerializable("user");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_profile,container,false);
        ivProfileImage = (ImageView) fragment.findViewById(R.id.iv_profile_image);
        civProfileImage = (CircleImageView) fragment.findViewById(R.id.civ_profile_image);
        txtDisplayName = (TextView) fragment.findViewById(R.id.txt_display_name);

        if(currentUser.getPhotoUrl()==null){
            ivProfileImage.setImageDrawable(InterfaceUtil.getTextDrawable(currentUser.getDisplayName()));
        }
        else{
            ivProfileImage.setVisibility(View.GONE);
            civProfileImage.setVisibility(View.VISIBLE);
            Picasso.with(context).load(currentUser.getPhotoUrl()).into(civProfileImage);
        }
        txtDisplayName.setText(currentUser.getDisplayName());

        return fragment;
    }
}
