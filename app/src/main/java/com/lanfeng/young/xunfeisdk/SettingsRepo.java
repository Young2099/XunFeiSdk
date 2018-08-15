package com.lanfeng.young.xunfeisdk;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.lanfeng.young.xunfeisdk.speech.Settings;

import org.json.JSONObject;

import javax.inject.Named;

/**
 * 设置repo
 */

public class SettingsRepo {
    public static final String KEY_AIUI_WAKEUP = "aiui_wakeup";
    public static final String KEY_AIUI_TRANSLATION = "aiui_translation";
    public static final String KEY_DEFAULT_APPID = "last_appid";
    public static final String KEY_DEFAULT_KEY = "last_key";
    public static final String KEY_DEFAULT_SCENE = "last_scene";
    public static final String KEY_TRANS_SCENE = "trans_scene";
    public static final String KEY_APPID = "appid";
    public static final String KEY_APP_KEY = "key";
    public static final String KEY_SCENE = "scene";
    public static final String AIUI_TTS = "aiui_tts";
    public static final String AIUI_LOG = "aiui_log";

    private Context mContext;
    private String mDefaultAppid;
    private String mDefaultAppKey;
    private String mDefaultScene;
    private String mTransScene;
    private MutableLiveData<Settings> mSettings = new MutableLiveData<>();
    private MutableLiveData<Boolean> mLiveWakeUpEnable = new MutableLiveData<>();
    private MutableLiveData<Boolean> mLiveTransEnable = new MutableLiveData<>();
    private MutableLiveData<Boolean> mLiveTTSEnable = new MutableLiveData<>();

    public SettingsRepo(Context context, @Named("AIUI cfg") JSONObject config) {
        mContext = context;
        //保存配置文件中默认的appid和key,scene
        mDefaultAppid = config.optJSONObject("login").optString(KEY_APPID);
        mDefaultAppKey = config.optJSONObject("login").optString(KEY_APP_KEY);
        mDefaultScene = config.optJSONObject("global").optString(KEY_SCENE);

        mSettings.postValue(getLatestSettings(mContext));
    }

    public void config(String appid, String key, String scene) {
        //设置新的appid，key及scene，更新到sharePreference中
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_APPID, appid);
        editor.putString(KEY_APP_KEY, key);
        editor.putString(KEY_SCENE, scene);
        editor.commit();

        updateSettings();
    }

    public LiveData<Settings> getSettings() {
        return mSettings;
    }

    public void updateSettings() {
        //通知监听配置更新
        mSettings.postValue(getLatestSettings(mContext));
    }

    public LiveData<Boolean> getWakeUpEnableState() {
        return mLiveWakeUpEnable;
    }

    public LiveData<Boolean> getTransEnableState() {
        return mLiveTransEnable;
    }

    public LiveData<Boolean> getTTSEnableState() {
        return mLiveTTSEnable;
    }

    @NonNull
    private Settings getLatestSettings(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String lastConfigAppid = preferences.getString(KEY_DEFAULT_APPID, "");
        String lastConfigKey = preferences.getString(KEY_DEFAULT_KEY, "");
        String lastConfigScene = preferences.getString(KEY_DEFAULT_SCENE, "");
        //不同说明APK重装更新了assets下的aiui.cfg，将新的appid，key的设置同步到所有的配置
        if(!lastConfigAppid.equals(mDefaultAppid) || !lastConfigKey.equals(mDefaultAppKey) ||
                !lastConfigScene.equals(mDefaultScene)) {
            syncDefaultConfig(preferences);
            restoreDefaultConfig(preferences);
        }

        //将appid和key为空时恢复默认appid，key
        if(TextUtils.isEmpty(preferences.getString(KEY_APPID, "")) && TextUtils.isEmpty(
                preferences.getString(KEY_APP_KEY, ""))) {
            restoreDefaultConfig(preferences);
        }

        //因为唤醒资源和appid绑定，当前appid不为默认配置时禁止唤醒功能开启
        if(!mDefaultAppid.equals(preferences.getString(KEY_APPID, ""))) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_AIUI_WAKEUP, false);
            editor.commit();
            mLiveWakeUpEnable.postValue(false);
        } else {
            mLiveWakeUpEnable.postValue(false);
        }

        Settings settings = new Settings();
        settings.wakeup = preferences.getBoolean(KEY_AIUI_WAKEUP, false);
        settings.translation = preferences.getBoolean(KEY_AIUI_TRANSLATION, false);
        settings.bos = Integer.valueOf(preferences.getString("aiui_bos", "5000"));
        settings.eos = Integer.valueOf(preferences.getString("aiui_eos", "1000"));
        settings.debugLog = preferences.getBoolean("aiui_debug_log", true);
        settings.saveDebugLog = preferences.getBoolean("aiui_save_datalog", false);
        settings.appid = preferences.getString(KEY_APPID, "");
        settings.key = preferences.getString(KEY_APP_KEY, "");
        settings.scene = preferences.getString(KEY_SCENE, "");
        settings.tts = preferences.getBoolean(AIUI_TTS, false);
        settings.saveAIUILog = preferences.getBoolean(AIUI_LOG, false);

        mLiveTTSEnable.postValue(settings.tts);
        mLiveTransEnable.postValue(settings.translation);

        return settings;
    }

    public String getTransScene() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return preferences.getString(KEY_TRANS_SCENE, "trans");
    }

    public void setTransScene(String scene){
        setTransScenePref(PreferenceManager.getDefaultSharedPreferences(mContext), scene);
    }

    /**
     * 更新默认配置
     * @param preferences 当前配置的SharePreference
     */
    private void syncDefaultConfig(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_DEFAULT_APPID, mDefaultAppid);
        editor.putString(KEY_DEFAULT_KEY, mDefaultAppKey);
        editor.putString(KEY_DEFAULT_SCENE, mDefaultScene);
        editor.commit();
    }

    /**
     * 恢复appid,key到默认配置
     * @param preferences 当前配置的SharePreference
     */
    private void restoreDefaultConfig(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_APPID, mDefaultAppid);
        editor.putString(KEY_APP_KEY, mDefaultAppKey);
        editor.putString(KEY_SCENE, mDefaultScene);
        editor.commit();
    }

    private void setTransScenePref(SharedPreferences preferences, String scene) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_TRANS_SCENE, scene);
        editor.apply();
    }
}
