package com.cs646.luliu.dongdong00.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.BoringLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cs646.luliu.dongdong00.R;
import com.cs646.luliu.dongdong00.model.NanoUser;
import com.cs646.luliu.dongdong00.model.Question;
import com.cs646.luliu.dongdong00.model.User;
import com.cs646.luliu.dongdong00.utils.Constants;
import com.cs646.luliu.dongdong00.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class UserHomePageActivity extends BaseActivity implements View.OnClickListener{
    String mCurrentUserEncodedEmail;
    TextView mFollowingTextView,mFollowerTextView,mUserTaglineTextView,mUserNameSchoolLevelTextView,mNumOfThanksTextView,mNumOfAnswersTextView,mNumOfQuestionsTextView,mNumOfPointsTextView;

    Button mFollowButton;
    Firebase mCurrentUserRef,mMyUserRef,mCurrentUserFollowersRef,mMyFollowingsRef;
    User mCurrentUser;
    User mMe;
    String LOG_TAG;
    Boolean isFollowing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);
        LOG_TAG=this.getClass().getSimpleName();


        //Initialize xml elements
        initializeScreen();

        //obtain user encodedemail from intent
        Intent intent=getIntent();
        mCurrentUserEncodedEmail=intent.getStringExtra(Constants.KEY_ENCODED_EMAIL);
        Log.i(LOG_TAG,"mCurrentUserEncodedEmail="+mCurrentUserEncodedEmail);
        //Initiate Firebase References
        mCurrentUserRef=new Firebase(Constants.FIREBASE_URL_USERS).child(mCurrentUserEncodedEmail);
        mMyUserRef=new Firebase(Constants.FIREBASE_URL_USERS).child(mEncodedEmail);
        mCurrentUserFollowersRef=new Firebase(Constants.FIREBASE_URL_FOLLOWERS_LIST).child(mCurrentUserEncodedEmail);
        mMyFollowingsRef=new Firebase(Constants.FIREBASE_URL_FOLLOWINGS_LIST).child(mEncodedEmail);

        //get Current user information from firebase

        mCurrentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCurrentUser=dataSnapshot.getValue(User.class);
                displayUserInfo();

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG,"reading user information failed");

            }
        });

        //at initiation, check if i'm already following current user. set button text depending on result.
        mMyFollowingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isFollowing = dataSnapshot.hasChild(mCurrentUserEncodedEmail);
                if (isFollowing) {
                    mFollowButton.setText(getResources().getString(R.string.unfollow_button));
                } else {
                    mFollowButton.setText(getResources().getString(R.string.follow_button));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, "reading my following list failed");

            }
        });







    }





    private void initializeScreen(){
        mUserNameSchoolLevelTextView=(TextView) findViewById(R.id.user_name_school_level);
        mUserTaglineTextView=(TextView) findViewById(R.id.user_tagline);
        mFollowingTextView=(TextView) findViewById(R.id.following);
        mFollowerTextView=(TextView) findViewById(R.id.follower);
        mFollowButton=(Button) findViewById(R.id.follow_button);

        mNumOfAnswersTextView=(TextView) findViewById(R.id.see_all_answers);
        mNumOfQuestionsTextView=(TextView) findViewById(R.id.see_all_questions);

        mNumOfThanksTextView=(TextView) findViewById(R.id.num_of_thanks);


        mFollowingTextView.setOnClickListener(this);
        mFollowerTextView.setOnClickListener(this);
        mFollowButton.setOnClickListener(this);


    }
    private void displayUserInfo(){
        mFollowerTextView.setText(getResources().getString(R.string.num_of_followers, mCurrentUser.getNum_of_followers()));
        mFollowingTextView.setText(getResources().getString(R.string.num_of_followings, mCurrentUser.getNum_of_followings()));
        mUserNameSchoolLevelTextView.setText(mCurrentUser.getUser_name()+" . "+mCurrentUser.getSchool_level());
        mUserTaglineTextView.setText(mCurrentUser.getUser_tagline());
        mNumOfAnswersTextView.setText(getResources().getString(R.string.num_of_answers,mCurrentUser.getTotal_num_of_answers()));
        mNumOfQuestionsTextView.setText(getResources().getString(R.string.num_of_questions,mCurrentUser.getTotal_num_of_questions()));

        mNumOfThanksTextView.setText(getResources().getString(R.string.num_of_thanks,mCurrentUser.getNum_of_thanks()));


    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.follow_button:
                if(isFollowing){ //if already following, remove the follow from database
                    isFollowing=false;
                    mFollowButton.setText(getResources().getString(R.string.follow_button));
                    //obtain my user profile
                    mMyUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            mMe = dataSnapshot.getValue(User.class);
                            //remove currentUser from MyUser following list, remove me from currentUser's followerlist,my num_of_following -1, current users num_of_follower -1;
                            removeFollowInfoFromFirebase();
                            updateUI();//refresh mCurrentUser, update UI display
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });




                }else{
                    isFollowing=true;
                    mFollowButton.setText(getResources().getString(R.string.unfollow_button));
                    //obtain my user profile, to be added to CurrentUser's follower's list
                    mMyUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            mMe=dataSnapshot.getValue(User.class);
                            //add currentUser to My followinglist, add me to currentUser's followerList, my num_of_following +1, currentUser's num_of_follower +1;
                            addFollowInfoInFirebase();
                            updateUI();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });


                }
                break;
            case R.id.follower:
                //go to FirebaseListViewActivity, display list of followers
                Intent followerIntent=new Intent(this,FirebaseListViewActivity.class);
                followerIntent.putExtra(Constants.KEY_LIST_URL, Constants.FIREBASE_URL_FOLLOWERS_LIST+"/"+mCurrentUserEncodedEmail);
                followerIntent.putExtra(Constants.KEY_LIST_NAME, Constants.FOLLOWER_LIST_NAME);
                startActivity(followerIntent);
                break;
            case R.id.following:
                //go to FirebaseListViewActivity, display list of followings
                Intent followingIntent=new Intent(this,FirebaseListViewActivity.class);
                followingIntent.putExtra(Constants.KEY_LIST_URL, Constants.FIREBASE_URL_FOLLOWINGS_LIST+"/"+mCurrentUserEncodedEmail);
                followingIntent.putExtra(Constants.KEY_LIST_NAME, Constants.FOLLOWING_LIST_NAME);
                startActivity(followingIntent);
                break;




        }

    }

    private void addFollowInfoInFirebase(){
        /**
         * Create the mapping to be added
         */
        HashMap<String, Object> MappingToAdd = new HashMap<String, Object>();


        /* Create a HashMap version of the nanoUser to add */
        NanoUser newMyNanoUser= new NanoUser(mMe.getUser_name(), mMe.getSchool_level(), mMe.getUser_tagline(),mEncodedEmail);
        HashMap<String, Object> newMyNanoUserMap = (HashMap<String, Object>)
                new ObjectMapper().convertValue(newMyNanoUser, Map.class);
        NanoUser newCurrentNanoUser=new NanoUser(mCurrentUser.getUser_name(),mCurrentUser.getSchool_level(),mCurrentUser.getUser_tagline(),mCurrentUserEncodedEmail);
        HashMap<String,Object> newCurrentNanoUserMap=(HashMap<String,Object>)
                new ObjectMapper().convertValue(newCurrentNanoUser,Map.class);


        //add My information to Current User's Follower List
        MappingToAdd.put("/" + Constants.FIREBASE_LOCATION_FOLLOWERS + "/" + mCurrentUserEncodedEmail+"/"+mEncodedEmail, newMyNanoUserMap);
        //add Current User information to My following list
        MappingToAdd.put("/"+Constants.FIREBASE_LOCATION_FOLLOWINGS+"/"+mEncodedEmail+"/"+mCurrentUserEncodedEmail,newCurrentNanoUserMap);
        //add 1 to my num_of_following
        MappingToAdd.put("/"+Constants.FIREBASE_LOCATION_USERS+"/"+mEncodedEmail+"/"+Constants.FIREBASE_LOCATION_NUM_OF_FOLLOWINGS, mMe.getNum_of_followings()+1);
        //add 1 to current user's num_of_followers
        MappingToAdd.put("/"+Constants.FIREBASE_LOCATION_USERS+"/"+mCurrentUserEncodedEmail+"/"+Constants.FIREBASE_LOCATION_NUM_OF_FOLLOWERS,mCurrentUser.getNum_of_followers()+1);

                /* Try to update the database */
        mFirebaseRef.updateChildren(MappingToAdd, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                //on Completion, send notification to the Following user
                Utils.sendNotificationToOwner(getApplicationContext(),mMe.getEncoded_email(),mMe.getUser_name(),mCurrentUserEncodedEmail,null,null,"follow");
                if (firebaseError != null) {
                    Log.e(LOG_TAG, "adding follow info failed: " + firebaseError.getMessage());
                }
            }
        });
    }
    private void removeFollowInfoFromFirebase(){
        /**
         * Create the mapping to be removed
         */
        HashMap<String, Object> MappingToRemove = new HashMap<String, Object>();


        //remove My information from Current User's Follower List

        MappingToRemove.put("/" + Constants.FIREBASE_LOCATION_FOLLOWERS + "/" + mCurrentUserEncodedEmail+"/"+mEncodedEmail,
                null);
        //remove Current User information from My following list
        MappingToRemove.put("/"+Constants.FIREBASE_LOCATION_FOLLOWINGS+"/"+mEncodedEmail+"/"+mCurrentUserEncodedEmail,
                null);

        //minus 1 from my num_of_following

        MappingToRemove.put("/"+Constants.FIREBASE_LOCATION_USERS+"/"+mEncodedEmail+"/"+Constants.FIREBASE_LOCATION_NUM_OF_FOLLOWINGS,mMe.getNum_of_followings()-1);
        //minus 1 from current user's num_of_followers
        MappingToRemove.put("/"+Constants.FIREBASE_LOCATION_USERS+"/"+mCurrentUserEncodedEmail+"/"+Constants.FIREBASE_LOCATION_NUM_OF_FOLLOWERS,mCurrentUser.getNum_of_followers()-1);

        Log.i("before updateChildren", mMe.getNum_of_followers() + " " + mMe.getNum_of_followings());

                /* Try to update the database */
        mFirebaseRef.updateChildren(MappingToRemove, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.e(LOG_TAG, "removing follow info failed: " + firebaseError.getMessage());
                }
            }
        });
    }

    private void updateUI(){

        //get Current user information from firebase

        mCurrentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCurrentUser=dataSnapshot.getValue(User.class);
                displayUserInfo();

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG,"reading user information failed");

            }
        });



    }

}




