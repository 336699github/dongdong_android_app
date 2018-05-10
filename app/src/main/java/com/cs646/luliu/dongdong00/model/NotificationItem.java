package com.cs646.luliu.dongdong00.model;

import android.app.Notification;

import java.util.HashMap;

/**
 * Created by luliu on 5/4/16.
 */
public class NotificationItem {
    private String sender_encoded_email,sender_user_name, receiver_encoded_email,action_type, action_message,question_id,answer_id;
    private HashMap<String, Object> timestampCreated;

    public NotificationItem(){

    }

    public NotificationItem(String sender_user_name,String sender_encoded_email,String action_type,String action_message,String question_id,String answer_id,HashMap<String, Object>timestampCreated){
        this.sender_user_name=sender_user_name;
        this.sender_encoded_email=sender_encoded_email;
        this.action_type=action_type;
        this.action_message=action_message;
        this.question_id=question_id;
        this.answer_id=answer_id;
        this.timestampCreated=timestampCreated;
    }

    public String getSender_encoded_email() {
        return sender_encoded_email;
    }

    public String getSender_user_name() {
        return sender_user_name;
    }

    public String getReceiver_encoded_email() {
        return receiver_encoded_email;
    }

    public String getAction_type() {
        return action_type;
    }

    public String getAction_message() {
        return action_message;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public String getAnswer_id() {
        return answer_id;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }
}
