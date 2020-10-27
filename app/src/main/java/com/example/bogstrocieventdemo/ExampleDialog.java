package com.example.bogstrocieventdemo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;



public class ExampleDialog extends AppCompatDialogFragment {


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    TimePickerDialog picker;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.example_dialog, null);
        final Calendar myCalendar = Calendar.getInstance();

        final EditText datum = (EditText) dialogView.findViewById(R.id.datum);
        final EditText vreme = (EditText) dialogView.findViewById(R.id.vreme);

// set date
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                datum.setText(sdf.format(myCalendar.getTime()));

            }

        };

        datum.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

// set time

        vreme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {

                                if(sHour<10 && sMinute<10){
                                    vreme.setText("0" + sHour + ":" + "0" + sMinute);
                                }
                                if(sHour>=10 && sMinute<10){
                                    vreme.setText(sHour + ":" + "0" + sMinute);
                                }
                                if(sHour<10 && sMinute>=10){
                                    vreme.setText("0" + sHour + ":" + sMinute);
                                }
                                if(sHour>=10 && sMinute>=10){
                                    vreme.setText(sHour + ":" + sMinute);
                                }

                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });



        builder.setView(dialogView)
                .setPositiveButton("Add Event", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                })
                //.setMessage("Create new event")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ExampleDialog.this.getDialog().cancel();
                    }
                });
        return  builder.create();
    }

//override na positive button za da bidat site polinja zadolzitelni za popoluvanje
    @Override
    public void onResume() {
        super.onResume();


        final AlertDialog d = (AlertDialog)getDialog();
        if(d != null)
        {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Boolean wantToCloseDialog = false;
                    final EditText editText = (EditText) d.findViewById(R.id.inputLocation);
                    final EditText datum = (EditText) d.findViewById(R.id.datum);
                    final EditText vreme = (EditText) d.findViewById(R.id.vreme);
                    Map<String, Object> data = new HashMap<>();
//
                        //za lokacija
                        String lokacija = editText.getText().toString();

                        //za chas
                        String fullTime = vreme.getText().toString();

                        //za datum
                        final Editable parseDatum = datum.getText();
                        final String finalenDatum = String.valueOf(parseDatum);

                        //za sender
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String email = user.getEmail();

                        //timestamp
                        long tsLong = System.currentTimeMillis()/1000;
                        //String ts = Long.toString(tsLong);
                    if(!lokacija.equals("") && !fullTime.equals("") && !finalenDatum.equals("")) {
                        wantToCloseDialog = true;
                    }
                    if(wantToCloseDialog) {


                        data.put("location", lokacija);
                        data.put("time", fullTime);
                        data.put("date", finalenDatum);
                        data.put("sender", email);
                        data.put("dateCreated", tsLong);
                        data.put("going", Arrays.asList(email));

                        db.collection("events")
                                .add(data)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        //Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Log.w(TAG, "Error adding document", e);
                                    }
                                });
                        d.dismiss();
                    }else
                        Toast.makeText(getActivity(), "Must fill all fields!", Toast.LENGTH_SHORT).show();
                        //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                }
            });
        }
    }
}
