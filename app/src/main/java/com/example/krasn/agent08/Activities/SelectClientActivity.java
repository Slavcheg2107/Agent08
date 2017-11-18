package com.example.krasn.agent08.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.krasn.agent08.R;
import com.example.krasn.agent08.adapter.SelectItemAdapter;
import com.example.krasn.agent08.bd.DbHelper;
import com.example.krasn.agent08.send.Order;

import java.util.ArrayList;

public class SelectClientActivity extends AppCompatActivity {
    private final String TAG = "SelectClientActivity";
    private ArrayList<Object> list = new ArrayList();
    private Integer[] mImages = new Integer[]{Integer.valueOf(R.drawable.client_1), Integer.valueOf(R.drawable.client_2), Integer.valueOf(R.drawable.client_3), Integer.valueOf(R.drawable.client_4), Integer.valueOf(R.drawable.client_5), Integer.valueOf(R.drawable.client_6)};
    OnItemClickListener onItemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View arg1, int index, long arg3) {
            try {
                Order.clientKey = ((MyDataList) SelectClientActivity.this.list.get(index)).getClientKey();
                Order.clientName = ((MyDataList) SelectClientActivity.this.list.get(index)).getText();
                SelectClientActivity.this.showSelectThingsActivity();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    class MyDataList implements SelectItemAdapter.DataList {
        private Integer mClientKey;
        private String mText = "";

        public MyDataList(String text, Integer key) {
            setText(text);
            setClientKey(key);
        }

        public void setText(String text) {
            this.mText = text;
        }

        public void setClientKey(Integer key) {
            this.mClientKey = key;
        }

        public String getText() {
            return this.mText;
        }

        public Integer getClientKey() {
            return this.mClientKey;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_select);
        initUI();
    }

    private void initUI() {
        ((ListView) findViewById(R.id.listView1)).setOnItemClickListener(this.onItemClickListener);
        ((ListView) findViewById(R.id.listView1)).setFastScrollAlwaysVisible(true);
        downloadDataToListView();
    }

    private void showSelectThingsActivity() {
        startActivity(new Intent(this, SelectThingsActivity.class));
    }

    private void downloadDataToListView() {
        DbHelper dbHelper = new DbHelper(this);
        Cursor c = dbHelper.getReadableDatabase().query(DbHelper.TABLE_CLIENTS_NAME, null, "store = ?", new String[]{String.valueOf(Order.storeNumber)}, null, null, null);
        this.list.clear();
        if (c.moveToFirst()) {
            int clientKeyColIndex = c.getColumnIndex("id");
            int clientColIndex = c.getColumnIndex("client");
            do {
                this.list.add(new MyDataList(c.getString(clientColIndex), c.getInt(clientKeyColIndex)));
            } while (c.moveToNext());
        }
        dbHelper.close();
        ((ListView) findViewById(R.id.listView1)).setAdapter(new SelectItemAdapter(this, this.list, this.mImages, R.layout.select_item, this));
    }
}
