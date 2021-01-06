package com.transportervendor.Adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.transportervendor.Bean.Leads;

import java.util.ArrayList;

public class MarketLeadAdapter extends RecyclerView.Adapter {

    public MarketLeadAdapter(Context context, ArrayList<Leads> leads) {
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void setLid(ArrayList<String> lid) {
    }
}

