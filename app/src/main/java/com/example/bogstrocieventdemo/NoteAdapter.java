package com.example.bogstrocieventdemo;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.NoteHolder> {


    public OnItemClickListener listener;
    LongKeyPressedEventListner longKeyPressedEventListner;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final FirebaseFirestore db = FirebaseFirestore.getInstance();


    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options) {
        //this.longKeyPressedEventListner = longKeyPressedEventListner;
        super(options);
    }

    private final int[] flag = {0};

    @Override
    protected void onBindViewHolder(@NonNull final NoteHolder holder, final int position, @NonNull Note model) {

        String date = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());


        holder.textViewLocation.setText(model.getLocation());
        holder.textViewTime.setText(model.getTime());
        if (date.equals(model.getDate())) {
            holder.textViewRealDate.setText("Today");
        } else {
            holder.textViewRealDate.setText(model.getDate());
        }

        //display event creator
        if (user.getEmail().toString().equals(model.getSender())) {
            holder.textViewDate.setText("Created by: " + "You");
        } else {
            final DocumentReference docRef = db.collection("users").document(model.getSender());
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {

                    if (snapshot != null && snapshot.exists()) {
                        String ime = (String) snapshot.get("name");
                        holder.textViewDate.setText("Created by: " + ime);
                    }
                }
            });
        }

        //prezemanje na slikata koj go kreiral eventot

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        final StorageReference downloadRefUser = storageReference.child("images/" + model.getSender() + ".jpg");
        downloadRefUser.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(holder.imageViewCreated.getContext())
                        .load(uri)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)//ALL or NONE as your requirement
                        .into(holder.imageViewCreated);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
//                Glide.with(context)
//                        .load(R.drawable.ic_image_error)
//                        .fitCenter()
//                        .into(myViewHolder.spImg);
            }
        });

    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);

        return new NoteHolder(v);
    }

    public void deleteItem(int position) {
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

                    if (position != RecyclerView.NO_POSITION && listener != null) {

                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }

                }
            });


        }
    }


    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface LongKeyPressedEventListner {
        void longKeyPressed(DocumentSnapshot documentSnapshot, int position);
    }

    public void setLongKeyPressedEventListner(LongKeyPressedEventListner longKeyPressedEventListner) {
        this.longKeyPressedEventListner = (LongKeyPressedEventListner) longKeyPressedEventListner;
    }


}
