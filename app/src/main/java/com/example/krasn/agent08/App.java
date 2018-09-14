package com.example.krasn.agent08;

import android.app.Application;
import android.content.Context;

import com.cloudrail.si.CloudRail;

/**
 * Created by krasn on 10/14/2017.
 */

public class App extends Application {
    private static Context context;
    public static final String EXTRA_REDOWNLOAD = "redownload";
    public static final String FILE_KEY_NAME = "file_name";
    public static final String FILE_NAME = "orders";
    public static final String APP_PREFERENCES = "Prefs";
    public static final String CREDENTIALS = "Credentials";
    public static final String DATE_MODIFIED = "Last_date";
    public static Long lastDate;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        CloudRail.setAppKey("592ac2b124c5b453109b3f07");
    }
    public static Context getContext(){
        return context;
    }
}
