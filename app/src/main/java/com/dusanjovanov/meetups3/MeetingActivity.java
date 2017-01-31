package com.dusanjovanov.meetups3;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dusanjovanov.meetups3.adapters.MeetingResponsesRecyclerAdapter;
import com.dusanjovanov.meetups3.decorations.HorizontalDividerItemDecoration;
import com.dusanjovanov.meetups3.fcm.MyFirebaseMessagingService;
import com.dusanjovanov.meetups3.fragments.GroupMeetingsFragment;
import com.dusanjovanov.meetups3.models.Group;
import com.dusanjovanov.meetups3.models.Meeting;
import com.dusanjovanov.meetups3.models.MeetingResponse;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.rest.ApiClient;
import com.dusanjovanov.meetups3.util.ConstantsUtil;
import com.dusanjovanov.meetups3.util.DateTimeUtil;
import com.dusanjovanov.meetups3.util.FirebaseUtil;
import com.dusanjovanov.meetups3.util.InterfaceUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeetingActivity extends AppCompatActivity {

    public static final String TAG = "TagMeetAct";
    private static final int ACTION_ENTER = 0;
    private static final int ACTION_CANCEL = 1;
    private TextView txtStartsIn;
    private LinearLayout llResponse;
    private ImageView ivResponseIcon;
    private RecyclerView rvResponses;
    private MeetingResponsesRecyclerAdapter adapter;
    private Group group;
    private Meeting meeting;
    private User currentUser;
    private List<MeetingResponse> meetingResponses = new ArrayList<>();
    private MeetingResponse yourResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);

        handleIntent();

        setupToolbar();

        initializeViews();

        setupRecyclerView();

        getData();

    }

    private void handleIntent() {
        Intent intent = getIntent();
        String action = null;
        if(intent!=null){
            action = intent.getStringExtra(ConstantsUtil.EXTRA_ACTION);
        }
        if(action!=null){
            if(action.equals(GroupMeetingsFragment.TAG)){
                currentUser = (User) intent.getSerializableExtra(ConstantsUtil.EXTRA_CURRENT_USER);
                group = (Group) intent.getSerializableExtra(ConstantsUtil.EXTRA_GROUP);
                meeting = (Meeting) intent.getSerializableExtra(ConstantsUtil.EXTRA_MEETING);
            }
            else if(action.equals(MyFirebaseMessagingService.TAG)){
                currentUser = (User) intent.getSerializableExtra(ConstantsUtil.EXTRA_CURRENT_USER);
                group = (Group) intent.getSerializableExtra(ConstantsUtil.EXTRA_GROUP);
                meeting = (Meeting) intent.getSerializableExtra(ConstantsUtil.EXTRA_MEETING);
            }
        }
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle(meeting.getLabel());
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeViews() {
        txtStartsIn = (TextView) findViewById(R.id.txt_starts_in);
        llResponse = (LinearLayout) findViewById(R.id.ll_response);
        ivResponseIcon = (ImageView) findViewById(R.id.iv_response_icon);
        rvResponses = (RecyclerView) findViewById(R.id.rv_responses);
    }

    private void setupViews(){
        txtStartsIn.setText(DateTimeUtil.getMeetingDateTime(meeting.getStartTime(),this));
        ivResponseIcon.setImageDrawable(InterfaceUtil.getMeetingResponseIcon(yourResponse==null ? 0 : yourResponse.getResponse(),this));
        llResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MeetingActivity.this)
                        .setSingleChoiceItems(R.array.response_choices, yourResponse==null? -1 : yourResponse.getResponse()-1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {
                                respondToMeeting(position+1);
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        });
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvResponses.setLayoutManager(layoutManager);
        HorizontalDividerItemDecoration itemDecoration = new HorizontalDividerItemDecoration(
                ResourcesCompat.getDrawable(getResources(), R.drawable.item_divider, null), true
        );
        rvResponses.addItemDecoration(itemDecoration);
        adapter = new MeetingResponsesRecyclerAdapter(this, meetingResponses);
        rvResponses.setAdapter(adapter);
    }

    private void getData() {
        Call<List<MeetingResponse>> call = ApiClient.getApi().getMeetingResponses(meeting.getId());
        call.enqueue(new Callback<List<MeetingResponse>>() {
            @Override
            public void onResponse(Call<List<MeetingResponse>> call, Response<List<MeetingResponse>> response) {
                if(response.isSuccessful()){
                    meetingResponses.clear();
                    meetingResponses.addAll(response.body());
                    for(MeetingResponse response1: meetingResponses){
                        if(response1.getUser().getId()==currentUser.getId()){
                            yourResponse = response1;
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                    setupViews();
                }
            }

            @Override
            public void onFailure(Call<List<MeetingResponse>> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(System.currentTimeMillis()>=(meeting.getStartTime()*1000)){
            MenuItem actionEnter = menu.add(Menu.NONE,ACTION_ENTER,Menu.NONE,"Enter");
            actionEnter.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            actionEnter.setIcon(R.drawable.ic_input_black_36dp);
        }

        MenuItem actionDelete = menu.add(Menu.NONE,ACTION_CANCEL,Menu.NONE,"Cancel/End");
        actionDelete.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        actionDelete.setIcon(R.drawable.ic_delete_black_36dp);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case ACTION_ENTER:
                Intent intent = new Intent(this,ChatActivity.class);
                intent.putExtra(ConstantsUtil.EXTRA_ACTION,TAG);
                intent.putExtra(ConstantsUtil.EXTRA_MEETING,meeting);
                intent.putExtra(ConstantsUtil.EXTRA_CURRENT_USER,currentUser);
                intent.putExtra(ConstantsUtil.EXTRA_GROUP,group);
                startActivity(intent);
                break;
            case ACTION_CANCEL:
                InterfaceUtil.showYesNoDialog(
                        this,
                        "Da li ste sigurni da hoćete da završite sastanak?",
                        "Da",
                        "Otkaži",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                deleteMeeting();
                            }
                        });
                break;
        }

        return true;
    }

    private void respondToMeeting(int response){
        Call<Void> call = ApiClient.getApi().updateMeetingResponse(meeting.getId(),currentUser.getId(),response);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    getData();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    private void deleteMeeting(){
        Call<Void> call = ApiClient.getApi().deleteMeeting(meeting.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    FirebaseUtil.deleteFirebaseNode("meetings",meeting.getFirebaseNode());
                    Toast.makeText(MeetingActivity.this, "Sastanak je završen", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}
