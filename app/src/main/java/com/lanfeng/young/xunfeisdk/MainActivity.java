package com.lanfeng.young.xunfeisdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.lanfeng.young.xunfeisdk.speech.AIUIRepository;
import com.lanfeng.young.xunfeisdk.speech.AIUIView;
import com.lanfeng.young.xunfeisdk.view.CircleButtonView;
import com.lanfeng.young.xunfeisdk.view.VoiceLineView;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements AIUIView {

    private static String TAG = MainActivity.class.getSimpleName();

    //录音权限
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    private CircleButtonView mStartRecord;
    private AIUIRepository aiuiRepository;
    private VoiceLineView mVoline;
    private RecyclerView mRecyclerView;
    private RecyclerView hotWordRecycler;
    private boolean isTouch = false;
    VoiceAdapter adapter;
    final List<String> list = new ArrayList<>();
    private boolean isFlag;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        requestPermission();
        aiuiRepository = new AIUIRepository(this);
        aiuiRepository.attach(this);
        aiuiRepository.initAIUIAgent();
//        aiuiRepository.getContract();
        initLayout();
    }

    /**
     * 初始化Layout。
     */

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ClickableViewAccessibility")
    private void initLayout() {
        mStartRecord = findViewById(R.id.circle);
        mVoline = findViewById(R.id.voicLine);
        mRecyclerView = findViewById(R.id.rv_recycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        hotWordRecycler = findViewById(R.id.hot_word_recy);

        mStartRecord.setOnLongClickListener(new CircleButtonView.OnLongClickListener() {
            @Override
            public void onLongClick() {
                aiuiRepository.startVoice();
            }

            @Override
            public void onNoMinRecord(int currentTime) {

            }

            @Override
            public void onRecordFinishedListener() {

            }
        });

        for (int i = 0; i < 18; i++) {
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
        }
        mStartRecord.setOnClickListener(new CircleButtonView.OnClickListener() {
            @Override
            public void onClick() {
                aiuiRepository.stopAudio();
            }
        });

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dx = event.getX();
                        dy = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float d = event.getY() - dy;
                        Log.e(TAG, "onTouch: " + d);
                        if (d < -400 ) {
                            final HotWordAdapter voiceAdapter = new HotWordAdapter(list);
                            mRecyclerView.setVisibility(View.GONE);
                            hotWordRecycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            hotWordRecycler.setVisibility(View.VISIBLE);

                            int resId = R.anim.layout_animation_fall_down;
                            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(SoApp.mContext, resId);
                            hotWordRecycler.setLayoutAnimation(animation);
                            hotWordRecycler.setAdapter(voiceAdapter);
                            voiceAdapter.notifyDataSetChanged();
                            hotWordRecycler.scheduleLayoutAnimation();
                        }
                        break;
                }
                return false;
            }
        });

        hotWordRecycler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dx = event.getX();
                        dy = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float d = event.getY() - dy;
                        Log.e(TAG, "onTouch: " + d);
                        if (d > 400) {
                            hotWordRecycler.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            isTouch = false;
                        } else {
                            isTouch = true;
                        }
                        break;
                }
                return isTouch;
            }

        });
    }


    public void setWindowBgAlpha(float f) {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = f;
        params.dimAmount = f;
        getWindow().setAttributes(params);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    private float dx;
    private float dy;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        aiuiRepository.detachView();
        if (null != mRecyclerView) {
        }
    }

    //申请录音权限
    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            if (i != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 321);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 321) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PERMISSION_GRANTED) {
                    this.finish();
                }
            }
        }
    }


    @Override
    public void showContent(String service, String message) {
        if ("telephone".equals(service)) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + message));
            startActivity(intent);
        }
    }

    @Override
    public void showVolume(final int arg2) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mVoline.setVolume(arg2);
            }
        });
    }

    @Override
    public void showErrorMessage(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
    private List<RawMessage> currentList;
    @Override
    public void showText(List<RawMessage> list) {
        currentList = list;
        adapter = new VoiceAdapter(list);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.scrollToPosition(adapter.getItemCount()-1);

    }

}