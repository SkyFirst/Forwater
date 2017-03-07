package com.example.lyk.forwater.services;

import android.content.Context;

import com.example.lyk.forwater.models.IWaterOrScore;
import com.example.lyk.forwater.models.WaterOrScore;

/**
 * Created by lyk on 2016/11/28.
 */

public class TempWOrS implements ITempWOrS {
    private IWaterOrScore waterOrScore=new WaterOrScore();
    @Override
    public void save(String data, boolean water, Context context) {
        waterOrScore.saveData(data,water,context);
    }

    @Override
    public String get(boolean eater, Context context) {
        return waterOrScore.getData(eater,context);
    }
}
