package com.thenorthside.beekeeper.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class EnSaveState {

    Context context;
    String saveName;
    SharedPreferences sp;

    public EnSaveState(Context context, String saveName) {
        this.context = context;
        this.saveName = saveName;
        sp = context.getSharedPreferences(saveName,context.MODE_PRIVATE);
    }

    public void EnSetState(int key){
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("Key",key);
        editor.apply();
    }

    public int getEnState(){
        return sp.getInt("Key",0);
    }

}
