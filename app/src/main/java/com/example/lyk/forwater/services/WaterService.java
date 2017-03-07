package com.example.lyk.forwater.services;

import com.example.lyk.forwater.utils.HttpConnect;

import net.sf.json.JSONObject;

/**
 * Created by lyk on 2016/11/28.
 */

public class WaterService implements IWaterService {
    @Override
    public String getWater(String url, String name) {
        String result= HttpConnect.Connect(url,name);
        if(result==null)
            return null;
        JSONObject jsonObject=JSONObject.fromObject(result);
        String water=jsonObject.get("water").toString();
        return water;
    }
}
