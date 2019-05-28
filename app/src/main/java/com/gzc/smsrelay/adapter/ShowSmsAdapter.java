package com.gzc.smsrelay.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gzc.smsrelay.R;

import java.util.ArrayList;
import java.util.List;

public class ShowSmsAdapter extends RecyclerView.Adapter<ShowSmsAdapter.ShowSmsViewHolder> {

    private List<String> list = new ArrayList<>();

    public void addData(String info) {
        list.add(info);
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
        showSmsViewHolder.bind((position + 1) + ". " + list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ShowSmsViewHolder extends RecyclerView.ViewHolder {
        private TextView infoText;

        ShowSmsViewHolder(@NonNull View itemView) {
            super(itemView);
            infoText = itemView.findViewById(R.id.info);
        }

        void bind(String info) {
            infoText.setText(info);
        }

    }
}
