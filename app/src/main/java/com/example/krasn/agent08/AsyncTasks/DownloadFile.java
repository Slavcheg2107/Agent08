package com.example.krasn.agent08.AsyncTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.cloudrail.si.services.Dropbox;
import com.cloudrail.si.types.CloudMetaData;
import com.example.krasn.agent08.App;
import com.example.krasn.agent08.Events.FileReadyEvent;
import com.example.krasn.agent08.Utils.UnzipUtility;
import com.example.krasn.agent08.bd.DownloadMaster;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by krasn on 10/14/2017.
 */

public class DownloadFile extends AsyncTask{
    InputStream inputStream;
    Long modifiedDate;
    @Override
    protected Object doInBackground(Object[] params) {
        Dropbox dropbox = com.example.krasn.agent08.Dropbox.Dropbox.init();
       inputStream  = dropbox.download("/orders/orders.zip");
        CloudMetaData data =  dropbox.getMetadata("/orders/orders.zip");
        if(data!=null) {
           modifiedDate = data.getModifiedAt();
        }
            File outputFile = new File(App.getContext().getFilesDir(), App.FILE_NAME);
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        int ind = 0;
        try {
            ind = writeFromInputToOutput(inputStream, new FileOutputStream(outputFile) );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        setCurrentFileName(App.getContext(), outputFile.getName());
        if(ind!=-1){
            try {
                decompressFile(App.getContext(),App.FILE_NAME);
                DownloadMaster.updateDbFromNewFile(App.getContext(), App.FILE_NAME+".txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
        for(Object v: values)
        Log.e("Progres",v.toString());
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        App.lastDate = modifiedDate;
        EventBus.getDefault().post(new FileReadyEvent());
    }

    private  int writeFromInputToOutput(InputStream source, OutputStream dest) {
        byte[] buffer = new byte[4096];
        int count = 0;
        while (true) {
            try {
                int bytesRead = source.read(buffer);
                if (bytesRead != -1) {
                    dest.write(buffer, 0, bytesRead);
                    count += bytesRead;
                } else {
                    dest.close();
                    return count;
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    dest.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return -1;
            }
        }
    }
    private  void setCurrentFileName(Context context, String value) {
        SharedPreferences.Editor edit = context.getSharedPreferences(App.APP_PREFERENCES, 0).edit();
        edit.putString(App.FILE_KEY_NAME, value);
        edit.apply();
    }
    private  void decompressFile(Context context, String fileName) throws IOException {
        new UnzipUtility().unzip(context.getFilesDir().getAbsolutePath() + File.separator + fileName, context.getFilesDir().getAbsolutePath());
    }
}
