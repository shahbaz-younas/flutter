package com.qearner.quiz.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.qearner.quiz.Constant;

public class Session {

    public static SharedPreferences pref;
    public static SharedPreferences.Editor editor;
    Context _context;

    public static final String SETTING_Quiz_PREF = "setting_quiz_pref";
    private static final String SOUND_ONOFF = "sound_enable_disable";
    private static final String SHOW_MUSIC_ONOFF = "showmusic_enable_disable";
    private static final String VIBRATION = "vibrate_status";


    public static final String IS_QUIZ_COMPLETED = "IS_QUIZ_COMPLETED";
    public static final String COUNT_QUESTION_COMPLETED = "count_question_completed";

    public static final String PREFER_NAME = "QuizToCashPref";
    public static final String KEY_POINT = "points";
    public static final String KEY_ACTIVEQUIZ = "activequiz";
    public static final String KEY_SCORE = "score";
    public static final String KEY_QUEATTEND = "queattend";
    public static final String KEY_CORRECTANS = "currectans";
    public static final String KEY_WRONGANS = "wrongans";

    public static final String LANG_MODE = "lang_mode";
    public static final String N_COUNT = "n_count";
    public static final String E_MODE = "e_mode";
    public static final String LOGIN = "login";
    public static final String USER_ID = "userId";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String MOBILE = "mobile";
    public static final String F_CODE = "f_code";
    public static final String IS_FIRST_TIME = "isfirsttime";
    public static final String REFER_CODE = "refer_code";
    public static final String TYPE = "type";
    public static final String PROFILE = "profile";
    public static final String JWT_KEY = "jwtkey";
    public static final String UID = "uid";
    public static final String LANGUAGE = "language";
    public static final String GETDAILY = "getdaily";
    public static final String GETCONTEST = "getcontest";
    public static final String FCM = "fcm";

    public Session(Context context) {
        this._context = context;
        pref = PreferenceManager.getDefaultSharedPreferences(_context);
        editor = pref.edit();
    }

    public static String getData(String id) {
        if (id.equalsIgnoreCase(KEY_SCORE) || id.equalsIgnoreCase(KEY_QUEATTEND) || id.equalsIgnoreCase(KEY_ACTIVEQUIZ) || id.equalsIgnoreCase(KEY_CORRECTANS))
            return pref.getString(id, "0");
        else
            return pref.getString(id, null);
    }

    public static void setData(String id, String val) {
        editor.putString(id, val);
        editor.commit();
    }

    // public static final String
    public static void setVibration(Context context, Boolean result) {
        SharedPreferences prefs = context.getSharedPreferences(SETTING_Quiz_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = prefs.edit();
        prefEditor.putBoolean(VIBRATION, result);
        prefEditor.apply();
    }

    public static boolean getVibration(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(SETTING_Quiz_PREF, Context.MODE_PRIVATE);
            return prefs.getBoolean(VIBRATION, Utils.DEFAULT_VIBRATION_SETTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Utils.DEFAULT_VIBRATION_SETTING;
    }

    public static void setSoundEnableDisable(Context context, Boolean result) {
        SharedPreferences prefs = context.getSharedPreferences(SETTING_Quiz_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = prefs.edit();
        prefEditor.putBoolean(SOUND_ONOFF, result);
        prefEditor.apply();
    }

    public static boolean getSoundEnableDisable(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(SETTING_Quiz_PREF, Context.MODE_PRIVATE);
            return prefs.getBoolean(SOUND_ONOFF, Utils.DEFAULT_SOUND_SETTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Utils.DEFAULT_SOUND_SETTING;
    }

    public static void setMusicEnableDisable(Context context, Boolean result) {
        SharedPreferences prefs = context.getSharedPreferences(SETTING_Quiz_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = prefs.edit();
        prefEditor.putBoolean(SHOW_MUSIC_ONOFF, result);
        prefEditor.apply();
    }

    public static boolean getMusicEnableDisable(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                SETTING_Quiz_PREF, Context.MODE_PRIVATE);
        return prefs.getBoolean(SHOW_MUSIC_ONOFF,
                Utils.DEFAULT_MUSIC_SETTING);
    }

    public static boolean setMark(Context context, String Key, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Key, value);
        editor.apply();
        return value;
    }

    public static boolean getBooleanValue(Context context, String key) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(key, false);
    }


    public static void setCurrentLanguage(String value, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LANGUAGE, value);
        editor.apply();

    }

    //this method will fetch the device token from shared preferences
    public static String getCurrentLanguage(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(LANGUAGE, Constant.D_LANG_ID);
    }

    public static boolean getBoolean(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, false);
    }

