package com.example.lyk.forwater.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.lyk.forwater.R;
import com.example.lyk.forwater.models.IName;
import com.example.lyk.forwater.models.Name;
import com.example.lyk.forwater.utils.DataInfoUtils;
import com.example.lyk.forwater.utils.HttpConnect;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lyk on 2016/12/8.
 */

public class UserService implements IUserService {
    private IName iName = new Name();

    @Override
    public boolean register(String url, Object obj) {
        String result = HttpConnect.Connect(url, obj);
        if (result == null)
            return false;
        return true;
    }

    public boolean login(String url, Object obj) {
        String result = HttpConnect.Connect(url, obj);
        if (result == null)
            return false;
        return true;
    }

    @Override
    public void saveName(String name, Context context) {
        iName.saveNamebyShareP(name, context);
    }

    @Override
    public boolean sendComment(String name, String content, String type, String id, String url) {
        List<String> list = new ArrayList<>();
        list.add(name);
        list.add(content);
        list.add(type);
        list.add(id);
        if (HttpConnect.Connect(url, list) == null)
            return false;
        return true;
    }

    @Override
    public boolean praise(String name, String type, int id) {
        Map<String, String> maps = new HashMap<>();
        maps.put("name", name);
        maps.put("id", id + "");
        maps.put("type", type);
        String res = HttpConnect.Connect(HttpConnect.URL + "praise", maps);
        if (res != null)
            return true;
        return false;
    }

