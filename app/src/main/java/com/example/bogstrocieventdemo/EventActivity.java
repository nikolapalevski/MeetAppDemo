package com.example.bogstrocieventdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class EventActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {



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
    ProgressDialog progressDialog;
    FirebaseStorage storage;
    StorageReference storageReference;
    StorageReference downloadRefUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);


        setContentView(R.layout.activity_event);

        //navigation menu
        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        setNavigationViewListener();

        NavigationView navigationView =  findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        final TextView textProfile =  headerView.findViewById(R.id.textProfile);
        final ImageView imageProfile =  headerView.findViewById(R.id.imageProfile);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        downloadRefUser = storageReference.child("images/"+ user.getEmail()+".jpg");
        //postavuvanje slika vo navigation menu
        try {
            final File localFile = File.createTempFile("images", "jpg");
            downloadRefUser.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    imageProfile.setImageBitmap(bitmap);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    imageProfile.setImageResource(R.drawable.gif);
                }
            });
        } catch (IOException e ) {
            Log.i("SingleEvent activity", String.valueOf(e));
        }
        //postavuvanje ime i prezime na user vo navigation menu
        DocumentReference docRef = db.collection("users").document(Objects.requireNonNull(user.getEmail()));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String ime = (String) document.get("name");
                        textProfile.setText(ime);
                    }
                }
            }
        });


        //end navigation menu

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
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//            @Override
//            public void onSwiped(@NonNull  RecyclerView.ViewHolder viewHolder, int direction) {
//                //adapter.deleteItem(viewHolder.getAdapterPosition());
//            }
//        }).attachToRecyclerView(recyclerView);


        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, final int position) {
                Note note = documentSnapshot.toObject(Note.class);
                final String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                String sender = note.getSender();
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
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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

//za navigation menu da raboti
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.menuProfile:
                startActivity(new Intent(EventActivity.this,ProfileActivity.class));
                finish();
                return true;
            case R.id.menuLogout:
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(EventActivity.this, MainActivity.class));
//                finish();
                AlertDialog.Builder builder1 = new AlertDialog.Builder(EventActivity.this);
                builder1.setMessage("Are you sure you want do logout?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(EventActivity.this, MainActivity.class));
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
                return true;
        }
        return false;
    }
    private void setNavigationViewListener() {
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(EventActivity.this);
        builder1.setMessage("Are you sure you want exit MeetApp?");
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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
}
