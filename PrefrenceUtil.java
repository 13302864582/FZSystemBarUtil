package fz.com.androidarcture;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Administrator on 2017/11/3.
 */

public class PrefrenceUtil {
    private static SharedPreferences mInstance;

    public static void init(Context context) {
        if (mInstance == null) {                         //Single Checked
            synchronized (PrefrenceUtil.class) {
                if (mInstance == null) {                 //Double Checked
                    mInstance = PreferenceManager.getDefaultSharedPreferences(context);
                }
            }
        }
    }

    public static int getInt(String name) {
        return getInt(name, 0);
    }

    public static int getInt(String name, int defValue) {
        return mInstance.getInt(name, defValue);
    }

    public static void putInt(String name, int Value) {
        Edit().putInt(name, Value).commit();
    }

    public static boolean getBoolean(String name) {
        return getBoolean(name, false);
    }

    public static boolean getBoolean(String name, boolean defValue) {
        return mInstance.getBoolean(name, defValue);
    }

    public static void putBoolean(String name, boolean Value) {
        Edit().putBoolean(name, Value).commit();
    }


    public static String getString(String name) {
        return getString(name, "");
    }

    public static String getString(String name, String defValue) {
        return mInstance.getString(name, defValue);
    }

    public static void putString(String name, String Value) {
        Edit().putString(name, Value).commit();
    }

    public static float getFloat(String name) {
        return getFloat(name, 0.0f);
    }

    public static float getFloat(String name, float defValue) {
        return mInstance.getFloat(name, defValue);
    }

    public static void putFloat(String name, float Value) {
        Edit().putFloat(name, Value).commit();
    }

    public static long getLong(String name) {
        return getLong(name, 0);
    }

    public static long getLong(String name, long defValue) {
        return mInstance.getLong(name, defValue);
    }

    public static void putLong(String name, long Value) {
        Edit().putLong(name, Value).commit();
    }

    public static Set<String> getStringSet(String name) {
        return getStringSet(name, new HashSet<String>());
    }

    public static Set<String> getStringSet(String name, Set<String> defValue) {
        return mInstance.getStringSet(name, defValue);
    }

    public static void putStringSet(String name, Set<String> Value) {
        Edit().putStringSet(name, Value).commit();
    }

    private static SharedPreferences.Editor Edit() {
        return mInstance.edit();
    }
}
