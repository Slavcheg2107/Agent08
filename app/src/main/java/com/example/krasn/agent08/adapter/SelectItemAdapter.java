package com.example.krasn.agent08.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.krasn.agent08.Activities.SelectClientActivity;
import com.example.krasn.agent08.R;

import java.util.ArrayList;

public class SelectItemAdapter extends ArrayAdapter<Object> {
    private Context mContext;
    private ArrayList<Object> mDataList;
    private Integer[] mImages;
    private Integer mItemId;
    private LayoutInflater inflater = null;
    Activity activity;

    public interface DataList {
        String getText();
    }

    public SelectItemAdapter(Context context, ArrayList<Object> dataList, Integer[] images, int itemId, Activity activity) {
        super(context, itemId, dataList);
        this.mContext = context;
        this.mDataList = dataList;
        this.mImages = images;
        this.mItemId = Integer.valueOf(itemId);
        this.activity = activity;
        inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View contentView, ViewGroup parent) {
        if (position >= this.mDataList.size()) {
            return new View(this.mContext);
        }
        int sel;
        View rowView = inflater.inflate(this.mItemId.intValue(), parent, false);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView1);
        if (position < this.mImages.length) {
            sel = position;
        } else {
            sel = position % this.mImages.length;
        }
        if (activity instanceof SelectClientActivity) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setImageResource(this.mImages[sel]);
        }
        ((TextView) rowView.findViewById(R.id.loadTextView)).setText(((DataList) this.mDataList.get(position)).getText());
        return rowView;
    }
}
