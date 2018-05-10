package com.cs646.luliu.dongdong00;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.firebase.client.Firebase;

/*Includes one-time initialization of Firebase related code*/
public class DongDongApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /*Initialize Firebase*/
        Firebase.setAndroidContext(this);
        /*Enable disk persistence*/
        Firebase.getDefaultConfig().setPersistenceEnabled(true);


    }
}
