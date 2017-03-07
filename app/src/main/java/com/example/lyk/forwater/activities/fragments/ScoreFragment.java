package com.example.lyk.forwater.activities.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lyk.forwater.R;
import com.example.lyk.forwater.services.IScoreService;
import com.example.lyk.forwater.services.ITempWOrS;
import com.example.lyk.forwater.services.IUserService;
import com.example.lyk.forwater.services.IWaterService;
import com.example.lyk.forwater.services.ScoreService;
import com.example.lyk.forwater.services.TempWOrS;
import com.example.lyk.forwater.services.UserService;
import com.example.lyk.forwater.services.WaterService;
import com.example.lyk.forwater.utils.DataInfoUtils;
import com.example.lyk.forwater.utils.HttpConnect;

import static com.example.lyk.forwater.utils.AnimationUtils.setAnimation;

/**
 * Created by lyk on 2016/11/27.
 */

public class ScoreFragment extends Fragment {

    private TextView mWater;
    private TextView mScore;
    private ImageView mIco;
    private Handler mhander;
    private static  final int SCORE=1;
    private static  final int WATER=2;
    private IUserService userService=new UserService();
    private ITempWOrS iTempWOrS=new TempWOrS();
    private  View view;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(view==null)
        {
            view=inflater.inflate(R.layout.fragment_score,null);
            mIco=(ImageView)view.findViewById(R.id.score);
            mWater=(TextView)view.findViewById(R.id.haswater);
            mContext=getContext();
            if(iTempWOrS.get(true,mContext)!=null)
            {
                mWater.setText(iTempWOrS.get(true,mContext));
            }
            mScore=(TextView)view.findViewById(R.id.hasscore);
            if(iTempWOrS.get(false,mContext)!=null)
            {
                mScore.setText(iTempWOrS.get(false,mContext));
            }
            mScore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final  String name=userService.getName(mContext);
                    if(name==null)
                    {
                        DataInfoUtils.login(mContext);
                        return;
                    }
                    DataInfoUtils.showOrDismissDlg(true,mContext);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                Thread.sleep(2000);
                            }
                            catch (Exception e){}
                            IScoreService scoreService=new ScoreService();
                            String s=scoreService.getScore(HttpConnect.URL+"userscore",name);
                            // DataInfoUtils.showOrDismissDlg(false,getContext());
                            Message message=new Message();
                            message.what=SCORE;
                            message.obj=s;
                            mhander.sendMessage(message);
                        }
                    }).start();
                }
            });
            mWater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final  String name=userService.getName(mContext);
                    if(name==null)
                    {
                        DataInfoUtils.login(mContext);
                        return;
                    }
                    DataInfoUtils.showOrDismissDlg(true,mContext);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                            }catch (Exception e){}
                            IWaterService waterService=new WaterService();
                            String result=waterService.getWater(HttpConnect.URL+"userwater",name);
                            Message message=new Message();
                            message.what=WATER;
                            message.obj=result;
                            mhander.sendMessage(message);
                        }
                    }).start();
                }
            });
            mhander=new Handler(){

                @Override
                public void handleMessage(Message msg) {
                    if(msg.what==SCORE)
                    {
                        if(getContext()!=null)
                            DataInfoUtils.showOrDismissDlg(false,getContext());
                        if(msg.obj==null&&getContext()!=null)
                        {
                            DataInfoUtils.showDataError(getContext());
                            return;
                        }
                        iTempWOrS.save(msg.obj.toString(),false,mContext);
                        mScore.setText(msg.obj.toString());
                        return;
                    }
                    else if(msg.what==WATER)
                    {
                        if(getContext()!=null)
                            DataInfoUtils.showOrDismissDlg(false,getContext());
                        if(msg.obj==null&&getContext()!=null)
                        {
                            DataInfoUtils.showDataError(getContext());
                            return;
                        }
                        iTempWOrS.save(msg.obj.toString(),true,mContext);
                        mWater.setText(msg.obj.toString()+" ml");
                    }
                    super.handleMessage(msg);
                }
            };
        }
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        setAnimation(mIco,new LinearInterpolator());
        return view;
    }

}
