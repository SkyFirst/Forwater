package com.example.lyk.forwater.models;

import android.content.Context;

/**
 * Created by lyk on 2016/11/28.
 */

public interface IName {
    void saveNamebyShareP(String name, Context context);
    String getName(Context context);
    boolean isPushNmae(String name,Context context);
}
