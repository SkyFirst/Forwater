package com.example.lyk.forwater.models;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

/**
 * Created by lyk on 2016/11/28.
 */

public class WaterOrScore implements IWaterOrScore {

    @Override
    public void saveData(String data, boolean water,Context context) {
        SharedPreferences sharedPreferences=context.getSharedPreferences("water",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        Calendar calendar=Calendar.getInstance();
        String date="";
        date=date+calendar.get(Calendar.YEAR)+calendar.get(Calendar.MONTH)+calendar.get(Calendar.DAY_OF_MONTH);
        editor.putString("date",date);
        if(water)
        {
            editor.putString("water",data);
        }
        else
        {
            editor.putString("score",data);
        }
        editor.commit();
    }

    @Override
    public String getData(boolean eater,Context context) {
        SharedPreferences sharedPreferences=context.getSharedPreferences("water",Context.MODE_PRIVATE);
        if(sharedPreferences.getString("date",null)==null)
            return null;
        Calendar calendar=Calendar.getInstance();
        String date="";
        date=date+calendar.get(Calendar.YEAR)+calendar.get(Calendar.MONTH)+calendar.get(Calendar.DAY_OF_MONTH);
        if(!date.equals(sharedPreferences.getString("date",null)))
        {
            return null;
        }
        if(eater)
        {
            return  sharedPreferences.getString("water",null);
        }
        return sharedPreferences.getString("score",null);
    }
}
