package com.ligf.androidutilslib.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 使用SharePreferences进行简单的数据的保存
 * Created by ligf on 2018/4/10.
 */

public class SharedPreferencesUtil {

    private static SharedPreferences mSharedPreferences = null;
    private static SharedPreferences.Editor mEditor = null;

    /**
     * SharedPreferences的初始化（建议在Application的onCreate方法中调用）
     */
    public static void init(Context context){
        mSharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public static void putBoolean(String key, boolean value){
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    public static void putInt(String key, int value){
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public static void putFloat(String key, float value){
        mEditor.putFloat(key, value);
        mEditor.commit();
    }

    public static void putLong(String key, long value){
        mEditor.putLong(key, value);
        mEditor.commit();
    }

    public static void putString(String key, String value){
        mEditor.putString(key, value);
        mEditor.commit();
    }

    public static boolean getBoolean(String key){
        return mSharedPreferences.getBoolean(key, false);
    }

    public static int getInt(String key){
        return mSharedPreferences.getInt(key, 0);
    }

    public static float getFloat(String key){
        return mSharedPreferences.getFloat(key, 0.0F);
    }

    public static long getLong(String key){
        return mSharedPreferences.getLong(key, 0L);
    }

    public static String getString(String key){
        return mSharedPreferences.getString(key, "");
    }
}
