package com.example.residuosapp.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.residuosapp.R;

import java.util.ArrayList;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.AlertViewHolder> {

    Context contex;
    ArrayList<Alert> list;

    public AlertAdapter(Context contex, ArrayList<Alert> list) {
        this.contex = contex;
        this.list = list;
    }

    @NonNull
    @Override
    public AlertAdapter.AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contex).inflate(R.layout.item_alert, parent, false);
        return new AlertAdapter.AlertViewHolder(v);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull AlertAdapter.AlertViewHolder holder, int position) {
        Alert s = list.get(position);
        holder.placeT.setText(s.getDistritoId());
        holder.placeT.setText(s.lugar);
        holder.dateT.setText(s.getFecha());
        holder.statusT.setText(s.getEstado());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class AlertViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView placeT, dateT, statusT;
        CardView cardView;

        public AlertViewHolder(@NonNull View itemView) {
            super(itemView);

            placeT = itemView.findViewById(R.id.alert_location);
            dateT = itemView.findViewById(R.id.alert_date);
            statusT = itemView.findViewById(R.id.alert_status);
            cardView = itemView.findViewById(R.id.cardview_alert);
            cardView.setOnCreateContextMenuListener(this);

        }

        @SuppressLint("ResourceType")
        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(this.getAdapterPosition(), 100, 0, "Ver");
        }
    }
}

