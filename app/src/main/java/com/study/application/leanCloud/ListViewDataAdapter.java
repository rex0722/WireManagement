package com.study.application.leanCloud;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.study.application.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.TreeMap;

public class ListViewDataAdapter extends BaseAdapter{

    private final LayoutInflater inflater;
    private final ArrayList<DisplayData> dataArrayList;

    public ListViewDataAdapter(Context c, ArrayList<DisplayData> arrayList){
        inflater = LayoutInflater.from(c);
        dataArrayList = arrayList;
    }

    @Override
    public int getCount() {
        return dataArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.listview_setting, null);

        TextView txtIndex = convertView.findViewById(R.id.txtIndex);
        TextView txtClassification = convertView.findViewById(R.id.txtClassification);
        TextView txtBorrowDate = convertView.findViewById(R.id.txtDate);
        TextView txtEstimatedReturnDate = convertView.findViewById(R.id.txtEstimatedTimeReturn);
        TextView txtReturnDate = convertView.findViewById(R.id.txtReturnDate);
        TextView txtUser = convertView.findViewById(R.id.txtUser);
        TextView txtStatus = convertView.findViewById(R.id.txtStatus);
        TextView txtSubscriber = convertView.findViewById(R.id.txtSubscriber);
        TextView txtSubscribeDate = convertView.findViewById(R.id.txtSubscribeDate);

        txtIndex.setText(dataArrayList.get(position).getIndex());
        txtClassification.setText(dataArrayList.get(position).getClassification());
        txtBorrowDate.setText(dataArrayList.get(position).getBorrowDate());
        txtEstimatedReturnDate.setText(dataArrayList.get(position).getEstimatedTimeReturn());
        txtReturnDate.setText(dataArrayList.get(position).getReturnDate());
        txtUser.setText(dataArrayList.get(position).getUser());
        txtStatus.setText(dataArrayList.get(position).getStatus());
        txtSubscriber.setText(dataArrayList.get(position).getSubscriber());
        txtSubscribeDate.setText(dataArrayList.get(position).getSubscribeDate());
        return convertView;
    }
}
