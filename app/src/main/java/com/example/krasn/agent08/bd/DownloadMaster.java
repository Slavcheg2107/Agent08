package com.example.krasn.agent08.bd;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.krasn.agent08.Utils.Config;
import com.facebook.stetho.common.Utf8Charset;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedMap;

public class DownloadMaster {
    private static final String FILE_NAME = "/orders/";
    private static final String TAG = "DownloadMaster";

    public static boolean updateDbFromNewFile(Context context, String fileName) throws IOException {
        return updateDbFromFile(context, fileName);
    }

//    public static void removeAllFiles(Context context) {
//        removeFiles(context);
//    }

//    private static void checkFile() throws Exception {
//        CheckNewFile checkNewFile = new CheckNewFile();
//        checkNewFile.execute();
//    }

//    private static String getCurrentFileName(Context context) {
//        return context.getSharedPreferences(App.APP_PREFERENCES, 0).getString(App.FILE_KEY_NAME, "");
//    }
//
//    private static void setCurrentFileName(Context context, String value) {
//        Editor edit = context.getSharedPreferences(App.APP_PREFERENCES, 0).edit();
//        edit.putString(App.FILE_KEY_NAME, value);
//        edit.apply();
//    }

    private static boolean updateDbFromFile(Context context, String fileName) throws IOException {
        File file = new File(context.getFilesDir(), fileName);
        if (!file.exists()) {
            return false;
        }
        FileInputStream fis = new FileInputStream(file);
        boolean valid = fis.getFD().valid();
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName(Utf8Charset.NAME));
//        Charset.forName(Utf8Charset.NAME)
        String encoding = isr.getEncoding();
        boolean supported = isr.markSupported();
        BufferedReader br = new BufferedReader(isr);
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        removeAllData(db);
        String table = "";
        ArrayList<String[]> data = new ArrayList();
        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            } else if (line.startsWith("*")) {
                try {
                    execSql(db, table, data);
                    table = line.substring(1).trim();
                    data.clear();
                } catch (Exception e) {
                    br.close();
                    throw new IOException(e);
                }
            } else if (table != null) {
                data.add(line.split("~"));
                if (data.size() > Config.MAX_ARRAY_DATA_SIZE) {
                    try {
                        execSql(db, table, data);
                        data.clear();
                    } catch (Exception e2) {
                        br.close();
                        throw new IOException(e2);
                    }
                }
                continue;
            } else {
                continue;
            }
        }
        if (data.size() > 0) {
            try {
                execSql(db, table, data);
            } catch (Exception e22) {
                br.close();
                throw new IOException(e22);
            }
        }
        db.close();
        dbHelper.close();
        br.close();
        return true;
    }

    private static void execSql(SQLiteDatabase db, String table, ArrayList<String[]> data) throws IOException {
        if (data != null && data.size() != 0) {
            try {
                if (table.equals(DbHelper.TABLE_AGENTS_NAME)) {
                    updateAgentsTable(db, data);
                } else if (table.equals(DbHelper.TABLE_PRICES_NAME)) {
                    updatePricesTable(db, data);
                } else if (table.equals(DbHelper.TABLE_CLIENTS_NAME)) {
                    updateClientsTable(db, data);
                } else if (table.equals(DbHelper.TABLE_THINGS_NAME)) {
                    updateThingsTable(db, data);
                } else if (table.equals(DbHelper.TABLE_STORES_NAME)) {
                    updateStoresTable(db, data);
                }
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }

    private static void removeAllData(SQLiteDatabase db) {
        db.delete(DbHelper.TABLE_STORES_NAME, null, null);
        db.delete(DbHelper.TABLE_THINGS_NAME, null, null);
        db.delete(DbHelper.TABLE_AGENTS_NAME, null, null);
        db.delete(DbHelper.TABLE_CLIENTS_NAME, null, null);
        db.delete(DbHelper.TABLE_PRICES_NAME, null, null);
    }

    private static void updateStoresTable(SQLiteDatabase db, ArrayList<String[]> data) {
        db.beginTransaction();
        SQLiteStatement statement = db.compileStatement("INSERT INTO stores(id, store) VALUES (?, ?)");
        Iterator it = data.iterator();
        while (it.hasNext()) {
            String[] row = (String[]) it.next();
            try {
                statement.bindLong(1, (long) Integer.parseInt(row[0]));
                statement.bindString(2, row[1]);
                statement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private static void updateThingsTable(SQLiteDatabase db, ArrayList<String[]> data) {
        db.beginTransaction();
        SQLiteStatement statement = db.compileStatement("INSERT INTO things(store, id, thing, count) VALUES (?, ?, ?, ?)");
        Iterator it = data.iterator();
        while (it.hasNext()) {
            String[] row = (String[]) it.next();
            try {
                statement.bindLong(1, (long) Integer.parseInt(row[0]));
                statement.bindLong(2, (long) Integer.parseInt(row[1]));
                statement.bindString(3, row[2]);
                statement.bindLong(4, (long) Integer.parseInt(row[3]));
                statement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private static void updateAgentsTable(SQLiteDatabase db, ArrayList<String[]> data) {
        db.beginTransaction();
        SQLiteStatement statement = db.compileStatement("INSERT INTO agents(store, id, agent) VALUES (?, ?, ?)");
        Iterator it = data.iterator();
        while (it.hasNext()) {
            String[] row = (String[]) it.next();
            try {
                statement.bindLong(1, (long) Integer.parseInt(row[0]));
                statement.bindLong(2, (long) Integer.parseInt(row[1]));
                statement.bindString(3, row[2]);
                statement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private static void updateClientsTable(SQLiteDatabase db, ArrayList<String[]> data) {
        db.beginTransaction();
        SQLiteStatement statement = db.compileStatement("INSERT INTO clients(id, store, client) VALUES (?, ?, ?)");
        Iterator it = data.iterator();
        while (it.hasNext()) {
            String[] row = (String[]) it.next();
            try {
                statement.bindLong(1, (long) Integer.parseInt(row[0]));
                statement.bindLong(2, (long) Integer.parseInt(row[1]));
                statement.bindString(3, row[2]);
                statement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private static void updatePricesTable(SQLiteDatabase db, ArrayList<String[]> data) {
        db.beginTransaction();
        SQLiteStatement statement = db.compileStatement("INSERT INTO prices(store, client, thing, price, num) VALUES (?, ?, ?, ?, ?)");
        Iterator it = data.iterator();
        while (it.hasNext()) {
            String[] row = (String[]) it.next();
            try {
                statement.bindLong(1, (long) Integer.parseInt(row[0]));
                statement.bindLong(2, (long) Integer.parseInt(row[1]));
                statement.bindLong(3, (long) Integer.parseInt(row[2]));
                try {
                    if (Float.parseFloat(row[3]) == 0.0f) {
                        statement.bindString(4, "0");
                    } else {
                        statement.bindString(4, row[3]);
                    }
                } catch (Exception e) {
                    statement.bindString(4, "0");
                }
                statement.bindString(5, row[4]);
                statement.execute();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private static void removeFiles(Context context) {
        for (File file : context.getFilesDir().listFiles()) {
            file.delete();
        }
    }
}
