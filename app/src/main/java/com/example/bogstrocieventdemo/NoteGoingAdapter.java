package com.example.bogstrocieventdemo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteGoingAdapter extends FirestoreRecyclerAdapter<NoteGoing, NoteGoingAdapter.NoteGoingHolder> {

    public NoteGoingAdapter(@NonNull FirestoreRecyclerOptions<NoteGoing> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteGoingHolder holder, int position, @NonNull NoteGoing model) {
        holder.textViewGoing.setText(model.getName());
    }

    @NonNull
    @Override
    public NoteGoingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item_going,parent,false);

        return new NoteGoingHolder(v);
    }

    class NoteGoingHolder extends RecyclerView.ViewHolder{
        TextView textViewGoing;

        public NoteGoingHolder(@NonNull View itemView) {
            super(itemView);
            textViewGoing = itemView.findViewById(R.id.textViewGoingActivity);
        }
    }
}
