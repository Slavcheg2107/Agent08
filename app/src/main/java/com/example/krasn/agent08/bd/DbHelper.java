package com.example.krasn.agent08.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    public static final String TABLE_AGENTS_NAME = "agents";
    public static final String TABLE_CLIENTS_NAME = "clients";
    public static final String TABLE_PRICES_NAME = "prices";
    public static final String TABLE_STORES_NAME = "stores";
    public static final String TABLE_THINGS_NAME = "things";

    public DbHelper(Context context) {
        super(context, "db", null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        createStoresTable(db);
        createThingsTable(db);
        createAgentsTable(db);
        createClientsTable(db);
        createPricesTable(db);
    }

    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
    }

    private void createStoresTable(SQLiteDatabase db) {
        db.execSQL("create table stores (id integer,store text);");
    }

    private void createThingsTable(SQLiteDatabase db) {
        db.execSQL("create table things (store integer,id integer,thing text,count integer);");
    }

    private void createAgentsTable(SQLiteDatabase db) {
        db.execSQL("create table agents (store integer,id integer,agent text);");
    }

    private void createClientsTable(SQLiteDatabase db) {
        db.execSQL("create table clients (id integer,store integer,client text);");
    }

    private void createPricesTable(SQLiteDatabase db) {
        db.execSQL("create table prices (id integer primary key autoincrement,store integer,client integer,thing integer,price text,num text);");
    }
}
