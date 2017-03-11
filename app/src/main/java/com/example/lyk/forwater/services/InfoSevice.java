package com.example.lyk.forwater.services;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.lyk.forwater.utils.DataInfoUtils;
import com.example.lyk.forwater.utils.HttpConnect;

import net.sf.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import static android.util.Base64.decode;

/**
 * Created by lyk on 2016/11/29.
 */

public class InfoSevice implements IInfoService {
    @Override
    /**
     * 解析新闻信息
     */
    public Object[] getInfo(String url, String time) {
        Object[] objects = null;
        String result = HttpConnect.Connect(url, time);
        if (result == null)
            return null;
        try {
            JSONObject jsonObject = JSONObject.fromObject(result);
            /*
            *size 新闻中的图片数量
            * 6 表示 文章标题 文章发布时间 文章访问量 文章ID
            * 文章的文字内容 文章被点赞的次数
            * */
            int size = jsonObject.getInt("size");
            objects = new Object[size + 6];
            if (size != 0) {
                for (int i = 0; i < size; i++) {
                    try {
                        byte[] by = decode(jsonObject.getString("" + i), android.util.Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(by, 0, by.length);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byteArrayOutputStream.close();
                        objects[i] = bitmap;
                    } catch (Exception e) {
                        objects = null;
                        break;
                    }


                }
            }
            if (objects == null) {
                objects = new Object[6];
                size = 0;
            }
            objects[size] = jsonObject.getString("title");
            objects[size + 1] = jsonObject.getString("time");
            objects[size + 2] = jsonObject.getString("content").toString();
            objects[size + 3] = jsonObject.getInt("id");
            objects[size + 4] = jsonObject.getInt("acess");
            objects[size + 5] = jsonObject.getInt("gcomt");
            return objects;
        }catch (Exception e)
        {

        }
      return null;
    }

    @Override
    public Bitmap[] getImage(String url, Context context) {
        Bitmap[] bitmaps = new Bitmap[6];
        String result = HttpConnect.getInfo(url);
        if (result == null) {
            return null;
        }
        try
        {

        }catch (Exception e)
        {

        }
        JSONObject jsonObject = JSONObject.fromObject(result);
        for (int i = 0; i < 6; i++) {
            try {
            String file = jsonObject.getString("img" + (i + 1));
            byte[] img = Base64.decode(file, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
                if (bitmap == null)
                    return null;
                String filename = "/sdcard/Android/data/" + context.getPackageName() + "/water/";
                File dir = new File(filename);
                if (!dir.exists())
                    dir.mkdirs();
                File imgFile = new File(filename + "img" + i + ".jpg");
                if (imgFile.exists())
                    imgFile.delete();
                imgFile.createNewFile();
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imgFile));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
                bitmaps[i] = bitmap;
            } catch (Exception e) {
                return null;
            }

        }
        DataInfoUtils.savemark(context);
        return bitmaps;
    }

}
