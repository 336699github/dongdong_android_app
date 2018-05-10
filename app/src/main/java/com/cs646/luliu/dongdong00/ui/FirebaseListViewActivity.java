package com.cs646.luliu.dongdong00.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cs646.luliu.dongdong00.R;
import com.cs646.luliu.dongdong00.model.NanoAnswer;
import com.cs646.luliu.dongdong00.model.NanoQuestion;
import com.cs646.luliu.dongdong00.model.NanoUser;
import com.cs646.luliu.dongdong00.utils.Constants;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;

public class FirebaseListViewActivity extends BaseActivity {
    String listName;
    String listURL;
    TextView mListNameTextView;
    ListView mListView;
    Firebase ref;
    FirebaseListAdapter<?> mAdapter;
    String LOG_TAG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_list_view);

        LOG_TAG=this.getClass().getSimpleName();

        Intent intent=getIntent();
        listName=intent.getStringExtra(Constants.KEY_LIST_NAME);
        Log.i(LOG_TAG, "LISTNAME= " + listName);
        listURL=intent.getStringExtra(Constants.KEY_LIST_URL);

        initializeScreen();
        mListNameTextView.setText(listName);

        ref=new Firebase(listURL);

        if (listName.equals(Constants.FOLLOWER_LIST_NAME)||listName.equals(Constants.FOLLOWING_LIST_NAME)){
            mAdapter=new FirebaseListAdapter<NanoUser>(this,NanoUser.class,android.R.layout.two_line_list_item,ref){
                @Override
                protected void populateView(View view, NanoUser nanoUser, int position){
                    ((TextView)view.findViewById(android.R.id.text1)).setText(nanoUser.getUser_name()+" . "+nanoUser.getSchool_level());
                    ((TextView)view.findViewById(android.R.id.text2)).setText(nanoUser.getUser_tagline());
                }
            };

            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //goto userhomepage activity , use encodedemail as extra
                    Intent intent = new Intent(getApplicationContext(), UserHomePageActivity.class);
                    //put ANSWER OBJECT into intent to pass to AnswerDetailActivity.class
                    NanoUser mCurrentNanoUser=(NanoUser)parent.getItemAtPosition(position);
                    Log.i(LOG_TAG, parent.getItemAtPosition(position).toString());
                    intent.putExtra(Constants.KEY_ENCODED_EMAIL,mCurrentNanoUser.getEncoded_email());
                    startActivity(intent);
                }
            });

        }else if (listName.equals(Constants.WATCHING_QUESTION_LIST_NAME)){
            Log.i(LOG_TAG,"IN WATCHINGQUESTIONLIST ADAPTER SETUP");
            mAdapter=new FirebaseListAdapter<NanoQuestion>(this,NanoQuestion.class,R.layout.custom_question_list_item,ref) {
                @Override
                protected void populateView(View view, NanoQuestion nanoQuestion, int position) {
                    //populate custom list view with question info
                    ((TextView)view.findViewById(R.id.subject_school_level_text_view)).setText(nanoQuestion.getSubject()+" . "+nanoQuestion.getSchool_level());
                    ((TextView)view.findViewById(R.id.user_name_text_view)).setText(nanoQuestion.getUser_name());
                    ((TextView)view.findViewById(R.id.question_content)).setText(nanoQuestion.getQuestion_content());

                }
            };

            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //goto questionDetail activity , use questionId as extra
                    Intent intent = new Intent(getApplicationContext(), QuestionDetailActivity.class);
                    String questionId=mAdapter.getRef(position).getKey();

                    Log.i(LOG_TAG, "QUESTION ID= "+questionId);
                    intent.putExtra(Constants.KEY_QUESTION_ID,questionId);
                    startActivity(intent);
                }
            });
        }else if (listName.equals(Constants.SAVED_ANSWERS_LIST_NAME)) {
            Log.i(LOG_TAG, "IN SAVEDANSWERLIST ADAPTER SETUP");
            mAdapter = new FirebaseListAdapter<NanoAnswer>(this, NanoAnswer.class, R.layout.custom_answer_list_item, ref) {
                @Override
                protected void populateView(View view, NanoAnswer nanoAnswer, int position) {
                    //populate listview with nanoAnswer info
                    ((TextView) view.findViewById(R.id.question_content)).setText(nanoAnswer.getQuestion_content());
                    ((TextView) view.findViewById(R.id.user_name_text_view)).setText(nanoAnswer.getUser_name());
                    ((TextView) view.findViewById(R.id.answer_content)).setText(nanoAnswer.getAnswer_content());

                }
            };

            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //goto questionDetail activity , use questionId as extra
                    Intent intent = new Intent(getApplicationContext(), AnswerDetailActivity.class);
                    String answerId = mAdapter.getRef(position).getKey();
                    String questionId=((NanoAnswer)parent.getItemAtPosition(position)).getQuestion_id();

                    Log.i(LOG_TAG, "ANSWER ID= " + answerId);
                    intent.putExtra(Constants.KEY_ANSWER_ID, answerId);
                    intent.putExtra(Constants.KEY_QUESTION_ID,questionId);
                    startActivity(intent);
                }
            });
        }




    }
    private void initializeScreen(){
        mListNameTextView=(TextView) findViewById(R.id.list_title);
        mListView=(ListView) findViewById(R.id.list_view);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mAdapter.cleanup();
    }

}
