package com.dusanjovanov.meetups3.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dusanjovanov.meetups3.R;
import com.dusanjovanov.meetups3.models.Group;
import com.dusanjovanov.meetups3.models.Meeting;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.util.UserUtil;

/**
 * Created by duca on 10/1/2017.
 */

public class GroupInfoFragment extends Fragment {

    private Group group;
    private User currentUser;
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Bundle args = getArguments();
        if(args!=null){
            currentUser = (User) args.getSerializable(UserUtil.EXTRA_CURRENT_USER);
            group = (Group) args.getSerializable(UserUtil.EXTRA_GROUP);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_group_info,container,false);
        TextView txtMeetingsInProgress = (TextView) fragment.findViewById(R.id.txt_meetings_in_progress);
        TextView txtNextMeeting = (TextView) fragment.findViewById(R.id.txt_next_meeting);
        TextView txtNextMeetingLabel= (TextView) fragment.findViewById(R.id.txt_next_meeting_label);
        TextView txtNextMeetingTime = (TextView) fragment.findViewById(R.id.txt_next_meeting_time);
        RecyclerView rvMembers = (RecyclerView) fragment.findViewById(R.id.rv_members);

        int currentMeetingCount = group.getCurrentMeetingCount();
        if(currentMeetingCount==0){
            txtMeetingsInProgress.setVisibility(View.GONE);
        }
        else{
            txtMeetingsInProgress.setText(
                    String.format(context.getString(R.string.fragment_group_info_meetings_progress)
                            ,group.getCurrentMeetingCount()));
        }

        Meeting nextMeeting = group.getNextMeeting();
        if(nextMeeting!=null){
            txtNextMeetingLabel.setText(nextMeeting.getLabel());
            txtNextMeetingTime.setText(String.valueOf(nextMeeting.getStartTime()));
        }
        else{
            txtNextMeeting.setText("Nema novih sastanaka");
        }


        return fragment;
    }
}
