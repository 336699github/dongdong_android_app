package com.cs646.luliu.dongdong00.model;

import com.cs646.luliu.dongdong00.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.firebase.client.ServerValue;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by luliu on 4/17/16.
 */
public class Question implements Serializable {

    private String question_content;
    private String user_name;
    private String mEncodedEmail;
    private String subject;
    private String school_level;
    private String school_name;
    private int num_of_answers;
    private int num_of_comments;
    private int num_of_watchers;
    private boolean hasAcceptedAnswer;
    private HashMap<String, Object> timestampLastChanged;
    private HashMap<String, Object> timestampCreated;
    private HashMap<String, Object> timestampLastChangedReverse;

    //required public constructor
    public Question(){

    }

    public Question(String question_content,String mEncodedEmail,String user_name,String subject, String school_level,HashMap<String, Object> timestampCreated){
        this.question_content=question_content;
        this.user_name=user_name;
        this.mEncodedEmail=mEncodedEmail;
        this.subject=subject;
        this.school_level=school_level;
        this.school_name=school_name;
        this.timestampCreated = timestampCreated;
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampNowObject;
        this.timestampLastChangedReverse = null;

    }

    public String getmEncodedEmail() {
        return mEncodedEmail;
    }

    public String getQuestion_content() {
        return question_content;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getSubject() {
        return subject;
    }

    public String getSchool_level() {
        return school_level;
    }

    public String getSchool_name() {
        return school_name;
    }

    public int getNum_of_answers() {
        return num_of_answers;
    }

    public int getNum_of_comments() {
        return num_of_comments;
    }

    public int getNum_of_watchers() {
        return num_of_watchers;
    }

    public boolean isHasAcceptedAnswer() {
        return hasAcceptedAnswer;
    }

    public HashMap<String, Object> getTimestampLastChanged() {
        return timestampLastChanged;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public HashMap<String, Object> getTimestampLastChangedReverse() {
        return timestampLastChangedReverse;
    }


    @JsonIgnore
    public long getTimestampLastChangedLong() {

        return (long) timestampLastChanged.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    @JsonIgnore
    public long getTimestampCreatedLong() {
        return (long) timestampLastChanged.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    @JsonIgnore
    public long getTimestampLastChangedReverseLong() {

        return (long) timestampLastChangedReverse.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }
    public void setTimestampLastChangedToNow() {
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampNowObject;
    }

}
