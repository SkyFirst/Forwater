package com.example.lyk.forwater.models;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lyk on 2016/11/28.
 */

public class Name implements IName {


    @Override
    public void saveNamebyShareP(String name, Context context) {
        SharedPreferences sharedPreferences=context.getSharedPreferences("water",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("username",name);
        editor.commit();
    }

    @Override
    public String getName(Context context) {
        return context.getSharedPreferences("water",Context.MODE_PRIVATE).getString("username",null);
    }
    @Override
    public boolean isPushNmae(String name,Context context)
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences("water",Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean(name,false)==false) {
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean(name,true);
            editor.commit();
            return false;
        }
        else
            return true;
    }
}
