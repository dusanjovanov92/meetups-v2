package com.dusanjovanov.meetups3.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dusanjovanov.meetups3.R;
import com.dusanjovanov.meetups3.models.MeetingResponse;
import com.dusanjovanov.meetups3.util.InterfaceUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by duca on 14/1/2017.
 */

public class MeetingResponsesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private List<MeetingResponse> meetingResponses;
    private LayoutInflater inflater;

    public MeetingResponsesRecyclerAdapter(Context context, List<MeetingResponse> meetingResponses) {
        this.context = context;
        this.meetingResponses = meetingResponses;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == R.id.header){
            View row = inflater.inflate(R.layout.item_header,parent,false);
            return new HeaderHolder(row);
        }
        else{
            View row = inflater.inflate(R.layout.item_meeting_response,parent,false);
            return new RowHolder(row);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderHolder){
            ((HeaderHolder)holder).bindModel("Odgovori članova");
        }
        else{
            ((RowHolder)holder).bindModel(meetingResponses.get(position-1));
        }
    }

    @Override
    public int getItemCount() {
        return meetingResponses.size()+1;
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

        public HeaderHolder(View itemView) {
            super(itemView);
            txtHeader = (TextView) itemView.findViewById(R.id.txt_header);
        }

        void bindModel(String header){
            txtHeader.setText(header);
        }
    }

    public class RowHolder extends RecyclerView.ViewHolder{

        private ImageView ivProfileImage;
        private CircleImageView civProfileImage;
        private TextView txtDisplayName;
        private ImageView ivResponseIcon;

        public RowHolder(View itemView) {
            super(itemView);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.iv_profile_image);
            civProfileImage = (CircleImageView) itemView.findViewById(R.id.civ_profile_image);
            txtDisplayName = (TextView) itemView.findViewById(R.id.txt_display_name);
            ivResponseIcon = (ImageView) itemView.findViewById(R.id.iv_response_icon);
        }

        void bindModel(MeetingResponse model){
            if(model.getUser().getPhotoUrl()==null){
                ivProfileImage.setImageDrawable(InterfaceUtil.getTextDrawable(model.getUser().getDisplayName()));
            }
            else{
                ivProfileImage.setVisibility(View.GONE);
                civProfileImage.setVisibility(View.VISIBLE);
                Picasso.with(context).load(model.getUser().getPhotoUrl()).into(civProfileImage);
            }

            txtDisplayName.setText(model.getUser().getDisplayName());

            ivResponseIcon.setImageDrawable(InterfaceUtil.getMeetingResponseIcon(model.getResponse(),context));
        }
    }
}
