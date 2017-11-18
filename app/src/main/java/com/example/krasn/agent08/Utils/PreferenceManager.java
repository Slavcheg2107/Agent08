package com.example.krasn.agent08.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceManager {
    private static PreferenceManager instance;
    private SharedPreferences sPref;

    public static PreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceManager(context);
        }
        return instance;
    }

    private PreferenceManager(Context context) {
        this.sPref = context.getSharedPreferences("AgentPref", 0);
    }

    public void saveAccount(String login, String pass) {
        Editor editor = this.sPref.edit();
        editor.putString("login", login);
        editor.putString("pass", pass);
        editor.apply();
    }

    public String getLogin() {
        return this.sPref.getString("login", "");
    }

    public String getPass() {
        return this.sPref.getString("pass", "");
    }

    public boolean isHasAcc() {
        return (getLogin().isEmpty() || getPass().isEmpty()) ? false : true;
    }
}
