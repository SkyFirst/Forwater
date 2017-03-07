package com.example.lyk.forwater.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lyk.forwater.R;
import com.example.lyk.forwater.activities.fragments.FeedbackFragment;
import com.example.lyk.forwater.services.UserService;

import java.util.List;
import java.util.Map;

import static com.example.lyk.forwater.R.id.load;

public class FeedbackActivity extends AppCompatActivity implements View.OnTouchListener{

    private Toolbar mToolbar;
    private LinearLayout parent;
    private ImageView loadImg;
    private TextView loadText;
    private int index = 0;
    private int pid;
    private boolean isLoading = false;
    private Handler mHandler = new Handler();
    private Animation animation;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            final List<Map<String, Object>> feedbacks = new UserService().getFeedBacks(index, pid, getApplicationContext());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    isLoading = false;
                    if(animation!=null)
                        animation.cancel();
                    loadImg.setVisibility(View.GONE);
                    loadText.setVisibility(View.VISIBLE);
                    if (feedbacks != null && feedbacks.size() == 0) {
                        loadText.setText("已经没有更多了");
                    } else if (feedbacks != null && feedbacks.size() > 0) {
                        loadText.setText("点击加载");
                        for (Map<String, Object> feedback : feedbacks) {
                            addTextView(feedback.get("name").toString(), feedback.get("content").toString());
                        }
                        index=Integer.parseInt(feedbacks.get(feedbacks.size()-1).get("id").toString());
                        if(index==0)
                            index=-1;
                    } else {
                        loadText.setText("加载失败，请点击重试");
                    }
                }
            });

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.mipmap.back);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //添加动画
                finish();
            }
        });
        ((TextView) findViewById(R.id.tv_title)).setText("反馈");
        Bundle bundle = getIntent().getExtras();
        ((TextView) findViewById(R.id.name)).setText(bundle.getString("name"));
        ((TextView) findViewById(R.id.time)).setText(bundle.getString("time"));
        ((TextView) findViewById(R.id.content)).setText(bundle.getString("content"));
        pid = bundle.getInt("id");
        ((RoundImageView) findViewById(R.id.img)).setImageBitmap(FeedbackFragment.caches.get(pid));
        loadImg = (ImageView) findViewById(R.id.loadimg);
        loadText = (TextView) findViewById(load);
        loadText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLoading) {
                    isLoading = true;
                     animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setDuration(2000);
                    animation.setRepeatCount(Animation.INFINITE);
                    loadImg.setVisibility(View.VISIBLE);
                    loadImg.startAnimation(animation);
                    loadText.setText("正在加载。。。");
                    new Thread(runnable).start();

                }
            }
        });
        parent = (LinearLayout) findViewById(R.id.activity_feedback);
        new Thread(runnable).start();

    }

    private void addTextView(String name, String Content) {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setText(Html.fromHtml("<font color='#0000ff'>" + name + "</font>:" + Content));
        textView.setTextColor(getResources().getColor(android.R.color.black));
        textView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        parent.addView(textView);
    }
    @Override
    public boolean onTouch(View view,MotionEvent event)
    {
        View v=getWindow().getDecorView();
        v.layout(300,0,v.getRight(),v.getBottom());
        return  true;
    }
}
