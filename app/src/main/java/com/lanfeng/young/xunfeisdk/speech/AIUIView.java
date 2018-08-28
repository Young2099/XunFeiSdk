package com.lanfeng.young.xunfeisdk.speech;

import com.lanfeng.young.xunfeisdk.RawMessage;

import java.util.List;

/**
 * Created by yf on 2018/8/9.
 */
public interface AIUIView {
    void showContent(String service, String message);

    void showVolume(int arg2);

    void showErrorMessage(String error);

    void showText(List<RawMessage> map);
}
