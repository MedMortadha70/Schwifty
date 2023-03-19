package com.thenorthside.beekeeper.Helpers;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.thenorthside.beekeeper.LoginActivity;
import com.thenorthside.beekeeper.MainActivity;
import com.thenorthside.beekeeper.SettingsActivity;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context mContext;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    public static final  String EMAIL = "EMAIL";

    public SessionManager(Context mContext) {
        this.mContext = mContext;

        sharedPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();

    }

    public void createSession(String email, Object o){

        editor.putBoolean(LOGIN, true);
        editor.putString(EMAIL,email);
        editor.commit();
        editor.apply();

    }

    public boolean isLogin(){
        return sharedPreferences.getBoolean(LOGIN, false);

    }

    public void checkLogin(){

        if (!this.isLogin()){
            Intent i = new Intent(mContext, LoginActivity.class);
            mContext.startActivity(i);
            ((MainActivity) mContext).finish();
        }
    }


    public HashMap<String, String> fetchUserDetail(){

        HashMap<String, String> user = new HashMap<>();
        user.put(EMAIL, sharedPreferences.getString(EMAIL, null));

        return user;

    }

    public void logOut(){

        editor.clear();
        editor.commit();
        Intent i = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(i);
        ((SettingsActivity) mContext).finish();

    }

    public void logOutAccountDel(){

        editor.clear();
        editor.commit();
        Intent i = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(i);
        ((SettingsActivity) mContext).finish();

    }

}