    public static void setBoolean(String key, boolean value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }


    public static boolean isQuizCompleted(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(IS_QUIZ_COMPLETED, false);
    }
    // /getPWDFromSP()

    public static void setQuizComplete(Context context, boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_QUIZ_COMPLETED, value);
        editor.apply();
    }

    //fireBase token
    public static void setDeviceToken(String token, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    //this method will fetch the device token from shared preferences
    public static String getDeviceToken(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("token", "");
    }

    public static void setFifty_Fifty(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("fifty_fifty", true);
        editor.apply();
    }

    public static boolean isFiftyFiftyUsed(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("fifty_fifty", false);
    }

    public static void setReset(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("reset", true);
        editor.apply();
    }

    public static boolean isResetUsed(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("reset", false);
    }

    public static void setAudiencePoll(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("audience", true);
        editor.apply();
    }

    public static boolean isAudiencePollUsed(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("audience", false);
    }


    public static void setSkip(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("skip", true);
        editor.apply();
    }

    public static boolean isSkipUsed(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("skip", false);
    }

    public static void setNCount(int value, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(N_COUNT, value);
        editor.apply();
    }

    public static int getNCount(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(N_COUNT, 0);
    }

    public static void setFCode(String value, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(F_CODE, value);
        editor.apply();
    }

    public static String getFCode(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(F_CODE, "");
    }

    public static void removeSharedPreferencesData(Context mContext) {
        if (mContext != null) {
            SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            if (mSharedPreferences != null) {
                mSharedPreferences.edit().remove("fifty_fifty").apply();
                mSharedPreferences.edit().remove("reset").apply();
                mSharedPreferences.edit().remove("audience").apply();
                mSharedPreferences.edit().remove("skip").apply();
            }
        }
    }

    public static void saveTextSize(Context context, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.PREF_TEXT_SIZE, value);
        editor.apply();
    }

    public static String getSavedTextSize(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(Constant.PREF_TEXT_SIZE, Constant.TEXT_SIZE_MIN);
    }


    public static String getUserData(String key, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, null);
    }

    public static boolean isLogin(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public static void saveUserDetail(Context context, String userId, String name, String email, String mobile, String profile, String referCode, String type, String jwtkey, String uid) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ID, userId);
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(MOBILE, mobile);
        editor.putString(PROFILE, profile);
        editor.putString(REFER_CODE, referCode);
        editor.putString(TYPE, type);
        editor.putString(JWT_KEY, jwtkey);
        editor.putString(UID, uid);
        editor.putBoolean(LOGIN, true);
        editor.apply();
    }


    public static void clearUserSession(Context mContext) {
        if (mContext != null) {
            SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            if (mSharedPreferences != null) {
                mSharedPreferences.edit().remove(USER_ID).apply();
                mSharedPreferences.edit().remove(NAME).apply();
                mSharedPreferences.edit().remove(EMAIL).apply();
                mSharedPreferences.edit().remove(MOBILE).apply();
                mSharedPreferences.edit().remove(LOGIN).apply();
                mSharedPreferences.edit().remove(PROFILE).apply();
                mSharedPreferences.edit().remove(LANGUAGE).apply();
                mSharedPreferences.edit().remove(IS_FIRST_TIME).apply();

            }
        }
    }


    public static void setUserData(String key, String value, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
}