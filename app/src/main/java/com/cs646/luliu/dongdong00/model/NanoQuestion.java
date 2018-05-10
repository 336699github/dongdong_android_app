package com.cs646.luliu.dongdong00.model;

/**
 * Created by luliu on 4/30/16.
 */
public class NanoQuestion {
    private String question_content;
    private String user_name;
    private String encoded_email;
    private String subject;
    private String school_level;

    //required public constructor
    public NanoQuestion(){

    }

    public NanoQuestion(String encoded_email,String user_name,String question_content,String subject, String school_level ){
        this.encoded_email=encoded_email;
        this.user_name=user_name;
        this.question_content=question_content;
        this.subject=subject;
        this.school_level=school_level;
    }

    public String getSchool_level() {
        return school_level;
    }

    public String getQuestion_content() {
        return question_content;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getEncoded_email() {
        return encoded_email;
    }

    public String getSubject() {
        return subject;
    }
}
