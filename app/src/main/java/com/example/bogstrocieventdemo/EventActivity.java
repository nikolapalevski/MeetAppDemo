package com.example.bogstrocieventdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Snapshot;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EventActivity extends AppCompatActivity  {



    TextView welcomeTextView;
    FloatingActionButton plus;
    ListView listView;
    static ArrayList<String> notes = new ArrayList<>();
    static ArrayAdapter arrayAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("events");

    private NoteAdapter adapter;
    private Object FirebaseUser;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        welcomeTextView = findViewById(R.id.welcomeTextView);
        plus = findViewById(R.id.floatingActionButton);

        setUpRecyclerView();




        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });


    }

    private void setUpRecyclerView(){
        Query query = notebookRef.orderBy("dateCreated",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();
        adapter = new NoteAdapter(options);

        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);



        // on swipe list


//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
//                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//
//                return false;
//            }
//
//
//
//            @Override
//            public void onSwiped(@NonNull  RecyclerView.ViewHolder viewHolder, int direction) {
//
//
//                //adapter.deletItem(viewHolder.getAdapterPosition());
//
//            }
//        }).attachToRecyclerView(recyclerView);




        //on long touch



//        adapter.setLongKeyPressedEventListner(new NoteAdapter.LongKeyPressedEventListner(){
//
//
//            @Override
//            public void longKeyPressed(DocumentSnapshot documentSnapshot, int position) {
//                Note note = documentSnapshot.toObject(Note.class);
//                String id = documentSnapshot.getId();
//                String path = documentSnapshot.getReference().getPath();
//                String sender = note.getSender();
//                //String going = note.getGoing();
//                //String[] goingh = note.getGoing();
//                //List<String> going = new Arrays(note.getGoing());
//
//                String time = note.getTime();
//                String location = note.getLocation();
//                String date = note.getDate();
//
//                Intent intent = new Intent(EventActivity.this, ExampleDialog.class);
//                intent.putExtra("location", location);
//                intent.putExtra("time",time);
//                intent.putExtra("date",date);
//                intent.putExtra("path",path);
//                intent.putExtra("id",id);
//
//                startActivity(intent);
//
//            }
//        });



        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Note note = documentSnapshot.toObject(Note.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                String sender = note.getSender();
                //String going = note.getGoing();
                //String[] goingh = note.getGoing();
                //List<String> going = new Arrays(note.getGoing());

                String time = note.getTime();
                String location = note.getLocation();
                String date = note.getDate();

                Intent intent = new Intent(EventActivity.this, SingleEventActivity.class);
                intent.putExtra("location", location);
                intent.putExtra("time",time);
                intent.putExtra("date",date);
                intent.putExtra("path",path);
                intent.putExtra("sender", sender);
                intent.putExtra("id",id);

                startActivity(intent);

            }
        });




    }

    //logout


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        if(!user.getEmail().toString().equals("nikola@palevski.com")){
            menu.findItem(R.id.logout).setVisible(false);
        }



        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(EventActivity.this, MainActivity.class));

                finish();
                return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    public void openDialog(){
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }


}
