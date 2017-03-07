package com.example.lyk.forwater.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
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
import com.example.lyk.forwater.services.UserService;
import com.example.lyk.forwater.utils.DataInfoUtils;
import com.example.lyk.forwater.utils.HttpConnect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.Locale;

import static android.os.Build.VERSION_CODES.M;


public class RegisterActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextView title;
    private LocationManager locationManager;
    private String locationProvider;
    private String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private String[] phonePermissions = {Manifest.permission.READ_PHONE_STATE};
    private AlertDialog dialog;
    private final int LOCATION_PERMISSION = 1;
    private final int PHONE_PERMISSION = 2;
    private EditText mName;
    private EditText mPwd1;
    private EditText mPwd2;
    private EditText mAddress;
    private EditText mTel;
    private ImageView cl1;
    private ImageView cl2;
    private ImageView cl3;
    private ImageView cl4;
    private ImageView cl5;
    private Button getpos;
    private Button gettel;
    private Button mRegister;
    private Handler handler = new Handler();
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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
        title = (TextView) mToolbar.findViewById(R.id.tv_title);
        title.setText("注册");
        mName = (EditText) findViewById(R.id.name);
        mPwd1 = (EditText) findViewById(R.id.pad1);
        mPwd2 = (EditText) findViewById(R.id.pad2);
        mAddress = (EditText) findViewById(R.id.pos);
        mTel = (EditText) findViewById(R.id.tel);
        cl1 = (ImageView) findViewById(R.id.cancle1);
        cl2 = (ImageView) findViewById(R.id.cancle2);
        cl3 = (ImageView) findViewById(R.id.cancle3);
        cl4 = (ImageView) findViewById(R.id.cancle4);
        cl5 = (ImageView) findViewById(R.id.cancle5);
        getpos = (Button) findViewById(R.id.getpos);
        gettel = (Button) findViewById(R.id.gettel);
        mRegister = (Button) findViewById(R.id.register);
        checkBox = (CheckBox) findViewById(R.id.show);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mPwd1.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mPwd2.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    mPwd1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPwd2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = mName.getText().toString();
                final String pwd1 = mPwd1.getText().toString();
                String pwd2 = mPwd2.getText().toString();
                if (name.isEmpty() || pwd1.isEmpty() || pwd2.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "用户名或者密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!pwd1.equals(pwd2)) {
                    Toast.makeText(RegisterActivity.this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                final List<String> list = new ArrayList<String>();
                list.add(name);
                list.add(pwd1);
                if (!mAddress.getText().toString().isEmpty()) {
                    list.add("address");
                    list.add(mAddress.getText().toString());
                }
                if (!mTel.getText().toString().isEmpty()) {
                    list.add(mTel.getText().toString());
                }
                DataInfoUtils.showOrDismissDlg(true, RegisterActivity.this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean result = new UserService().register(HttpConnect.URL + "realuser", list);
                        if (result) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    DataInfoUtils.showOrDismissDlg(false, RegisterActivity.this);
                                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                    new AlertDialog.Builder(RegisterActivity.this)
                                            .setMessage("现在要登陆吗")
                                            .setPositiveButton("登陆", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("name", name);
                                                    bundle.putString("pwd", pwd1);
                                                    intent.putExtras(bundle);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            })
                                            .setNegativeButton("取消", null).setCancelable(true).show();

                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    DataInfoUtils.showOrDismissDlg(false, RegisterActivity.this);
                                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();

            }
        });
        addTextWatch(mName, cl1);
        addTextWatch(mPwd1, cl2);
        addTextWatch(mPwd2, cl3);
        addTextWatch(mAddress, cl4);
        addTextWatch(mTel, cl5);
        addCancle(mName, cl1);
        addCancle(mPwd1, cl2);
        addCancle(mPwd2, cl3);
        addCancle(mAddress, cl4);
        addCancle(mTel, cl5);
        getpos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getpos.setEnabled(false);
                getPermission(LOCATION_PERMISSION);
            }
        });
        gettel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gettel.setEnabled(false);
                getPermission(PHONE_PERMISSION);
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
            public void afterTextChanged(Editable editable) {

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

    private void getLocation() {
        if (locationManager == null)
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            return;
        }
        //获取Location
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            parseLocation(location);
        } else {
            Toast.makeText(this, "获取位置失败", Toast.LENGTH_SHORT).show();
        }
        getpos.setEnabled(true);
    }

    private void getPermission(int permission) {
        if (Build.VERSION.SDK_INT >= M) {
            if (permission == LOCATION_PERMISSION) {
                if (ContextCompat.checkSelfPermission(RegisterActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED||ContextCompat.checkSelfPermission(RegisterActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                    showDialogTipUserRequestPermission(permission);
                    return;
                }

            } else {
                if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED) {
                    showDialogTipUserRequestPermission(permission);
                    return;
                }
            }

        }
        if (permission == LOCATION_PERMISSION) {
            getLocation();
        } else {
            getPhone();
        }
    }

    private void showDialogTipUserRequestPermission(int permission) {

        if (permission == LOCATION_PERMISSION) {
            new AlertDialog.Builder(this)
                    .setTitle("位置权限不可用")
                    .setMessage("请开启位置权限")
                    .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(RegisterActivity.this, permissions, LOCATION_PERMISSION);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getpos.setEnabled(true);
                        }
                    }).setCancelable(false).show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("电话号码权限未开启")
                    .setMessage("请开启获取电话号码的权限")
                    .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(RegisterActivity.this, phonePermissions, PHONE_PERMISSION);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            gettel.setEnabled(true);
                        }
                    }).setCancelable(false).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (requestCode == LOCATION_PERMISSION) {
            int count = 0;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    count++;
                }
            }
            if (count == grantResults.length)
                getLocation();
            else
                getpos.setEnabled(true);
        } else {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getPhone();
            else
                gettel.setEnabled(true);
        }


    }

    private void parseLocation(Location location) {
        Geocoder geocoder = new Geocoder(RegisterActivity.this, Locale.getDefault());
        List<Address> locationList = null;
        try {
            locationList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (locationList == null || locationList.size() == 0) {
            Toast.makeText(this, "获取位置失败", Toast.LENGTH_SHORT).show();
            return;
        }
        Address address = locationList.get(0);
        String countryName = address.getCountryName();
        String street = "";
        if (address.getAddressLine(0) != null)
            street = address.getAddressLine(0);
        mAddress.setText(countryName + street);
    }

    private void getPhone() {

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String phone = telephonyManager.getLine1Number();
        if (phone != null || phone.isEmpty()) {
            Toast.makeText(this, "获取手机号码失败", Toast.LENGTH_SHORT).show();
            gettel.setEnabled(true);
            return;
        }
        mTel.setText(phone);
        gettel.setEnabled(true);
    }

}
