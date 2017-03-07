package com.example.lyk.forwater.services;

import com.example.lyk.forwater.utils.HttpConnect;

import net.sf.json.JSONObject;


/**
 * Created by lyk on 2016/11/28.
 */

public class ScoreService implements IScoreService {
    @Override
    public String getScore(String url,String name) {
        String reault = HttpConnect.Connect(url, name);
        if (null == reault)
            return null;
        try {
            JSONObject jsonObject=JSONObject.fromObject(reault);
            return (String)jsonObject.get("score");
        } catch (Exception e)
        {
            System.out.print(e.toString());
        }
        return null;

    }
}
