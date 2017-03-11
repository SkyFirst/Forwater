package com.example.lyk.forwater.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.lyk.forwater.R;
import com.example.lyk.forwater.services.UserService;
import com.example.lyk.forwater.utils.DataInfoUtils;
import com.example.lyk.forwater.utils.HttpConnect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.lyk.forwater.R.id.loadimg;

public class MyFriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ListView listView;
    private int isScroll;
    private boolean isLoading;
    private boolean isClick = true;
    private int index = 0;
    private int width;
    private int oldx;
    private int oldy;
    private View decorView;
    private ValueAnimator valueAnimator;
    private List<Map<String, Object>> infos;
    private MyAdapter myAdapter;
    private PopupWindow popupWindow;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            final UserService userService = new UserService();
            final List<String> friends = userService.getFriends(userService.getName(MyFriendsActivity.this), index);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (popupWindow != null && popupWindow.isShowing())
                        popupWindow.dismiss();
                    if (friends == null) {
                        popupOrDown(getLayoutInflater().inflate(R.layout.popupwindow_load, null), getResources().getDrawable(R.mipmap.e_11), "加载失败");
                    } else if (friends.size() == 0) {
                        popupOrDown(getLayoutInflater().inflate(R.layout.popupwindow_load, null), getResources().getDrawable(R.mipmap.e_15), "没有更多了");
                    } else {
                        popupOrDown(getLayoutInflater().inflate(R.layout.popupwindow_load, null), getResources().getDrawable(R.mipmap.e_15), "加载成功");
                        index = Integer.parseInt(friends.get(friends.size() - 1).split(" ")[1]) + 1;
                        final List<Map<String, Object>> myinfos = new ArrayList<Map<String, Object>>();
                        for (int i = 0; i < friends.size(); i++) {
                            Map<String, Object> myinfo = new HashMap<String, Object>();
                            myinfo.put("name", friends.get(i).split(" ")[0]);
                            myinfo.put("head", BitmapFactory.decodeResource(getResources(), R.mipmap.water));
                            myinfos.add(myinfo);
                        }
                        if (myAdapter != null) {
                            infos.addAll(myinfos);
                            myAdapter.notifyDataSetChanged();
                        } else {
                            infos = myinfos;
                            myAdapter = new MyAdapter(MyFriendsActivity.this, infos);
                            listView.setAdapter(myAdapter);
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < friends.size(); i++) {
                                    Bitmap header = userService.getHeader(friends.get(i).split(" ")[0], HttpConnect.URL + "getimg");
                                    if (header != null)
                                        myinfos.get(i).put("head", header);
                                }
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        myAdapter.notifyDataSetChanged();
                                    }
                                });
                            }

                        }).start();
                    }
                    popdismiss();


                }
            });

        }
    };

    private class ViewHolder {
        public RoundImageView header;
        public TextView name;
    }

    private class MyAdapter extends BaseAdapter {
        private Context context;
        private List<Map<String, Object>> friends;

        public MyAdapter(Context context, List<Map<String, Object>> friends) {
            this.context = context;
            this.friends = friends;
        }

        @Override
        public int getCount() {
            // How many items are in the data set represented by this Adapter.(在此适配器中所代表的数据集中的条目数)
            return friends.size();
        }

        @Override
        public Object getItem(int position) {
            // Get the data item associated with the specified position in the data set.(获取数据集中与指定索引对应的数据项)
            return friends.get(position);
        }

        @Override
        public long getItemId(int position) {
            // Get the row id associated with the specified position in the list.(取在列表中与指定索引对应的行id)
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_myfriends, null);
                viewHolder.header = (RoundImageView) convertView.findViewById(R.id.head);
                viewHolder.name = (TextView) convertView.findViewById(R.id.name);
                convertView.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.name.setText(friends.get(position).get("name").toString());
            viewHolder.header.setImageBitmap((Bitmap) friends.get(position).get("head"));
            return convertView;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);
        init();
    }

    private void init() {
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
        final TextView title = (TextView) mToolbar.findViewById(R.id.tv_title);
        title.setText("我的好友");
        listView = (ListView) findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!isClick)
                    return;
                Intent intent = new Intent(MyFriendsActivity.this, ChatActivity.class);
                intent.putExtra("isfriend", true);
                intent.putExtra("name", ((TextView) view.findViewById(R.id.name)).getText().toString());
                startActivity(intent);
            }
        });
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                boolean result = false;
                int x = (int) motionEvent.getRawX();
                int y = (int) motionEvent.getRawY();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (valueAnimator != null && valueAnimator.isRunning())
                            valueAnimator.cancel();
                        isClick = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isClick = false;
                        if (Math.abs(oldx - x) > Math.abs(oldy - y) && Math.abs((int) decorView.getX()) < width) {
                            result = true;
                            decorView.scrollBy((oldx - x) / 2, 0);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        int dis = decorView.getScrollX();
                        if (Math.abs(dis) >= width / 2) {
                            valueAnimator = ValueAnimator.ofInt(dis, dis < 0 ? -width : width);
                        } else
                            valueAnimator = ValueAnimator.ofInt(dis, 0);
                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                decorView.scrollTo((int) valueAnimator.getAnimatedValue(), 0);
                            }
                        });
                        valueAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if ((int) ((ValueAnimator) animation).getAnimatedValue() != 0)
                                    finish();
                                super.onAnimationEnd(animation);
                            }
                        });
                        valueAnimator.setDuration((int) (Math.abs(dis) / (float) width * 1000));
                        valueAnimator.start();
                        break;


                }
                oldx = x;
                oldy = y;
                return result;
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                isScroll = i;
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (listView.getChildCount() == 0)
                    return;
                Rect rect = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                if (i + i1 == i2 && listView.getChildAt(listView.getChildCount() - 1).getBottom() == rect.height() - mToolbar.getBottom()) {
                    if (!isLoading && !popupWindow.isShowing()) {
                        isLoading = true;
                        popupOrDown(animation(), null, null);
                        new Thread(runnable).start();
                    }
                }
            }
        });
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        decorView = getWindow().getDecorView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {

                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        popupOrDown(animation(), null, null);
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {

                }
                runnable.run();

            }
        }).start();
    }

    private void popupOrDown(View view, Drawable load, String prompt) {
        if(isFinishing())
        {
           return;
        }
        if (load != null) {
            view.findViewById(loadimg).setBackground(load);
        }
        if (prompt != null) {
            ((TextView) view.findViewById(R.id.load)).setText(prompt);
        }
        popupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, DataInfoUtils.dip2px(this, 50));
        popupWindow.setAnimationStyle(R.style.popup_anim);
        popupWindow.showAtLocation(((ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0), Gravity.BOTTOM, 0, 0);

    }

    private void popdismiss() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (Exception e) {

                }
                if (popupWindow != null && popupWindow.isShowing()) {
                    {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                popupWindow.dismiss();
                            }
                        });

                    }
                }
                isLoading = false;
            }
        }).start();

    }

    private View animation() {
        View v = getLayoutInflater().inflate(R.layout.popupwindow_load, null);
        Animation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(2000);
        animation.setRepeatCount(Animation.INFINITE);
        v.findViewById(R.id.loadimg).startAnimation(animation);
        return v;
    }

    @Override
    protected void onDestroy()
    {
        if (popupWindow != null && popupWindow.isShowing())
            popupWindow.dismiss();
        super.onDestroy();
    }

}
