package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class FormPage extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_form, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View backgroundimage = view.findViewById(R.id.image);
        Drawable background = backgroundimage.getBackground();
        background.setAlpha(90);

        start();
    }

    public void start() {

        //SETUP VARIABLES FOR CONNECTING TO FORM FIELDS
        EditText name = (EditText) getView().findViewById(R.id.name);
        EditText mobile = (EditText) getView().findViewById(R.id.mobile);
        EditText address = (EditText) getView().findViewById(R.id.address);
        EditText date = (EditText) getView().findViewById(R.id.date);
        EditText time = (EditText) getView().findViewById(R.id.time);
        Button bookNow = (Button) getView().findViewById(R.id.bookNow);

        //FIREBASE INITIALIZATION
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        String[] dropservice = new String[] {"Exterior Foam Car Wash",
                "Complete Car Wash", "Complete Car Wash with Rubbing and Polishing"};

        ArrayAdapter adapter =
                new ArrayAdapter<>(
                        getActivity(),
                        R.layout.spinner_item,
                        dropservice);

        AutoCompleteTextView dropService = getView().findViewById(R.id.autoComplete);
        dropService.setAdapter(adapter);

        final Calendar cldr = Calendar.getInstance();
        //SETUP DATE PICKER

        date.setInputType(InputType.TYPE_NULL);
        date.setOnClickListener(new View.OnClickListener() {
            final int day = cldr.get(Calendar.DAY_OF_MONTH);
            final int month = cldr.get(Calendar.MONTH);
            final int year = cldr.get(Calendar.YEAR);

            @Override
            public void onClick(View v) {
                DatePickerDialog picker = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                    String string = dayOfMonth + "/" + month + "/" + year;
                    date.setText(string);
                }, year, month, day);
                cldr.add(cldr.DAY_OF_MONTH,1);
                picker.getDatePicker().setMinDate(cldr.getTimeInMillis());
                picker.show();
            }
        });

        //SETUP TIME PICKER
        time.setInputType(InputType.TYPE_NULL);
        time.setOnClickListener(new View.OnClickListener() {
            final int hour = cldr.get(Calendar.HOUR_OF_DAY);
            final int minute = cldr.get(Calendar.MINUTE);

            @Override
            public void onClick(View v) {
                TimePickerDialog picker = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
                    String string = hourOfDay + ":" + minute;
                    time.setText(string);
                }, hour, minute, false);
                picker.show();
            }
        });

        //BOOK NOW BUTTON
        bookNow.setOnClickListener(v -> {
            //EXTRACT STRING VALUES
            String databaseName = name.getText().toString();
            String databaseMobile = mobile.getText().toString();
            String databaseAddress = address.getText().toString();
            String databaseDate = date.getText().toString();
            String databaseTime = time.getText().toString();
            String databaseService = dropService.getEditableText().toString();

            //CHECK FOR EMPTY FIELDS
            if (databaseName.isEmpty() || databaseMobile.isEmpty() || databaseAddress.isEmpty() ||
                    databaseDate.isEmpty() || databaseTime.isEmpty() || databaseService.isEmpty()) {
                Toast.makeText(getContext(), "Please Fill All The Details", Toast.LENGTH_SHORT).show();
            } else {
                Booking booking = new Booking(databaseName, databaseMobile, databaseAddress,
                        databaseDate, databaseTime, databaseService);

                //UPLOADING DATA TO DATABASE
                database.child("booking").push().setValue(booking, (databaseError, databaseReference) -> {
                    //CHECKING FOR ERROR IN UPLOADING
                    if (databaseError == null) {
                        Toast.makeText(getContext(), "BOOKING DONE SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                        name.setText("");
                        mobile.setText("");
                        address.setText("");
                        date.setText("");
                        time.setText("");
                    } else
                        Toast.makeText(getContext(), "ERROR: Please Try Again", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    //CLASS Definition for uploading the Data in database in proper structure
    public class Booking {

        public String name;
        public String mobile;
        public String address;
        public String date;
        public String time;
        public String service;

        public Booking() {
        }

        public Booking(String name, String mobile, String address, String date, String time, String service) {
            this.name = name;
            this.mobile = mobile;
            this.address = address;
            this.date = date;
            this.time = time;
            this.service = service;
        }
    }
}
