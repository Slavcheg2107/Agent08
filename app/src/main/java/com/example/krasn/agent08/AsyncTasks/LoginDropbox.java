package com.example.krasn.agent08.AsyncTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.cloudrail.si.exceptions.ParseException;
import com.cloudrail.si.services.Dropbox;
import com.example.krasn.agent08.App;
import com.example.krasn.agent08.Events.LoginEvent;
import com.example.krasn.agent08.Events.NoConnectionEvent;

import org.greenrobot.eventbus.EventBus;

import static com.example.krasn.agent08.Dropbox.Dropbox.dropbox;

/**
 * Created by krasn on 10/14/2017.
 */

public class LoginDropbox extends AsyncTask {
    private String credentials;
    private boolean login;
    @Override
    protected Object doInBackground(Object[] params) {

            Dropbox dropbox = com.example.krasn.agent08.Dropbox.Dropbox.init();
            dropbox.login();
            login = true;

        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        login =false;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        credentials = dropbox.saveAsString();
        SharedPreferences prefs = App.getContext().getSharedPreferences(App.APP_PREFERENCES, Context.MODE_PRIVATE);
        prefs.edit().putString(App.CREDENTIALS, credentials).apply();
        EventBus.getDefault().post(new LoginEvent(login));
        Log.e("LoginDropbox","PostExecute");
    }
}
