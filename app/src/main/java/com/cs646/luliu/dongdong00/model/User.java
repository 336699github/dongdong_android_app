package com.cs646.luliu.dongdong00.model;

import com.cs646.luliu.dongdong00.R;
import com.cs646.luliu.dongdong00.utils.Constants;
import com.firebase.client.ServerValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by luliu on 4/16/16.
 */

//Defines the data structure for User Objects
public class User  implements Serializable{
    private String user_name, school_level;
    private String encoded_email;
    private String user_tagline;
    private int num_of_thanks;
    private int num_of_followers;
    private int num_of_followings;
    private int total_num_of_answers;
    private int total_num_of_questions;
    private HashMap<String,Object> timestampJoined;
    private HashMap<String,Object> timestampLastChanged;
    private boolean hasLoggedInWithPassword;

    /**
     * Required public constructor
     */
    public User()  {
    }

    /**
     * Use this constructor to create new User.
     *
     */
    public User(String user_name,String school_level, String encoded_email, HashMap<String, Object> timestampJoined) {
        this.user_name = user_name;
        this.school_level=school_level;
        this.encoded_email = encoded_email;
        this.timestampJoined = timestampJoined;
        this.hasLoggedInWithPassword = false;
        this.user_tagline= "Please Add Your Description";
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampNowObject;
    }



    public String getUser_tagline() {
        return user_tagline;
    }

    public int getNum_of_followings() {
        return num_of_followings;
    }

    public int getNum_of_followers() {
        return num_of_followers;
    }


    public boolean isHasLoggedInWithPassword() {
        return hasLoggedInWithPassword;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getSchool_level() {
        return school_level;
    }


    public String getEncoded_email() {
        return encoded_email;
    }


    public int getNum_of_thanks() {
        return num_of_thanks;
    }


    public int getTotal_num_of_answers() {
        return total_num_of_answers;
    }


    public int getTotal_num_of_questions() {
        return total_num_of_questions;
    }

    public HashMap<String, Object> getTimestampJoined() {
        return timestampJoined;
    }

    public HashMap<String, Object> getTimestampLastChanged() {
        return timestampLastChanged;
    }
}
