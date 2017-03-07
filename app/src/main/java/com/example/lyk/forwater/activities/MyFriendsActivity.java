package com.example.lyk.forwater.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lyk.forwater.R;
import com.example.lyk.forwater.services.UserService;

public class MyFriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private View header;
    private ListView listView;
    private int index=0;
    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            UserService userService=new UserService();

        }
    };

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
        TextView title = (TextView) mToolbar.findViewById(R.id.tv_title);
        title.setText("我的好友");
        header=findViewById(R.id.top);
        listView=(ListView)findViewById(R.id.listview);

    }


}
