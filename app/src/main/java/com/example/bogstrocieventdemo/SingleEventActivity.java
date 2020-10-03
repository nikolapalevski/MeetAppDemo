package com.example.bogstrocieventdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SingleEventActivity extends AppCompatActivity {

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
   // final String sender = getIntent().getStringExtra("sender");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_event);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        final String location = getIntent().getStringExtra("location");
        final String time = getIntent().getStringExtra("time");
        final String date = getIntent().getStringExtra("date");
        //String[] going = getIntent().getStringArrayExtra("going");
        //final List going = getIntent().getStringArrayListExtra("going");
        final String path = getIntent().getStringExtra("path");
        final String id = getIntent().getStringExtra("id");


        final TextView textViewLocation = findViewById(R.id.textLocation);
        TextView textViewTime = findViewById(R.id.textTime);
        TextView textViewDate = findViewById(R.id.textDate);
        final Button buttonGoing = findViewById(R.id.buttonGoing);
        final Button buttonNotGoing = findViewById(R.id.buttonNotGoing);
        final Button buttonUpdate = findViewById(R.id.buttonUpdate);
        final Button buttonDelete = findViewById(R.id.buttonDelete);
        final TextView textViewGoing = findViewById(R.id.textGoing);

        final DocumentReference docRef = db.collection("events").document(id);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable final DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Log.w(TAG, "Listen failed.", e);
                    return;
                }

                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";



                //String text = (String) snapshot.get("date");
                 final List<String> going = (List<String>) snapshot.get("going");

                //printanje going lista
                String finalno = "";
                for (int j = 0;j<going.size();j++);{
                    finalno = finalno + going;
                }
                textViewGoing.setText(finalno);

                //update i delete button
                String prakjac = (String) snapshot.get("sender");
                if(Objects.equals(user.getEmail(), prakjac)){

                    buttonUpdate.setVisibility(View.VISIBLE);
                    buttonDelete.setVisibility(View.VISIBLE);


                    buttonDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            Intent intent = new Intent(SingleEventActivity.this, EventActivity.class);
                            startActivity(intent);
                            finish();
                            docRef.delete();



                        }
                    });


                    buttonUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {



                            Intent intent = new Intent(SingleEventActivity.this, UpdateSingleEvent.class);
                            intent.putExtra("location", location);
                            intent.putExtra("time",time);
                            intent.putExtra("date",date);
                            //intent.putExtra("path",path);
                            intent.putExtra("id",id);
                            startActivity(intent);
                            finish();

//                            UpdateExampleDialog exampleDialog = new UpdateExampleDialog();
//                            exampleDialog.show(getSupportFragmentManager(), "example dialog");
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






        textViewLocation.setText(location);
        textViewDate.setText(date);
        textViewTime.setText(time);





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
