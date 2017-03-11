package com.example.lyk.forwater.activities.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.lyk.forwater.R;
import com.example.lyk.forwater.activities.ContactActivity;
import com.example.lyk.forwater.activities.MyFeedbackActivity;
import com.example.lyk.forwater.activities.MyFriendsActivity;
import com.example.lyk.forwater.activities.RankingActivity;
import com.example.lyk.forwater.activities.RecentActivity;

import java.util.HashMap;
import java.util.Map;

;

/**
 * Created by lyk on 2016/11/27.
 */

public class MyFragment extends Fragment {
    private RelativeLayout mMyFriend;
    private RelativeLayout mPaiming;
    private RelativeLayout mMyFeedback;
    private RelativeLayout mRecent;
    private RelativeLayout mContact;
    private View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view==null)
        {
            view = inflater.inflate(R.layout.fragment_my, null);
            mMyFriend = (RelativeLayout) view.findViewById(R.id.friend);
            mPaiming = (RelativeLayout) view.findViewById(R.id.paiming);
            mMyFeedback = (RelativeLayout) view.findViewById(R.id.myfeedback);
            mRecent = (RelativeLayout) view.findViewById(R.id.recentwater);
            mContact = (RelativeLayout) view.findViewById(R.id.contact);
            views = new HashMap<>();
            views.put(0, mMyFriend);
            views.put(1, mPaiming);
            views.put(2, mMyFeedback);
            views.put(3, mRecent);
            views.put(4, mContact);

            addTouchListener(mMyFriend, 0);
            addTouchListener(mPaiming,1);
            addTouchListener(mMyFeedback, 2);
            addTouchListener(mRecent, 3);
            addTouchListener(mContact, 4);
        }
        return view;
    }

    private Map<Integer, View> views;

    private void addTouchListener(View view, final int index ){
        view.setTag(index);
        AidTouchListener aidTouchListener=new AidTouchListener() {
            @Override
            public void start() {
                switch (index)
                {
                    case 0:
                        startActivity(new Intent(getContext(),MyFriendsActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(getContext(), RankingActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(getContext(), MyFeedbackActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(getContext(), RecentActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(getContext(), ContactActivity.class));
                        break;
                }
            }
        };
        TouchListener touchListener = new TouchListener();
        touchListener.setViews(views);
        touchListener.setAidTouchListener(aidTouchListener);
        view.setOnTouchListener(touchListener);
    }


}
