package com.cs646.luliu.dongdong00.ui.tabFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.cs646.luliu.dongdong00.R;
import com.cs646.luliu.dongdong00.model.NotificationItem;
import com.cs646.luliu.dongdong00.ui.AnswerDetailActivity;
import com.cs646.luliu.dongdong00.ui.QuestionDetailActivity;
import com.cs646.luliu.dongdong00.ui.UserHomePageActivity;
import com.cs646.luliu.dongdong00.utils.Constants;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.ui.FirebaseListAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by luliu on 4/19/16.
 */
public class NotificationFragment extends Fragment {
    private String mEncodedEmail,LOG_TAG;
    private Context mContext;
    private Firebase mCurrentUserNotificationListRef;
    private FirebaseListAdapter<NotificationItem> mAdapter;
    private ListView mListView;



    public NotificationFragment(){
        /*Required empty public constructor */
    }

    public static NotificationFragment newInstance(String encodedEmail) {
        NotificationFragment fragment = new NotificationFragment();
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
        mContext=getContext();
        LOG_TAG=mContext.getClass().getSimpleName();

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        /**
         * Initialize UI elements
         */
        final View rootView = inflater.inflate(R.layout.fragment_notification, container, false);
        mListView=(ListView) rootView.findViewById(R.id.list_view);

        mCurrentUserNotificationListRef=new Firebase(Constants.FIREBASE_URL_NOTIFICATION_LIST+"/"+mEncodedEmail);

        mAdapter=new FirebaseListAdapter<NotificationItem>(getActivity(),NotificationItem.class,R.layout.custom_notification_list_item,mCurrentUserNotificationListRef){
            @Override
            protected void populateView(View view, final NotificationItem notificationItem, int position){
                ((TextView)view.findViewById(R.id.sender_name)).setText(notificationItem.getSender_user_name());
                ((TextView)view.findViewById(R.id.action_message)).setText(notificationItem.getAction_message());

                Long timestamp=(Long) notificationItem.getTimestampCreated().get("timestamp");
                Date myDate = new Date(timestamp);
                SimpleDateFormat df = new SimpleDateFormat("HH:mm yyyy-MM-dd");
                String formatedTime = df.format(myDate);
                ((TextView)view.findViewById(R.id.time_created)).setText(formatedTime);

                final Firebase notificationRefToDelete=mAdapter.getRef(position);

                ((ImageButton)view.findViewById(R.id.delete_button)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                       notificationRefToDelete.removeValue(new Firebase.CompletionListener() {
                           @Override
                           public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                               Log.i("notification","deleted");
                           }
                       });
                    }
                });
            }
        };

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NotificationItem mClickedNotification = (NotificationItem) parent.getItemAtPosition(position);
                if (mClickedNotification.getAction_type().equals("watch")) {
                    //if actionType=watch, then go to QuestionDetailActivity
                    Intent questionIntent = new Intent(mContext, QuestionDetailActivity.class);
                    questionIntent.putExtra(Constants.KEY_QUESTION_ID, mClickedNotification.getQuestion_id());
                    startActivity(questionIntent);


                } else if (mClickedNotification.getAction_type().equals("follow")) {
                    //if actionType=follow, then go to UserHomePageActivity
                    Intent followIntent = new Intent(mContext, UserHomePageActivity.class);
                    followIntent.putExtra(Constants.KEY_ENCODED_EMAIL,mClickedNotification.getSender_encoded_email());
                    startActivity(followIntent);

                } else {
                    //for other action Type, go to AnswerDetailActivity
                    Intent answerIntent = new Intent(mContext, AnswerDetailActivity.class);
                    answerIntent.putExtra(Constants.KEY_QUESTION_ID, mClickedNotification.getQuestion_id());
                    answerIntent.putExtra(Constants.KEY_ANSWER_ID, mClickedNotification.getAnswer_id());
                    startActivity(answerIntent);
                }

            }
        });




        return rootView;
    }




}
