package com.gzc.smsrelay.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gzc.smsrelay.R;
import com.gzc.smsrelay.mail.MessageInfo;

import java.util.ArrayList;
import java.util.List;

public class ShowSmsAdapter extends RecyclerView.Adapter<ShowSmsAdapter.ShowSmsViewHolder> {

    private List<MessageInfo> list = new ArrayList<>();

    public void initData(List<MessageInfo> messageInfoList) {
        list.addAll(messageInfoList);
    }

    public void addData(MessageInfo info) {
        list.add(info);
    }


    public void clearData() {
        list.clear();
    }

    @NonNull
    @Override
    public ShowSmsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View inflate = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.show_sms_item, parent, false);
        return new ShowSmsViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowSmsViewHolder showSmsViewHolder, int position) {
        showSmsViewHolder.bind(position, list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ShowSmsViewHolder extends RecyclerView.ViewHolder {
        private TextView idx;
        private TextView source;
        private TextView date;
        private TextView sender;
        private TextView content;

        ShowSmsViewHolder(@NonNull View itemView) {
            super(itemView);
            idx = itemView.findViewById(R.id.idx);
            source = itemView.findViewById(R.id.source);
            date = itemView.findViewById(R.id.date);
            sender = itemView.findViewById(R.id.sender);
            content = itemView.findViewById(R.id.content);
        }

        @SuppressLint("SetTextI18n")
        void bind(int position, MessageInfo info) {
            idx.setText(String.valueOf(position + 1) + ". ");
            source.setText(info.getSource());
            date.setText(info.getDate());
            sender.setText(info.getSenderName());
            content.setText(info.getContent());
        }

    }
}
