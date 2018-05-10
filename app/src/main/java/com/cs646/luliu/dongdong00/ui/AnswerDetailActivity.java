package com.cs646.luliu.dongdong00.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cs646.luliu.dongdong00.R;
import com.cs646.luliu.dongdong00.model.Answer;
import com.cs646.luliu.dongdong00.model.NanoAnswer;
import com.cs646.luliu.dongdong00.model.Question;
import com.cs646.luliu.dongdong00.model.User;
import com.cs646.luliu.dongdong00.utils.Constants;
import com.cs646.luliu.dongdong00.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AnswerDetailActivity extends BaseActivity implements View.OnClickListener {

    private Answer mCurrentAnswer;

    private TextView mQuestionContentTextView, mAnswerContentTextView, mUserInformationTextView;

    private Button mUpvoteButton, mDownvoteButton, mSaveButton, mThanksButton;

    private CardView mQuestionContentCardView, mUserInformationCardView;

    private String LOG_TAG;

    private String answer_id, question_id;

    private Firebase mFirebaseAnswerListRef, mFirebaseUserListRef;

    private Boolean hasVoted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_detail);

        LOG_TAG = this.getClass().getSimpleName();

        Intent intent = getIntent();
        question_id = intent.getStringExtra(Constants.KEY_QUESTION_ID);
        answer_id = intent.getStringExtra(Constants.KEY_ANSWER_ID);

        /**
         * Link layout elements from XML
         */
        initializeScreen();

        mFirebaseUserListRef = new Firebase(Constants.FIREBASE_URL_USERS);
        mFirebaseAnswerListRef = new Firebase(Constants.FIREBASE_URL_ANSWERS_LIST);
        mFirebaseAnswerListRef.child(question_id).child(answer_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCurrentAnswer = dataSnapshot.getValue(Answer.class);
                displayAnswerContents();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.i(LOG_TAG, "reading answer object failed");

            }
        });

        /**
         * retrieve current user information from Firebase
         */

        mCurrentUserRef = new Firebase(Constants.FIREBASE_URL_USERS).child(mEncodedEmail);
        mCurrentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCurrentUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }


    private void initializeScreen() {
        mQuestionContentTextView = (TextView) findViewById(R.id.question_content);
        mUserInformationTextView = (TextView) findViewById(R.id.user_information);
        mAnswerContentTextView = (TextView) findViewById(R.id.answer_content);
        mQuestionContentCardView = (CardView) findViewById(R.id.question_content_cardview);
        mUserInformationCardView = (CardView) findViewById(R.id.user_information_cardview);

        mUpvoteButton = (Button) findViewById(R.id.upvote_button);
        mDownvoteButton = (Button) findViewById(R.id.downvote_button);
        mSaveButton = (Button) findViewById(R.id.save_button);
        mThanksButton = (Button) findViewById(R.id.thanks_button);

        mQuestionContentCardView.setOnClickListener(this);
        mUserInformationCardView.setOnClickListener(this);

        mUpvoteButton.setOnClickListener(this);
        mDownvoteButton.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
        mThanksButton.setOnClickListener(this);


    }

    private void displayAnswerContents() {
        //Display contents
        mQuestionContentTextView.setText(mCurrentAnswer.getQuestion_content());
        mUserInformationTextView.setText(mCurrentAnswer.getUser_name());
        mAnswerContentTextView.setText(mCurrentAnswer.getAnswer_content());
        mUpvoteButton.setText(mCurrentAnswer.getNum_of_upvotes() + "");
        mDownvoteButton.setText(mCurrentAnswer.getNum_of_downvotes() + "");
        mSaveButton.setText(mCurrentAnswer.getNum_of_fav() + "");
        mThanksButton.setText("Give Thanks");

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.question_content_cardview:
                Intent questionIntent = new Intent(this, QuestionDetailActivity.class);
                questionIntent.putExtra(Constants.KEY_QUESTION_ID, mCurrentAnswer.getQuestion_id());
                startActivity(questionIntent);
                break;
            case R.id.user_information_cardview:
                Intent userIntent = new Intent(this, UserHomePageActivity.class);
                userIntent.putExtra(Constants.KEY_ENCODED_EMAIL, mCurrentAnswer.getmEncodedEmail());
                startActivity(userIntent);
                break;
            case R.id.upvote_button:
                /**
                 * increase answer.num_of_upvotes by 1, update display
                 */
                if (hasVoted) {
                    Toast.makeText(getApplicationContext(), "You have voted this Answer", Toast.LENGTH_SHORT).show();
                } else {

                    mFirebaseAnswerListRef.child(question_id).child(answer_id)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    mCurrentAnswer = dataSnapshot.getValue(Answer.class);
                                    mFirebaseAnswerListRef.child(question_id).child(answer_id).child(Constants.FIREBASE_LOCATION_NUM_OF_UPVOTES).setValue(mCurrentAnswer.getNum_of_upvotes() + 1);
                                    mUpvoteButton.setText(String.valueOf(String.valueOf(mCurrentAnswer.getNum_of_upvotes() + 1)));
                                    //notify Answer Owner
                                    Utils.sendNotificationToOwner(getApplicationContext(),mEncodedEmail, mCurrentUser.getUser_name(), mCurrentAnswer.getmEncodedEmail(), question_id, answer_id, "upvote");
                                    hasVoted = true;
                                }


                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });
                }
                break;
            case R.id.downvote_button:
                /**
                 * increase answer.num_of_downvotes by 1, update display
                 */

                if (hasVoted) {
                    Toast.makeText(getApplicationContext(), "You have voted this Answer", Toast.LENGTH_SHORT).show();
                } else {

                    mFirebaseAnswerListRef.child(question_id).child(answer_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Answer answer = dataSnapshot.getValue(Answer.class);
                            mFirebaseAnswerListRef.child(question_id).child(answer_id).child(Constants.FIREBASE_LOCATION_NUM_OF_DOWNVOTES)
                                    .setValue(answer.getNum_of_downvotes() + 1);
                            mDownvoteButton.setText(String.valueOf(answer.getNum_of_downvotes() + 1));
                            //notify Answer Owner
                            Utils.sendNotificationToOwner(getApplicationContext(),mEncodedEmail, mCurrentUser.getUser_name(), mCurrentAnswer.getmEncodedEmail(), question_id, answer_id, "downvote");
                            hasVoted = true;
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
                break;
            case R.id.save_button:
                /**
                 * add new nanoAnswer object to saved_answers_list.encodedEmail;
                 * increase answerlist.questionid.answerid.num_of_fav by 1
                 */

                //Check if this answer has already been added to user's saved_answer_list

                Firebase mFirebaseSavedAnswerRef = new Firebase(Constants.FIREBASE_URL_SAVED_ANSWERS_LIST).child(mEncodedEmail);
                mFirebaseSavedAnswerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(answer_id)) {//if answer has already been saved
                            Toast.makeText(getApplicationContext(), "Answer has already been saved", Toast.LENGTH_SHORT).show();

                        } else {// if not, then add new nanoAnswer object to SavedAnswerList
                            addAnswerToSavedAnswersList();
                        }

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

                break;

            case R.id.thanks_button:
                /**
                 * increase user.num_of_thanks by 1.
                 */

                mFirebaseUserListRef.child(mCurrentAnswer.getmEncodedEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User mMe = dataSnapshot.getValue(User.class);
                        mFirebaseUserListRef.child(mCurrentAnswer.getmEncodedEmail()).child(Constants.FIREBASE_LOCATION_NUM_OF_THANKS).setValue(mMe.getNum_of_thanks() + 1);
                        Toast.makeText(getApplicationContext(), "Thanks Given", Toast.LENGTH_LONG).show();
                        //notify Answer Owner
                        Utils.sendNotificationToOwner(getApplicationContext(),mEncodedEmail, mCurrentUser.getUser_name(), mCurrentAnswer.getmEncodedEmail(), question_id, answer_id, "thanks");

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Log.i(LOG_TAG, "increment thanks count failed");

                    }
                });
                break;
        }


    }

    public void addAnswerToSavedAnswersList() {
        NanoAnswer newNanoAnswer = new NanoAnswer(mEncodedEmail, mCurrentAnswer.getUser_name(), mCurrentAnswer.getQuestion_id(), mCurrentAnswer.getQuestion_content(), mCurrentAnswer.getAnswer_content());

        /**
         * Create the mapping to be added
         */
        final HashMap<String, Object> savedAnswerMapping = new HashMap<String, Object>();

        HashMap<String, Object> newSavedAnswerMap = (HashMap<String, Object>)
                new ObjectMapper().convertValue(newNanoAnswer, Map.class);

        //add answer to SavedAnswerList
        savedAnswerMapping.put("/" + Constants.FIREBASE_LOCATION_SAVED_ANSWERS_LIST + "/" + mEncodedEmail + "/" + answer_id, newSavedAnswerMap);

        mFirebaseAnswerListRef.child(question_id).child(answer_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //increase answer.num_of_fav by 1
                final Answer answer = dataSnapshot.getValue(Answer.class);
                savedAnswerMapping.put("/" + Constants.FIREBASE_LOCATION_ANSWERS_LIST + "/" + question_id + "/" + answer_id + "/" + Constants.FIREBASE_LOCATION_NUM_OF_FAV, answer.getNum_of_fav() + 1);


                // do the update
                mFirebaseRef.updateChildren(savedAnswerMapping, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        mSaveButton.setText((answer.getNum_of_fav() + 1) + "");
                        //notify Answer Owner
                        Utils.sendNotificationToOwner(getApplicationContext(),mEncodedEmail, mCurrentUser.getUser_name(), mCurrentAnswer.getmEncodedEmail(), question_id, answer_id, "save");

                        if (firebaseError != null) {
                            Log.e(LOG_TAG, "saving answer failed: " + firebaseError.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
}
