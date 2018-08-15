package com.lanfeng.young.xunfeisdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 应用设置帮助类，SharedPreference存储
 *
 * @author liuyao
 */
public final class SharedPreferencesUtil {
    private static final String PHONE_NUMBER = "PHONE_NUMBER";
    private static final String APP = "app_user";
    public static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }


    /**
     * 设置Int型键值
     *
     * @param key
     * @param value
     */
    public static void setInteger(String filename, String key, int value) {
        Editor editor = mContext.getSharedPreferences(filename, Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 设置String型键值
     *
     * @param key
     * @param value
     */
    public static void setString(String filename, String key, String value) {
        Editor editor = mContext.getSharedPreferences(filename, Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS).edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 设置Boolean型键值
     *
     * @param key
     * @param value
     */
    public static void setBoolean(String filename, String key, boolean value) {
        Editor editor = mContext.getSharedPreferences(filename, Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 设置Long型键值
     *
     * @param key
     * @param value
     */
    public static void setLong(String filename, String key, long value) {
        Editor editor = mContext.getSharedPreferences(filename, Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * 获取Int型值
     *
     * @param key
     * @return
     */
    public static int getInteger(String filename, String key, Integer defaultValue) {
        SharedPreferences preferences = mContext.getSharedPreferences(filename, Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        return preferences.getInt(key, defaultValue);
    }

    /**
     * 获取String型值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getString(String filename, String key, String defaultValue) {
        SharedPreferences preferences = mContext.getSharedPreferences(filename, Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        return preferences.getString(key, defaultValue);
    }


    /**
     * 获取Boolean型值
     *
     * @param key
     * @return
     */
    public static boolean getBoolean(String filename, String key, boolean defaultValue) {
        SharedPreferences preferences = mContext.getSharedPreferences(filename, Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        return preferences.getBoolean(key, defaultValue);
    }

    /**
     * 获取Long型值
     *
     * @param key
     * @return
     */
    public static long getLong(String filename, String key, long defaultValue) {
        SharedPreferences preferences = mContext.getSharedPreferences(filename, Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        return preferences.getLong(key, defaultValue);
    }


    /**
     * 保存当前登录用户名
     *
     * @param name
     */
    public static void setUserName(String name) {
        SharedPreferencesUtil.setString(APP, PHONE_NUMBER, name);
    }

    /**
     * 读取当前登录用户名
     *
     * @return
     */
    public static String getUserName() {
        return SharedPreferencesUtil.getString(APP, PHONE_NUMBER, "");
    }


}
