package com.example.krasn.agent08.Activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.krasn.agent08.R;
import com.example.krasn.agent08.Utils.AddAccountDialog;
import com.example.krasn.agent08.Utils.Config;
import com.example.krasn.agent08.Utils.OneThing;
import com.example.krasn.agent08.Utils.PreferenceManager;
import com.example.krasn.agent08.adapter.SelectThingsAdapter;
import com.example.krasn.agent08.bd.DbHelper;
import com.example.krasn.agent08.send.MailSender;
import com.example.krasn.agent08.send.Order;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.Permission;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class SelectThingsActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_TO_STORAGE = 100;
    private final String TAG = "SelectOrdersActivity";
    private String allPrice = "";
    private TextView allPriceTextView;
    private boolean isHaveOrder = false;
    private boolean isSendData = false;
    private ArrayList<OneThing> list = new ArrayList();
    private LoadDataRunnable loadDataRunnable;
    ExitDialogFragment.ExitDialogListener mExitDialogListener = new ExitDialogFragment.ExitDialogListener() {
        public void clickYesButtonOnDialog() {
            SelectThingsActivity.this.finish();
        }
    };
    private ListView mListView;
    AddAccountDialog.OnSaveListener mOnSaveListener = new AddAccountDialog.OnSaveListener() {
        public void onSave() {
            SelectThingsActivity.this.processData();
        }
    };
    private View mProgressView;
    SelectThingsAdapter.SelectThingsListener mSelectThingsListener = new SelectThingsAdapter.SelectThingsListener() {
        public void onDoneClick() {
            SelectThingsActivity.this.refreshCost();
        }
    };
    OnClickListener onClickListener = new OnClickListener() {
        public void onClick(View arg0) {
            Order.printLog();
            boolean is = PreferenceManager.getInstance(SelectThingsActivity.this).isHasAcc();
            if (PreferenceManager.getInstance(SelectThingsActivity.this).isHasAcc()) {
                processData();
            } else {
                new AddAccountDialog(SelectThingsActivity.this, SelectThingsActivity.this.mOnSaveListener).show();
            }
        }
    };
    OnItemClickListener onItemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View arg1, int index, long arg3) {
        }
    };

    public final class LoadDataRunnable implements Runnable {
        private boolean done = false;

        public void shutdown() {
            this.done = true;
        }

        public void run() {
            SelectThingsActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    SelectThingsActivity.this.mProgressView.setVisibility(View.VISIBLE);
                }
            });
            DbHelper dbHelper = new DbHelper(SelectThingsActivity.this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            SelectThingsActivity.this.list.clear();
            Cursor c = db.rawQuery("select things.thing as Thing, prices.price as Price, things.id as ThingKey, things.count as Count, prices.num as Num  FROM prices join things on prices.thing = things.id  WHERE prices.client = ? and things.store = ? and prices.store = ?", new String[]{String.valueOf(Order.clientKey), String.valueOf(Order.storeNumber), String.valueOf(Order.storeNumber)});
            if (c.moveToFirst() && !this.done) {
                int thingKeyColIndex = c.getColumnIndex("ThingKey");
                int priceColIndex = c.getColumnIndex("Price");
                int countColIndex = c.getColumnIndex("Count");
                int thingColIndex = c.getColumnIndex("Thing");
                int numColIndex = c.getColumnIndex("Num");
                do {
                    SelectThingsActivity.this.list.add(new OneThing(Integer.valueOf(c.getInt(thingKeyColIndex)), c.getString(thingColIndex), c.getString(countColIndex), c.getString(priceColIndex), c.getString(numColIndex)));
                    if (!c.moveToNext()) {
                        break;
                    }
                } while (!this.done);
            } else {
                SelectThingsActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        SelectThingsActivity.this.findViewById(R.id.noDataTextView).setVisibility(View.GONE);
                    }
                });
            }
            dbHelper.close();
            SelectThingsActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    SelectThingsActivity.this.mListView.setAdapter(new SelectThingsAdapter(SelectThingsActivity.this, SelectThingsActivity.this.list, SelectThingsActivity.this.mSelectThingsListener));
                    SelectThingsActivity.this.mProgressView.setVisibility(View.GONE);
                }
            });
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_select_product);
        this.loadDataRunnable = new LoadDataRunnable();
        initUI();
    }

    public void onBackPressed() {
        if (this.isHaveOrder) {
            ExitDialogFragment dialog = new ExitDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putString("TEXT", getString(R.string.exit_from_order));
            dialog.setArguments(bundle);
            dialog.setListener(this.mExitDialogListener);
            dialog.show(getSupportFragmentManager(), "exitdialog");
            return;
        }
        super.onBackPressed();
    }

    protected void onDestroy() {
        this.loadDataRunnable.shutdown();
        this.loadDataRunnable = null;
        super.onDestroy();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == 0) {
                    processData();
                    return;
                }
                return;
            default:
                return;
        }
    }

    private void initUI() {
        this.allPriceTextView = (TextView) findViewById(R.id.loadTextView);
        this.allPriceTextView.setText("");
        this.mListView = (ListView) findViewById(R.id.listView1);
        this.mListView.setFastScrollAlwaysVisible(true);
        this.mListView.setOnItemClickListener(this.onItemClickListener);
        downloadDataToListView();
        findViewById(R.id.button1).setOnClickListener(this.onClickListener);
        setTitle(Order.clientName);
        this.mProgressView = findViewById(R.id.progress);
        this.mProgressView.setVisibility(View.GONE);
//        this.mProgressView.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//            }
//        });
    }

    private void processData() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
        else
        if (this.isHaveOrder) {
                prepareData();
            } else {
                Toast.makeText(this, R.string.not_selected_item, Toast.LENGTH_SHORT).show();
            }

    }

    private void downloadDataToListView() {
        new Thread(this.loadDataRunnable).start();
    }

    private void refreshCost() {
        if (!this.isSendData) {
            new Thread(new Runnable() {
                public void run() {
                    double price = 0.0d;
                    synchronized (SelectThingsActivity.this.list) {
                        int i = 0;
                        while (i < SelectThingsActivity.this.list.size()) {
                            if (((OneThing) SelectThingsActivity.this.list.get(i)).getCount() != null && ((OneThing) SelectThingsActivity.this.list.get(i)).getCount().length() > 0) {
                                try {
                                    price += (double) (Float.parseFloat(((OneThing) SelectThingsActivity.this.list.get(i)).getCount()) * Float.parseFloat(((OneThing) SelectThingsActivity.this.list.get(i)).getPrice()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            i++;
                        }
                        final double fPrice = price;
                        SelectThingsActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                if (fPrice != 0.0d) {
                                    SelectThingsActivity.this.allPrice = String.valueOf(new DecimalFormat("0.00").format(fPrice)) + " " + SelectThingsActivity.this.getString(R.string.uah);
                                    SelectThingsActivity.this.allPriceTextView.setText(SelectThingsActivity.this.allPrice);
                                    SelectThingsActivity.this.isHaveOrder = true;
                                    return;
                                }
                                SelectThingsActivity.this.allPriceTextView.setText("");
                                SelectThingsActivity.this.isHaveOrder = false;
                            }
                        });
                    }
                }
            }).start();
        }
    }

    private String createFile() {
        String rand = String.valueOf(System.currentTimeMillis() / 1000);
        String filePath = Environment.getExternalStorageDirectory() + File.separator + getString(R.string.app_name) + File.separator + rand;
        new File(filePath).mkdirs();
        String fileName = String.valueOf(Order.storeNumber);
        int strLength = rand.length();
        File file = new File(filePath, fileName + rand.substring(strLength - 7, strLength) + ".txt");
        try {
            file.createNewFile();
            try {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                int i = 0;
                while (i < this.list.size()) {
                    if (((OneThing) this.list.get(i)).getCount() != null && ((OneThing) this.list.get(i)).getCount().length() > 0) {
                        bw.write(String.valueOf(Order.clientKey) + "," + String.valueOf(((OneThing) this.list.get(i)).getId()) + "," + ((OneThing) this.list.get(i)).getCount() + "," + String.valueOf(Order.agentKey) + "," + ((OneThing) this.list.get(i)).getNum() + "\r\n");
                    }
                    i++;
                }
                bw.close();
                return file.getAbsolutePath();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return "";
            } catch (Exception e2) {
                return "";
            }
        } catch (IOException e3) {
            e3.printStackTrace();
            return "";
        }
    }

    private void sendDataToEmail(String file) throws Exception {
        new MailSender(PreferenceManager.getInstance(this).getLogin(), PreferenceManager.getInstance(this).getPass()).sendMail("Заказ", "", file, PreferenceManager.getInstance(this).getLogin(), Config.SEND_TO);
    }

    private void prepareData() {
        this.isSendData = true;
        new Thread(new Runnable() {
            public void run() {
                SelectThingsActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        SelectThingsActivity.this.mProgressView.setVisibility(View.VISIBLE);
                        SelectThingsActivity.this.findViewById(R.id.button1).setEnabled(false);
                        SelectThingsActivity.this.mListView.setEnabled(false);
                    }
                });
                boolean isSuccess = false;
                try {
                    SelectThingsActivity.this.sendDataToEmail(SelectThingsActivity.this.createFile());
                    isSuccess = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final boolean success = isSuccess;
                SelectThingsActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (success) {
                            Toast.makeText(SelectThingsActivity.this, R.string.send_done, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SelectThingsActivity.this, R.string.send_with_error, Toast.LENGTH_LONG).show();
                        }
                        SelectThingsActivity.this.finish();
                    }
                });
            }
        }).start();
    }
}
