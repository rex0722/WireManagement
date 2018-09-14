package com.study.application.leanCloud;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.study.application.R;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    ArrayList<DisplayData> dataArrayList;

    public RecyclerViewAdapter(ArrayList<DisplayData> list){
        dataArrayList = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtIndex, txtClassification, txtBorrowDate, txtEstimatedReturnDate,
                txtReturnDate, txtUser, txtStatus, txtSubscriber, txtSubscribeDate;

        public ViewHolder(View itemView) {
            super(itemView);

            txtIndex = itemView.findViewById(R.id.txtIndex);
            txtClassification = itemView.findViewById(R.id.txtClassification);
            txtBorrowDate = itemView.findViewById(R.id.txtDate);
            txtEstimatedReturnDate = itemView.findViewById(R.id.txtEstimatedTimeReturn);
            txtReturnDate = itemView.findViewById(R.id.txtReturnDate);
            txtUser = itemView.findViewById(R.id.txtUser);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtSubscriber = itemView.findViewById(R.id.txtSubscriber);
            txtSubscribeDate = itemView.findViewById(R.id.txtSubscribeDate);
        }
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.v("TAG", "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_setting, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
        Log.i("TAG", "onBindViewHolder");
        holder.txtIndex.setText(dataArrayList.get(position).getIndex());
        holder.txtClassification.setText(dataArrayList.get(position).getClassification());
        holder.txtBorrowDate.setText(dataArrayList.get(position).getBorrowDate());
        holder.txtEstimatedReturnDate.setText(dataArrayList.get(position).getEstimatedTimeReturn());
        holder.txtReturnDate.setText(dataArrayList.get(position).getReturnDate());
        holder.txtUser.setText(dataArrayList.get(position).getUser());
        holder.txtStatus.setText(dataArrayList.get(position).getStatus());
        holder.txtSubscriber.setText(dataArrayList.get(position).getSubscriber());
        holder.txtSubscribeDate.setText(dataArrayList.get(position).getSubscribeDate());
    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }
}
