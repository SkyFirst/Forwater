package com.example.lyk.forwater.activities.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.lyk.forwater.R;
import com.example.lyk.forwater.activities.InfoActivity;
import com.example.lyk.forwater.activities.MySwipeRefreshLayout;
import com.example.lyk.forwater.activities.MyTransformation;
import com.example.lyk.forwater.services.IInfoService;
import com.example.lyk.forwater.services.InfoSevice;
import com.example.lyk.forwater.utils.DataInfoUtils;
import com.example.lyk.forwater.utils.HttpConnect;
import com.example.lyk.forwater.utils.StringUtils;

import java.util.Calendar;

/**
 * Created by lyk on 2016/11/27.
 */

public class InfoFragment extends Fragment {
    public static int SAVEWATER=0;
    public static int ENVIRONMENT=1;
    public static int HEALTH=2;
    private View view;
    private LinearLayout msave;
    private LinearLayout mhealth;
    private LinearLayout menviro;
    private ViewPager mViewPager;
    private RelativeLayout rwater;
    private  RelativeLayout rhealh;
    private RelativeLayout renviro;
    private int[] imgaeId={R.mipmap.ienviro,R.mipmap.iwater,R.mipmap.ihealth,R.mipmap.comment};
    private final int TIME=0;
    private int imgId=0;
    private Bitmap[] bitmaps=null;
    private boolean mDestory=false;
    private Context mContext;
    private Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            if(msg.what==TIME&&!mDestory)
            {
                imgId=imgId+1;
                imgId=imgId%6;
                if(mViewPager!=null)
                {
                    mViewPager.setCurrentItem(imgId);
                    setCircleBg(imgId);
                }

            }
            super.handleMessage(msg);
        }
    };
    private MySwipeRefreshLayout swipeRefreshLayout;
    private ImageView mCircles[]=new ImageView[6];
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view==null)
        {
            mContext=inflater.getContext();
            view=inflater.inflate(R.layout.fragemnt_info,null);
            msave=(LinearLayout)view.findViewById(R.id.isave);
            menviro=(LinearLayout)view.findViewById(R.id.ienviro);
            mhealth=(LinearLayout)view.findViewById(R.id.ihealth);
            rwater=(RelativeLayout)view.findViewById(R.id.savewater);
            renviro=(RelativeLayout)view.findViewById(R.id.environment);
            rhealh=(RelativeLayout)view.findViewById(R.id.health);
            initeCircle(view);
            swipeRefreshLayout=(MySwipeRefreshLayout)view.findViewById(R.id.swipe_container);
            swipeRefreshLayout.setDistanceToTriggerSync(400);
            swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.darkblue));
            swipeRefreshLayout.setBackgroundColor(getResources().getColor(R.color.darkblue));
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getImage();
                }
            });
            rwater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    msave.setVisibility(View.VISIBLE);
                    menviro.setVisibility(View.GONE);
                    mhealth.setVisibility(View.GONE);
                }
            });
            renviro.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    msave.setVisibility(View.GONE);
                    menviro.setVisibility(View.VISIBLE);
                    mhealth.setVisibility(View.GONE);
                }
            });
            rhealh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    msave.setVisibility(View.GONE);
                    menviro.setVisibility(View.GONE);
                    mhealth.setVisibility(View.VISIBLE);
                }
            });
            mViewPager=(ViewPager)view.findViewById(R.id.viewPager);
            mViewPager.setPageTransformer(true, new MyTransformation());
            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    setCircleBg(position);
                    imgId=position;
                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            msave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getContext(), InfoActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("type","savewater");
                    bundle.putInt("typenum",SAVEWATER);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            mhealth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getContext(), InfoActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("type","health");
                    bundle.putInt("typenum",HEALTH);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            menviro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getContext(), InfoActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("type","environment");
                    bundle.putInt("type",ENVIRONMENT);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            getImage();
        }
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        mDestory=false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!mDestory) {
                    try {
                        Thread.sleep(2000);
                        handler.sendEmptyMessage(TIME);

                    } catch (Exception e)
                    {

                    }
                }
            }
        }).start();

        return view;
    }
    private class MyAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return bitmaps.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageButton imageButton=new ImageButton(mContext);
            imageButton.setBackground(new BitmapDrawable(bitmaps[position]));
            container.addView(imageButton);
            return imageButton;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
    }
    private  void getImage()
    {
        String oldtime=DataInfoUtils.getmark(mContext);
        if(oldtime!=null&&StringUtils.isDateEq(StringUtils.realconvertCalendar(Calendar.getInstance()),oldtime))
        {
            bitmaps=new Bitmap[6];
            for(int i=0;i<bitmaps.length;i++)
            {
                bitmaps[i]= BitmapFactory.decodeFile("/sdcard/Android/data/"+mContext.getPackageName()+"/water/"+"img"+i+".jpg");
            }
            mViewPager.setAdapter(new MyAdapter());
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                IInfoService infoService=new InfoSevice();
                bitmaps=infoService.getImage(HttpConnect.URL+"photo",mContext);
                if(bitmaps!=null)
                {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mViewPager.setAdapter(new MyAdapter() );
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
                else
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(mContext,"获取失败",Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        }).start();
    }
    private void initeCircle(View view)
    {
        int[] ids={R.id.circle1,R.id.circle2,R.id.circle3,R.id.circle4,R.id.circle5,R.id.circle6};
        for(int i=0;i<ids.length;i++)
        {
            final int ii=i;
            mCircles[i]=(ImageView)view.findViewById(ids[i]);
            mCircles[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mViewPager.setCurrentItem(ii);
                    setCircleBg(ii);
                    imgId=ii;
                }
            });
        }

    }
    private  void setCircleBg(int index)
    {
        for(int i=0;i<mCircles.length;i++)
        {
            mCircles[i].setBackground(getResources().getDrawable(R.drawable.circle_null));
        }
        mCircles[index].setBackground(getResources().getDrawable(R.drawable.circle));
    }

    @Override
    public void onDestroyView() {
        mDestory=true;
        super.onDestroyView();
    }
}
