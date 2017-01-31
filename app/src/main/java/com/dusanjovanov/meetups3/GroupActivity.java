package com.dusanjovanov.meetups3;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dusanjovanov.meetups3.fragments.GroupInfoFragment;
import com.dusanjovanov.meetups3.fragments.GroupMeetingsFragment;
import com.dusanjovanov.meetups3.fragments.GroupsFragment;
import com.dusanjovanov.meetups3.models.Group;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.rest.ApiClient;
import com.dusanjovanov.meetups3.util.ConstantsUtil;
import com.dusanjovanov.meetups3.util.InterfaceUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupActivity extends AppCompatActivity {

    public static final String TAG = "TagGrAct";
    private ViewPager pager;
    private TabLayout tabs;
    private GroupPagerAdapter pagerAdapter;
    private Group group;
    private User currentUser;
    private FloatingActionButton fabScheduleMeeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        handleIntent();

        setupToolbar();

        setupViewPager();

        setupViews();

    }

    private void handleIntent(){
        Intent intent = getIntent();
        String action = null;
        if(intent!=null){
            action = intent.getStringExtra("action");
        }
        if(action!=null){
            if(action.equals(GroupsFragment.TAG)){
                currentUser = (User) intent.getSerializableExtra("current_user");
                group = (Group) intent.getSerializableExtra("group");
            }
        }
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle(group.getName());
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupViewPager(){
        pager = (ViewPager) findViewById(R.id.pager);
        tabs = (TabLayout) findViewById(R.id.tabs);
        pagerAdapter = new GroupPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        tabs.setupWithViewPager(pager);
        tabs.getTabAt(0).setText("info");
        tabs.getTabAt(1).setText("meetings");
    }

    private void setupViews(){
        fabScheduleMeeting = (FloatingActionButton) findViewById(R.id.fab_schedule_meeting);
        if(group.getAdmin().getId()!=currentUser.getId()){
            fabScheduleMeeting.setVisibility(View.GONE);
        }
        else{
            fabScheduleMeeting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(GroupActivity.this,ScheduleMeetingActivity.class);
                    intent.putExtra(ConstantsUtil.EXTRA_ACTION,TAG);
                    intent.putExtra(ConstantsUtil.EXTRA_GROUP,group);
                    intent.putExtra(ConstantsUtil.EXTRA_CURRENT_USER,currentUser);
                    startActivity(intent);
                }
            });
        }
    }

    private class GroupPagerAdapter extends FragmentPagerAdapter{

        public GroupPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    GroupInfoFragment groupInfoFragment = new GroupInfoFragment();
                    Bundle args1 = new Bundle();
                    args1.putSerializable("current_user",currentUser);
                    args1.putSerializable("group",group);
                    groupInfoFragment.setArguments(args1);
                    return groupInfoFragment;
                case 1:
                    GroupMeetingsFragment groupMeetingsFragment = new GroupMeetingsFragment();
                    Bundle args2 = new Bundle();
                    args2.putSerializable("current_user",currentUser);
                    args2.putSerializable("group",group);
                    groupMeetingsFragment.setArguments(args2);
                    return groupMeetingsFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    private static final int ACTION_ADD_MEMBER = 1;
    private static final int ACTION_DELETE_GROUP = 2;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(currentUser.getId()==group.getAdmin().getId()) {
            MenuItem actionAddMember = menu.add(Menu.NONE, ACTION_ADD_MEMBER, Menu.NONE, "Add member");
            actionAddMember.setIcon(R.drawable.ic_person_add_black_36dp);
            actionAddMember.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            MenuItem actionDeleteGroup = menu.add(Menu.NONE, ACTION_DELETE_GROUP, Menu.NONE, "Delete group");
            actionDeleteGroup.setIcon(R.drawable.ic_delete_black_36dp);
            actionDeleteGroup.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case ACTION_ADD_MEMBER:
                Intent intent = new Intent(this,SearchActivity.class);
                intent.putExtra(ConstantsUtil.EXTRA_ACTION,TAG);
                intent.putExtra(ConstantsUtil.EXTRA_GROUP,group);
                startActivity(intent);
                break;
            case ACTION_DELETE_GROUP:
                InterfaceUtil.showYesNoDialog(
                        this,
                        "Da li ste sigurni da hoćete da obrišete grupu?",
                        "Da",
                        "Otkaži",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                deleteGroup();
                            }
                        });
                break;
        }

        return true;
    }

    private void deleteGroup(){
        Call<Void> call = ApiClient.getApi().deleteGroup(group.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(GroupActivity.this, "Grupa je obrisana", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

}
