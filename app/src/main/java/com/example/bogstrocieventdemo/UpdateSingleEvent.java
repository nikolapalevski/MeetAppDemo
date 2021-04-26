package com.example.bogstrocieventdemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpdateSingleEvent extends AppCompatActivity {

    TimePickerDialog picker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_single_event);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String location = getIntent().getStringExtra("location");
        final String time = getIntent().getStringExtra("time");
        final String date = getIntent().getStringExtra("date");
        final String id = getIntent().getStringExtra("id");

        final EditText editTextLocation = findViewById(R.id.editViewLocation);
        final EditText editTextTime = findViewById(R.id.editViewTime);
        final EditText editTextDate = findViewById(R.id.editViewDate);
        final Button buttonUpdate = findViewById(R.id.buttonUpdate);

        editTextLocation.setText(location);
        editTextTime.setText(time);
        editTextDate.setText(date);

        //set new date
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener dateUpdated = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                editTextDate.setText(sdf.format(myCalendar.getTime()));
            }

        };

        editTextDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(UpdateSingleEvent.this, dateUpdated, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //set new time

        editTextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(UpdateSingleEvent.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {

                                if(sHour<10 && sMinute<10){
                                    editTextTime.setText("0" + sHour + ":" + "0" + sMinute);
                                }
                                if(sHour>=10 && sMinute<10){
                                    editTextTime.setText(sHour + ":" + "0" + sMinute);
                                }
                                if(sHour<10 && sMinute>=10){
                                    editTextTime.setText("0" + sHour + ":" + sMinute);
                                }
                                if(sHour>=10 && sMinute>=10){
                                    editTextTime.setText(sHour + ":" + sMinute);
                                }

                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });


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

                buttonUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                       String lokacija = editTextLocation.getText().toString();

                        docRef.update("location",  lokacija);
                        docRef.update("date", editTextDate.getText().toString());
                        docRef.update("time", editTextTime.getText().toString());

                        finish();

                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
