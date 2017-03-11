package com.example.lyk.forwater.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lyk.forwater.R;
import com.example.lyk.forwater.activities.LoginActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by lyk on 2016/11/28.
 */

public class DataInfoUtils {
   /* public static int SAVEWATER = 0;
    public static int ENVIRONMENT = 1;
    public static int HEALTH = 2;*/
    private static AlertDialog alertDialog;
    public static  final int maxResult=10;
    public static final char SUCCESS=0;
    public static final char FAILD=1;
    public static final char HAS=2;
    public static int[] emotions={R.mipmap.e_1,R.mipmap.e_2,R.mipmap.e_3,R.mipmap.e_4,R.mipmap.e_5,R.mipmap.e_6,R.mipmap.e_7,R.mipmap.e_8,R.mipmap.e_9,R.mipmap.e_10
            ,R.mipmap.e_11,R.mipmap.e_12,R.mipmap.e_13,R.mipmap.e_14,R.mipmap.e_15,R.mipmap.e_16,R.mipmap.e_17,R.mipmap.e_18,R.mipmap.e_19,
            R.mipmap.e_20,R.mipmap.e_21,R.mipmap.e_22,R.mipmap.e_23,R.mipmap.e_24};
    public static String getImgPos(Context context) {
        String filename = "/sdcard/Android/data/" + context.getPackageName() + "/water";
        return filename;
    }

    public static String getHeader(Context context) {
        return getImgPos(context) + "/header.jpg";
    }

