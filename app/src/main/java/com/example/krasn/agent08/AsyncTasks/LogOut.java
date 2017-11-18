package com.example.krasn.agent08.AsyncTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.cloudrail.si.exceptions.ParseException;
import com.cloudrail.si.services.Dropbox;
import com.example.krasn.agent08.App;
import com.example.krasn.agent08.Events.LoginEvent;

import org.greenrobot.eventbus.EventBus;

import static com.example.krasn.agent08.App.CREDENTIALS;
import static com.example.krasn.agent08.Dropbox.Dropbox.dropbox;

/**
 * Created by krasn on 10/14/2017.
 */

public class LogOut extends AsyncTask {
    private SharedPreferences preferences = App.getContext().getSharedPreferences(App.APP_PREFERENCES, Context.MODE_PRIVATE);
    private SharedPreferences.Editor editor = preferences.edit();
    @Override
    protected Object doInBackground(Object[] params) {
        Dropbox dropbox = com.example.krasn.agent08.Dropbox.Dropbox.init();
        dropbox.logout();
        editor.putString(App.CREDENTIALS, null).apply();
        return null;
    }


    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        EventBus.getDefault().post(new LoginEvent(false));
    }
}
