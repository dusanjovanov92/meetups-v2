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
import com.dusanjovanov.meetups3.fragments.ProfileFragment;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.util.ConstantsUtil;
import com.google.firebase.auth.FirebaseAuth;

import static com.dusanjovanov.meetups3.util.ConstantsUtil.EXTRA_ACTION;
import static com.dusanjovanov.meetups3.util.ConstantsUtil.EXTRA_CURRENT_USER;

public class MainScreenActivity extends AppCompatActivity {

    public static final String TAG = "TagMainScreen";
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
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
                    Intent intent = new Intent(MainScreenActivity.this,SearchActivity.class);
                    intent.putExtra(EXTRA_CURRENT_USER,currentUser);
                    intent.putExtra(EXTRA_ACTION,TAG);
                    startActivity(intent);
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
            action = intent.getStringExtra(ConstantsUtil.EXTRA_ACTION);
        }
        if(action!=null){
            if(action.equals(MainActivity.TAG)){
                currentUser = (User) intent.getSerializableExtra(ConstantsUtil.EXTRA_CURRENT_USER);
            }
        }
    }

    private void setTabIcons(){
        tabs.getTabAt(0).setIcon(R.drawable.tab_home);
        tabs.getTabAt(1).setIcon(R.drawable.tab_groups);
        tabs.getTabAt(2).setIcon(R.drawable.tab_contacts);
        tabs.getTabAt(3).setIcon(R.drawable.tab_profile);
    }

    private class PagerAdapter extends FragmentPagerAdapter{

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    HomeFragment homeFragment = new HomeFragment();
                    Bundle args4 = new Bundle();
                    args4.putSerializable("user",currentUser);
                    homeFragment.setArguments(args4);
                    return homeFragment;
                case 1:
                    GroupsFragment groupsFragment = new GroupsFragment();
                    Bundle args = new Bundle();
                    args.putSerializable("user",currentUser);
                    groupsFragment.setArguments(args);
                    return groupsFragment;
                case 2:
                    ContactsFragment contactsFragment = new ContactsFragment();
                    Bundle args2 = new Bundle();
                    args2.putSerializable("user",currentUser);
                    contactsFragment.setArguments(args2);
                    return contactsFragment;
                case 3:
                    ProfileFragment profileFragment = new ProfileFragment();
                    Bundle args3 = new Bundle();
                    args3.putSerializable("user",currentUser);
                    profileFragment.setArguments(args3);
                    return profileFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
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