    @Override
    public List<Map<String, Object>> getComment(String url, String id, String index, int type) {
        String result = HttpConnect.Connect(url, type + " " + index.trim() + " " + id.trim());
        List<Map<String, Object>> lists = new ArrayList<>();
        if (result!=null&&result.equals("{\"size\":0}")) {
            lists.add(null);
            return lists;
        }
        Map<String, Object> map;
        if (result != null) {
            JSONArray jsonArray = JSONArray.fromObject(result);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                map = new HashMap<>();
                map.put("id", jsonObject.getString("id"));
                map.put("time", jsonObject.getString("time"));
                map.put("name", jsonObject.getString("name"));
                map.put("content", jsonObject.getString("content"));
                try {
                    JSONArray jsonArray1 = jsonObject.getJSONArray("replay");
                    List<Map<String, String>> mapList = null;
                    if (jsonArray1 != null) {
                        mapList = new ArrayList<>();
                        Map<String, String> map1;
                        for (int j = 0; j < jsonArray1.size(); j++) {
                            map1 = new HashMap<>();
                            JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                            map1.put("rep", jsonObject1.getString("rep"));
                            map1.put("rec", jsonObject1.getString("rec"));
                            map1.put("con", jsonObject1.getString("con"));
                            map1.put("cid", jsonObject1.getString("cid"));
                            map1.put("rid", jsonObject1.getString("rid"));
                            map1.put("prid", jsonObject1.getString("prid"));
                            mapList.add(map1);
                        }
                        mapList = DataInfoUtils.sort(mapList);
                    }
                    if (mapList != null) {
                        map.put("replay", mapList);
                    }
                } catch (Exception e) {

                }

                lists.add(map);
            }
            return lists;

        }
        return null;
    }

    @Override
    public boolean sendHeader(Bitmap bitmap, String name, String url) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        String img = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
        try {
            stream.flush();
            stream.close();
            List<String> para = new ArrayList<>();
            para.add(img);
            para.add(name);
            if (HttpConnect.Connect(url, para) != null)
                return true;
        } catch (Exception e) {

        }
        return false;
    }

    @Override
    public Bitmap getHeader(String name, String url) {
        String img = null;
        if ((img = HttpConnect.Connect(url, name)) != null) {
            JSONObject jsonObject = JSONObject.fromObject(img);
            String res = jsonObject.getString("img");
            byte[] bt = Base64.decode(res, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bt, 0, bt.length);
            return bitmap;
        }
        return null;
    }

    @Override
    public boolean sendMsg(String fromer, String toer, String time, String content) {
        Map<String, String> req = new HashMap<>();
        req.put("fromer", fromer);
        req.put("toer", toer);
        req.put("time", time);
        req.put("content", content);
        Object result = HttpConnect.Connect(HttpConnect.URL + "sendmsg", req);
        if (result != null)
            return true;
        return false;
    }

    @Override
    public List<Map<String, Object>> getMsg(String fromer, String toer, int index, String type) {
        Map<String, String> req = new HashMap<>();
        req.put("fromer", fromer);
        req.put("toer", toer);
        req.put("index", index + "");
        req.put("type", type);
        String result = HttpConnect.Connect(HttpConnect.URL + "msginfo", req);
        if (result == null)
            return null;
        List<Map<String, Object>> res = new ArrayList<>();
        if (result.equals(IUserService.NORESULT)) {
            res.add(null);
            return res;
        }
        JSONArray jsonArray = JSONArray.fromObject(result);
        for (int i = jsonArray.size() - 1; i >= 0; i--) {
            Map<String, Object> map = new HashMap<>();
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            map.put("fromer", jsonObject.getString("fromer"));
            map.put("time", jsonObject.getString("time"));
            map.put("content", jsonObject.getString("content"));
            map.put("id", jsonObject.getString("id"));
            res.add(map);
        }
        return res;
    }

    @Override
    public char sendReq(String fromer, String toer, String time) {
        Map<String, String> req = new HashMap<>();
        req.put("fromer", fromer);
        req.put("toer", toer);
        req.put("reqtime", time);
        String result = HttpConnect.Connect(HttpConnect.URL + "sendreq", req);
        if (result == null)
            return DataInfoUtils.FAILD;
        if (result.equals(OK))
            return DataInfoUtils.SUCCESS;
        return DataInfoUtils.HAS;
    }

    @Override
    public char receiveReq(String fromer, String toer, String time) {
        Map<String, String> req = new HashMap<>();
        req.put("fromer", fromer);
        req.put("toer", toer);
        req.put("reptime", time);
        String result = HttpConnect.Connect(HttpConnect.URL + "receivereq", req);
        if (result == null)
            return DataInfoUtils.FAILD;
        if (result.equals(OK))
            return DataInfoUtils.SUCCESS;
        return DataInfoUtils.HAS;
    }

    @Override
    public int sendReplay(String cont, String rep, String rec, String id, String rid, String url) {
        List<String> list = new ArrayList<>();
        list.add(cont);
        list.add(rep);
        list.add(rec);
        list.add(id);
        list.add(rid);
        String s = HttpConnect.Connect(url, list);
        if (s != null) {
            JSONObject jsonObject = JSONObject.fromObject(s);
            int res = jsonObject.getInt("res");
            return res;
        }
        return -1;
    }

    @Override
    public char isFriend(String fromer, String toer) {
        Map<String, String> req = new HashMap<>();
        req.put("fromer", fromer);
        req.put("toer", toer);
        String result = HttpConnect.Connect(HttpConnect.URL + "isfriend", req);
        if (result == null)
            return DataInfoUtils.FAILD;//数据访问失败
        else if (result.equals(OK))
            return DataInfoUtils.SUCCESS;
        return DataInfoUtils.HAS;//不是
    }

    @Override
    public String getName(Context c) {
        return iName.getName(c);
    }

    @Override
    public char sendRep(String fromer, String toer, String rep) {
        List<String> req = new ArrayList<>();
        req.add(fromer);
        req.add(toer);
        req.add(rep);
        String result = HttpConnect.Connect(HttpConnect.URL + "sendrep", req);
        if(result==null)
            return DataInfoUtils.FAILD;
        if (result.equals(OK)) {
            return DataInfoUtils.SUCCESS;
        }
        return DataInfoUtils.FAILD;
    }

    @Override
    public List<Map<String, Object>> getFeedBacks(int index,int pid,Context context) {
        String res = HttpConnect.Connect(HttpConnect.URL + "feedback", index+" "+pid);
        if (res==null) {
            return null;
        }
        List<Map<String,Object>> feedbacks=new ArrayList<>();
        if(res.equals(NORESULT))
            return feedbacks;
        JSONArray jsonArray = JSONArray.fromObject(res);
        for (int i = 0; i < jsonArray.size(); i++)
        {
            JSONObject jsonObject=jsonArray.getJSONObject(i);
            Map<String,Object> feedback=new HashMap<>();
            feedback.put("name",jsonObject.getString("name"));
            feedback.put("content",jsonObject.getString("content"));
            feedback.put("id",jsonObject.getInt("id"));
            feedback.put("time",jsonObject.getString("time"));
            feedback.put("img",BitmapFactory.decodeResource(context.getResources(), R.mipmap.water));
            feedbacks.add(feedback);
        }
        return feedbacks;

    }
}
