package com.cs646.luliu.dongdong00.ui.tabFragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cs646.luliu.dongdong00.R;
import com.cs646.luliu.dongdong00.model.User;
import com.cs646.luliu.dongdong00.ui.FirebaseListViewActivity;
import com.cs646.luliu.dongdong00.ui.MainActivity;
import com.cs646.luliu.dongdong00.ui.dialogFragments.EditUserProfileFragment;
import com.cs646.luliu.dongdong00.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by luliu on 4/19/16.
 */
public class MeFragment extends Fragment  {
    private Button mLogoutButton,mEditProfileButton;
    private View rootView;
    private TextView mWatchingQuestionTextView,mSavedAnswersTextView;
    private TextView mUserNameSchoolLevelTextView,mUserTagline,mFollowingTextView,mFollowerTextView,mThanksTextView,mTotalAnswersTextView,mTotalQuestionsTextView;
    private String mEncodedEmail,LOG_TAG;
    private Context context;
    private User mMe;
    private Firebase mMyFirebaseRef;



    public MeFragment(){
        /*Required empty public constructor */
    }

    public static MeFragment newInstance(String encodedEmail) {
        MeFragment fragment = new MeFragment();
        Bundle args = new Bundle();
        args.putString(Constants.KEY_ENCODED_EMAIL, encodedEmail);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initialize instance variables with data from bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEncodedEmail = getArguments().getString(Constants.KEY_ENCODED_EMAIL);
        }
        context=getContext();
        LOG_TAG=context.getClass().getSimpleName();

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        rootView = inflater.inflate(R.layout.fragment_me, container, false);


        /**
         * Initialize UI elements
         */
        initializeScreen();


        mMyFirebaseRef=new Firebase(Constants.FIREBASE_URL_USERS).child(mEncodedEmail);



        //get Current user information from firebase

        mMyFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMe = dataSnapshot.getValue(User.class);
                displayUserInfo();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, "reading user information failed");

            }
        });



        return rootView;

    }

    public void initializeScreen(){
        mUserNameSchoolLevelTextView=(TextView) rootView.findViewById(R.id.user_name_school_level);
        mUserTagline=(TextView) rootView.findViewById(R.id.user_tagline);
        mFollowingTextView=(TextView) rootView.findViewById(R.id.following);
        mFollowerTextView=(TextView) rootView.findViewById(R.id.follower);
        mThanksTextView=(TextView) rootView.findViewById(R.id.num_of_thanks);
        mTotalAnswersTextView=(TextView) rootView.findViewById(R.id.num_of_answers);
        mTotalQuestionsTextView=(TextView) rootView.findViewById(R.id.num_of_questions);

        mWatchingQuestionTextView=(TextView) rootView.findViewById(R.id.watching_questions);
        mSavedAnswersTextView=(TextView) rootView.findViewById(R.id.saved_answers);

        mEditProfileButton=(Button) rootView.findViewById(R.id.edit_profile_button);
        mEditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //display dialog to edit :user contact badge, user name , school level, user Tagline
                EditUserProfileFragment dialog = EditUserProfileFragment.newInstance(mMe, mEncodedEmail);
                dialog.show(getActivity().getSupportFragmentManager(), "EditUserProfileFragment");
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        //refresh user data and display user info

                        mMyFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                mMe = dataSnapshot.getValue(User.class);
                                displayUserInfo();

                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                Log.e(LOG_TAG, "reading user information failed");

                            }
                        });

                    }
                });

            }
        });
        mLogoutButton=(Button) rootView.findViewById(R.id.log_out_button);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).logout();
            }
        });

    }
    public void displayUserInfo(){
        mUserNameSchoolLevelTextView.setText(mMe.getUser_name()+" . "+ mMe.getSchool_level());
        mUserTagline.setText(mMe.getUser_tagline());
        mFollowerTextView.setText(getResources().getString(R.string.num_of_followers, mMe.getNum_of_followers()));
        mFollowingTextView.setText(getResources().getString(R.string.num_of_followings, mMe.getNum_of_followings()));
        mThanksTextView.setText(getResources().getString(R.string.num_of_thanks, mMe.getNum_of_thanks()));
        mTotalAnswersTextView.setText(getResources().getString(R.string.num_of_answers, mMe.getTotal_num_of_answers()));
        mTotalQuestionsTextView.setText(getResources().getString(R.string.num_of_questions,mMe.getTotal_num_of_questions()));



        mWatchingQuestionTextView.setText(R.string.watching_questions);
        mSavedAnswersTextView.setText(R.string.saved_answers);
        mWatchingQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to FirebaseListViewActivity, display list of watching questions
                Intent watchingQuestionIntent=new Intent(context,FirebaseListViewActivity.class);
                watchingQuestionIntent.putExtra(Constants.KEY_LIST_URL, Constants.FIREBASE_URL_WATCHING_QUESTIONS_LIST+"/"+mEncodedEmail);
                watchingQuestionIntent.putExtra(Constants.KEY_LIST_NAME, Constants.WATCHING_QUESTION_LIST_NAME);
                startActivity(watchingQuestionIntent);


            }
        });
        mSavedAnswersTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to firebase list view display list of saved answers
                Intent savedAnswerIntent=new Intent(context,FirebaseListViewActivity.class);
                savedAnswerIntent.putExtra(Constants.KEY_LIST_URL, Constants.FIREBASE_URL_SAVED_ANSWERS_LIST+"/"+mEncodedEmail);
                savedAnswerIntent.putExtra(Constants.KEY_LIST_NAME, Constants.SAVED_ANSWERS_LIST_NAME);
                startActivity(savedAnswerIntent);
            }
        });

    }





}
