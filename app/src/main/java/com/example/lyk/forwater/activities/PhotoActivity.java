package com.example.lyk.forwater.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lyk.forwater.R;
import com.example.lyk.forwater.utils.DataInfoUtils;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Bitmap content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
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
        title.setText("图片查看");
        PhotoView photoView = (PhotoView) findViewById(R.id.photo);
        Intent intent = getIntent();
        content= InfoActivity.imgCaches.get(intent.getIntExtra("id", 0));
        photoView.setImageBitmap(content);
        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(photoView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.down)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final EditText editText=new EditText(this);
            builder.setCancelable(true).setTitle("请输入文件名\n文件大小"+ DataInfoUtils.getBitmapSize(content)).setView(editText);
            builder.setCancelable(true).setNegativeButton("取消",null).setPositiveButton("下载", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(editText.getText().toString().isEmpty())
                    {
                        Toast.makeText(PhotoActivity.this,"文件名不能为空",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String res=DataInfoUtils.download(content,editText.getText().toString());
                        if(res!=null)
                        {
                            Toast.makeText(PhotoActivity.this,"下载成功,文件路径 "+res,Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(PhotoActivity.this,"下载失败",Toast.LENGTH_SHORT).show();
                    }
                }
            }).show();
        }
        return  super.onOptionsItemSelected(item);
    }
}
