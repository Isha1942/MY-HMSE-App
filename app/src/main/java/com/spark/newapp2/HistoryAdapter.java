package com.spark.newapp2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<CommandHistory> historyList = new ArrayList<>();

    public void setHistory(List<CommandHistory> list) {
        this.historyList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        CommandHistory item = historyList.get(position);
        holder.tvCommand.setText(item.getCommand());
        holder.tvTime.setText(android.text.format.DateFormat.format("dd/MM/yyyy HH:mm:ss", item.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCommand, tvTime;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCommand = itemView.findViewById(R.id.tv_command);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }
}
