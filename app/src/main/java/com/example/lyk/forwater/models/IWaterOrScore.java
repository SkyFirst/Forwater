package com.example.lyk.forwater.models;

import android.content.Context;

/**
 * Created by lyk on 2016/11/28.
 */

public interface IWaterOrScore {
    void saveData(String data, boolean water, Context context);
    String getData(boolean eater,Context context);

}
