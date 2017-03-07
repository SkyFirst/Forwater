package com.example.lyk.forwater.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by lyk on 2017/3/5.
 */

public  class  TouchListener {
    private int id;
    private String name;
    private String content;
    private String time;
    private Context context;

    public TouchListener(int id,String name,String content,String time,Context context,Class aClass)
    {
        this.id=id;
        this.name=name;
        this.content=content;
        this.time=time;
        this.context=context;
        this.aClass=aClass;
    }
    public TouchListener()
    {

    }
    public Class getaClass() {
        return aClass;
    }

    public void setaClass(Class aClass) {
        this.aClass = aClass;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private Class aClass;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public  void onTouch(){
        Intent intent=new Intent(context,aClass);
        Bundle bundle=new Bundle();
        bundle.putInt("id",id);
        bundle.putString("content",content);
        bundle.putString("name",name);
        bundle.putString("time",time);
        intent.putExtras(bundle);
        context.startActivity(intent);
    };
}
