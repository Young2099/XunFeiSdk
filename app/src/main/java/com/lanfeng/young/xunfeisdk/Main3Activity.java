package com.lanfeng.young.xunfeisdk;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

public class Main3Activity extends AppCompatActivity {
    MyRecyclerView mRecyclerView;
    final List<String> list = new ArrayList<>();
    private boolean isFlag = false;
    private boolean isMove = false;
    private MyLinayout ll_content;
    public float lastY;
    private String translationY = "translationY";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        mRecyclerView = findViewById(R.id.recyclerview);
        ll_content = findViewById(R.id.ll_content);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        list.add("今天天气怎么样");
        list.add("西瓜用英文怎么说");
        list.add("九九乘法表");
        list.add("朗读一首李白的诗");
        list.add("安静的静怎么写");
        list.add("魑魅魍魉是啥意思");
        list.add("给中国移动打电话");
        list.add("周杰伦是谁");
        list.add("我要查清华大学的分数线");
        list.add("给我来个段子");
        list.add("北京有哪些大学");
        list.add("历史上的今天发生了什么");
        list.add("我要学英语");
        list.add("难过的反义词");
        list.add("关于励志的经典语句");
        list.add("给我来个演说");
        list.add("来一句英语");

        HotWordAdapter adapter = new HotWordAdapter(list);
        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);

        ll_content.setMoveFlag(isMove);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastY = event.getRawY();
                        Log.e("TAG", "mRecyclerView: " + lastY);
                        break;

                }
                return false;
            }
        });


        ll_content.setOnTouchListener(new View.OnTouchListener() {
            float dy;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.e("TAG", "onTouch: " + event.getY());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        dy = event.getY() - lastY;
                        Log.e("TAG", "onTouchMove: " + dy);
                        if (dy > -ll_content.getHeight() / 2 && dy < 0) {
                            ll_content.scrollTo(0, 0);
                        }
                        break;
                    case MotionEvent.ACTION_UP:

                        break;
                }
                return true;
            }
        });

    }


}
