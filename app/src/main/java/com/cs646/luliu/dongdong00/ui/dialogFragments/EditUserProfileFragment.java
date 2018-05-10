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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.cs646.luliu.dongdong00.R;
import com.cs646.luliu.dongdong00.model.User;
import com.cs646.luliu.dongdong00.utils.Constants;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;

/**
 * Created by luliu on 5/6/16.
 */
public class EditUserProfileFragment extends DialogFragment {
    private User mMe;
    private String mMyEncodedEmail, mUsername, mSchoolLevel, mTagline;
    private EditText mUserNameEditText, mUserTaglineEditText;
    private Spinner mSchoolLevelSpinner;
    private String mNewUserName, mNewSchoolLevel, mNewTagline;
    private Firebase mFirebaseMyUserListURL;

    private DialogInterface.OnDismissListener onDismissListener;

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }


    /**
     * Public static constructor that creates fragment
     */
    public static EditUserProfileFragment newInstance(User mMe, String mMyEncodedEmail) {
        EditUserProfileFragment editUserProfileFragment = new EditUserProfileFragment();


        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.KEY_USER_OBJECT, mMe);
        bundle.putString(Constants.KEY_ENCODED_EMAIL, mMyEncodedEmail);
        editUserProfileFragment.setArguments(bundle);
        return editUserProfileFragment;
    }

    /**
     * Initialize instance variables with data from bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMe = (User) getArguments().getSerializable(Constants.KEY_USER_OBJECT);
        mMyEncodedEmail = getArguments().getString(Constants.KEY_ENCODED_EMAIL);
        mUsername = mMe.getUser_name();
        mSchoolLevel = mMe.getSchool_level();
        mTagline = mMe.getUser_tagline();


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
        View rootView = inflater.inflate(R.layout.dialog_edit_profile, null);
        mUserNameEditText = (EditText) rootView.findViewById(R.id.edit_user_name);
        mUserNameEditText.setText(mUsername);
        mSchoolLevelSpinner = (Spinner) rootView.findViewById(R.id.school_level_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> schoolLevelAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.school_level, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        schoolLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSchoolLevelSpinner.setAdapter(schoolLevelAdapter);
        mSchoolLevelSpinner.setSelection(1);


        mUserTaglineEditText = (EditText) rootView.findViewById(R.id.edit_user_tagline);
        mUserTaglineEditText.setText(mTagline);


        /* Inflate and set the layout for the dialog */
        builder.setView(rootView)
                /* Add action buttons */
                .setPositiveButton(R.string.positive_button_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mNewUserName = mUserNameEditText.getText().toString();
                        mNewSchoolLevel = mSchoolLevelSpinner.getSelectedItem().toString();
                        mNewTagline = mUserTaglineEditText.getText().toString();

                        if (mNewUserName.equals("") || mNewTagline.equals("") || mNewSchoolLevel.equals("")) {
                            Toast.makeText(getContext(), "Please complete content", Toast.LENGTH_SHORT).show();
                            Log.i("add user profile", "UserName=" + mNewUserName + " school level=" + mNewSchoolLevel + " tagline=" + mNewTagline);
                        } else {
                            addUserProfile();
                        }
                    }
                })
                .setNegativeButton(R.string.negative_button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return builder.create();

    }

    //Edit new user profile in database
    public void addUserProfile() {
        // update users list


        //Create Firebase references
        mFirebaseMyUserListURL = new Firebase(Constants.FIREBASE_URL_USERS).child(mMyEncodedEmail);

        //HashMap for data to update
        final HashMap<String, Object> mappingToUpdate = new HashMap<>();

        mappingToUpdate.put("/" + Constants.FIREBASE_LOCATION_USER_NAME, mNewUserName);
        mappingToUpdate.put("/" + Constants.FIREBASE_LOCATION_SCHOOL_LEVEL, mNewSchoolLevel);
        mappingToUpdate.put("/" + Constants.FIREBASE_LOCATION_USER_TAGLINE, mNewTagline);

         /* Try to update the database */
        mFirebaseMyUserListURL.updateChildren(mappingToUpdate, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {

            }
        });
        //Close the dialog fragment
        EditUserProfileFragment.this.getDialog().dismiss();


    }
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

}


