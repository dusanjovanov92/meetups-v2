package com.dusanjovanov.meetups3.adapters;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dusanjovanov.meetups3.R;
import com.dusanjovanov.meetups3.models.Meeting;
import com.dusanjovanov.meetups3.util.DateTimeUtil;
import com.dusanjovanov.meetups3.util.InterfaceUtil;

import java.util.ArrayList;

/**
 * Created by duca on 11/1/2017.
 */

public class MeetingsRecyclerAdapter extends RecyclerView.Adapter<MeetingsRecyclerAdapter.RowHolder>{

    private Context context;
    private ArrayList<Meeting> meetings;
    private LayoutInflater layoutInflater;
    private InterfaceUtil.OnRowClickListener listener;


    public MeetingsRecyclerAdapter(Context context, ArrayList<Meeting> meetings,InterfaceUtil.OnRowClickListener listener) {
        this.context = context;
        this.meetings = meetings;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listener = listener;
    }

    @Override
    public RowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowHolder holder = new RowHolder(layoutInflater.inflate(R.layout.item_meeting,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RowHolder holder, int position) {
        holder.bindModel(meetings.get(position));
    }

    @Override
    public int getItemCount() {
        return meetings.size();
    }

    class RowHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtLabel;
        TextView txtTime;
        ImageView ivIcon;

        public RowHolder(View itemView) {
            super(itemView);
            txtLabel = (TextView) itemView.findViewById(R.id.txt_label);
            txtTime = (TextView) itemView.findViewById(R.id.txt_time);
            ivIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
            itemView.setOnClickListener(this);
        }

        void bindModel(Meeting model){
            txtLabel.setText(model.getLabel());
            txtTime.setText(new DateTimeUtil(context).getMeetingTime(model.getStartTime()));
            int icon = R.drawable.ic_event_black_36dp;

            switch (DateTimeUtil.getProximity(model.getStartTime()*1000)){
                case DateTimeUtil.THIS_HOUR_BEFORE:
                case DateTimeUtil.TODAY_BEFORE:
                case DateTimeUtil.YESTERDAY:
                case DateTimeUtil.LAST_WEEK:
                case DateTimeUtil.BEFORE:
                    icon = R.drawable.ic_event_green_36dp;
                    break;
                case DateTimeUtil.THIS_HOUR_AFTER:
                case DateTimeUtil.TODAY_AFTER:
                case DateTimeUtil.TOMORROW:
                    icon = R.drawable.ic_event_blue_36dp;
                    break;
                case DateTimeUtil.THIS_WEEK:
                case DateTimeUtil.FAR:
                    icon = R.drawable.ic_event_black_36dp;
                    break;
            }
            ivIcon.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),icon,null));
        }

        @Override
        public void onClick(View view) {
            listener.onRowClick(meetings.get(getAdapterPosition()));
        }
    }
}
