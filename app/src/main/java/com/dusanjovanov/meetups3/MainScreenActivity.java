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
import com.dusanjovanov.meetups3.fragments.SearchResultsFragment;
import com.google.firebase.auth.FirebaseAuth;

public class MainScreenActivity extends AppCompatActivity {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private SearchView searchView;
    private TabLayout tabs;
    private ViewPager pager;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.requestFocus();
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayShowTitleEnabled(false);
//            actionBar.setIcon(R.drawable.ic_launcher);
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
                    pager.setCurrentItem(4);
                }
                else{
                    onBackPressed();
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + pager.getCurrentItem());
                ((SearchResultsFragment)fragment).search(query);
                
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    private void setTabIcons(){
        tabs.getTabAt(0).setIcon(R.drawable.tab_home);
        tabs.getTabAt(1).setIcon(R.drawable.tab_home);
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
                    return new GroupsFragment();
                case 2:
                    return new ContactsFragment();
                case 4:
                    return new SearchResultsFragment();
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
    protected void onStart() {
        super.onStart();
        if(searchView!=null){
            searchView.setIconified(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_screen,menu);

//        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        searchView.setQueryHint("Search for users...");
//
//        searchView.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainScreenActivity.this,SearchActivity.class));
//            }
//        });

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
