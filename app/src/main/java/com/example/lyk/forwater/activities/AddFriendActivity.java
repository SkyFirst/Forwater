package com.example.lyk.forwater.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lyk.forwater.R;
import com.example.lyk.forwater.services.UserService;
import com.example.lyk.forwater.utils.DataInfoUtils;

import java.util.List;

public class AddFriendActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private int index;
    private Handler handler = new Handler();

    private class ViewHolder {
        public TextView content;
        public View cancle;
        public View add;
        public View top;
    }

    private class MyAdapter extends BaseAdapter {
        private Context context;
        private List<String> friends;
        private LinearLayoutEx linearLayoutEx;

        public MyAdapter(Context context, List<String> friends) {
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
                convertView = LayoutInflater.from(context).inflate(R.layout.listview_friend_item, null);
                viewHolder = new ViewHolder();
                viewHolder.add = convertView.findViewById(R.id.add);
                linearLayoutEx = (LinearLayoutEx) convertView.findViewById(R.id.scroll);
                viewHolder.top = addButton(linearLayoutEx, "置顶");
                viewHolder.cancle = addButton(linearLayoutEx, "删除");
                viewHolder.content = (TextView) convertView.findViewById(R.id.friend);
                convertView.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.content.setText(Html.fromHtml("<font color='#0000ff'>" + friends.get(position).split(" ")[0] + "</font>请求添加你为好友\n" + friends.get(position).substring(friends.get(position).split(" ")[0].length() + 1)));
            viewHolder.cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayoutEx parent = (LinearLayoutEx) view.getParent();
                    parent.mScroller.startScroll(parent.getScrollX(), 0, -parent.getScrollX(), 0, 100);
                    parent.invalidate();
                    parent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            friends.remove(position);
                            notifyDataSetChanged();
                        }
                    }, 100);
                }
            });
            viewHolder.cancle.setBackgroundResource(R.drawable.bg_score1);
            viewHolder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (view.getTag() != null)
                                return;
                            view.setTag("");
                            UserService userService = new UserService();
                            final char res = userService.sendRep(friends.get(position).split(" ")[0], userService.getName(AddFriendActivity.this), "ok");
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (res == DataInfoUtils.SUCCESS) {
                                        view.setEnabled(false);
                                        ((Button) view).setText("已添加");
                                    } else {
                                        Toast.makeText(AddFriendActivity.this, "添加失败，请重试", Toast.LENGTH_SHORT).show();
                                        view.setTag(null);

                                    }
                                }
                            });
                        }
                    }).start();
                }
            });
            viewHolder.add.setBackgroundResource(R.drawable.bg_score1);
            viewHolder.top.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayoutEx parent = (LinearLayoutEx) view.getParent();
                    parent.mScroller.startScroll(parent.getScrollX(), 0, -parent.getScrollX(), 0, 100);
                    parent.invalidate();
                    parent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String friend = new String(friends.get(position));
                            friends.remove(position);
                            friends.add(0, friend);
                            notifyDataSetChanged();
                        }
                    }, 100);
                }
            });
            viewHolder.top.setBackgroundResource(R.drawable.bg_score1);
            return convertView;
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
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
        ((TextView) findViewById(R.id.tv_title)).setText("添加好友");
        //((ListViewEx) findViewById(R.id.friends)).setAdapter(new MyAdapter(this, getIntent().getExtras().getStringArrayList("friends")));
        ListViewEx lists = (ListViewEx) findViewById(R.id.friends);
        lists.setRefreshFalse();
        MyAdapter myAdapter = new MyAdapter(this, getIntent().getExtras().getStringArrayList("friends"));
        lists.setAdapter(myAdapter);


    }

    private View addButton(LinearLayout linearLayout, String text) {
        Button button = new Button(this);
        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(DataInfoUtils.dip2px(this, 80), ViewGroup.LayoutParams.MATCH_PARENT);
        marginLayoutParams.setMargins(0, 0, 0, 0);
        button.setLayoutParams(marginLayoutParams);
        button.setText(text);
        linearLayout.addView(button);
        return button;
    }

}
