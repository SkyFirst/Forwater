package com.example.lyk.forwater.utils;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lyk on 2016/11/28.
 */
public class HttpConnect {

    //192.168.206.196:8888
    //1.10
    public static  final String URL="http://192.168.206.196:8888/Water/";
    public static String Connect(String url, Object content) {
        HttpURLConnection httpURLConnection=null;
        try {
            URL url1 = new URL(url);
            httpURLConnection = (HttpURLConnection) url1.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.connect();
            OutputStream outputStream = httpURLConnection.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(content);
            objectOutputStream.flush();
            objectOutputStream.close();
            if (httpURLConnection.getResponseCode() == 200) {
                InputStream inputStream = httpURLConnection.getInputStream();
                String result = readMyInputStream(inputStream);
                httpURLConnection.disconnect();
                if(result==null||result.equals("{}"))
                    return null;
                return result;
            }
            else
                return  null;
        } catch (Exception e) {
            Log.d("e", e.toString());
            if (httpURLConnection != null)
                httpURLConnection.disconnect();
            return null;
        }

    }
    public static String readMyInputStream(InputStream is) {
        byte[] result;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer))!=-1) {
                baos.write(buffer,0,len);
            }
            is.close();
            baos.close();
            result = baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return new String(result);
    }
    public static String getInfo(String url)
    {
        HttpURLConnection httpURLConnection=null;
        try {
            URL url1 = new URL(url);
            httpURLConnection = (HttpURLConnection) url1.openConnection();
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");
            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == 200) {
                InputStream inputStream = httpURLConnection.getInputStream();
                String result = readMyInputStream(inputStream);
                httpURLConnection.disconnect();
                if(result==null||result.equals("{}"))
                    return null;
                return result;
            }
        } catch (Exception e) {
                System.out.print(e.toString());
        }
        if (httpURLConnection != null)
            httpURLConnection.disconnect();
        return null;
    }

}
