package com.lanfeng.young.xunfeisdk.speech;

/**
 * created by yf on 2018/8/15.
 */
public class BaseVoice {
    private String rc;
    private String text;
    private String service;

    public String getRc() {
        return rc;
    }

    public void setRc(String rc) {
        this.rc = rc;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    @Override
    public String toString() {
        return "BaseVoice{" +
                "rc='" + rc + '\'' +
                ", text='" + text + '\'' +
                ", service='" + service + '\'' +
                '}';
    }
}