    public static void showOrDismissDlg(boolean show, Context context) {
        if (show) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.loading);
            AnimationUtils.setAnimation(imageView, new AccelerateDecelerateInterpolator());
            builder.setCancelable(true);
            builder.setView(view);
            alertDialog = builder.create();
            alertDialog.show();
        } else {
            if (alertDialog != null && alertDialog.isShowing())
                alertDialog.dismiss();
            alertDialog = null;
        }
    }

    public static void
    showDataError(Context context) {
        if (context == null)
            return;
        Toast toast = Toast.makeText(context, "数据获取失败", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public static void savemark(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("water", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("infodate", StringUtils.realconvertCalendar(Calendar.getInstance()));
        editor.commit();
    }

    public static String getmark(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("water", Context.MODE_PRIVATE);
        return sharedPreferences.getString("infodate", null);
    }

    public static void saveFile(Bitmap bm, Context context) {

        File dirFile = new File(getImgPos(context));
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File myCaptureFile = new File(dirFile, "header.jpg");
        if (myCaptureFile.exists())
            myCaptureFile.delete();
        try {
            myCaptureFile.createNewFile();
            OutputStream bos = new FileOutputStream(myCaptureFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
        }
    }

    public static String getHeaderStr(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        String photodata = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
        try {
            baos.close();
        } catch (Exception e) {
        }

        return photodata;
    }

    public static void login(final Context context) {
        new AlertDialog.Builder(context)
                .setMessage("您还未登录,现在要登陆吗")
                .setPositiveButton("登陆", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("取消", null).setCancelable(true).show();
    }

    /**
     *
     * 对回复的内容进行整理排序
     *
     */
    public static List<Map<String, String>> sort(List<Map<String, String>> src) {
        List<Map<String, String>> end = new ArrayList<>();
        for (int i = 0; i < src.size(); i++) {
            if (src.get(i).get("rid").isEmpty()) {
                end.add(src.get(i));
                src.remove(i);
                i--;
            }
        }
        int i = 0;
        for (; i < end.size(); i++) {
            Map<String, String> end1 = end.get(i);
            for (int j = 0; j < src.size(); j++) {
                Map<String, String> src1 = src.get(j);
                if (src1.get("rid").equals(end1.get("prid"))) {
                    int k = 1;
                    while (i + k < end.size()) {
                        if (end.get((i + k)).get("rid").equals(end1.get("prid")))
                            k++;
                        else
                            break;
                    }
                    if (i + k < end.size())
                        end.add(i + k, src1);
                    else
                        end.add(src1);
                    src.remove(src1);
                    j--;
                }
            }
        }

        return end;
    }

    public static String getBitmapSize(Bitmap bitmap) {
        float size;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            size = bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            size = bitmap.getByteCount();
        }
        // 在低版本中用一行的字节x高度
        else
            size = bitmap.getRowBytes() * bitmap.getHeight();
        //earlier version
        if (size / 1024 < 1)
            return size + "B";
        if (size / 1024 / 1024 < 1)
            return (size / 1024) + "K";
        return (size / 1024 / 1024) + "M";
    }

    public static String download(Bitmap bitmap, String name) {
        String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "water";
        File file = new File(dir);
        if (!file.exists())
            file.mkdirs();
        File file1 = new File(file, name+".jpg");
        try {
            OutputStream os = new FileOutputStream(file1);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            return file1.getAbsolutePath();
        } catch (Exception e) {

        }
        return null;

    }
    public static  void showPopup(PopupWindow popupWindow,View Parent)
    {
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setAnimationStyle(R.style.popup_anim);
        popupWindow.showAtLocation(Parent,Gravity.BOTTOM,0,0);

    }

    public static  void  paeseEmotion(String con, TextView textView)
    {

        while(true)
        {
            int index=-1;
            String emotion;
            int real=0;
            for(int i=0;i<emotions.length;i++)
            {
                int temp=0;
                emotion=(emotions[i]+"").trim();
                if(i==0)
                {
                    if((temp=con.indexOf(emotion))>=0) {
                        index = temp;
                        real=i;
                    }
                }
                else
                {
                    if(((temp=con.indexOf(emotion))>=0&&(index<0||index>temp)))
                    {
                        index=temp;
                        real=i;
                    }
                }
            }
            if(index<0) {
                textView.append(con);
                break;
            }
            else
            {
                textView.append(con.substring(0,index));
                textView.append(convertImg((emotions[real]+"").trim(),textView.getContext()));
               con= con.substring(index+(emotions[real]+"").trim().length());
            }
        }
    }
    public static  GridView getGridview(Context context,int i)
    {
        GridView gridView=(GridView) LayoutInflater.from(context).inflate(R.layout.pager_emotion,null);
        gridView.setAdapter(new MyAdapter(i,emotions,gridView.getContext()));
        return gridView;
    }
    public static class  MyAdapter extends BaseAdapter
    {
        private int index;
        private int[] mEmotios;
        private Context mContext;
        public MyAdapter(int i,int[] emotos,Context context) {
            super();
            index=i;
            mEmotios=emotos;
            mContext=context;
        }

        @Override
        public int getCount() {
            return index==2?4:10;
        }

        @Override
        public Object getItem(int i) {
            return  mEmotios[index*10+i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
           if(view==null)
               view=LayoutInflater.from(mContext).inflate(R.layout.gridview_item,null);
            ((ImageView)view.findViewById(R.id.src)).setImageResource(Integer.parseInt(getItem(i).toString()));
            return view;
        }


    }
    public static  class MyPager extends PagerAdapter
    {
        private  View content[];
        public MyPager(View content[])
        {
            super();
            this.content=content;

        }
        @Override

        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public int getCount() {
            return content.length;
        }

        @Override
        public void destroyItem(ViewGroup container,int position, Object object) {
            container.removeView(content[position]);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(content[position]);
            return content[position];
        }
    }
    public static  void setPagerClick(final EditText editText, GridView gridView, final int index)
    {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int id=DataInfoUtils.emotions[index*10+i];
                editText.append(convertImg((id+"").trim(),editText.getContext()));
            }
        });

    }
    public static  SpannableString convertImg(String emotion,Context context)
    {
        SpannableString spannableString = new SpannableString(emotion);
        Drawable drawable =context.getResources().getDrawable(Integer.parseInt(emotion));
        drawable.setBounds(0,0,drawable.getIntrinsicWidth()/5,drawable.getIntrinsicHeight()/5);
        ImageSpan span = new ImageSpan(drawable,ImageSpan.ALIGN_BASELINE);
        spannableString.setSpan(span,0,spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return  spannableString;
    }

}
