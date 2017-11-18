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

public class  SelectAgentActivity extends AppCompatActivity {
    private final String TAG = "SelectAgentActivity";
    private ArrayList<Object> list = new ArrayList();
    private Integer[] mImages = new Integer[]{Integer.valueOf(R.drawable.agent_1), Integer.valueOf(R.drawable.agent_2), Integer.valueOf(R.drawable.agent_3)};
    OnItemClickListener onItemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View arg1, int index, long arg3) {
            try {
                Order.agentKey = ((MyDataList) SelectAgentActivity.this.list.get(index)).getAgentKey();
                SelectAgentActivity.this.showSelectClientActivty();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private class MyDataList implements SelectItemAdapter.DataList {
        private Integer mAgentKey;
        private String mText = "";

        MyDataList(String text, Integer key) {
            setText(text);
            setAgentKey(key);
        }

        public void setText(String text) {
            this.mText = text;
        }

        void setAgentKey(Integer key) {
            this.mAgentKey = key;
        }

        public String getText() {
            return this.mText;
        }

        Integer getAgentKey() {
            return this.mAgentKey;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_select);
        initUI();
    }

    private void initUI() {
        ((ListView) findViewById(R.id.listView1)).setOnItemClickListener(this.onItemClickListener);
        downloadDataToListView();
    }

    private void showSelectClientActivty() {
        startActivity(new Intent(this, SelectClientActivity.class));
    }

    private void downloadDataToListView() {
        DbHelper dbHelper = new DbHelper(this);
        Cursor c = dbHelper.getReadableDatabase().query(DbHelper.TABLE_AGENTS_NAME, null, "store = ?", new String[]{String.valueOf(Order.storeNumber)}, null, null, null);
        this.list.clear();
        if (c.moveToFirst()) {
            int keyColIndex = c.getColumnIndex("id");
            int agentColIndex = c.getColumnIndex("agent");
            do {
                this.list.add(new MyDataList(c.getString(agentColIndex), Integer.valueOf(c.getInt(keyColIndex))));
            } while (c.moveToNext());
        }
        dbHelper.close();
        ((ListView) findViewById(R.id.listView1)).setAdapter(new SelectItemAdapter(this, this.list, this.mImages, R.layout.select_item, this));
    }
}
