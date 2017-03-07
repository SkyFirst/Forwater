package com.example.lyk.forwater.services;

import android.content.Context;

/**
 * Created by lyk on 2016/11/28.
 */

public interface ITempWOrS {
    void save(String data, boolean water, Context context);
    String get(boolean eater,Context context);
}
