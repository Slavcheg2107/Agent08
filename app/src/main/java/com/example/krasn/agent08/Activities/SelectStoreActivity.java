package com.example.krasn.agent08.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.example.krasn.agent08.R;
import com.example.krasn.agent08.Utils.AddAccountDialog;
import com.example.krasn.agent08.adapter.SelectItemAdapter;
import com.example.krasn.agent08.bd.DbHelper;
import com.example.krasn.agent08.send.Order;

import java.util.ArrayList;

public class SelectStoreActivity extends AppCompatActivity {
    private final String TAG = "SelectStoreActivity";
    private ArrayList<Object> list = new ArrayList();
    ExitDialogFragment.ExitDialogListener mExitDialogListener = new ExitDialogFragment.ExitDialogListener() {
        public void clickYesButtonOnDialog() {
            SelectStoreActivity.this.finish();
        }
    };
    private Integer[] mImages = new Integer[]{Integer.valueOf(R.drawable.box1), Integer.valueOf(R.drawable.box2)};
    OnItemClickListener onItemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View arg1, int index, long arg3) {
            try {
                Order.storeNumber = ((MyDataList) SelectStoreActivity.this.list.get(index)).getStoreNumber();
                SelectStoreActivity.this.showSelectAgentActivity();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private class MyDataList implements SelectItemAdapter.DataList {
        private Integer mStoreNumber;
        private String mText = "";

        MyDataList(String text, Integer key) {
            setText(text);
            setStoreNumber(key);
        }

        public void setText(String text) {
            this.mText = text;
        }

        void setStoreNumber(Integer num) {
            this.mStoreNumber = num;
        }

        public String getText() {
            return this.mText;
        }

        Integer getStoreNumber() {
            return this.mStoreNumber;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_select);
        initUI();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_email) {
            new AddAccountDialog(this, null).show();
            return true;
        } else if (id != R.id.action_redownload) {
            return super.onOptionsItemSelected(item);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.EXTRA_REDOWNLOAD, true);
            startActivity(intent);
            finish();
            return true;
        }
    }

    public void onBackPressed() {
        ExitDialogFragment dialog = new ExitDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("TEXT", getString(R.string.realy_exit));
        dialog.setArguments(bundle);
        dialog.setListener(this.mExitDialogListener);
        dialog.show(getSupportFragmentManager(), "exitdialog");
    }

    private void initUI() {
        ((ListView) findViewById(R.id.listView1)).setOnItemClickListener(this.onItemClickListener);
        try {
            downloadDataToListView();
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка отображения информации, обновите файл данных", Toast.LENGTH_LONG).show();
        }
    }

    private void showSelectAgentActivity() {
        startActivity(new Intent(this, SelectAgentActivity.class));
    }

    private void downloadDataToListView() {
        DbHelper dbHelper = new DbHelper(this);
        Cursor c = dbHelper.getReadableDatabase().query(DbHelper.TABLE_STORES_NAME, null, null, null, null, null, null);
        this.list.clear();
        int count = c.getCount();
        boolean move = c.moveToFirst();
        if (c.moveToFirst()) {
            int storeKeyColIndex = c.getColumnIndex("id");
            int storeColIndex = c.getColumnIndex("store");
            do {
                this.list.add(new MyDataList(c.getString(storeColIndex), Integer.valueOf(c.getInt(storeKeyColIndex))));
            } while (c.moveToNext());
        }
        dbHelper.close();
        c.close();
        ((ListView) findViewById(R.id.listView1)).setAdapter(new SelectItemAdapter(this, this.list, this.mImages, R.layout.select_store_item, this));
    }
}
