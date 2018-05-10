package com.cs646.luliu.dongdong00.utils;

import com.cs646.luliu.dongdong00.BuildConfig;

/**
 * Created by luliu on 4/16/16.
 */
public final class Constants {

    /**
     * Constants related to locations in Firebase
     */
    public static final String FIREBASE_LOCATION_ANSWERS_LIST = "answersList";
    public static final String FIREBASE_LOCATION_USERS = "users";
    public static final String FIREBASE_LOCATION_UID_MAPPINGS = "uidMappings";
    public static final String FIREBASE_LOCATION_QUESTIONS_LIST="questionsList";
    public static final String FIREBASE_LOCATION_FOLLOWERS="followersList";
    public static final String FIREBASE_LOCATION_FOLLOWINGS="followingsList";
    public static final String FIREBASE_LOCATION_WATCHING_QUESTIONS_LIST="watching_questions_list";
    public static final String FIREBASE_LOCATION_SAVED_ANSWERS_LIST="saved_answers_list";
    public static final String FIREBASE_LOCATION_NUM_OF_WATCHERS="num_of_watchers";
    public static final String FIREBASE_LOCATION_NUM_OF_FAV="num_of_fav";
    public static final String FIREBASE_LOCATION_TOTAL_NUM_OF_ANSWERS="total_num_of_answers";
    public static final String FIREBASE_LOCATION_NUM_OF_ANSWERS="num_of_answers";
    public static final String FIREBASE_LOCATION_NUM_OF_THANKS="num_of_thanks";
    public static final String FIREBASE_LOCATION_NUM_OF_DOWNVOTES="num_of_downvotes";
    public static final String FIREBASE_LOCATION_NUM_OF_UPVOTES="num_of_upvotes";
    public static final String FIREBASE_LOCATION_NOTIFICATION_LIST="notificationList";
    public static final String FIREBASE_LOCATION_TOTAL_NUM_OF_QUESTIONS="total_num_of_questions";
    public static final String FIREBASE_LOCATION_USER_NAME="user_name";
    public static final String FIREBASE_LOCATION_USER_TAGLINE="user_tagline";
    public static final String FIREBASE_LOCATION_SCHOOL_LEVEL="school_level";
    public static final String FIREBASE_LOCATION_NUM_OF_FOLLOWERS="num_of_followers";
    public static final String FIREBASE_LOCATION_NUM_OF_FOLLOWINGS="num_of_followings";



    /**
     * Constants for Firebase object properties
     */

    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";
    public static final String FIREBASE_PROPERTY_EMAIL = "email";
    public static final String FIREBASE_PROPERTY_USER_HAS_LOGGED_IN_WITH_PASSWORD = "hasLoggedInWithPassword";
    public static final String FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED_REVERSE = "timestampLastChangedReverse";


    /**
     * Constants for Firebase URL
     */
    public static final String FIREBASE_URL = BuildConfig.UNIQUE_FIREBASE_ROOT_URL;
    public static final String FIREBASE_URL_QUESTIONS_LIST=FIREBASE_URL+"/"+FIREBASE_LOCATION_QUESTIONS_LIST;
    public static final String FIREBASE_URL_ANSWERS_LIST = FIREBASE_URL + "/" + FIREBASE_LOCATION_ANSWERS_LIST;
    public static final String FIREBASE_URL_USERS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USERS;
    public static final String FIREBASE_URL_FOLLOWERS_LIST=FIREBASE_URL+"/"+FIREBASE_LOCATION_FOLLOWERS;
    public static final String FIREBASE_URL_FOLLOWINGS_LIST=FIREBASE_URL+"/"+FIREBASE_LOCATION_FOLLOWINGS;
    public static final String FIREBASE_URL_WATCHING_QUESTIONS_LIST=FIREBASE_URL+"/"+FIREBASE_LOCATION_WATCHING_QUESTIONS_LIST;
    public static final String FIREBASE_URL_SAVED_ANSWERS_LIST=FIREBASE_URL+"/"+FIREBASE_LOCATION_SAVED_ANSWERS_LIST;
    public static final String FIREBASE_URL_NOTIFICATION_LIST=FIREBASE_URL+"/"+FIREBASE_LOCATION_NOTIFICATION_LIST;


    /**
     * Constants for bundles, extras and shared preferences keys
     */
    public static final String KEY_LIST_NAME = "LIST_NAME";
    public static final String KEY_LIST_URL="LIST_URL";
    public static final String KEY_SIGNUP_EMAIL = "SIGNUP_EMAIL";
    public static final String KEY_PROVIDER = "PROVIDER";
    public static final String KEY_ENCODED_EMAIL = "ENCODED_EMAIL";
    public static final String KEY_USER_NAME="USER_NAME";
    public static final String KEY_QUESTION_ID="QUESTION_ID";
    public static final String KEY_ANSWER_ID="ANSWER_ID";
    public static final String KEY_QUESTION_CONTENT="QUESTION_CONTENT";
    public static final String KEY_QUESTION_OBJECT="QUESTION_OBJECT";
    public static final String KEY_USER_OBJECT="USER_OBJECT";
    public static final String KEY_GOOGLE_EMAIL = "GOOGLE_EMAIL";


    /**
     * Constants for Firebase login
     */
    public static final String PASSWORD_PROVIDER = "password";
    public static final String GOOGLE_PROVIDER = "google";
    public static final String PROVIDER_DATA_DISPLAY_NAME = "displayName";


    /**
     * Constant for sorting
     */


    /**
     * list names
     * */
    public static final String FOLLOWER_LIST_NAME="FOLLOWERS:";
    public static final String FOLLOWING_LIST_NAME="FOLLOWINGS:";
    public static final String WATCHING_QUESTION_LIST_NAME="WATCHING QUESTIONS:";
    public static final String SAVED_ANSWERS_LIST_NAME="SAVED ANSWERS:";

}

