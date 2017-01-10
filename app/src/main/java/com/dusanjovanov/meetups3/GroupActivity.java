package com.dusanjovanov.meetups3;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.dusanjovanov.meetups3.fragments.GroupInfoFragment;
import com.dusanjovanov.meetups3.fragments.GroupMeetingsFragment;
import com.dusanjovanov.meetups3.fragments.GroupsFragment;
import com.dusanjovanov.meetups3.models.Group;
import com.dusanjovanov.meetups3.models.User;

public class GroupActivity extends AppCompatActivity {

    private ViewPager pager;
    private TabLayout tabs;
    private GroupPagerAdapter pagerAdapter;
    private Group group;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        handleIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle(group.getName());
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        pager = (ViewPager) findViewById(R.id.pager);
        tabs = (TabLayout) findViewById(R.id.tabs);
        pagerAdapter = new GroupPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        tabs.setupWithViewPager(pager);
        tabs.getTabAt(0).setText("info");
        tabs.getTabAt(1).setText("meetings");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return true;
    }
}
