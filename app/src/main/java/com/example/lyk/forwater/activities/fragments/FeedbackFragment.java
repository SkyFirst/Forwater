package com.example.lyk.forwater.activities.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lyk.forwater.R;
import com.example.lyk.forwater.activities.FeedbackActivity;
import com.example.lyk.forwater.activities.LinearLayoutEx;
import com.example.lyk.forwater.activities.ListViewEx;
import com.example.lyk.forwater.activities.RoundImageView;
import com.example.lyk.forwater.activities.TouchListener;
import com.example.lyk.forwater.services.UserService;
import com.example.lyk.forwater.utils.DataInfoUtils;
import com.example.lyk.forwater.utils.HttpConnect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lyk on 2016/11/27.
 */

public class FeedbackFragment extends Fragment {
    private ListViewEx mlistview;
    private int index = 0;
    private Runnable runnable;
    private UserService userService = new UserService();
    private List<Map<String, Object>> proxyFeedbacks;
    private Handler mHandler = new Handler();
    private boolean isFirstLoad = true;
    private MyAdapter myAdapter;
    public static Map<Integer, Bitmap> caches = new HashMap<>();
    private Context mContext;
    public View view;//test

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_feedback, null);
            mContext=inflater.getContext();
            mlistview = (ListViewEx) view.findViewById(R.id.feedback);
            runnable = new Runnable() {
                @Override
                public void run() {
                    final List<Map<String, Object>> feedbacks = userService.getFeedBacks(index, -1, mContext);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (feedbacks == null) {
                                if(getContext()!=null)
                                    Toast.makeText(mContext, "数据获取失败", Toast.LENGTH_SHORT).show();
                                if (!isFirstLoad)
                                    mlistview.setIsLoadingFalse(false);
                            } else if (feedbacks.size() > 0) {
                                if (!isFirstLoad)
                                    mlistview.setIsLoadingFalse(true);
                                if (myAdapter == null) {
                                    proxyFeedbacks = feedbacks;
                                    myAdapter = new MyAdapter(mContext, proxyFeedbacks);
                                    mlistview.setAdapter(myAdapter);
                                } else {
                                    proxyFeedbacks.addAll(feedbacks);
                                    myAdapter.notifyDataSetChanged();
                                }
                                index = Integer.parseInt(feedbacks.get(feedbacks.size() - 1).get("id").toString());
                                if (index == 0)
                                    index = -1;
                            } else {
                                if(getContext()!=null)
                                    Toast.makeText(mContext, "已经没有更多了", Toast.LENGTH_SHORT).show();
                                if (!isFirstLoad)
                                    mlistview.setIsLoadingFalse(true);
                            }
                            isFirstLoad = false;
                            if (feedbacks != null) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (Map<String, Object> feedback : feedbacks) {
                                            Bitmap header = userService.getHeader(feedback.get("name").toString(), HttpConnect.URL + "getimg");
                                            if (header != null) {
                                                feedback.put("img", header);
                                            }
                                            mHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    myAdapter.notifyDataSetChanged();
                                                }
                                            });

                                        }
                                    }
                                }).start();

                            }

                        }
                    });
                }
            };
            mlistview.setHeader((RelativeLayout) view.findViewById(R.id.header));
            mlistview.setLoadingEvent(runnable);
            new Thread(runnable).start();
        }
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }

    private final class ViewHolder {
        public RoundImageView imageView;
        public TextView name;
        public TextView time;
        public TextView content;
        public View top;
        public View cancle;
    }

    private class MyAdapter extends BaseAdapter {

        private Context context;
        private List<Map<String, Object>> feedbacks;
        private LinearLayoutEx linearLayoutEx;

        public MyAdapter(Context context, List<Map<String, Object>> feedbacks) {
            this.context = context;
            this.feedbacks = feedbacks;
        }

        @Override
        public int getCount() {
            return feedbacks.size();
        }

        @Override
        public Object getItem(int i) {
            return feedbacks.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public android.view.View getView(final int i, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_feedback, null);
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView) convertView.findViewById(R.id.name);
                linearLayoutEx = (LinearLayoutEx) convertView.findViewById(R.id.scroll);
                viewHolder.top = addButton(linearLayoutEx, "置顶");
                viewHolder.cancle = addButton(linearLayoutEx, "删除");
                viewHolder.content = (TextView) convertView.findViewById(R.id.content);
                viewHolder.time = (TextView) convertView.findViewById(R.id.time);
                viewHolder.imageView = (RoundImageView) convertView.findViewById(R.id.img);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.content.setText(feedbacks.get(i).get("content").toString());
            viewHolder.time.setText(feedbacks.get(i).get("time").toString());
            viewHolder.name.setText(feedbacks.get(i).get("name").toString());
            viewHolder.imageView.setImageBitmap((Bitmap) feedbacks.get(i).get("img"));
            viewHolder.cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayoutEx parent = (LinearLayoutEx) view.getParent();
                    parent.mScroller.startScroll(parent.getScrollX(), 0, -parent.getScrollX(), 0, 100);
                    parent.invalidate();
                    parent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            feedbacks.remove(i);
                            notifyDataSetChanged();
                        }
                    }, 100);
                }
            });
            viewHolder.cancle.setBackgroundResource(R.drawable.bg_del);
            viewHolder.top.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayoutEx parent = (LinearLayoutEx) view.getParent();
                    parent.mScroller.startScroll(parent.getScrollX(), 0, -parent.getScrollX(), 0, 100);
                    parent.invalidate();
                    parent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Map<String, Object> item = feedbacks.get(i);
                            feedbacks.remove(i);
                            feedbacks.add(0, item);
                            notifyDataSetChanged();
                        }
                    }, 100);
                }
            });
            viewHolder.top.setBackgroundResource(R.drawable.bg_add);
            TouchListener touchListener = new TouchListener(Integer.parseInt(feedbacks.get(i).get("id").toString()), viewHolder.name.getText().toString(),
                    viewHolder.content.getText().toString(), viewHolder.time.getText().toString(), getContext(), FeedbackActivity.class);
            ((LinearLayoutEx) viewHolder.top.getParent()).setTouchListener(touchListener);
            caches.put(Integer.parseInt(feedbacks.get(i).get("id").toString()), (Bitmap) feedbacks.get(i).get("img"));
            return convertView;
        }
    }

    private View addButton(LinearLayout linearLayout, String text) {
        Button button = new Button(mContext);
        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(DataInfoUtils.dip2px(mContext, 80), ViewGroup.LayoutParams.MATCH_PARENT);
        marginLayoutParams.setMargins(0, 0, 0, 0);
        button.setLayoutParams(marginLayoutParams);
        button.setText(text);
        linearLayout.addView(button);
        return button;
    }


}
