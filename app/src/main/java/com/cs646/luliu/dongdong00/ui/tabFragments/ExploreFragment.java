package com.cs646.luliu.dongdong00.ui.tabFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.QuickContactBadge;
import android.widget.Spinner;
import android.widget.TextView;

import com.cs646.luliu.dongdong00.R;
import com.cs646.luliu.dongdong00.model.Question;
import com.cs646.luliu.dongdong00.ui.QuestionDetailActivity;
import com.cs646.luliu.dongdong00.ui.UserHomePageActivity;
import com.cs646.luliu.dongdong00.utils.Constants;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;

/**
 * Created by luliu on 4/19/16.
 */


/**
 * use recyclerview and cardview to display a list of popular questions
 * filter function
 * search function
 */
public class ExploreFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private static String mEncodedEmail;
    private RecyclerView mFirebaseRecyclerView;
    private Spinner mSubjectSpinner, mLevelSpinner, mSortSpinner;
    private static FirebaseRecyclerAdapter<Question,CardViewHolder> mFirebaseAdapter;

    private static Context context;

    private static String LOG_TAG;
    private static Firebase mFirebaseRef;


    //record the filters from dropdown menu, initiate with default values
    private String subject="ALL SUBJECTS", school_level="ALL SCHOOL LEVELS", sort_by="MOST RECENT";


    public ExploreFragment(){
        /*Required empty public constructor */
    }

    public static ExploreFragment newInstance(String encodedEmail) {
        ExploreFragment fragment = new ExploreFragment();
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
        mFirebaseRef=new Firebase(Constants.FIREBASE_URL);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState){

        Firebase questionsListRef=new Firebase(Constants.FIREBASE_URL_QUESTIONS_LIST);

        /**
         * Initialize UI elements
         */
        View rootView = inflater.inflate(R.layout.fragment_explore, container, false);

        mFirebaseRecyclerView=(RecyclerView) rootView.findViewById(R.id.explore_recycler_view);
        mFirebaseRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        mFirebaseAdapter=new FirebaseRecyclerAdapter<Question,CardViewHolder>(Question.class,R.layout.question_card_view,CardViewHolder.class,questionsListRef){

            @Override
            public void populateViewHolder(CardViewHolder cardViewHolder, Question question, int position){

                //populate cardview with question info
                cardViewHolder.mSubject.setText(question.getSubject());
                cardViewHolder.mLevel.setText(question.getSchool_level());
                cardViewHolder.mNumOfWatchers.setText(getResources().getString(R.string.num_of_watchers,question.getNum_of_watchers()));
                cardViewHolder.mUserName.setText(question.getUser_name());
                cardViewHolder.mQuestionContent.setText(question.getQuestion_content());
                cardViewHolder.mNumOfAnswers.setText(getResources().getString(R.string.num_of_answers,question.getNum_of_answers()));

            }

        };


        mFirebaseRecyclerView.setAdapter(mFirebaseAdapter);




        //Initiate Spinners
        mSubjectSpinner=(Spinner) rootView.findViewById(R.id.subject_spinner);
        mLevelSpinner=(Spinner) rootView.findViewById(R.id.school_level_spinner);
        mSortSpinner=(Spinner) rootView.findViewById(R.id.sort_by_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> subjectAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.subject, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> levelAdapter=ArrayAdapter.createFromResource(getContext(),
                R.array.school_level,android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> sortAdapter=ArrayAdapter.createFromResource(getContext(),
                R.array.sort_by,android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSubjectSpinner.setAdapter(subjectAdapter);
        mLevelSpinner.setAdapter(levelAdapter);
        mSortSpinner.setAdapter(sortAdapter);


        //set OnItemSelected Listeners for all spinners
        mSubjectSpinner.setOnItemSelectedListener(this);
        mLevelSpinner.setOnItemSelectedListener(this);
        mSortSpinner.setOnItemSelectedListener(this);


        return rootView;
    }


    //record selection on filter spinners
    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        int spinner_id = parent.getId();
        switch (spinner_id)
        {
            case R.id.subject_spinner:
                subject=mSubjectSpinner.getSelectedItem().toString();
                Log.i("EXPLORE", "SUBJECT="+subject);
                //updateUI(subject, school_level,sort_by);
                break;
            case R.id.school_level_spinner:
                school_level=mLevelSpinner.getSelectedItem().toString();
                Log.i("EXPLORE", "school_level=" + school_level);
                //updateUI(subject, school_level,sort_by);
                break;
            case R.id.sort_by_spinner:
                sort_by=mSortSpinner.getSelectedItem().toString();
                Log.i("EXPLORE","sort_by"+sort_by);
                //updateUI(subject, school_level,sort_by);
                break;
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?>parent){
        //do nothing for now. all variables are set with default value at initiation
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //cleanup adapter
        mFirebaseAdapter.cleanup();

    }


    //used by recyclerview to hold each cardview
    public static class CardViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnClickListener {

        private Question question;
        private TextView mSubject;
        private TextView mLevel;
        private TextView mQuestionContent;
        private QuickContactBadge mQuickBadge;
        private TextView mUserName;
        private TextView mNumOfWatchers,mNumOfAnswers;



        public CardViewHolder(View itemView) {
            super(itemView);

            mSubject = (TextView) itemView.findViewById(R.id.subject);
            mLevel = (TextView) itemView.findViewById(R.id.school_level);
            mQuestionContent = (TextView) itemView.findViewById(R.id.question);
            mUserName = (TextView) itemView.findViewById(R.id.user_name);
            mNumOfWatchers = (TextView) itemView.findViewById(R.id.num_of_watchers);
            mNumOfAnswers=(TextView) itemView.findViewById(R.id.num_of_answers);


            mUserName.setOnClickListener(this);

            itemView.setOnClickListener(this);


        }


        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.user_name:
                    //goto userHomepage activity
                    String mUserEncodedEmail= mFirebaseAdapter.getItem(getAdapterPosition()).getmEncodedEmail();
                    Intent userIntent=new Intent(context, UserHomePageActivity.class);
                    userIntent.putExtra(Constants.KEY_ENCODED_EMAIL,mUserEncodedEmail);
                    context.startActivity(userIntent);
                    break;

                default:
                    //when click on the itemView, to go detail of the Question
                Intent intent = new Intent(context, QuestionDetailActivity.class);
                String questionId = mFirebaseAdapter.getRef(getAdapterPosition()).getKey();
                Log.i("EXPLORE", "ITEM CLICKED" + questionId);
                intent.putExtra(Constants.KEY_QUESTION_ID, questionId);

                context.startActivity(intent);
            }

        }
    }

}
