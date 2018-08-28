package com.lanfeng.young.xunfeisdk;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.iflytek.cloud.SpeechUtility;

/**
 * Created by yf on 2018/8/8.
 */
public class SoApp extends Application {
    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        SpeechUtility.createUtility(this, String.format("engine_start=ivw,delay_init=0,appid=%s","5b695d90"));
        mContext = this;
    }

}
