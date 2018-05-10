package com.cs646.luliu.dongdong00.ui;

/**
 * Created by luliu on 4/16/16.
 */


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.cs646.luliu.dongdong00.R;
import com.cs646.luliu.dongdong00.model.User;
import com.cs646.luliu.dongdong00.ui.login.CreateAccountActivity;
import com.cs646.luliu.dongdong00.ui.login.LoginActivity;
import com.cs646.luliu.dongdong00.utils.Constants;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * BaseActivity class is used as a base class for all activities in the app
 * It implements GoogleApiClient callbacks to enable "Logout" in all activities
 * and defines variables that are being shared across all activities
 */
public abstract class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private static final String LOG_TAG=BaseActivity.class.getSimpleName();
    protected String mProvider , mEncodedEmail;
    protected GoogleApiClient mGoogleApiClient;
    protected Firebase.AuthStateListener mAuthListener;
    protected Firebase mFirebaseRef,mCurrentUserRef;
    protected User mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        /*setup the Google API object to allow Google logins */
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        /**
         * Build a GoogleApiClient with access to the Google Sign-In API and the options specified by gso.
         */
        /*Setup the Google API object to allow Google+ logins*/
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this/*FragmentActivity*/,this/*OnConnectionFailedListener*/)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        /**
         * Getting mProvider and mEncodedEmail from SharedPreferences
         */
        final SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        mEncodedEmail=sp.getString(Constants.KEY_ENCODED_EMAIL,null);
        Log.i(LOG_TAG,"mEncodedEmail="+mEncodedEmail);
        mProvider=sp.getString(Constants.KEY_PROVIDER,null);
        Log.i(LOG_TAG,"mProvider="+mProvider);
        /**
         * add AuthStateListener to monitor logout
         */
        if (!((this instanceof LoginActivity) || (this instanceof CreateAccountActivity))) {
            mFirebaseRef = new Firebase(Constants.FIREBASE_URL);
            mAuthListener = new Firebase.AuthStateListener() {
                @Override
                public void onAuthStateChanged(AuthData authData) {
                     /* The user has been logged out */
                    if (authData == null) {
                        /* Clear out shared preferences */
                        SharedPreferences.Editor spe = sp.edit();
                        spe.putString(Constants.KEY_ENCODED_EMAIL, null);
                        spe.putString(Constants.KEY_PROVIDER, null);

                        takeUserToLoginScreenOnUnAuth();
                    }
                }
            };
            mFirebaseRef.addAuthStateListener(mAuthListener);
        }




    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        /* Cleanup the AuthStateListener */
        if (!((this instanceof LoginActivity) || (this instanceof CreateAccountActivity))) {
            mFirebaseRef.removeAuthStateListener(mAuthListener);
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        if (id == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void initializeBackground(LinearLayout linearLayout) {

        /**
         * Set different background image for landscape and portrait layouts
         */
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            linearLayout.setBackgroundResource(R.drawable.background_loginscreen_land);
        } else {
            linearLayout.setBackgroundResource(R.drawable.background_loginscreen);
        }
    }

    /**
     * Logs out the user from their current session and starts LoginActivity.
     * Also disconnects the mGoogleApiClient if connected and provider is Google
     */
    public void logout() {

        /* Logout if mProvider is not null */
        if (mProvider != null) {
            mFirebaseRef.unauth();

            if (mProvider.equals(Constants.GOOGLE_PROVIDER)) {

                /* Logout from Google+ */
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                //nothing
                                Log.i("in BaseActivity","GoogleApiclient signout callback"+status);
                            }
                        });
            }
        }
        Log.i("BaseActivity", "mProvider is null, didnt sign in");
    }

    private void takeUserToLoginScreenOnUnAuth() {
        /* Move user to LoginActivity, and remove the backstack */
        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.i(LOG_TAG, "Connection to google api client failed"+connectionResult);
    }

}
