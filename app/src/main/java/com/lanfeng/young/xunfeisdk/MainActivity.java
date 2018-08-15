package com.lanfeng.young.xunfeisdk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Window;
import android.widget.Toast;

import com.lanfeng.young.xunfeisdk.speech.AIUIRepository;
import com.lanfeng.young.xunfeisdk.speech.AIUIView;
import com.lanfeng.young.xunfeisdk.view.AutoPollAdapter;
import com.lanfeng.young.xunfeisdk.view.AutoPollRecyclerView;
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
    private AutoPollRecyclerView mRecyclerView;

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
    private void initLayout() {
        mStartRecord = findViewById(R.id.circle);
        mVoline = findViewById(R.id.voicLine);
        mRecyclerView = findViewById(R.id.rv_recycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; ) {
            list.add(" Item333: " + ++i);
        }
        AutoPollAdapter adapter = new AutoPollAdapter(list);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.start();

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

        mStartRecord.setOnClickListener(new CircleButtonView.OnClickListener() {
            @Override
            public void onClick() {
                aiuiRepository.stopAudio();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        aiuiRepository.detachView();
        if (null != mRecyclerView) {
            mRecyclerView.stop();
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

}