package com.example.lyk.forwater.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;

import com.example.lyk.forwater.R;

import java.util.Calendar;

import static com.example.lyk.forwater.utils.DataInfoUtils.dip2px;


public class AnimationActivity extends AppCompatActivity {

    private FloatingActionButton fab_add;
    private FloatingActionButton fab_comment;
    private FloatingActionButton fab_time;
    private boolean ismAnimating = true;
    private static final int animationTime = 1000;
    private Calendar time=null;
    private float posx,posy;
    private  boolean isComment=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       //requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_animation);
        Intent intent=getIntent();
        float[] position=intent.getFloatArrayExtra("pos");
        posx=position[0];
        posy=position[1];
        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_comment = (FloatingActionButton) findViewById(R.id.fab_comment);
        fab_comment.setVisibility(View.GONE);
        fab_time = (FloatingActionButton) findViewById(R.id.fab_time);
        fab_time.setVisibility(View.GONE);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               if(!ismAnimating)
               {
                   ismAnimating=true;
                    fab_add.setImageResource(R.mipmap.add);
                   backAnimation();
               }

            }
        });
        fab_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ismAnimating) {
                    final Calendar calendar = Calendar.getInstance();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(AnimationActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                            ismAnimating=true;
                            time=calendar;
                            backAnimation();

                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.setTitle("请选择查询日期");
                    datePickerDialog.show();

                }
            }
        });
        fab_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ismAnimating) {
                    ismAnimating = true;
                    isComment=true;
                    backAnimation();
                }
            }
        });
        findViewById(R.id.coordinator_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ismAnimating)
                {
                    ismAnimating=true;
                    fab_add.setImageResource(R.mipmap.add);
                    backAnimation();
                }
            }
        });
    }
    private void backAnimation() {
        ObjectAnimator timeX = ObjectAnimator.ofFloat(fab_time, "x", posx);
        // ObjectAnimator timeY=ObjectAnimator.ofFloat(fab_time,"y",fab_add.getY());
        ObjectAnimator timeScaleX = ObjectAnimator.ofFloat(fab_time, "scaleX", 0.75f, 0);
        ObjectAnimator timeScaleY = ObjectAnimator.ofFloat(fab_time, "scaleY", 0.75f, 0);
        final ObjectAnimator timeRotation = ObjectAnimator.ofFloat(fab_time, "rotation", 360, 0);
        AnimatorSet animatorTimeSet = new AnimatorSet();
        animatorTimeSet.setDuration(animationTime);
        animatorTimeSet.playTogether(timeX, timeScaleX, timeScaleY, timeRotation);
        animatorTimeSet.start();
        //ObjectAnimator commentX=ObjectAnimator.ofFloat(fab_comment,"x",fab_add.getX());
        ObjectAnimator commentY = ObjectAnimator.ofFloat(fab_comment, "y", posy);
        ObjectAnimator commentScaleX = ObjectAnimator.ofFloat(fab_comment, "scaleX", 0.75f, 0);
        ObjectAnimator commentScaleY = ObjectAnimator.ofFloat(fab_comment, "scaleY", 0.75f, 0);
        ObjectAnimator commentRotation = ObjectAnimator.ofFloat(fab_comment, "rotation", 360, 0);
        AnimatorSet animatorCommentSet = new AnimatorSet();
        animatorCommentSet.setDuration(animationTime);
        animatorCommentSet.playTogether(commentY, commentScaleX, commentScaleY, commentRotation);
        animatorCommentSet.start();
        animatorTimeSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ismAnimating = false;
                super.onAnimationEnd(animation);
                fab_comment.setVisibility(View.GONE);
                fab_time.setVisibility(View.GONE);
                if(time!=null)
                {
                    Intent intent=new Intent(AnimationActivity.this,InfoActivity.class);
                    intent.putExtra("time",time);
                    startActivity(intent);

                }
                if(isComment)
                {
                    Intent intent=new Intent(AnimationActivity.this,InfoActivity.class);
                    intent.putExtra("comment"," ");
                    startActivity(intent);
                }
                finish();
                overridePendingTransition(R.anim.empty,R.anim.empty);


            }
        });
    }
    private void forwardAnimation() {
        fab_comment.setVisibility(View.VISIBLE);
        fab_time.setVisibility(View.VISIBLE);
        ObjectAnimator timeX = ObjectAnimator.ofFloat(fab_time, "x", posx,posx - dip2px(this, 80));
        // ObjectAnimator timeY=ObjectAnimator.ofFloat(fab_time,"y",fab_add.getY()-dip2px(this,80));
        ObjectAnimator timeScaleX = ObjectAnimator.ofFloat(fab_time, "scaleX", 0, 0.75f);
        ObjectAnimator timeScaleY = ObjectAnimator.ofFloat(fab_time, "scaleY", 0, 0.75f);
        ObjectAnimator timeRotation = ObjectAnimator.ofFloat(fab_time, "rotation", 0, 360);
        AnimatorSet animatorTimeSet = new AnimatorSet();
        animatorTimeSet.setDuration(animationTime);
        animatorTimeSet.playTogether(timeX, timeScaleX, timeScaleY, timeRotation);
        animatorTimeSet.start();
        // ObjectAnimator commentX=ObjectAnimator.ofFloat(fab_comment,"x",fab_add.getX()-dip2px(this,100));
        ObjectAnimator commentY = ObjectAnimator.ofFloat(fab_comment, "y", posy,posy - dip2px(this, 80));
        ObjectAnimator commentScaleX = ObjectAnimator.ofFloat(fab_comment, "scaleX", 0, 0.75f);
        ObjectAnimator commentScaleY = ObjectAnimator.ofFloat(fab_comment, "scaleY", 0, 0.75f);
        ObjectAnimator commentRotation = ObjectAnimator.ofFloat(fab_comment, "rotation", 0, 360);
        AnimatorSet animatorCommentSet = new AnimatorSet();
        animatorCommentSet.setDuration(animationTime);
        animatorCommentSet.playTogether(commentY, commentScaleX, commentScaleY, commentRotation);
        animatorCommentSet.start();
        animatorTimeSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ismAnimating = false;
                super.onAnimationEnd(animation);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        forwardAnimation();
    }

    @Override
    public void onBackPressed() {
        if(!ismAnimating)
        {
            ismAnimating=true;
            fab_add.setImageResource(R.mipmap.add);
            backAnimation();
        }

    }
}
