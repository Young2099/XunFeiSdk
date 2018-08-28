package com.lanfeng.young.xunfeisdk;

/**
 * Created by yf on 2018/8/28.
 */
public class RawMessage {
    private String voice = "你好，很高兴见到你";
    private String message = "";

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "RawMessage{" +
                "voice='" + voice + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
