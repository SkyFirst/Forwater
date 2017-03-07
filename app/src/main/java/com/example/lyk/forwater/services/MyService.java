package com.example.lyk.forwater.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

import com.example.lyk.forwater.R;
import com.example.lyk.forwater.activities.AddFriendActivity;
import com.example.lyk.forwater.models.Name;
import com.example.lyk.forwater.utils.HttpConnect;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;

public class MyService extends Service {
    private UserService userService = new UserService();
    private Handler handler = new Handler();

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {

                    }
                    String name = userService.getName(MyService.this);
                    if (name != null) {
                        Intent intent = null;
                        ArrayList<String> names = null;
                        String res = HttpConnect.Connect(HttpConnect.URL + "pushfriend", name);
                        if (res != null && !res.equals(UserService.NORESULT)) {
                            JSONArray jsonArray = JSONArray.fromObject(res);
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if (!new Name().isPushNmae(jsonObject.getString("name"), MyService.this)) {
                                    if (intent == null) {
                                        intent = new Intent(MyService.this, AddFriendActivity.class);
                                        names = new ArrayList<String>();
                                    }
                                    names.add(jsonObject.getString("name") + " " + jsonObject.getString("time"));

                                }
                            }

                        }
                        if (intent != null) {
                            Bundle bundle=new Bundle();
                            bundle.putStringArrayList("friends",names);
                            intent.putExtras(bundle);
                            NotificationManager notificationManager = (NotificationManager) MyService.this.getSystemService(NOTIFICATION_SERVICE);
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MyService.this);
                            builder.setContentTitle("有人请求添加你为好友").setContentText("water").
                                    setContentIntent(PendingIntent.getActivity(MyService.this, 0, intent, 0)).setAutoCancel(true)
                                    .setWhen(System.currentTimeMillis()).setSmallIcon(R.mipmap.water).setLargeIcon(BitmapFactory.decodeResource(MyService.this.getResources(), R.mipmap.water));
                            notificationManager.notify(1, builder.build());

                        }
                    }
                }

            }
        }).start();
        super.onCreate();
    }


}
