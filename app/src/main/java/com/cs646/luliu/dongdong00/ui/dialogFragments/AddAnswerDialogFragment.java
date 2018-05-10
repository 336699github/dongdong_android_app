package com.cs646.luliu.dongdong00.ui.dialogFragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cs646.luliu.dongdong00.R;
import com.cs646.luliu.dongdong00.model.Answer;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luliu on 4/23/16.
 *
 */


public class AddAnswerDialogFragment extends DialogFragment {
    private String mCurrentUserEncodedEmail, mQuestionId,mCurrentUserName,mQuestionContent,mSubject;
    private EditText mAnswerContentEditText;
    private TextView mQuestionContentTextView;
    private String mAnswerContent;
    private Question mCurrentQuestion;
    private String mAnswerId;
    private Context mContext;

    private HashMap<String,Object> addAnswerUpdateMap=new HashMap<String,Object>();

    private Firebase mFirebaseRef,mCurrentQuestionAnswerListRef,mFirebaseQuestionRef;

    private DialogInterface.OnDismissListener onDismissListener;

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }



    /**
     * Public static constructor that creates fragment
     */
    public static AddAnswerDialogFragment newInstance(Question mCurrentQuestion,String question_id,String mCurrentUserEncodedEmail,String mCurrentUserName) {
        AddAnswerDialogFragment addAnswerDialogFragment = new AddAnswerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.KEY_QUESTION_OBJECT,mCurrentQuestion);
        bundle.putString(Constants.KEY_QUESTION_ID, question_id);
        bundle.putString(Constants.KEY_ENCODED_EMAIL,mCurrentUserEncodedEmail);
        bundle.putString(Constants.KEY_USER_NAME,mCurrentUserName);
        addAnswerDialogFragment.setArguments(bundle);
        return addAnswerDialogFragment;
    }


    /**
     * Initialize instance variables with data from bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentQuestion=(Question) getArguments().getSerializable(Constants.KEY_QUESTION_OBJECT);
        mCurrentUserEncodedEmail = getArguments().getString(Constants.KEY_ENCODED_EMAIL);
        mQuestionId = getArguments().getString(Constants.KEY_QUESTION_ID);
        mCurrentUserName=getArguments().getString(Constants.KEY_USER_NAME);
        mQuestionContent=mCurrentQuestion.getQuestion_content();
        mSubject=mCurrentQuestion.getSubject();



    }

    /**
     * Open the keyboard automatically when the dialog fragment is opened
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.full_screen_dialog);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_add_answer, null);
        mAnswerContentEditText = (EditText) rootView.findViewById(R.id.edit_answer);
        mQuestionContentTextView=(TextView) rootView.findViewById(R.id.question_content);
        mQuestionContentTextView.setText(mQuestionContent);

        /* Inflate and set the layout for the dialog */
        builder.setView(rootView)
                /* Add action buttons */
                .setPositiveButton(R.string.positive_button_add_answer, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mAnswerContent = mAnswerContentEditText.getText().toString();
                        if (mAnswerContent.equals("")) {
                            Toast.makeText(getContext(), "Please complete content", Toast.LENGTH_LONG).show();
                            Log.i("addAnswerDialog", "answer Content=" + mAnswerContent);
                        } else {
                            addAnswer();
                        }
                    }
                });

        return builder.create();

    }
    //Add new answer to database
    public void addAnswer(){
        /**
         * add Answer to AnswerList; increase user:total_num_of_answers by 1; update question:num_of_answers;
         */

        //Create Firebase references
        mFirebaseRef=new Firebase(Constants.FIREBASE_URL);
        mCurrentQuestionAnswerListRef=new Firebase(Constants.FIREBASE_URL_ANSWERS_LIST).child(mQuestionId);
        mFirebaseQuestionRef=new Firebase(Constants.FIREBASE_URL_QUESTIONS_LIST).child(mQuestionId);

        //Create a new location reference for the Answer
        Firebase newAnswerRef=mCurrentQuestionAnswerListRef.push();
        //Save answerId to maintain same random Id
        mAnswerId=newAnswerRef.getKey();

        //Set raw version of data to the ServerValue.TIMESTAMP value and save into timestampCreatedMap
        HashMap<String,Object> timestampCreated=new HashMap<>();
        timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);


        //Build the answer
        Answer newAnswer=new Answer(mQuestionId,mQuestionContent,mAnswerContent,mCurrentUserEncodedEmail,mCurrentUserName,mSubject,timestampCreated);

        HashMap<String,Object> newAnswerMap=(HashMap<String,Object>) new ObjectMapper().convertValue(newAnswer,Map.class);

        addAnswerUpdateMap.put("/" + Constants.FIREBASE_LOCATION_ANSWERS_LIST + "/" + mQuestionId + "/" + mAnswerId, newAnswerMap);

        //get user.total_num_of_answers and increase by 1
        mFirebaseRef.child(Constants.FIREBASE_LOCATION_USERS).child(mCurrentUserEncodedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User mMe = (User) dataSnapshot.getValue(User.class);
                addAnswerUpdateMap.put("/" + Constants.FIREBASE_LOCATION_USERS + "/" + mCurrentUserEncodedEmail + "/" + Constants.FIREBASE_LOCATION_TOTAL_NUM_OF_ANSWERS, mMe.getTotal_num_of_answers() + 1);
                makeTheUpdate();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        //Close the dialog fragment
        AddAnswerDialogFragment.this.getDialog().dismiss();
    }



    private void makeTheUpdate(){
        mFirebaseRef.updateChildren(addAnswerUpdateMap, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                //update question:num_of_answers
                mCurrentQuestionAnswerListRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long num_of_answer=dataSnapshot.getChildrenCount();
                        mFirebaseQuestionRef.child(Constants.FIREBASE_LOCATION_NUM_OF_ANSWERS).setValue(num_of_answer);
                        Utils.sendNotificationToOwner(getContext(),mCurrentUserEncodedEmail,mCurrentUserName,mCurrentQuestion.getmEncodedEmail(),mQuestionId,mAnswerId,"answer");

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });




                Log.i("adding answer", "Completed");

            }
        });




    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }




}