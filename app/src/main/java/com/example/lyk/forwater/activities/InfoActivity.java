package com.example.lyk.forwater.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lyk.forwater.R;
import com.example.lyk.forwater.services.IInfoService;
import com.example.lyk.forwater.services.IUserService;
import com.example.lyk.forwater.services.InfoSevice;
import com.example.lyk.forwater.services.UserService;
import com.example.lyk.forwater.utils.AnimationUtils;
import com.example.lyk.forwater.utils.DataInfoUtils;
import com.example.lyk.forwater.utils.HttpConnect;
import com.example.lyk.forwater.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.lyk.forwater.utils.DataInfoUtils.dip2px;
import static com.example.lyk.forwater.utils.DataInfoUtils.px2dip;

public class InfoActivity extends AppCompatActivity {

    private IInfoService infoService = new InfoSevice();
    private Toolbar mToolbar;
    private LinearLayout mlinearLayout;
    private LinearLayout mLinaearComm;
    private RelativeLayout mIsLoading;
    private Handler mhander;
    private final int BITMAP = 1;
    private final int COMMENT = 2;
    private String type;
    private int typeNum;
    private Object[] objects;
    private TextView title;
    private TextView time;
    private TextView acess;
    private TextView pnum;
    private EditText editText_rep;
    private PopupWindow popupWindow;
    private ImageView praise;
    private FloatingActionButton fab_add;
    private boolean mIsFirstloading = true;
    private Button mComment;
    private View mDivder;
    private String titleId = null;
    public static Map<Integer, Bitmap> imgCaches;
    private int mIndex = 0;//记录查询到位置

