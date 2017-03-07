package com.example.lyk.forwater.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lyk.forwater.R;
import com.example.lyk.forwater.services.IUserService;
import com.example.lyk.forwater.services.UserService;
import com.example.lyk.forwater.utils.DataInfoUtils;
import com.example.lyk.forwater.utils.HttpConnect;
import com.example.lyk.forwater.utils.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ListView mShow;
    private View mHeader;
    private EditText mContent;
    private Button mSend;
    private List<Map<String, Object>> contents = new ArrayList<>();
    private IUserService userService = new UserService();
    private int index = 0;
    private int indexTop = 0;
    private boolean isFriend = false;
    private Handler mHandler = new Handler();
    private String mFriend;
    private String mName;
    private MyAdapter myAdapter = null;
    private Map<String, Bitmap> header_map = new HashMap<>();
    private View left, right, center;
    private boolean mIsHasHeader = false;
    private boolean mIsScroll = false;
    private boolean mIsFirstLoad = true;

    private class ViewHolderLeft {
        public RoundImageView header;
        public TextView content;
    }

    private class ViewHolderRight {
        public RoundImageView header;
        public TextView content;
    }

    private class MyAdapter extends BaseAdapter {


        private Context mContext;
        private List<Map<String, Object>> mData;
        private LayoutInflater mInflater = null;
        private String mName;
        private final int USER = 0;
        private final int FRIEND = 1;

        public MyAdapter(Context context, List<Map<String, Object>> data, String name) {
            mContext = context;
            mData = data;
            mName = name;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (mData.get(position).get("fromer").equals(mName))
                return USER;
            return FRIEND;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int i) {
            return mData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            int type = getItemViewType(i);

            if (type == USER) {
                ViewHolderRight viewHolderRight = null;
                if (view == null) {
                    view = mInflater.inflate(R.layout.list_right, null);
                    viewHolderRight = new ViewHolderRight();
                    viewHolderRight.header = (RoundImageView) view.findViewById(R.id.l_header);
                    viewHolderRight.content = (TextView) view.findViewById(R.id.l_content);
                    view.setTag(viewHolderRight);
                } else
                    viewHolderRight = (ViewHolderRight) view.getTag();
                viewHolderRight.content.setText("");
                DataInfoUtils.paeseEmotion(mData.get(i).get("content").toString(), viewHolderRight.content);
                viewHolderRight.header.setImageBitmap((Bitmap) mData.get(i).get("header"));
            } else {
                ViewHolderLeft viewHolderLeft = null;
                if (view == null) {
                    view = mInflater.inflate(R.layout.list_left, null);
                    viewHolderLeft = new ViewHolderLeft();
                    viewHolderLeft.header = (RoundImageView) view.findViewById(R.id.l_header);
                    viewHolderLeft.content = (TextView) view.findViewById(R.id.l_content);
                    view.setTag(viewHolderLeft);
                } else
                    viewHolderLeft = (ViewHolderLeft) view.getTag();
                viewHolderLeft.content.setText("");
                DataInfoUtils.paeseEmotion(mData.get(i).get("content").toString(), viewHolderLeft.content);
                viewHolderLeft.header.setImageBitmap((Bitmap) mData.get(i).get("header"));

            }
            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mName = userService.getName(this);
        Intent intent = getIntent();
        isFriend = intent.getBooleanExtra("isfriend", false);
        mFriend = intent.getStringExtra("name");
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.water);
        header_map.put("name", bitmap);
        header_map.put("friend", bitmap);
        init(mFriend);
        load();

    }

    private void init(final String name) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.mipmap.back);
        mToolbar.setTitle("返回");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //添加动画
                finish();
            }
        });
        TextView title = (TextView) mToolbar.findViewById(R.id.tv_title);
        title.setText(name);
        mShow = (ListView) findViewById(R.id.chat);
        mShow.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)
                    mIsScroll = false;
                else
                    mIsScroll = true;
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (mIsScroll && !mIsHasHeader && i == 0) {
                    if (mShow.getChildAt(0) != null && mShow.getChildAt(0).getTop() == 0) {
                        mIsHasHeader = true;
                        if (mHeader == null) {
                            mHeader = LayoutInflater.from(ChatActivity.this).inflate(R.layout.listview_header, null);
                            View roate = mHeader.findViewById(R.id.load);
                            Animation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                            animation.setRepeatCount(Animation.INFINITE);
                            animation.setDuration(2000);
                            animation.setInterpolator(new LinearInterpolator());
                            roate.startAnimation(animation);
                        }
                        mShow.addHeaderView(mHeader);
                        load();
                    }
                }
            }
        });
        mContent = (EditText) findViewById(R.id.content);
        mSend = (Button) findViewById(R.id.send);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContent.getText().toString().length() == 0) {
                    Toast.makeText(ChatActivity.this, "发送内容不能为空", Toast.LENGTH_SHORT).show();

                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String time = StringUtils.realconvertCalendar(Calendar.getInstance());
                            final String msg = mContent.getText().toString();
                            final boolean res = userService.sendMsg(mName, mFriend, time, msg);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (!res) {
                                        Toast.makeText(ChatActivity.this, "发送失败", Toast.LENGTH_SHORT).show();

                                    } else {
                                        mContent.setText("");
                                        Map<String, Object> content = new HashMap<>();
                                        content.put("fromer", mName);
                                        content.put("time", time);
                                        content.put("content", msg);
                                        content.put("header", header_map.get("name"));
                                        {
                                            contents.add(content);
                                            myAdapter.notifyDataSetChanged();
                                            mShow.setSelection(myAdapter.getCount() - 1);
                                        }
                                    }
                                }
                            });
                        }
                    }).start();

                }

            }
        });
        final LinearLayout parentOfPager = (LinearLayout) findViewById(R.id.linear_emotions);
        GridView gridViewLeft = DataInfoUtils.getGridview(this, 0);
        DataInfoUtils.setPagerClick(mContent, gridViewLeft, 0);
        GridView gridViewCenter = DataInfoUtils.getGridview(this, 1);
        DataInfoUtils.setPagerClick(mContent, gridViewCenter, 1);
        GridView gridViewRight = DataInfoUtils.getGridview(this, 2);
        DataInfoUtils.setPagerClick(mContent, gridViewRight, 2);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager_emotions);
        GridView gridViews[] = {gridViewLeft, gridViewCenter, gridViewRight};
        viewPager.setAdapter(new DataInfoUtils.MyPager(gridViews));
        left = findViewById(R.id.left);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCircleNull();
                left.setBackgroundResource(R.drawable.circle);
                viewPager.setCurrentItem(0);
            }
        });
        center = findViewById(R.id.center);
        center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCircleNull();
                center.setBackgroundResource(R.drawable.circle);
                viewPager.setCurrentItem(1);
            }
        });
        right = findViewById(R.id.right);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCircleNull();
                right.setBackgroundResource(R.drawable.circle);
                viewPager.setCurrentItem(2);
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setCircleNull();
                if (position == 0) {
                    left.setBackgroundResource(R.drawable.circle);
                } else if (position == 1) {
                    center.setBackgroundResource(R.drawable.circle);
                } else {
                    right.setBackgroundResource(R.drawable.circle);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        findViewById(R.id.emotions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (parentOfPager.getVisibility() == View.GONE)
                    parentOfPager.setVisibility(View.VISIBLE);
                else
                    parentOfPager.setVisibility(View.GONE);
            }
        });

        getHeader();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isFriend) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_chat, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void load() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Map<String, Object>> result = userService.getMsg(userService.getName(ChatActivity.this), mFriend, index, "old");
                if (result != null && result.get(0) != null) {
                    {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                {
                                    for (int i = 0; i < result.size(); i++) {
                                        if (result.get(i).get("fromer").equals(mFriend))
                                            result.get(i).put("header", header_map.get("friend"));
                                        else
                                            result.get(i).put("header", header_map.get("name"));
                                    }
                                }
                                contents.addAll(0, result);
                                if (index == 0) {
                                    mShow.setAdapter((myAdapter = new MyAdapter(ChatActivity.this, contents, mName)));
                                } else {
                                    myAdapter.notifyDataSetChanged();
                                }
                                index = Integer.valueOf(result.get(0).get("id").toString());
                                if (mIsFirstLoad) {
                                    indexTop = Integer.valueOf(result.get(result.size() - 1).get("id").toString());
                                    mIsFirstLoad = false;
                                    loadNew();
                                }
                                if (index == 0)
                                    index = -1;
                                if (mIsHasHeader) {
                                    mShow.setSelection(result.size() - 1);
                                } else
                                    mShow.setSelection(mShow.getCount() - 1);
                            }
                        });
                    }
                } else if (result == null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            DataInfoUtils.showDataError(ChatActivity.this);
                        }
                    });
                }
                if (mIsHasHeader) {
                    mIsHasHeader = false;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mShow.removeHeaderView(mHeader);
                        }
                    });

                }
            }
        }).start();
    }

    private void getHeader() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = userService.getHeader(mName, HttpConnect.URL + "getimg");
                final Bitmap bitmap1 = userService.getHeader(mFriend, HttpConnect.URL + "getimg");
                {
                    if (bitmap1 != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean fixed = false;
                                for (int i = 0; i < contents.size(); i++)
                                    if (contents.get(i).get("fromer").equals(mFriend)) {
                                        contents.get(i).put("header", bitmap1);
                                        fixed = true;
                                    }
                                if (myAdapter != null && fixed)
                                    myAdapter.notifyDataSetChanged();
                                header_map.put("friend", bitmap1);
                            }
                        });
                    }
                    if (bitmap != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean fiexed = false;
                                for (int i = 0; i < contents.size(); i++)
                                    if (contents.get(i).get("fromer").equals(mName)) {
                                        contents.get(i).put("header", bitmap);
                                        fiexed = true;
                                    }
                                if (myAdapter != null && fiexed)
                                    myAdapter.notifyDataSetChanged();
                                header_map.put("name", bitmap);
                            }
                        });
                    }
                }

            }
        }).start();
    }

    private void setCircleNull() {
        left.setBackgroundResource(R.drawable.circle_null);
        right.setBackgroundResource(R.drawable.circle_null);
        center.setBackgroundResource(R.drawable.circle_null);
    }

    private void loadNew() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Map<String, Object>> result = userService.getMsg(mFriend, userService.getName(ChatActivity.this), indexTop, "new");
                if (result != null && result.get(0) != null) {
                    {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                {
                                    for (int i = 0; i < result.size(); i++) {
                                        if (result.get(i).get("fromer").equals(mFriend))
                                            result.get(i).put("header", header_map.get("friend"));
                                        else
                                            result.get(i).put("header", header_map.get("name"));
                                    }
                                }
                                contents.addAll(result);
                                if (index == 0) {
                                    mShow.setAdapter((myAdapter = new MyAdapter(ChatActivity.this, contents, mName)));
                                } else {
                                    myAdapter.notifyDataSetChanged();
                                }
                                indexTop = Integer.valueOf(result.get(result.size() - 1).get("id").toString());
                            }
                        });
                    }
                }
                loadNew();
            }
        }).start();
    }

}
