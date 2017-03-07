package com.example.lyk.forwater.services;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.List;
import java.util.Map;

/**
 * Created by lyk on 2016/12/8.
 */

public interface IUserService {

    public final static String OK="{\"res\":\"ok\"}";//成功
    public final static String HAS="{\"res\":\"dup\"}";//重复
    public final static String NORESULT="{\"res\":0}";//已经没有更多了
    boolean register(String url,Object obj);
    boolean login(String url,Object obj);
    void saveName(String name,Context context);
    String getName(Context context);
    boolean sendComment(String name, String content, String type, String id,String url);
    List<Map<String,Object>>  getComment(String url,String id,String index,int type);
    int sendReplay(String cont,String rep,String rec,String id,String rid,String url);
    boolean sendHeader(Bitmap bitmap,String name,String url);
    Bitmap getHeader(String name,String url);
    boolean praise(String name,String type,int id);
    boolean sendMsg(String fromer,String toer,String time,String content);
    List<Map<String,Object>> getMsg(String fromer,String toer,int index,String type);
    char sendReq(String fromer,String toer,String time);
    char receiveReq(String fromer,String toer,String time);
    char isFriend(String fromer,String toer);
    char sendRep(String fromer,String toer,String rep);
    List<Map<String,Object>> getFeedBacks(int index,int pid,Context context);

}
