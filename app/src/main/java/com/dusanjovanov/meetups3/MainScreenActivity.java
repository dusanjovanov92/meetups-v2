package com.dusanjovanov.meetups3;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dusanjovanov.meetups3.fragments.ContactsFragment;
import com.dusanjovanov.meetups3.fragments.GroupsFragment;
import com.dusanjovanov.meetups3.fragments.HomeFragment;
import com.dusanjovanov.meetups3.models.User;
import com.google.firebase.auth.FirebaseAuth;

public class MainScreenActivity extends AppCompatActivity {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private SearchView searchView;
    private TabLayout tabs;
    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        handleIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.requestFocus();
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayShowTitleEnabled(false);
        }

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(pager);
        setTabIcons();

        searchView = (SearchView) findViewById(R.id.search);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    startActivity(new Intent(MainScreenActivity.this,SearchActivity.class));
                }
                else{

                }
            }
        });

    }

    private void handleIntent(){
        Intent intent = getIntent();
        String action = null;
        if(intent!=null){
            action = intent.getStringExtra("action");
        }
        if(action!=null){
            if(action.equals(MainActivity.TAG)){
                currentUser = (User) intent.getSerializableExtra("user");
            }
        }
    }

    private void setTabIcons(){
        tabs.getTabAt(0).setIcon(R.drawable.tab_home);
        tabs.getTabAt(1).setIcon(R.drawable.tab_groups);
        tabs.getTabAt(2).setIcon(R.drawable.tab_home);
    }

    private class PagerAdapter extends FragmentPagerAdapter{

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new HomeFragment();
                case 1:
                    GroupsFragment groupsFragment = new GroupsFragment();
                    Bundle args = new Bundle();
                    args.putSerializable("user",currentUser);
                    groupsFragment.setArguments(args);
                    return groupsFragment;
                case 2:
                    return new ContactsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(searchView!=null){
            searchView.clearFocus();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_screen,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_sign_out:
                firebaseAuth.signOut();
                startActivity(new Intent(this,SignInActivity.class));
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
