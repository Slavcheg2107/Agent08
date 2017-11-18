package com.example.krasn.agent08.AsyncTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;


import com.cloudrail.si.services.Dropbox;
import com.cloudrail.si.types.CloudMetaData;
import com.example.krasn.agent08.App;
import com.example.krasn.agent08.Events.NewFileEvent;
import com.example.krasn.agent08.Events.NoConnectionEvent;
import com.example.krasn.agent08.bd.DownloadMaster;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import static com.example.krasn.agent08.Dropbox.Dropbox.dropbox;

/**
 * Created by krasn on 10/14/2017.
 */

public class CheckNewFile extends AsyncTask{
    private boolean isexist;
    private boolean isNew;
    private String lastDate;
    Dropbox dropbox;
    @Override
    protected Object doInBackground(Object[] params) {

        this.dropbox = com.example.krasn.agent08.Dropbox.Dropbox.init();
        isexist = dropbox.exists("/orders/orders.zip");
        if(isexist){
          CloudMetaData data =  dropbox.getMetadata("/orders/orders.zip");
            if(data!=null) {
                Long modifiedDate = data.getModifiedAt();
                SharedPreferences preferences = App.getContext().getSharedPreferences(App.APP_PREFERENCES, Context.MODE_PRIVATE);
                lastDate = preferences.getString(App.DATE_MODIFIED, null);
                if(lastDate!=null) {
                Long lastDate1 = Long.parseLong(lastDate);
                    if (lastDate1 < modifiedDate) {
                        App.lastDate = modifiedDate;
                        isNew = true;
                    }else {
                        isNew = false;
                    }
                }else {
                    isNew = true;
                    App.lastDate = modifiedDate;
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        String credentials = this.dropbox.saveAsString();
        SharedPreferences prefs = App.getContext().getSharedPreferences(App.APP_PREFERENCES, Context.MODE_PRIVATE);
        prefs.edit().putString(App.CREDENTIALS, credentials).apply();
            EventBus.getDefault().post(new NewFileEvent(isNew));
    }
}
