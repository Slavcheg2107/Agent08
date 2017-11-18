package com.example.krasn.agent08.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.krasn.agent08.App;
import com.example.krasn.agent08.R;

public class AddAccountDialog extends Dialog {
    private Context context;
    private OnSaveListener mOnSaveListener;

    public interface OnSaveListener {
        void onSave();
    }

    public AddAccountDialog(Context context, OnSaveListener l) {
        super(context);
        this.context = context;
        this.mOnSaveListener = l;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_acc);
        setTitle("ukr.net");
        SharedPreferences preferences = App.getContext().getSharedPreferences(App.APP_PREFERENCES, Context.MODE_PRIVATE);
        final EditText login = (EditText) findViewById(R.id.editText1);
        login.setText(PreferenceManager.getInstance(this.context).getLogin());
        final EditText pass = (EditText) findViewById(R.id.editText2);
        pass.setText(PreferenceManager.getInstance(this.context).getPass());
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (login.getText().toString().isEmpty() || pass.getText().toString().isEmpty()) {
                    Toast.makeText(AddAccountDialog.this.context, "Введите все данные!", Toast.LENGTH_SHORT).show();
                    return;
                }
                PreferenceManager.getInstance(AddAccountDialog.this.context).saveAccount(login.getText().toString(), pass.getText().toString());
                if (AddAccountDialog.this.mOnSaveListener != null) {
                    AddAccountDialog.this.mOnSaveListener.onSave();
                }
                AddAccountDialog.this.cancel();
            }

        });
    }
}
