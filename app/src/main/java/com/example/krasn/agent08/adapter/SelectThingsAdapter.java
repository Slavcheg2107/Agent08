package com.example.krasn.agent08.adapter;


        import android.content.Context;
        import android.text.Editable;
        import android.text.InputType;
        import android.text.TextWatcher;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.view.ViewGroup;

        import android.view.inputmethod.EditorInfo;
        import android.widget.ArrayAdapter;
        import android.widget.EditText;
        import android.widget.LinearLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.krasn.agent08.R;
        import com.example.krasn.agent08.Utils.OneThing;

        import java.util.ArrayList;

public class SelectThingsAdapter extends ArrayAdapter<OneThing> {
    private final String TAG = "SelectThingsAdapter";
    private Context mContext;
    private ArrayList<OneThing> mDataList;
    private SelectThingsListener mSelectThingsListener;
    LayoutInflater inflater = null;
    public interface SelectThingsListener {
        void onDoneClick();
    }

    public SelectThingsAdapter(Context context, ArrayList<OneThing> dataList, SelectThingsListener selectThingsListener) {
        super(context, R.layout.select_order_item, dataList);
        this.mContext = context;
        this.mDataList = dataList;
        this.mSelectThingsListener = selectThingsListener;
        inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View contentView, ViewGroup parent) {
        if (position >= this.mDataList.size()) {
            return new View(this.mContext);
        }
        View rowView = inflater.inflate(R.layout.select_order_item, parent, false);
        final EditText editText = (EditText) rowView.findViewById(R.id.count);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setText(((OneThing) this.mDataList.get(position)).getCount());
        editText.setTransformationMethod(null);
        editText.setTag(position);

        final int pos = position;
        editText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable text) {
                if (text.toString().equals("0")) {
                    text.clear();
                    return;
                }
                ((OneThing) SelectThingsAdapter.this.mDataList.get(pos)).setCount(text.toString());
                SelectThingsAdapter.this.mSelectThingsListener.onDoneClick();
            }

            public void beforeTextChanged(CharSequence seq, int arg1, int arg2, int arg3) {
            }

            public void onTextChanged(CharSequence seq, int arg1, int arg2, int arg3) {
            }
        });
        try {
            ((TextView) rowView.findViewById(R.id.loadTextView)).setText(this.mDataList.get(position).getName());
            try {
                String text = "";
                if (((OneThing) this.mDataList.get(position)).getPrice().length() > 0 && !((OneThing) this.mDataList.get(position)).getPrice().equals("0")) {
                    text = text + this.mContext.getString(R.string.price) + " " + ((OneThing) this.mDataList.get(position)).getPrice() + " " + this.mContext.getString(R.string.uah_dot) + " ";
                }
                if (((OneThing) this.mDataList.get(position)).getAllCount().length() > 0 && !((OneThing) this.mDataList.get(position)).getAllCount().equals("0")) {
                    text = text + this.mContext.getString(R.string.count) + " " + ((OneThing) this.mDataList.get(position)).getAllCount() + " " + this.mContext.getString(R.string.count_unit);
                }
                if (!((OneThing) this.mDataList.get(position)).getNum().equals("0")) {
                    text = text + " (" + ((OneThing) this.mDataList.get(position)).getNum() + ")";
                }
                ((TextView) rowView.findViewById(R.id.TextView01)).setText(text);
                if (text.equals("")) {
                    editText.setVisibility(View.INVISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            LinearLayout itemLayout = (LinearLayout) rowView.findViewById(R.id.itemLayout);
            itemLayout.setTag(position);
            itemLayout.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Toast toast = Toast.makeText(SelectThingsAdapter.this.mContext, ((OneThing) SelectThingsAdapter.this.mDataList.get((Integer) v.getTag())).getName(), Toast.LENGTH_SHORT);
                    toast.setGravity(16, 0, 0);
                    toast.show();
                }
            });
            return rowView;
        } catch (Exception e2) {
            Log.e("Exep", e2.getMessage());
            e2.printStackTrace();
            return new View(this.mContext);
        }
    }

    public int getCount() {
        return this.mDataList.size();
    }

    public long getItemId(int position) {
        return (long) position;
    }
}
