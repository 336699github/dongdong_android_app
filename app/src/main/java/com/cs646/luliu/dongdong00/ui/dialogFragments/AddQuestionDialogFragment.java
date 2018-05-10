package com.cs646.luliu.dongdong00.ui.dialogFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cs646.luliu.dongdong00.R;
import com.cs646.luliu.dongdong00.model.Question;
import com.cs646.luliu.dongdong00.model.User;
import com.cs646.luliu.dongdong00.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luliu on 4/19/16.
 */

//Adds a new question
public class AddQuestionDialogFragment extends DialogFragment {
    String mEncodedEmail;
    EditText mQuestionContentEditText;
    Spinner mSubjectSpinner;
    String subject,question_content;
    String user_name,school_level;
    TextView mUserInformation;
    String LOG_TAG;


    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */
    public static AddQuestionDialogFragment newInstance(String encodedEmail) {
        AddQuestionDialogFragment addQuestionDialogFragment = new AddQuestionDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_ENCODED_EMAIL, encodedEmail);
        addQuestionDialogFragment.setArguments(bundle);
        return addQuestionDialogFragment;
    }


    /**
     * Initialize instance variables with data from bundle
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEncodedEmail = getArguments().getString(Constants.KEY_ENCODED_EMAIL);
        LOG_TAG=this.getClass().getSimpleName();





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
        View rootView = inflater.inflate(R.layout.dialog_add_question, null);
        mQuestionContentEditText = (EditText) rootView.findViewById(R.id.edit_question);
        mSubjectSpinner=(Spinner) rootView.findViewById(R.id.subject);
        mUserInformation=(TextView) rootView.findViewById(R.id.user_information_display);

        //read user related information from firebase database
        //create firebase reference for the user
        Firebase userRef=new Firebase(Constants.FIREBASE_URL_USERS).child(mEncodedEmail);
        //get the user information
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    user_name = user.getUser_name();
                    school_level = user.getSchool_level();
                    mUserInformation.setText(user_name + " ." + school_level);

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(getContext().getClass().getSimpleName(),
                        R.string.log_error_the_read_failed +
                                firebaseError.getMessage());

            }
        });




        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.subject, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSubjectSpinner.setAdapter(adapter);


        mSubjectSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                //get the selected subject
                subject=mSubjectSpinner.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getContext(),"Please Select the Subject of your question",Toast.LENGTH_LONG).show();
            }

        });



        /* Inflate and set the layout for the dialog */
        /* Pass null as the parent view because its going in the dialog layout*/
        builder.setView(rootView)
                /* Add action buttons */
                .setPositiveButton(R.string.positive_button_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        question_content=mQuestionContentEditText.getText().toString();
                        if (question_content.equals("") || subject.equals("")) {
                            Toast.makeText(getContext(),"Please complete content",Toast.LENGTH_LONG).show();
                            Log.i("addQuestionDialog","questionContent="+question_content+"subject="+subject);
                        }else {
                            addQuestion();
                        }
                    }
                });

        return builder.create();
    }



    //Add new question to database
    public void addQuestion(){
        /**
         * add question to QuestionList; increase user:total_num_of_questions by 1;
         */


            Log.i(LOG_TAG,"in addQuestion()"+ question_content+"  "+subject);

            //Create Firebase references
            Firebase questionsListRef=new Firebase(Constants.FIREBASE_URL_QUESTIONS_LIST);
            final Firebase firebaseRef=new Firebase(Constants.FIREBASE_URL);
            //Create a new location reference for the Question
            Firebase newQuestionRef=questionsListRef.push();
            //Save questionListRef.push() to maintain same random Id
            final String questionId=newQuestionRef.getKey();

            //HashMap for data to update
            final HashMap<String,Object> mappingToAdd=new HashMap<>();

            //Set raw version of data to the ServerValue.TIMESTAMP value and save into timestampCreatedMap
            HashMap<String,Object> timestampCreated=new HashMap<>();
            timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

            //Build the question
            Question newQuestion=new Question(question_content,mEncodedEmail,user_name,subject,school_level,timestampCreated);

            HashMap<String,Object> newQuestionMap=(HashMap<String,Object>) new ObjectMapper().convertValue(newQuestion,Map.class);


            mappingToAdd.put("/" + Constants.FIREBASE_LOCATION_QUESTIONS_LIST + "/" + questionId, newQuestionMap);

           firebaseRef.child(Constants.FIREBASE_LOCATION_USERS).child(mEncodedEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User mMe = dataSnapshot.getValue(User.class);

                        mappingToAdd.put("/" + Constants.FIREBASE_LOCATION_USERS + "/" + mEncodedEmail+"/"+Constants.FIREBASE_LOCATION_TOTAL_NUM_OF_QUESTIONS ,
                                mMe.getTotal_num_of_questions() + 1);
                                  /* Try to update the database */
                        firebaseRef.updateChildren(mappingToAdd, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                if (firebaseError != null) {
                                    Log.e(LOG_TAG, "adding new question failed: " + firebaseError.getMessage());
                                }
                            }
                        });

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });



            //Close the dialog fragment
            AddQuestionDialogFragment.this.getDialog().cancel();
    }


}
