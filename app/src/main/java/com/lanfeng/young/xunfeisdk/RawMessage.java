package com.lanfeng.young.xunfeisdk;

import com.google.gson.JsonObject;

/**
 * Created by yf on 2018/8/28.
 */
public class RawMessage {
    private String voice = "";
    private String message = "";
    private String intent;
    private JsonObject jsonObject;

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

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

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    @Override
    public String toString() {
        return "RawMessage{" +
                "voice='" + voice + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
