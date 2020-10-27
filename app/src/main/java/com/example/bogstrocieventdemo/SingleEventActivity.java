package com.example.bogstrocieventdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SingleEventActivity extends AppCompatActivity {

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db;
    String id;
    Button buttonDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_event);

        db = FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        final String location = getIntent().getStringExtra("location");
        final String time = getIntent().getStringExtra("time");
        final String date = getIntent().getStringExtra("date");
        final String sender = getIntent().getStringExtra("sender");
        final String path = getIntent().getStringExtra("path");
        id = getIntent().getStringExtra("id");


        final TextView textViewLocation = findViewById(R.id.textLocation);
        TextView textViewTime = findViewById(R.id.textTime);
        TextView textViewDate = findViewById(R.id.textDate);
        final TextView textViewSender = findViewById(R.id.textSender);
        final Button buttonGoing = findViewById(R.id.buttonGoing);
        final Button buttonNotGoing = findViewById(R.id.buttonNotGoing);
        final Button buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonDelete = findViewById(R.id.buttonDelete);
        final TextView textViewGoing = findViewById(R.id.textGoing);
        final TextView textViewPlusGoing = findViewById(R.id.textViewPlusGoing);
        final ImageView imageViewCentral = findViewById(R.id.imageViewCentral);
        final ImageView imageViewLeft = findViewById(R.id.imageViewleft);
        final ImageView imageViewRight = findViewById(R.id.imageViewRight);
        final TextView buttonShowPeople = findViewById(R.id.buttonShowPeople);

        final DocumentReference docRef = db.collection("events").document(id);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable final DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {

                final List<String> going= (List<String>) snapshot.get("going");

                assert going != null;
                final String[] elements = new String[going.size()];
                String goingZaPrintanje = "";
                for(int i = 0; i<going.size();i++){
                    elements[i] = going.get(i);
                    goingZaPrintanje = goingZaPrintanje+ going.get(i)+ ",";
                }


                // prezemanje na iminjata na userite sto imaat going
                final String[] goinzi = new String[elements.length];
                for(int i = 0; i<elements.length; i++){

                    DocumentReference docRef = db.collection("users").document(elements[i]);
                    final int finalI = i;
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String ime = (String) document.get("name");
                                    goinzi[finalI] = (String) document.get("name");
                                }
                            }
                        }
                    });



                }
                //if there is not even one going user
                textViewGoing.setText(goingZaPrintanje);
                if(going.size() == 0){
                    textViewGoing.setText("None");
                }

                //displaying users in imageView that are going
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReference();
                StorageReference downloadRefUser = null;
                    if(going.size()>0){
                        imageViewCentral.setVisibility(View.VISIBLE);
                        imageViewCentral.setBackgroundColor(Color.parseColor("#000000"));
                        downloadRefUser = storageReference.child("images/"+ elements[0]+".jpg");
                        try {
                            final File localFile = File.createTempFile("images", "jpg");
                            downloadRefUser.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                    imageViewCentral.setImageBitmap(bitmap);


                                }
                            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull FileDownloadTask.TaskSnapshot snapshot) {
                                    imageViewCentral.setBackgroundColor(Color.parseColor("#000000"));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {

                                }
                            });
                        } catch (IOException exception ) {}
                    }else{
                        imageViewCentral.setVisibility(View.INVISIBLE);
                    }
                    if(going.size()>1){
                        imageViewRight.setVisibility(View.VISIBLE);
                        imageViewRight.setBackgroundColor(Color.parseColor("#000000"));
                        downloadRefUser = storageReference.child("images/"+ elements[1]+".jpg");
                        try {
                            final File localFile = File.createTempFile("images", "jpg");
                            downloadRefUser.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                    imageViewRight.setImageBitmap(bitmap);


                                }
                            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull FileDownloadTask.TaskSnapshot snapshot) {
                                    imageViewRight.setBackgroundColor(Color.parseColor("#000000"));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {

                                }
                            });
                        } catch (IOException exception ) {}
                    }else{
                        imageViewRight.setVisibility(View.INVISIBLE);
                    }
                    if(going.size()>2){
                        imageViewLeft.setVisibility(View.VISIBLE);
                        imageViewLeft.setBackgroundColor(Color.parseColor("#000000"));
                        downloadRefUser = storageReference.child("images/"+ elements[2]+".jpg");
                        try {
                            final File localFile = File.createTempFile("images", "jpg");
                            downloadRefUser.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                    imageViewLeft.setImageBitmap(bitmap);


                                }
                            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull FileDownloadTask.TaskSnapshot snapshot) {
                                    imageViewLeft.setBackgroundColor(Color.parseColor("#000000"));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {

                                }
                            });
                        } catch (IOException exception ) {}
                    }
                    else{
                        imageViewLeft.setVisibility(View.INVISIBLE);
                    }

                //displaying if there are more than 3 going users
                if(going.size()>3){
                    textViewPlusGoing.setVisibility(View.VISIBLE);
                    int plusGoing = going.size()-3;
                    textViewPlusGoing.setText("+"+plusGoing);
                }else{
                    textViewPlusGoing.setVisibility(View.INVISIBLE);
                }


                //view people going
                buttonShowPeople.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SingleEventActivity.this, GoingPeopleActivity.class);
                        intent.putExtra("going",elements.length);
                        intent.putExtra("id", id);
                        intent.putExtra("site",elements);
                        intent.putExtra("finalni",goinzi);
                        startActivity(intent);
                    }
                });

                //update i delete button
                String prakjac = (String) snapshot.get("sender");
                if(Objects.equals(user.getEmail(), prakjac)){

                    buttonUpdate.setVisibility(View.VISIBLE);
                    buttonDelete.setVisibility(View.VISIBLE);


                    buttonDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            final DocumentReference docRef1 = db.collection("events").document(id);
//                            docRef1.delete();
//                            Intent intent = new Intent(SingleEventActivity.this, EventActivity.class);
//                            startActivity(intent);
//                            finish();

                            android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(SingleEventActivity.this);
                            builder1.setMessage("Are you sure you want to delete this event?");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "Delete",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int ids) {
                                            final DocumentReference docRef1 = db.collection("events").document(id);
                                            docRef1.delete();
                                            Intent intent = new Intent(SingleEventActivity.this, EventActivity.class);
                                            startActivity(intent);
                                            finish();
                                            dialog.cancel();
                                        }
                                    });

                            builder1.setNegativeButton(
                                    "No",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();


                        }
                    });

                    buttonUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(SingleEventActivity.this, UpdateSingleEvent.class);
                            intent.putExtra("location", location);
                            intent.putExtra("time",time);
                            intent.putExtra("date",date);
                            intent.putExtra("id",id);
                            startActivity(intent);
                            finish();
                        }
                    });
                }

                if(going.contains(user.getEmail())){
                    buttonNotGoing.setVisibility(View.VISIBLE);
                    buttonGoing.setVisibility(View.INVISIBLE);
                }else{
                    buttonNotGoing.setVisibility(View.INVISIBLE);
                    buttonGoing.setVisibility(View.VISIBLE);
                }


                buttonGoing.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(!going.contains(user.getEmail()))
                        {
                            going.add(user.getEmail());
                            docRef.update("going", going);
                        }

                    }
                });
                buttonNotGoing.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(going.contains(user.getEmail())){
                            going.remove(user.getEmail());
                            docRef.update("going", going);
                        }
                    }
                });

            }
        });

        //prezemanje podetalni informacii za userot
        final DocumentReference docRefUser = db.collection("users").document(sender);
        docRefUser.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                //probno // userot da go printa so celo ime
                if (snapshot != null && snapshot.exists()) {

                    String ime = (String) snapshot.get("name");
                    textViewSender.setText("Created by : " + ime);

                    if(user.getEmail().equals(sender)){
                        textViewSender.setText("Created by : " + "You");
                    }
                }
            }
        });

        String dateCurr = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());

        textViewLocation.setText(location);
        textViewTime.setText(time);
        if(dateCurr.equals(date)){
            textViewDate.setText("Today");
        }else{
            textViewDate.setText(date);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SingleEventActivity.this,EventActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update_delete_menu, menu);
        final String sender = getIntent().getStringExtra("sender");
        if(!user.getEmail().toString().equals(sender)){
            menu.findItem(R.id.updateMenu).setVisible(false);
            menu.findItem(R.id.deleteMenu).setVisible(false);
        }
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        final String location = getIntent().getStringExtra("location");
        final String time = getIntent().getStringExtra("time");
        final String date = getIntent().getStringExtra("date");
        final String path = getIntent().getStringExtra("path");
        final String id = getIntent().getStringExtra("id");
        final String sender = getIntent().getStringExtra("sender");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("events").document(id);

        switch (item.getItemId()){

            case R.id.updateMenu:
                Intent intentUpdate = new Intent(SingleEventActivity.this, UpdateSingleEvent.class);
                intentUpdate.putExtra("location", location);
                intentUpdate.putExtra("time",time);
                intentUpdate.putExtra("date",date);
                intentUpdate.putExtra("id",id);
                startActivity(intentUpdate);
                finish();
                return true;
            case R.id.deleteMenu:

                db.collection("events").document(id)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Intent intent = new Intent(SingleEventActivity.this, EventActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(SingleEventActivity.this, "Successfully deleted event!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SingleEventActivity.this, "Error with deleting event!", Toast.LENGTH_SHORT).show();
                            }
                        });


                return true;
        }
        return false;
    }

}
