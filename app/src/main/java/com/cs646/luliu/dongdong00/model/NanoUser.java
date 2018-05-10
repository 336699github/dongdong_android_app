package com.cs646.luliu.dongdong00.model;

/**
 * Created by luliu on 4/27/16.
 */
public class NanoUser {
    String user_name,school_level,user_tagline,encoded_email;
    public NanoUser(){

    }
    public NanoUser(String user_name,String school_level,String user_tagline,String encoded_email){
        this.user_name=user_name;
        this.school_level=school_level;
        this.user_tagline=user_tagline;
        this.encoded_email=encoded_email;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getSchool_level() {
        return school_level;
    }

    public String getUser_tagline() {
        return user_tagline;
    }

    public String getEncoded_email() {
        return encoded_email;
    }
}
