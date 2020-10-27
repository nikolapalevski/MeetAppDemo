package com.example.bogstrocieventdemo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.NoteHolder>{


    public OnItemClickListener listener;
    LongKeyPressedEventListner longKeyPressedEventListner;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final FirebaseFirestore db = FirebaseFirestore.getInstance();



    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options) {
        //this.longKeyPressedEventListner = longKeyPressedEventListner;
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final NoteHolder holder, int position, @NonNull Note model) {
        final int[] flag = {0};
        String date = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());


        holder.textViewLocation.setText(model.getLocation());
        holder.textViewTime.setText(model.getTime());
        if(date.equals(model.getDate())){
            holder.textViewRealDate.setText("Today");
        }else{
            holder.textViewRealDate.setText(model.getDate());
        }

        //display event creator
        if(user.getEmail().toString().equals(model.getSender())){
            holder.textViewDate.setText("Created by: " + "You");
        }else{
            final DocumentReference docRef = db.collection("users").document(model.getSender());
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {

                    if (snapshot != null && snapshot.exists()) {
                        String ime = (String) snapshot.get("name");
                        holder.textViewDate.setText("Created by: " +ime);
                    }
                }
            });
        }

        //prezemanje na slikata koj go kreiral eventot

        if(holder.imageViewCreated.getDrawable() == null) {

            if (flag[0] == 0) {

                try {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference();
                    final StorageReference downloadRefUser = storageReference.child("images/" + model.getSender() + ".jpg");
                    final File localFile = File.createTempFile("images", "jpg");
                    downloadRefUser.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            holder.imageViewCreated.setBackgroundColor(Color.parseColor("#000000"));
                            holder.imageViewCreated.setImageBitmap(bitmap);
                            localFile.deleteOnExit();


                        }
                    }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull FileDownloadTask.TaskSnapshot snapshot) {

                            //holder.imageViewCreated.setBackgroundColor(Color.parseColor("#000000"));
                            holder.imageViewCreated.setImageResource(R.drawable.gif);
                            flag[0] = 1;


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }
                    });
                } catch (IOException e) {
                }
            }
        }







    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item,parent,false);

        return new NoteHolder(v);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }




    class NoteHolder extends RecyclerView.ViewHolder {
        TextView textViewTime;
        TextView textViewLocation;
        TextView textViewDate;
        TextView textViewRealDate;
        ImageView imageViewCreated;
        final FirebaseFirestore db = FirebaseFirestore.getInstance();


        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            textViewTime = itemView.findViewById(R.id.text_view_time);
            textViewLocation = itemView.findViewById(R.id.text_location);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            textViewRealDate = itemView.findViewById(R.id.text_view_realDate);
            imageViewCreated = itemView.findViewById(R.id.imageViewCreated);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION && listener != null){

                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }

                }
            });



//            itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    int position = getAdapterPosition();
//
//                    if(position != RecyclerView.NO_POSITION && listener != null){
//
//                        longKeyPressedEventListner.longKeyPressed(getSnapshots().getSnapshot(position),position);
//                    }
//                    return true;
//                }
//            });



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
