package com.example.bogstrocieventdemo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.NoteHolder>{


    public OnItemClickListener listener;
    LongKeyPressedEventListner longKeyPressedEventListner;


    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options) {
        //this.longKeyPressedEventListner = longKeyPressedEventListner;
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteHolder holder, int position, @NonNull Note model) {
        holder.textViewLocation.setText(model.getLocation());
        holder.textViewTime.setText(model.getTime());
        holder.textViewDate.setText(model.getDate());

    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item,parent,false);

        return new NoteHolder(v);
    }

    public void deletItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }




    class NoteHolder extends RecyclerView.ViewHolder {
        TextView textViewTime;
        TextView textViewLocation;
        TextView textViewDate;


        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            textViewTime = itemView.findViewById(R.id.text_view_time);
            textViewLocation = itemView.findViewById(R.id.text_location);
            textViewDate = itemView.findViewById(R.id.text_view_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION && listener != null){

                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }

                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION && listener != null){

                        longKeyPressedEventListner.longKeyPressed(getSnapshots().getSnapshot(position),position);
                    }
                    return true;
                }
            });



//            itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    int position = getAdapterPosition();
//                    if(listener != null){
//
//                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
//                    }
//                    return true;
//                }
//            });

        }
    }


//    public interface OnItemLongClickListener{
//        void onItemLongClick(DocumentSnapshot documentSnapshot, int position);
//    }
//
//
//    public void onLongClickListener(OnItemLongClickListener onLongClickListener) {
//        this.onLongClickListener = onLongClickListener;
//    }




    public interface  OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot,int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public interface LongKeyPressedEventListner {
        void longKeyPressed(DocumentSnapshot documentSnapshot, int position);
    }
    public void setLongKeyPressedEventListner(LongKeyPressedEventListner longKeyPressedEventListner){
        this.longKeyPressedEventListner = (LongKeyPressedEventListner) longKeyPressedEventListner;
    }


    //    public  void setOnLongClickListener(OnLongClickListener listener){
//        this.onLongClickListener = onLongClickListener;
//    }


}
