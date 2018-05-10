package com.cs646.luliu.dongdong00.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.cs646.luliu.dongdong00.R;
import com.cs646.luliu.dongdong00.model.User;
import com.cs646.luliu.dongdong00.ui.tabFragments.ExploreFragment;
import com.cs646.luliu.dongdong00.ui.tabFragments.MeFragment;
import com.cs646.luliu.dongdong00.ui.tabFragments.NotificationFragment;
import com.cs646.luliu.dongdong00.ui.dialogFragments.AddQuestionDialogFragment;
import com.cs646.luliu.dongdong00.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * Created by luliu on 4/16/16.
 */

/**
 * Represents the home screen of the app which
 * has a ViewPager with ExploreFragment, NotificationFragment, MeFragment and TutorFragment
 */
public class MainActivity extends BaseActivity {

    private Firebase mUserRef;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ValueEventListener mUserRefListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /**
         * Create Firebase references
         */
        mUserRef = new Firebase(Constants.FIREBASE_URL_USERS).child(mEncodedEmail);

        /**
         * Link layout elements from XML and setup the toolbar
         */
        initializeScreen();


        /**
         * Add ValueEventListeners to Firebase references
         * to control get data and control behavior and visibility of elements
         */
        mUserRefListener = mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, getString(R.string.log_error_the_read_failed) +
                            firebaseError.getMessage());
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mUserRef.removeEventListener(mUserRefListener);
    }


    /**
     * Link layout elements from XML and setup the toolbar
     */
    public void initializeScreen() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        /**
         * Create SectionPagerAdapter, set it as adapter to viewPager with setOffscreenPageLimit(2)
         **/
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
        /**
         * Setup the mTabLayout with view pager
         */
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_explore);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_notifications_none);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_sentiment_very_satisfied);
    }

    /**
     * Create an instance of the AddQuestion dialog fragment and show it
     */
    public void showAddQuestionDialog(View view) {
        /* Create an instance of the dialog fragment and show it */
        DialogFragment dialog = AddQuestionDialogFragment.newInstance(mEncodedEmail);
        dialog.show(MainActivity.this.getSupportFragmentManager(), "AddQuestionDialogFragment");
    }






    /**
     * SectionPagerAdapter class that extends FragmentStatePagerAdapter to save fragments state
     */
    public class SectionPagerAdapter extends FragmentStatePagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Use positions (0,1,2) to find and instantiate fragments with newInstance()
         *
         * @param position
         */
        @Override
        public Fragment getItem(int position) {

            Fragment fragment = null;

            /**
             * Set fragment to different fragments depending on position in ViewPager
             */
            switch (position) {
                case 0:
                    fragment = ExploreFragment.newInstance(mEncodedEmail);
                    break;
                case 1:
                    fragment = NotificationFragment.newInstance(mEncodedEmail);
                    break;
                case 2:
                    fragment= MeFragment.newInstance(mEncodedEmail);
                    break;
                default:
                    fragment = ExploreFragment.newInstance(mEncodedEmail);
                    break;
            }

            return fragment;
        }


        @Override
        public int getCount() {
            return 3;
        }

        /**
         * Set string resources as titles for each fragment by it's position
         *
         * @param position
         */

        /*
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.pager_title_explore);
                case 1:
                    return getString(R.string.pager_title_notification);
                case 2:
                    return getString(R.string.pager_title_my_portfolio);

                default:
                    return getString(R.string.pager_title_explore);
            }
        }
        */
    }




    /**
     * Logs out the user from their current session and starts LoginActivity.
     * Also disconnects the mGoogleApiClient if connected and provider is Google
     */
    public void logout() {

        /* Logout if mProvider is not null */
        if (mProvider != null) {
            mFirebaseRef.unauth();

            if (mProvider.equals(Constants.GOOGLE_PROVIDER)) {

                /* Logout from Google+ */
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                //nothing
                            }
                        });
            }
        }
    }

}