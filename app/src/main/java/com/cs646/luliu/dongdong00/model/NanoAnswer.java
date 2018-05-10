package com.cs646.luliu.dongdong00.model;

/**
 * Created by luliu on 4/30/16.
 */
public class NanoAnswer {
    String encoded_email, user_name,question_id,question_content,answer_content;


    public NanoAnswer(){

    }

    public NanoAnswer(String encoded_email,String user_name,String question_id,String question_content,String answer_content){
        this.encoded_email=encoded_email;
        this.user_name=user_name;
        this.question_id=question_id;
        this.question_content=question_content;
        this.answer_content=answer_content;


    }

    public String getEncoded_email() {
        return encoded_email;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public String getQuestion_content() {
        return question_content;
    }

    public String getAnswer_content() {
        return answer_content;
    }
}
