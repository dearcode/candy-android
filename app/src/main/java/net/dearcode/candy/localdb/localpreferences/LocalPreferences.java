package net.dearcode.candy.localdb.localpreferences;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

/**
 * Created by lujinfeifly on 16/9/27.
 */

public class LocalPreferences {

    public static String PREFERFILE = "CANDY";

    public Application application = null;

    public LocalPreferences(Application app) {
        application = app;
    }

    public void save(String prefsName, String data) {
        SharedPreferences settings = application.getSharedPreferences(PREFERFILE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(prefsName, data);
        editor.commit();
    }

    public String get(String prefsName) {
        SharedPreferences settings = application.getSharedPreferences(PREFERFILE, Activity.MODE_PRIVATE);
        return settings.getString(prefsName, null);
    }

    public void saveBool(String prefsName, boolean data) {
        SharedPreferences settings = application.getSharedPreferences(PREFERFILE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(prefsName, data);
        editor.commit();
    }

    public boolean getBool(String prefsName) {
        SharedPreferences settings = application.getSharedPreferences(PREFERFILE, Activity.MODE_PRIVATE);
        return settings.getBoolean(prefsName, false);
    }
}
