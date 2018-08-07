package com.lanfeng.young.xunfeisdk;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;
import com.iflytek.aiui.AIUIListener;
import com.iflytek.aiui.AIUIMessage;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getP();
        AIUIAgent mAIUIAgent = AIUIAgent.createAgent(this, getAIUIParams(), mAIUIListener);
        // 先发送唤醒消息，改变AIUI内部状态，只有唤醒状态才能接收语音输入
        if (AIUIConstant.STATE_WORKING != mAIUIState) {
            AIUIMessage wakeupMsg = new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null);
            mAIUIAgent.sendMessage(wakeupMsg);
        }

// 打开AIUI内部录音机，开始录音
        String params = "sample_rate=16000,data_type=audio";
        AIUIMessage writeMsg = new AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, params, null);
        mAIUIAgent.sendMessage(writeMsg);
    }

    private void getP() {


    }

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

    private int mAIUIState;
    private AIUIListener mAIUIListener = new AIUIListener() {

        @Override
        public void onEvent(AIUIEvent event) {
            switch (event.eventType) {
                case AIUIConstant.EVENT_WAKEUP:
                    //唤醒事件
                    Log.i(TAG, "on event: " + event.eventType);
                    break;

                case AIUIConstant.EVENT_RESULT: {
                    //结果解析事件
                    try {
                        JSONObject bizParamJson = new JSONObject(event.info);
                        JSONObject data = bizParamJson.getJSONArray("data").getJSONObject(0);
                        JSONObject params = data.getJSONObject("params");
                        JSONObject content = data.getJSONArray("content").getJSONObject(0);

                        if (content.has("cnt_id")) {
                            String cnt_id = content.getString("cnt_id");
                            JSONObject cntJson = new JSONObject(new String(event.data.getByteArray(cnt_id), "utf-8"));
                            String sub = params.optString("sub");
                            if ("nlp".equals(sub)) {
                                // 解析得到语义结果
                                String resultStr = cntJson.optString("intent");
                                Log.i(TAG, resultStr);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
                break;

                case AIUIConstant.EVENT_ERROR: {
                    //错误事件
                    Log.i(TAG, "on event: " + event.eventType);
                    Log.e(TAG, "错误: " + event.arg1 + "\n" + event.info);
                }
                break;

                case AIUIConstant.EVENT_VAD: {
                    if (AIUIConstant.VAD_BOS == event.arg1) {
                        //语音前端点
                    } else if (AIUIConstant.VAD_EOS == event.arg1) {
                        //语音后端点
                    }
                }
                break;

                case AIUIConstant.EVENT_START_RECORD: {
                    Log.i(TAG, "on event: " + event.eventType);
                    //开始录音
                }
                break;

                case AIUIConstant.EVENT_STOP_RECORD: {
                    Log.i(TAG, "on event: " + event.eventType);
                    // 停止录音
                }
                break;

                case AIUIConstant.EVENT_STATE: {
                    // 状态事件
                    mAIUIState = event.arg1;
                    if (AIUIConstant.STATE_IDLE == mAIUIState) {
                        // 闲置状态，AIUI未开启
                    } else if (AIUIConstant.STATE_READY == mAIUIState) {
                        // AIUI已就绪，等待唤醒
                    } else if (AIUIConstant.STATE_WORKING == mAIUIState) {
                        // AIUI工作中，可进行交互
                    }
                }
                break;

                default:
                    break;
            }
        }
    };
}
