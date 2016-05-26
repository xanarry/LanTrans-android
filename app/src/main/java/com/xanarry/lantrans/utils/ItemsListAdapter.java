package com.xanarry.lantrans.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xanarry.lantrans.R;
import com.xanarry.lantrans.SendActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by xanarry on 2016/5/24.
 */
public class ItemsListAdapter extends BaseAdapter {
    private ArrayList<HashMap<String, String>> list;
    private TextView fileNameText;
    private TextView fileSizeText;
    private TextView progressText;
    private TextView speedText;
    private ProgressBar progressBar;
    private ArrayList<Integer> progressRecords;
    private ArrayList<Integer> speedRecords;
    private Context context;

    public ItemsListAdapter(Context context, ArrayList<HashMap<String, String>> list, ArrayList<Integer> progressRecords, ArrayList<Integer> speedRecords) {
        this.context = context;
        this.list = list;
        this.progressRecords = progressRecords;
        this.speedRecords = speedRecords;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);// getLayoutInflater();
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.file_item, null);
            holder = new ViewHolder();

            holder.fileNameText = (TextView) convertView.findViewById(R.id.fileNameText);
            holder.fileSizeText = (TextView) convertView.findViewById(R.id.fileSizeText);
            holder.progressText = (TextView) convertView.findViewById(R.id.progressText);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            holder.speedText = (TextView) convertView.findViewById(R.id.speedText);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
            convertView.setTag(holder);
        }

        HashMap<String, String> item = list.get(position);
        holder.fileNameText.setText(item.get(SendActivity.FILENAME_MK));
        holder.fileSizeText.setText(item.get(SendActivity.FILESIZE_MK));
        holder.progressBar.setProgress(progressRecords.get(position));
        holder.progressText.setText(progressRecords.get(position) + "%");//////////////
        if (progressRecords.get(position) == 100) {
            holder.speedText.setText("已完成");
        } else {
            holder.speedText.setText(speedRecords.get(position) + "kb/s");
        }
        return convertView;
    }
}