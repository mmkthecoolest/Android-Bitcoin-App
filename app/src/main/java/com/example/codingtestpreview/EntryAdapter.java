package com.example.codingtestpreview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {
    private ArrayList<ListEntry> entryList;

    public static class EntryViewHolder extends RecyclerView.ViewHolder{
        public TextView t1;
        public TextView t2;

        public EntryViewHolder(View itemView){
            super(itemView);
            t1 = itemView.findViewById(R.id.t1);
            t2 = itemView.findViewById(R.id.t2);
        }


    }

    public EntryAdapter(ArrayList<ListEntry> entryList){
        this.entryList = entryList;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_entry, parent, false);
        EntryViewHolder evh = new EntryViewHolder(view);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        ListEntry currentItem = entryList.get(position);

        holder.t1.setText(currentItem.getS1());
        holder.t2.setText(currentItem.getS2());
    }

    @Override
    public int getItemCount() {
        return entryList.size();
    }
}
