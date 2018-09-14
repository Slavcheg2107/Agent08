package com.example.krasn.agent08.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudrail.si.CloudRail;
import com.example.krasn.agent08.App;
import com.example.krasn.agent08.AsyncTasks.CheckNewFile;
import com.example.krasn.agent08.AsyncTasks.DownloadFile;
import com.example.krasn.agent08.AsyncTasks.LogOut;
import com.example.krasn.agent08.AsyncTasks.LoginDropbox;
import com.example.krasn.agent08.Events.CredentialNullEvent;
import com.example.krasn.agent08.Events.FileReadyEvent;
import com.example.krasn.agent08.Events.LoginEvent;
import com.example.krasn.agent08.Events.NewFileEvent;
import com.example.krasn.agent08.R;
import com.example.krasn.agent08.bd.DbHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_REDOWNLOAD = "redownload";
    private static final String BROWSABLE = "android.intent.category.BROWSABLE";
    ProgressBar pb;
    TextView loadText;
    boolean isReDownload;
    SharedPreferences sp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getApplicationContext().getSharedPreferences(App.APP_PREFERENCES, MODE_PRIVATE);
        pb = findViewById(R.id.progressBar1);
        loadText = findViewById(R.id.loadTextView);
        pb.setVisibility(View.VISIBLE);
        ConnectivityManager cm = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
            startSelectStoreActivity();
        } else {
            isReDownload = getIntent().getBooleanExtra(EXTRA_REDOWNLOAD, false);
            if (isReDownload) {
                downloadFile();
            }
            else
            checkFile();
        }
    }


    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void startSelectStoreActivity() {
        Intent i = new Intent(this, SelectStoreActivity.class);
// set the new task and clear flags
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    public void login() {
        LoginDropbox loginDropbox = new LoginDropbox();
        loginDropbox.execute();
    }

    public void downloadFile() {
        DownloadFile downloadFile = new DownloadFile();
        downloadFile.execute();
        loadText.setText(R.string.file_loading);
    }

    public void logout() {
        LogOut logOut = new LogOut();
        logOut.execute();
    }

    public void checkFile() {
        CheckNewFile checkNewFile = new CheckNewFile();
        checkNewFile.execute();
        loadText.setText(R.string.checking_for_update);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getCategories().contains(BROWSABLE)) {
            CloudRail.setAuthenticationResponse(intent);
        }
        super.onNewIntent(intent);
    }

    @Subscribe
    public void onNullCredentials(CredentialNullEvent event) {
    }

    @Subscribe
    public void onLoginEvent(LoginEvent event) {
        if (event.isLogedIn()) {
            checkFile();

        } else Toast.makeText(App.getContext(), R.string.cannot_login, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onNewFileEvent(NewFileEvent event) {
        if (event.isFileNew()) {
            if (!isReDownload) {
                Toast.makeText(this, R.string.update_available, Toast.LENGTH_LONG).show();
                DbHelper dbHelper = new DbHelper(this);
                Cursor c = dbHelper.getReadableDatabase().query(DbHelper.TABLE_STORES_NAME, null, null, null, null, null, null);
                int count = c.getCount();
                if (count != 0) {
                    startSelectStoreActivity();
                } else {
                    downloadFile();
                }
            } else {
                downloadFile();
            }
        } else {
            Toast.makeText(this, R.string.no_updates, Toast.LENGTH_LONG).show();
            startSelectStoreActivity();
        }
    }

    @Subscribe
    public void onFileReadyEvent(FileReadyEvent event) {
       sp.edit().putString(App.DATE_MODIFIED, App.lastDate.toString()).apply();
        pb.setVisibility(View.GONE);
        Toast.makeText(this, R.string.update_complete, Toast.LENGTH_SHORT).show();
        startSelectStoreActivity();
    }
}
