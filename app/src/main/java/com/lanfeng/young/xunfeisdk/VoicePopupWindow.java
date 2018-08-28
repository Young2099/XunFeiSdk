package com.lanfeng.young.xunfeisdk;

import android.view.View;

/**
 * Created by yf on 2018/8/27.
 */
public class VoicePopupWindow extends BasePopupWindow {
    @Override
    public View getView() {
        return layoutInflater.inflate(R.layout.item_voice, null);
    }
}
