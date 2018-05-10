package com.cs646.luliu.dongdong00.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cs646.luliu.dongdong00.R;
import com.cs646.luliu.dongdong00.model.Answer;
import com.cs646.luliu.dongdong00.model.NanoQuestion;
import com.cs646.luliu.dongdong00.model.Question;
import com.cs646.luliu.dongdong00.model.User;
import com.cs646.luliu.dongdong00.ui.dialogFragments.AddAnswerDialogFragment;
import com.cs646.luliu.dongdong00.utils.Constants;
import com.cs646.luliu.dongdong00.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseListAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luliu on 4/23/16.
 */
public class QuestionDetailActivity extends BaseActivity {
    private static final String LOG_TAG = QuestionDetailActivity.class.getSimpleName();
    private String question_id;
    private Firebase mCurrentQuestionRef,mAnswersRef;

    private ListView mAnswerListView;

    private TextView mSubject, mLevel, mQuestionContent, mUserName, mNumOfAnswers, mNumOfWatchers;
    private Button mWatchButton, mAnswerButton;

    private Question mCurrentQuestion;

    private User mCurrentUser;

    private Boolean isWatching;

    private static FirebaseListAdapter<Answer> mAnswerListViewAdapter;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_details);


         /* Get the push ID from the extra passed by ExploreFragment */
        Intent intent = this.getIntent();
        question_id = intent.getStringExtra(Constants.KEY_QUESTION_ID);
        Log.i(LOG_TAG, "QUESTION ID=" + question_id);
        if (question_id == null) {
            /* No point in continuing without a valid ID. */
            finish();
            return;
        }

        /**
         * Create Firebase references
         */
        mCurrentQuestionRef = new Firebase(Constants.FIREBASE_URL_QUESTIONS_LIST).child(question_id);

        mAnswersRef = new Firebase(Constants.FIREBASE_URL_ANSWERS_LIST).child(question_id);

        /**
         * Link layout elements from XML
         */
        initializeScreen();

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


        /**
         * retrieve  question details from firebase
         */
        mCurrentQuestionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(LOG_TAG, dataSnapshot.getValue().toString());
                Log.i(LOG_TAG, "IN singlevalueevent listener");
                mCurrentQuestion = dataSnapshot.getValue(Question.class);
                displayQuestionInfo();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, "The read failed:" + firebaseError.getMessage());
            }
        });


        //display list of answers on the listview
        mAnswerListViewAdapter = new FirebaseListAdapter<Answer>(this, Answer.class, R.layout.single_answer_list_item, mAnswersRef) {
            @Override
            protected void populateView(View view, Answer answer, int position) {
                ((TextView) view.findViewById(R.id.user_name)).setText(answer.getUser_name());
                ((TextView) view.findViewById(R.id.upvotes)).setText(answer.getNum_of_upvotes() + " upvotes");
                ((TextView) view.findViewById(R.id.downvotes)).setText(answer.getNum_of_downvotes() + " downvotes");
                ((TextView) view.findViewById(R.id.answer_content)).setText(answer.getAnswer_content());
            }
        };
        mAnswerListView.setAdapter(mAnswerListViewAdapter);
        mAnswerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), AnswerDetailActivity.class);

                String answer_id=mAnswerListViewAdapter.getRef(position).getKey();
                Log.i(LOG_TAG, "AnswerId= " + answer_id);

                intent.putExtra(Constants.KEY_QUESTION_ID,question_id);
                intent.putExtra(Constants.KEY_ANSWER_ID, answer_id);


                startActivity(intent);

            }
        });

    }


    /**
     * Cleanup when the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        //cleanup adapter
        mAnswerListViewAdapter.cleanup();
    }

    /**
     * Link layout elements from XML
     */
    private void initializeScreen() {
        Log.i(LOG_TAG, "in initializing screen");
        mAnswerListView = (ListView) findViewById(R.id.list_view_answers);

        mSubject = (TextView) findViewById(R.id.subject);
        mLevel = (TextView) findViewById(R.id.school_level);
        mQuestionContent = (TextView) findViewById(R.id.question);
        mUserName = (TextView) findViewById(R.id.user_name);
        mNumOfWatchers = (TextView) findViewById(R.id.num_of_watchers);
        mNumOfAnswers = (TextView) findViewById(R.id.num_of_answers);
        mWatchButton = (Button) findViewById(R.id.watch_button);
        mAnswerButton = (Button) findViewById(R.id.answer_button);

        Firebase mMyWatchingQuestionsListRef=new Firebase(Constants.FIREBASE_URL_WATCHING_QUESTIONS_LIST+"/"+mEncodedEmail);

        //at initiation, check if i'm already watching current question. set button text depending on result.
        mMyWatchingQuestionsListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isWatching = dataSnapshot.hasChild(question_id);
                if (isWatching) {
                    mWatchButton.setText("UNWATCH");
                } else {
                    mWatchButton.setText("WATCH");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, "reading my watching question list failed");

            }
        });


    }


    private void displayQuestionInfo() {

        // populate UI with question info

        mSubject.setText(mCurrentQuestion.getSubject());
        mLevel.setText(mCurrentQuestion.getSchool_level());
        mNumOfWatchers.setText(getResources().getString(R.string.num_of_watchers,mCurrentQuestion.getNum_of_watchers()));
        mNumOfAnswers.setText(getResources().getString(R.string.num_of_answers,mCurrentQuestion.getNum_of_answers()));
        mUserName.setText(mCurrentQuestion.getUser_name());
        mQuestionContent.setText(mCurrentQuestion.getQuestion_content());

        mUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //goto userHomepage activity
                String mEncodedEmail = mCurrentQuestion.getmEncodedEmail();
                Log.i(LOG_TAG, "mEncodedEmail=" + mEncodedEmail);
                Intent userIntent = new Intent(getApplicationContext(), UserHomePageActivity.class);
                userIntent.putExtra(Constants.KEY_ENCODED_EMAIL, mEncodedEmail);
                startActivity(userIntent);

            }
        });







        mWatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 *add new nanoQuestion to WatchingQuestionList;
                 *increase question:num_of_watchers by 1；
                 */

                if (isWatching) {//if user is already watching the question, change button text, and remove the question from watching list
                    isWatching = false;
                    mWatchButton.setText("WATCH");
                    removeQuestionFromWatchingQuestionList();
                } else {
                    isWatching = true;
                    mWatchButton.setText("UNWATCH");
                    addQuestionToWatchingQuestionList();

                }


            }
        });

    }

    /**
     * Create an instance of the AddAnswer dialog fragment and show it
     */
    public void showAddAnswerDialog(View view) {
        AddAnswerDialogFragment dialog = AddAnswerDialogFragment.newInstance(mCurrentQuestion, question_id,mEncodedEmail,mCurrentUser.getUser_name());
        dialog.show(this.getSupportFragmentManager(), "AddAnswerDialogFragment");
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Utils.showNotification(getApplicationContext(), "answered your question");
            }
        });


    }

    /**
     * add Question to Watching Question List
     */
    private void addQuestionToWatchingQuestionList(){
        NanoQuestion newNanoQuestion = new NanoQuestion(mEncodedEmail, mCurrentQuestion.getUser_name(), mCurrentQuestion.getQuestion_content()
                , mCurrentQuestion.getSubject(), mCurrentQuestion.getSchool_level());

        /**
         * Create the mapping to be added
         */
        final HashMap<String, Object> mappingToAdd = new HashMap<String, Object>();

        HashMap<String, Object> newWatchingQuestionMap = (HashMap<String, Object>)
                new ObjectMapper().convertValue(newNanoQuestion, Map.class);

        //add nanoQuestion to WatchingQuestionList
        mappingToAdd.put("/" + Constants.FIREBASE_LOCATION_WATCHING_QUESTIONS_LIST + "/" + mEncodedEmail + "/" + question_id, newWatchingQuestionMap);
        //add 1 to questionList:num_of_watchers
        mFirebaseRef.child(Constants.FIREBASE_LOCATION_QUESTIONS_LIST).child(question_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Question mCurrentQuestion = dataSnapshot.getValue(Question.class);

                        mappingToAdd.put("/" + Constants.FIREBASE_LOCATION_QUESTIONS_LIST + "/" + question_id + "/" + Constants.FIREBASE_LOCATION_NUM_OF_WATCHERS,
                                mCurrentQuestion.getNum_of_watchers() + 1);
                                  /* Try to update the database */
                        mFirebaseRef.updateChildren(mappingToAdd, new Firebase.CompletionListener() {
                                @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                mNumOfWatchers.setText(getResources().getString(R.string.num_of_watchers, mCurrentQuestion.getNum_of_watchers() + 1));
                                //send notification to Owner of the Question
                                Utils.sendNotificationToOwner(getApplicationContext(),mEncodedEmail, mCurrentUser.getUser_name(), mCurrentQuestion.getmEncodedEmail(), question_id, null, "watch");

                                if (firebaseError != null) {
                                    Log.e(LOG_TAG, "adding watching question failed: " + firebaseError.getMessage());
                                }
                            }
                        });

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

    }

    private void removeQuestionFromWatchingQuestionList(){
        //remove question from watching list , subtract question.num_of_watchers by 1, update UI of num_of_watchers
        /**
         * Create the mapping to be removed
         */
       final HashMap<String, Object> mappingToRemove = new HashMap<String, Object>();


        //remove current question from watching questions list

        mappingToRemove.put("/" + Constants.FIREBASE_LOCATION_WATCHING_QUESTIONS_LIST + "/" + mEncodedEmail+"/"+question_id,
                null);


        mFirebaseRef.child(Constants.FIREBASE_LOCATION_QUESTIONS_LIST).child(question_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Question mCurrentQuestion = dataSnapshot.getValue(Question.class);

                        mappingToRemove.put("/" + Constants.FIREBASE_LOCATION_QUESTIONS_LIST + "/" + question_id + "/" + Constants.FIREBASE_LOCATION_NUM_OF_WATCHERS,
                                mCurrentQuestion.getNum_of_watchers() - 1);
                                  /* Try to update the database */
                        mFirebaseRef.updateChildren(mappingToRemove, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                mNumOfWatchers.setText(getResources().getString(R.string.num_of_watchers, mCurrentQuestion.getNum_of_watchers() - 1));

                                if (firebaseError != null) {
                                    Log.e(LOG_TAG, "removing watching question failed: " + firebaseError.getMessage());
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
