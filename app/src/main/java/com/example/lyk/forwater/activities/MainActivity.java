package com.example.lyk.forwater.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lyk.forwater.R;
import com.example.lyk.forwater.activities.fragments.FeedbackFragment;
import com.example.lyk.forwater.activities.fragments.InfoFragment;
import com.example.lyk.forwater.activities.fragments.MyFragment;
import com.example.lyk.forwater.activities.fragments.ScoreFragment;
import com.example.lyk.forwater.services.UserService;
import com.example.lyk.forwater.utils.DataInfoUtils;
import com.example.lyk.forwater.utils.HttpConnect;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.example.lyk.forwater.R.id.header;

public class MainActivity extends AppCompatActivity {

    private FragmentTabHost mFragmentTabHost;
    private Class[] mFragments = {ScoreFragment.class, InfoFragment.class, FeedbackFragment.class, MyFragment.class};
    private String[] mTitles = {"首页", "消息", "反馈", "我的"};
    private Toolbar mToolbar;
    private DrawerLayout mDrawerlayout;
    private int current = 0;
    private LinearLayout mLogin;
    private RoundImageView roundImageView;
    private RoundImageView mNavi;
    private static final int REQUESTCODE_PIC = 1;//相册
    private static final int REQUESTCODE_CAM = 2;//相机
    private static final int REQUESTCODE_CUT = 3;//图片裁剪
    private Bitmap mBitmap;
    private File mFile;
    private Handler mHander = new Handler();
    private String mName;
    private static Map<Integer,Fragment> fragemts=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerlayout = (DrawerLayout) findViewById(R.id.id_drawerlayout);
        roundImageView = (RoundImageView) findViewById(header);
        roundImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((mName = new UserService().getName(MainActivity.this)) != null) {
                    setHeader();
                } else {
                    DataInfoUtils.login(MainActivity.this);
                }

            }
        });

        mLogin = (LinearLayout) findViewById(R.id.login);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        mNavi = (RoundImageView) findViewById(R.id.navi);
        mNavi.setVisibility(View.VISIBLE);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawerlayout.isDrawerOpen(Gravity.RIGHT)) {
                    mDrawerlayout.closeDrawer(Gravity.RIGHT);
                } else {
                    mDrawerlayout.openDrawer(Gravity.RIGHT);
                }
            }
        });
        final File file = new File(DataInfoUtils.getHeader(MainActivity.this));
        if (file.exists()) {
            Drawable drawable = new BitmapDrawable(BitmapFactory.decodeFile(DataInfoUtils.getHeader(MainActivity.this)));
            roundImageView.setImageDrawable(drawable);
            mNavi.setImageDrawable(drawable);
        } else
            loadHeader();
        initTabhost();


    }

    private void initTabhost() {
        mFragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mFragmentTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        int size = mFragments.length;
        for (int i = 0; i < size; i++) {
            final int index = i;
            TabHost.TabSpec tabSpec = mFragmentTabHost.newTabSpec(mTitles[i]).setIndicator(getView(i));
            mFragmentTabHost.addTab(tabSpec, mFragments[i], null);
            mFragmentTabHost.getTabWidget().getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (index == current)
                        return;
                    else
                        current = index;
                    int size = mFragmentTabHost.getTabWidget().getChildCount();
                    for (int i = 0; i < size; i++) {
                        TextView textView = (TextView) mFragmentTabHost.getTabWidget().getChildAt(i).findViewById(R.id.specview);
                        textView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    }
                    TextView textView = (TextView) view.findViewById(R.id.specview);
                    if (textView.getCurrentTextColor() != getResources().getColor(R.color.darkblue)) {
                        textView.setTextColor(getResources().getColor(R.color.darkblue));
                    }
                    ((TextView) mToolbar.findViewById(R.id.tv_title)).setText(mTitles[index]);
                    try {
                        Fragment fragment=fragemts.get(index);
                        if(fragment==null)
                        {
                            fragment=(Fragment) mFragments[index].newInstance();
                            fragemts.put(index,fragment);
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.realtabcontent, fragment).commit();
                    } catch (Exception e) {

                    }
                    // getSupportFragmentManager().beginTransaction().replace(R.id.realtabcontent,(Fragment) mFragments[index].newInstance()).commit();

                }
            });

        }
    }

    private View getView(int i) {
        View view = getLayoutInflater().inflate(R.layout.tab_spec, null);
        ((TextView) view.findViewById(R.id.specview)).setText(mTitles[i]);
        return view;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerlayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerlayout.closeDrawer(Gravity.RIGHT);
            return;
        }
        super.onBackPressed();
    }

    private void setHeader() {
        View view = getLayoutInflater().inflate(R.layout.photo_popup, null);
        final PopupWindow popupWindow = new PopupWindow(view, DataInfoUtils.dip2px(MainActivity.this, 150), WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(roundImageView, -120, -20, Gravity.CENTER_HORIZONTAL);
        TextView quxiao = (TextView) view.findViewById(R.id.quxiao);
        TextView paizhao = (TextView) view.findViewById(R.id.paizhao);
        TextView tuku = (TextView) view.findViewById(R.id.tuku);
        quxiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        paizhao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
                popupWindow.dismiss();
            }
        });
        tuku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPic();
                popupWindow.dismiss();
            }
        });

    }

    private void openCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(DataInfoUtils.getImgPos(MainActivity.this));
            if (!file.exists()) {
                file.mkdirs();
            }
            mFile = new File(file, "touxiang.jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(intent, REQUESTCODE_CAM);
        } else {
            Toast.makeText(this, "请确认已经插入SD卡", Toast.LENGTH_SHORT).show();
        }
    }

    private void openPic() {
        Intent picIntent = new Intent(Intent.ACTION_PICK, null);
        picIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(picIntent, REQUESTCODE_PIC);
    }

    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true); //黑边
        intent.putExtra("scaleUpIfNeeded", true); //黑边
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUESTCODE_CUT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUESTCODE_CAM:
                    startPhotoZoom(Uri.fromFile(mFile));
                    break;
                case REQUESTCODE_PIC:
                    if (data == null || data.getData() == null) {
                        return;
                    }
                    startPhotoZoom(data.getData());

                    break;
                case REQUESTCODE_CUT:
                    if (data != null) {
                        setPicToView(data);
                    }
                    break;
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String name = new UserService().getName(this);
        if (name != null) {
            TextView textView = (TextView) findViewById(R.id.username);
            textView.setText(name);
        }

    }

    private void setPicToView(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            mBitmap = bundle.getParcelable("data");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (new UserService().sendHeader(mBitmap, mName, HttpConnect.URL + "imgheader")) {
                        mHander.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "上传头像成功", Toast.LENGTH_SHORT).show();
                                roundImageView.setImageDrawable(new BitmapDrawable(mBitmap));
                                mNavi.setImageDrawable(new BitmapDrawable(mBitmap));
                                DataInfoUtils.saveFile(mBitmap, MainActivity.this);
                            }
                        });
                    } else
                        mHander.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "上传头像失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            }).start();

        }
    }

    private void loadHeader() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    UserService userService = new UserService();
                    String name = userService.getName(getApplicationContext());
                    if (name != null) {
                        final Bitmap bitmap = userService.getHeader(name, HttpConnect.URL + "getimg");
                        if (bitmap != null) {
                            mHander.post(new Runnable() {
                                @Override
                                public void run() {
                                    DataInfoUtils.saveFile(bitmap,MainActivity.this);
                                    roundImageView.setImageBitmap(bitmap);
                                    mNavi.setImageBitmap(bitmap);
                                }
                            });
                            break;
                        }
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {

                    }
                }
            }
        }).start();
    }


}
