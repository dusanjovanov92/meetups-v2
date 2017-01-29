package com.dusanjovanov.meetups3.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dusanjovanov.meetups3.R;
import com.dusanjovanov.meetups3.adapters.MembersRecyclerAdapter;
import com.dusanjovanov.meetups3.decorations.HorizontalDividerItemDecoration;
import com.dusanjovanov.meetups3.models.Group;
import com.dusanjovanov.meetups3.models.Meeting;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.rest.ApiClient;
import com.dusanjovanov.meetups3.util.ConstantsUtil;
import com.dusanjovanov.meetups3.util.DateTimeUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by duca on 10/1/2017.
 */

public class GroupInfoFragment extends Fragment {

    public static final String TAG = "TagGIFragment";
    private Group group;
    private User currentUser;
    private Context context;
    private boolean updateData = false;
    TextView txtMeetingsInProgress;
    TextView txtNextMeeting;
    TextView txtNextMeetingLabel;
    TextView txtNextMeetingTime;
    RecyclerView rvMembers;
    private ArrayList<User> members = new ArrayList<>();
    private MembersRecyclerAdapter adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Bundle args = getArguments();
        if(args!=null){
            currentUser = (User) args.getSerializable(ConstantsUtil.EXTRA_CURRENT_USER);
            group = (Group) args.getSerializable(ConstantsUtil.EXTRA_GROUP);
        }
        adapter = new MembersRecyclerAdapter(context,members,group.getAdmin());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_group_info,container,false);
        txtMeetingsInProgress = (TextView) fragment.findViewById(R.id.txt_meetings_in_progress);
        txtNextMeeting = (TextView) fragment.findViewById(R.id.txt_next_meeting);
        txtNextMeetingLabel= (TextView) fragment.findViewById(R.id.txt_next_meeting_label);
        txtNextMeetingTime = (TextView) fragment.findViewById(R.id.txt_next_meeting_time);
        rvMembers = (RecyclerView) fragment.findViewById(R.id.rv_members);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvMembers.setLayoutManager(layoutManager);
        HorizontalDividerItemDecoration decoration =
                new HorizontalDividerItemDecoration(ResourcesCompat.getDrawable(getResources(),R.drawable.item_divider,null),true);
        rvMembers.addItemDecoration(decoration);
        rvMembers.setAdapter(adapter);

        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData();
    }

    private void setupViews(){
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
            txtNextMeetingTime.setText(DateTimeUtil.getMeetingDateTime(nextMeeting.getStartTime(),context));
            txtNextMeeting.setText("Sledeći sastanak");
        }
        else{
            txtNextMeeting.setText("Nema novih sastanaka");
            txtNextMeetingLabel.setVisibility(View.INVISIBLE);
            txtNextMeetingTime.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        updateData = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(isVisibleToUser){
            if(updateData){
                getData();
            }
        }
    }

    private void getData(){
        Call<Group> call = ApiClient.getApi().getGroup(group.getId());
        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if(response.isSuccessful()){
                    group = response.body();
                    setupViews();
                    members.clear();
                    members.addAll(group.getMembers());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {

            }
        });
    }
}
