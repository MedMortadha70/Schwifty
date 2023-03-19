package com.thenorthside.beekeeper.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManagerAr {

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context mContext;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    public static final  String EMAIL = "EMAIL";

    public SessionManagerAr(Context mContext) {
        this.mContext = mContext;

        sharedPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();

    }

    public void createSessionAr(String email, Object o){

        editor.putBoolean(LOGIN, true);
        editor.putString(EMAIL,email);
        editor.commit();
        editor.apply();

    }

    public boolean isLogin(){
        return sharedPreferences.getBoolean(LOGIN, false);

    }



    public HashMap<String, String> fetchUserDetail(){

        HashMap<String, String> user = new HashMap<>();
        user.put(EMAIL, sharedPreferences.getString(EMAIL, null));

        return user;

    }


}
