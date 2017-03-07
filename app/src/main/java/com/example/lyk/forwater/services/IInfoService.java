package com.example.lyk.forwater.services;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by lyk on 2016/11/29.
 */

public interface IInfoService {
    Object[] getInfo(String url,String time);
    Bitmap[] getImage(String url, Context context);
}