    // private int replay_index=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        imgCaches = new HashMap<>();
        mlinearLayout = (LinearLayout) findViewById(R.id.activity_info);
        mLinaearComm = (LinearLayout) findViewById(R.id.comment);
        mIsLoading = (RelativeLayout) findViewById(R.id.isLoading);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        time = (TextView) findViewById(R.id.info_time);
        mToolbar.setNavigationIcon(R.mipmap.back);
        acess = (TextView) findViewById(R.id.acess);
        pnum = (TextView) findViewById(R.id.p_num);
        praise = (ImageView) findViewById(R.id.praise);
        praise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final IUserService userService = new UserService();
                String name = null;
                if ((name = userService.getName(InfoActivity.this)) != null) {
                    final String real = name;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean res = userService.praise(real, type, Integer.parseInt(titleId.trim()));
                            if (res) {
                                mhander.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        pnum.setText((Integer.parseInt(pnum.getText().toString().trim()) + 1) + "");
                                        praise.setBackgroundResource(R.mipmap.praise2);
                                    }
                                });
                            } else
                                mhander.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(InfoActivity.this, "您已经点过赞了", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        }
                    }).start();
                } else
                    DataInfoUtils.login(InfoActivity.this);
            }
        });
        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab_add.setImageResource(R.mipmap.minus);
                Intent intent1 = new Intent(InfoActivity.this, AnimationActivity.class);
                float[] position = {fab_add.getX(), fab_add.getY()};
                intent1.putExtra("pos", position);
                startActivity(intent1);
                overridePendingTransition(R.anim.empty, R.anim.empty);

            }
        });
        mToolbar.setTitle("返回");
        mDivder = findViewById(R.id.divder);
        mComment = (Button) findViewById(R.id.load_comm);
        mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (Exception e) {

                        }
                        List<Map<String, Object>> comment = new UserService().getComment(HttpConnect.URL + "comment", titleId, mIndex + "", typeNum);
                        Message message = new Message();
                        message.what = COMMENT;
                        message.obj = comment;
                        mhander.sendMessage(message);
                    }
                }).start();
                mComment.setVisibility(View.GONE);
                mIsLoading.setVisibility(View.VISIBLE);
                AnimationUtils.setAnimation(mIsLoading.findViewById(R.id.wait), new LinearInterpolator());

            }
        });
        type = bundle.getString("type");
        typeNum = bundle.getInt("typenum");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //添加动画
                finish();
            }
        });

        title = (TextView) mToolbar.findViewById(R.id.tv_title);
        title.setText("");
        mhander = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == BITMAP) {
                    DataInfoUtils.showOrDismissDlg(false, InfoActivity.this);
                    if (objects == null) {
                        DataInfoUtils.showDataError(InfoActivity.this);
                        return;
                    } else {
                        if (mIsFirstloading) {
                            mComment.setVisibility(View.VISIBLE);
                            mDivder.setVisibility(View.VISIBLE);
                            mIsFirstloading = false;
                            findViewById(R.id.rpraise).setVisibility(View.VISIBLE);

                        } else //!first
                        {
                            praise.setBackgroundResource(R.mipmap.praise);
                            int size = mlinearLayout.getChildCount() - 1;
                            mlinearLayout.removeViews(1, size);
                            size = mLinaearComm.getChildCount() - 1;
                            mLinaearComm.removeViews(1, size);
                            imgCaches.clear();
                        }
                        showResult();

                    }
                } else if (msg.what == COMMENT) {
                    List<Map<String, Object>> lists = (List<Map<String, Object>>) msg.obj;
                    if (lists == null) {
                        DataInfoUtils.showDataError(InfoActivity.this);
                        mComment.setText("重新加载");
                    } else if (lists.get(0) == null) {
                        mComment.setText("没有更多了");
                    } else {
                        mComment.setText("加载更多");
                        mIndex =Integer.valueOf(lists.get(lists.size()-1).get("id").toString());
                        if (mIndex==0)
                            mIndex=-1;
                        initComment(lists);
                        Toast.makeText(InfoActivity.this, "已为您更新" + lists.size() + "条评论", Toast.LENGTH_SHORT).show();
                    }
                    mComment.setVisibility(View.VISIBLE);
                    mIsLoading.setVisibility(View.GONE);

                }
                super.handleMessage(msg);
            }
        };
        getBitmap(Calendar.getInstance());

    }


    private ImageButton getImageView(final Bitmap bitmap) {
        final ImageButton imageButton = new ImageButton(InfoActivity.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(dip2px(this, 300), dip2px(this, 300));
        if (px2dip(this, bitmap.getWidth()) >= 300 && px2dip(this, bitmap.getHeight()) >= 300) {
            layoutParams1.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams1.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else if (px2dip(this, bitmap.getWidth()) >= 300) {
            layoutParams1.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else if (px2dip(this, bitmap.getHeight()) >= 300) {
            layoutParams1.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        layoutParams1.gravity = Gravity.CENTER;
        int dp = dip2px(this, 20);
        layoutParams1.setMargins(0, dp, 0, dp);
        imageButton.setLayoutParams(layoutParams1);
        imageButton.setBackground(new BitmapDrawable(bitmap));
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InfoActivity.this, PhotoActivity.class);
                intent.putExtra("id", bitmap.hashCode());
                startActivity(intent);
            }
        });
        imgCaches.put(bitmap.hashCode(), bitmap);
        return imageButton;
    }

    private TextView getTextView() {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(20);
        int dp = dip2px(this, 20);
        layoutParams.setMargins(dp, dp, dp, dp);
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    private void getBitmap(final Calendar calendar) {
        DataInfoUtils.showOrDismissDlg(true, InfoActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {

                }
                objects = infoService.getInfo(HttpConnect.URL + type, StringUtils.convertCalendar(calendar));
                mhander.sendEmptyMessage(BITMAP);
            }
        }).start();

    }


    private List<String> getFromContent(String content) {
        List<String> list = new ArrayList<String>();
        int temp = 0;
        for (int i = 0; i < content.length(); ) {
            if (content.charAt(i) == '@' && (i + 4) < content.length()) {
                for (int j = 1; j <= 4; j++) {
                    if (content.charAt(i + j) == '@') {
                        if (j == 4) {
                            {
                                if (temp != i)
                                    list.add(content.substring(temp, i));
                                i += 5;
                                temp = i;
                                list.add(null);
                            }
                        }
                    } else {
                        i++;
                        break;
                    }
                }
            } else
                i++;

        }
        if (temp <= content.length() - 1)
            list.add(content.substring(temp, content.length()));
        return list;
    }


    private void showResult() {
        mIndex = 0;
        mComment.setText("加载评论");
        int size = objects.length;
        time.setText(objects[size - 5].toString());
        title.setText(objects[size - 6].toString());
        pnum.setText(objects[size - 1].toString());
        acess.setText("阅读量:" + objects[size - 2].toString());
        titleId = objects[size - 3].toString();
        if (size == 6) {
            TextView textView = getTextView();
            textView.setText(objects[2].toString());
            mlinearLayout.addView(textView);
        } else {
            int imagesize = size - 6;
            String content = objects[size - 4].toString();
            List<String> strings = getFromContent(content);
            int index = 0;
            for (int i = 0; i < strings.size(); i++) {
                if (strings.get(i) == null) {
                    ImageButton imageButton = getImageView((Bitmap) objects[index]);
                    mlinearLayout.addView(imageButton);
                    index++;
                } else {
                    TextView textView = getTextView();
                    textView.setText(strings.get(i));
                    mlinearLayout.addView(textView);
                }
            }

        }
        objects = null;
    }

    private void initComment(List<Map<String, Object>> lists) {
        for (int i = 0; i < lists.size(); i++) {
            View view = LayoutInflater.from(InfoActivity.this).inflate(R.layout.comment_basic, null);
            final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.c_replay);
            final Map<String, Object> map = lists.get(i);
            TextView text_header = ((TextView) view.findViewById(R.id.c_name));
            text_header.setText(map.get("name").toString());
            text_header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startChat(new UserService().getName(InfoActivity.this), map.get("name").toString());
                }
            });
            ((TextView) view.findViewById(R.id.c_time)).setText(map.get("time").toString());
            final RoundImageView roundImageView = (RoundImageView) view.findViewById(R.id.c_header);
            roundImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startChat(new UserService().getName(InfoActivity.this), map.get("name").toString());
                }
            });
            final IUserService userService = new UserService();
            if (map.get("name").toString().equals(userService.getName(InfoActivity.this))) {
                File file = new File(DataInfoUtils.getHeader(InfoActivity.this));
                if (file.exists()) {
                    roundImageView.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
                }
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap head = userService.getHeader(map.get("name").toString(), HttpConnect.URL + "getimg");
                        if (head != null) {
                            mhander.post(new Runnable() {
                                @Override
                                public void run() {
                                    roundImageView.setImageBitmap(head);
                                }
                            });
                        }
                    }
                }).start();
            }
            final TextView textView = ((TextView) view.findViewById(R.id.c_content));
            DataInfoUtils.paeseEmotion(map.get("content").toString(),textView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sentReplay(map.get("name").toString(), map.get("id").toString(), linearLayout, null);
                }
            });
            List<Map<String, String>> replay = (List<Map<String, String>>) map.get("replay");
            if (replay != null) {
                for (int j = 0; j < replay.size(); j++) {
                    Map<String, String> rep = replay.get(j);
                    addReplayView(rep, linearLayout, null);

                }
            }
            mLinaearComm.addView(view);
        }
    }

    private void sentReplay(final String rep, final String id, final LinearLayout parent, final View view) {
        final IUserService userService = new UserService();
        if (userService.getName(InfoActivity.this) == null) {
            DataInfoUtils.login(InfoActivity.this);
            return;
        }
        //   final EditText editText = new EditText(InfoActivity.this);
        String title = "回复" + rep;
        if (title.contains(userService.getName(InfoActivity.this)))
            title = "追加";
        showDialog(title, new View.OnClickListener() {
            @Override
            public void onClick(final View editview) {
                if (editText_rep.getText().toString().isEmpty()) {
                    Toast.makeText(InfoActivity.this, "回复内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String rid = null;
                        if (view != null) {
                            rid = view.getTag().toString().split(" ")[0];
                        }
                        final String realrid = rid;
                        final int result = userService.sendReplay(editText_rep.getText().toString(), userService.getName(InfoActivity.this), rep, id, rid, HttpConnect.URL + "sendreplay");
                        if (result != -1) {
                            mhander.post(new Runnable() {
                                @Override
                                public void run() {
                                    Map<String, String> replay = new HashMap<String, String>();
                                    replay.put("rep", userService.getName(InfoActivity.this));
                                    replay.put("rec", rep);
                                    replay.put("con", editText_rep.getText().toString());
                                    replay.put("cid", id);
                                    replay.put("prid", result + "");
                                    replay.put("rid", realrid);
                                    addReplayView(replay, parent, view);
                                    popupWindow.dismiss();
                                }
                            });
                        } else {
                            mhander.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(InfoActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                                    popupWindow.dismiss();
                                }
                            });
                        }
                    }
                }).start();
            }
        });

    }

    private void addReplayView(final Map<String, String> rep, final LinearLayout parent, View view) {
        final TextView textView1 = new TextView(InfoActivity.this);
        textView1.setTextSize(18);
        textView1.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, dip2px(InfoActivity.this, 5));
        textView1.setLayoutParams(layoutParams);
        if (rep.get("rec").equals(rep.get("rep"))) {
            textView1.setText(Html.fromHtml("<font color='#0080ff'>" + rep.get("rep") + "</font>" + ":"));

        } else
            textView1.setText(Html.fromHtml("<font color='#0080ff'>" + rep.get("rep") + "</font>" + "回复" + "<font color='#0080ff'>" + rep.get("rec") + "</font>" + ":"));
        DataInfoUtils.paeseEmotion(rep.get("con"), textView1);
        // textView1.append(Html.fromHtml("<img src='"+2130903048+"'>",DataInfoUtils.getImageGetter(InfoActivity.this),null));

        if (rep.get("rid") != null)
            textView1.setTag(rep.get("prid") + " " + rep.get("rid"));
        else
            textView1.setTag(rep.get("prid"));
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sentReplay(rep.get("rep"), rep.get("cid"), parent, textView1);
            }
        });
        if (view != null) {
            int i = 1;
            int index = parent.indexOfChild(view);
            while (index + i < parent.getChildCount()) {
                if (getRid(parent.getChildAt(index + i).getTag()).equals(view.getTag().toString().split(" ")[0])) {
                    i++;
                } else
                    break;
            }
            if (index + i < parent.getChildCount()) {
                parent.addView(textView1, index + i);
                return;
            }
        }
        parent.addView(textView1);

    }

    private String getRid(Object tag) {
        String real = tag.toString().trim();
        if (real.contains(" ")) {
            return real.split(" ")[1];
        }
        return "";
    }

    private void startChat(final String user, final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                char res = new UserService().isFriend(user, name);
                if (res == DataInfoUtils.HAS) {
                    mhander.post(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(name, false);
                        }
                    });
                } else if (res == DataInfoUtils.SUCCESS)
                    mhander.post(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(name, true);
                        }
                    });
                else
                    mhander.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(InfoActivity.this, "加载失败,蓝瘦香菇,小主再试试吧", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        }).start();

    }

    private void startActivity(String name, boolean isFriend) {
        Intent intent = new Intent(InfoActivity.this, ChatActivity.class);
        intent.putExtra("isfriend", isFriend);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    private void setCircleOrNull(View left, View center, View right, int position) {
        left.setBackgroundResource(R.drawable.circle_null);
        right.setBackgroundResource(R.drawable.circle_null);
        center.setBackgroundResource(R.drawable.circle_null);
        if (position == 0)
            left.setBackgroundResource(R.drawable.circle);
        else if (position == 1)
            center.setBackgroundResource(R.drawable.circle);
        else
            right.setBackgroundResource(R.drawable.circle);
    }

    private void setViewCircleOrNull(final View left, final View center, final View right, final ViewPager viewPager, final int pos) {
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                left.setBackgroundResource(R.drawable.circle);
                right.setBackgroundResource(R.drawable.circle_null);
                center.setBackgroundResource(R.drawable.circle_null);
                viewPager.setCurrentItem(pos);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {

        if (intent != null && intent.getSerializableExtra("time") != null) {
            getBitmap((Calendar) intent.getSerializableExtra("time"));
        }
        if (intent != null && intent.getStringExtra("comment") != null && titleId != null) {
            final String name = new UserService().getName(this);
            if (name == null) {
                DataInfoUtils.login(this);
                return;
            }
            showDialog("评论", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (editText_rep.getText().toString().isEmpty()) {
                        Toast.makeText(InfoActivity.this, "评论内容不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final boolean res = new UserService().sendComment(name, editText_rep.getText().toString(), typeNum + "", titleId, HttpConnect.URL + "sendcomment");
                            mhander.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (res) {
                                        Toast.makeText(InfoActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                                        popupWindow.dismiss();
                                    } else {
                                        Toast.makeText(InfoActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                                        popupWindow.dismiss();
                                    }
                                }
                            });
                        }
                    }).start();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        fab_add.setImageResource(R.mipmap.add);
        super.onResume();
    }

    private void showDialog(String title, View.OnClickListener onClickListener) {
        View pop = LayoutInflater.from(InfoActivity.this).inflate(R.layout.comment_popup, null);
        popupWindow = new PopupWindow(pop, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        ((TextView) pop.findViewById(R.id.content)).setText(title);
        pop.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        editText_rep = (EditText) pop.findViewById(R.id.replay);
        final LinearLayout parentOfPager = (LinearLayout) pop.findViewById(R.id.linear_emotions);
        GridView gridViewLeft = DataInfoUtils.getGridview(InfoActivity.this, 0);
        DataInfoUtils.setPagerClick(editText_rep, gridViewLeft, 0);
        GridView gridViewCenter = DataInfoUtils.getGridview(InfoActivity.this, 1);
        DataInfoUtils.setPagerClick(editText_rep, gridViewCenter, 1);
        GridView gridViewRight = DataInfoUtils.getGridview(InfoActivity.this, 2);
        DataInfoUtils.setPagerClick(editText_rep, gridViewRight, 2);
        ViewPager viewPager = (ViewPager) pop.findViewById(R.id.pager_emotions);
        GridView gridViews[] = {gridViewLeft, gridViewCenter, gridViewRight};
        viewPager.setAdapter(new DataInfoUtils.MyPager(gridViews));
        final View left = pop.findViewById(R.id.left);
        final View center = pop.findViewById(R.id.center);
        final View right = pop.findViewById(R.id.right);
        setViewCircleOrNull(left, right, center, viewPager, 0);
        setViewCircleOrNull(right, left, center, viewPager, 2);
        setViewCircleOrNull(center, left, right, viewPager, 1);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setCircleOrNull(left, center, right, position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pop.findViewById(R.id.emotions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (parentOfPager.getVisibility() == View.GONE)
                    parentOfPager.setVisibility(View.VISIBLE);
                else
                    parentOfPager.setVisibility(View.GONE);
            }
        });
        pop.findViewById(R.id.send).setOnClickListener(onClickListener);
        DataInfoUtils.showPopup(popupWindow, findViewById(R.id.activity_info));

    }
}

