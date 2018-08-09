package com.lanfeng.young.xunfeisdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.iflytek.aiui.AIUIConstant;
import com.lanfeng.young.xunfeisdk.speech.AIUIRepository;
import com.lanfeng.young.xunfeisdk.speech.AIUIView;



import java.io.IOException;
import java.io.InputStream;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements AIUIView {

    private static String TAG = MainActivity.class.getSimpleName();

    //录音权限
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    private Toast mToast;
    private Button mStartRecord;
    //交互状态
    private int mAIUIState = AIUIConstant.STATE_IDLE;
    //是否检测到前端点，提示 ’为说话‘ 时判断使用
    private boolean mVadBegin = false;
    private AIUIRepository aiuiRepository;

    @SuppressLint("ShowToast")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        requestPermission();
        aiuiRepository = new AIUIRepository(this);
        aiuiRepository.attach(this);
        aiuiRepository.initAIUIAgent();
        aiuiRepository.getContract();
        initLayout();

    }

    /**
     * 初始化Layout。
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initLayout() {
        mStartRecord = findViewById(R.id.nlp_start);
//        mStartRecord.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                aiuiRepository.initAIUIAgent();
//                aiuiRepository.startVoice();
//            }
//        });
        mStartRecord.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                aiuiRepository.startVoice();
                return true;
            }
        });

    }


    /**
     * 读取配置
     */
    private String getAIUIParams() {
        String params = "";

        AssetManager assetManager = getResources().getAssets();
        try {
            InputStream ins = assetManager.open("cfg/aiui_phone.cfg");
            byte[] buffer = new byte[ins.available()];

            ins.read(buffer);
            ins.close();

            params = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return params;
    }


//    //开始录音
//    private void startVoiceNlp() {
//        Log.i(TAG, "start voice nlp");
//        mNlpText.setText("");
//
//        // 先发送唤醒消息，改变AIUI内部状态，只有唤醒状态才能接收语音输入
//        // 默认为oneshot 模式，即一次唤醒后就进入休眠，如果语音唤醒后，需要进行文本语义，请将改段逻辑copy至startTextNlp()开头处
//        if (AIUIConstant.STATE_WORKING != this.mAIUIState) {
//            AIUIMessage wakeupMsg = new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null);
//            mAIUIAgent.sendMessage(wakeupMsg);
//        }
//
//        // 打开AIUI内部录音机，开始录音
//        String params = "sample_rate=16000,data_type=audio";
//        AIUIMessage writeMsg = new AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, params, null);
//        mAIUIAgent.sendMessage(writeMsg);
//
//
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


        aiuiRepository.detachView();
    }

    private void showTip(final String str) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mToast.setText(str);
                mToast.show();
            }
        });
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
    public void showContent(String message) {

    }

}