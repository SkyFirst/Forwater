
package com.example.lyk.forwater.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lyk.forwater.R;
import com.example.lyk.forwater.services.IUserService;
import com.example.lyk.forwater.services.UserService;
import com.example.lyk.forwater.utils.DataInfoUtils;
import com.example.lyk.forwater.utils.HttpConnect;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText mName;
    private EditText mPwd;
    private Toolbar mToolbar;
    private Handler handler = new Handler();
    private CheckBox checkBox;
    private Button mRegister;
    private Button mLogin;
    private ImageView cl1;
    private ImageView cl2;
    private IUserService userService=new UserService();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
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
        title.setText("登陆");
        mName = (EditText) findViewById(R.id.name);
        mPwd = (EditText) findViewById(R.id.pad1);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mName.setText(bundle.getString("name"));
            mPwd.setText(bundle.getString("pwd"));
        }
        mRegister = (Button) findViewById(R.id.register);
        checkBox = (CheckBox) findViewById(R.id.show);
        mLogin = (Button) findViewById(R.id.login);
        cl1 = (ImageView) findViewById(R.id.cancle1);
        cl2 = (ImageView) findViewById(R.id.cancle2);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    mPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
        addTextWatch(mName, cl1);
        addTextWatch(mPwd, cl2);
        addCancle(mName, cl1);
        addCancle(mPwd, cl2);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = mName.getText().toString();
                String pwd = mPwd.getText().toString();
                if (name.isEmpty() || pwd.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "用户名或者密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(userService.getName(LoginActivity.this)!=null)
                {
                    Toast.makeText(LoginActivity.this, "当前用户"+userService.getName(LoginActivity.this)+"在线,请注销", Toast.LENGTH_SHORT).show();
                    return;
                }
                final List<String> list = new ArrayList<String>();
                list.add(name);
                list.add(pwd);
                DataInfoUtils.showOrDismissDlg(true, LoginActivity.this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean result = userService.login(HttpConnect.URL + "userlogin", list);
                        if (result) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    DataInfoUtils.showOrDismissDlg(false, LoginActivity.this);
                                    Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                                    userService.saveName(name,LoginActivity.this);
                                    finish();
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    DataInfoUtils.showOrDismissDlg(false, LoginActivity.this);
                                    Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }

    private void addTextWatch(EditText editText, final ImageView imageView) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0)
                    imageView.setVisibility(View.VISIBLE);
                else
                    imageView.setVisibility(View.GONE);
            }

            @Override
            public void  afterTextChanged(Editable editable) {

            }
        });
    }

    private void addCancle(final EditText editText, ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });


    }
}
