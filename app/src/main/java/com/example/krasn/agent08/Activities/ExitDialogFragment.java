package com.example.krasn.agent08.Activities;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.example.krasn.agent08.R;


public class ExitDialogFragment extends DialogFragment {
    private OnClickListener listener = new OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            if (which == -1 && ExitDialogFragment.this.mExitDialogListener != null) {
                ExitDialogFragment.this.mExitDialogListener.clickYesButtonOnDialog();
            }
        }
    };
    private ExitDialogListener mExitDialogListener;

    interface ExitDialogListener {
        void clickYesButtonOnDialog();
    }

    public void setListener(ExitDialogListener exitDialogListener) {
        this.mExitDialogListener = exitDialogListener;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Builder(getActivity()).setTitle(R.string.warning).setMessage(getArguments().getString("TEXT")).setPositiveButton(R.string.ok, this.listener).setNegativeButton(R.string.cancel, this.listener).create();
    }
}
