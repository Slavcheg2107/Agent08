package com.example.krasn.agent08.Dropbox;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.cloudrail.si.exceptions.ParseException;
import com.example.krasn.agent08.App;
import com.example.krasn.agent08.R;

/**
 * Created by krasn on 10/14/2017.
 */

public class Dropbox {
   public static com.cloudrail.si.services.Dropbox dropbox = new com.cloudrail.si.services.Dropbox(App.getContext(),
            App.getContext().getString(R.string.dropbox_key),
            App.getContext().getString(R.string.dropbox_secret),
            App.getContext().getString(R.string.dropbox_redirect), "08agent");

    public static com.cloudrail.si.services.Dropbox init(){
        String credentials;
        SharedPreferences prefs = App.getContext().getSharedPreferences(App.APP_PREFERENCES, Context.MODE_PRIVATE);
        credentials = prefs.getString(App.CREDENTIALS, null);
        dropbox.useAdvancedAuthentication();
        try {
            if(credentials!=null) {
                dropbox.loadAsString(credentials);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dropbox;
    }
}
