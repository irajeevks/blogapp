package com.example.reviewapp.reviewapp.activities;

import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;

import com.example.reviewapp.reviewapp.R;
import com.example.reviewapp.reviewapp.fragments.AnswersFragment;
import com.example.reviewapp.reviewapp.fragments.ProfilesFragment;
import com.example.reviewapp.reviewapp.fragments.QueriesFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainSwipeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference notificationReference;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_swipe);

        auth = FirebaseAuth.getInstance();
        notificationReference = FirebaseDatabase.getInstance().getReference("com/example/ashik/blogapp/notifications");
        String recent_token = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        Log.d("Auth Token",recent_token);
        if (auth.getCurrentUser() != null)
        {
            String userid = auth.getCurrentUser().getUid();
            //send it to the firebase database with the current userid
            notificationReference.child(userid).child("auth_token").setValue(recent_token);
        }
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Queries");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setTabTextColors(Color.parseColor("#90FFFFFF"), Color.parseColor("#FFFFFF"));
        tabLayout.setTabMode(TabLayout.GRAVITY_FILL);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                toolbar.setTitle(mSectionsPagerAdapter.getPageTitle(position));
                //update the action bar
                setSupportActionBar(toolbar);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


    }

    /**
     * A placeholder fragment containing a simple view.
     */

    //Placeholder fragment deleted form here.

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    QueriesFragment queriesFragment = new QueriesFragment();
                    return queriesFragment;
                case 1:
                    AnswersFragment answersFragment = new AnswersFragment();
                    return answersFragment;
                case 2:
                    ProfilesFragment profilesFragment = new ProfilesFragment();
                    return profilesFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Queries";
                case 1:
                    return "Answers";
                case 2:
                    return "Profile";
            }
            return null;
        }
    }
}
