package com.cs646.luliu.dongdong00.model;

import android.os.Parcelable;

import com.cs646.luliu.dongdong00.utils.Constants;
import com.firebase.client.ServerValue;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by luliu on 4/17/16.
 */
public class Answer implements Serializable {
    private String question_id;
    private String answer_content;
    private String question_content;
    private String mEncodedEmail;
    private String user_name;
    private String subject;
    private int num_of_comments;
    private int num_of_upvotes;
    private int num_of_downvotes;
    private int num_of_fav;
    private HashMap<String, Object> timestampLastChanged;
    private HashMap<String, Object> timestampCreated;


    //required public constructor
    public Answer(){

    }


    public Answer(String question_id,String question_content,String answer_content,String mEncodedEmail,String user_name,String subject,HashMap<String, Object> timestampCreated){
        this.question_id=question_id;
        this.answer_content=answer_content;
        this.mEncodedEmail=mEncodedEmail;
        this.user_name=user_name;
        this.question_content=question_content;
        this.subject=subject;

        this.timestampCreated = timestampCreated;
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampNowObject;

    }

    public int getNum_of_fav() {
        return num_of_fav;
    }

    public String getQuestion_content() {
        return question_content;
    }

    public String getSubject() {
        return subject;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public String getAnswer_content() {
        return answer_content;
    }

    public String getmEncodedEmail() {
        return mEncodedEmail;
    }

    public String getUser_name() {
        return user_name;
    }

    public int getNum_of_comments() {
        return num_of_comments;
    }

    public int getNum_of_upvotes() {
        return num_of_upvotes;
    }

    public int getNum_of_downvotes() {
        return num_of_downvotes;
    }

    public HashMap<String, Object> getTimestampLastChanged() {
        return timestampLastChanged;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

}
