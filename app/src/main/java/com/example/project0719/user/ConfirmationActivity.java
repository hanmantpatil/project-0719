package com.example.project0719.user;

import android.app.DatePickerDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.project0719.BaseActivity;
import com.example.project0719.Constants;
import com.example.project0719.R;
import com.example.project0719.admin.AddPackageActivity;
import com.example.project0719.entities.Booking;
import com.example.project0719.entities.Package;
import com.example.project0719.entities.Venue;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class ConfirmationActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {

    Package selectedPackage;
    ArrayList<Venue> venues = new ArrayList<>();
    ArrayList<String> venuesStrings = new ArrayList<>();
    ArrayList<String> eventTypes = new ArrayList<>();

    Spinner eventsSpinner;
    Spinner venuesSpinner;
    TextView date;

    private Calendar calendar = Calendar.getInstance();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    boolean userWriteComplete = false;
    boolean adminWriteComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        venuesSpinner = findViewById(R.id.venues);
        eventsSpinner = findViewById(R.id.event_type);
        date = findViewById(R.id.date);

        selectedPackage = getIntent().getParcelableExtra("pack");

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        date.setText(String.format(getString(R.string.date), getDateString(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))));

        findViewById(R.id.book_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = ((EditText) findViewById(R.id.phone)).getText().toString();

                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(ConfirmationActivity.this, R.string.empty_phone_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                placeBooking(phone);
            }
        });

        eventTypes.add(getString(R.string.birthday));
        eventTypes.add(getString(R.string.aniversary));
        eventTypes.add(getString(R.string.send_off));
        eventTypes.add(getString(R.string.treat));

        ArrayAdapter<String> eventsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eventTypes);
        eventsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventsSpinner.setAdapter(eventsAdapter);

        ((TextView)findViewById(R.id.pack)).setText("Package: " + selectedPackage.name);

        showLoader();
        fetchVenues();
    }

    private void placeBooking(String phone) {
        Map<String, Object> booking = Booking.get(eventTypes.get(eventsSpinner.getSelectedItemPosition()), selectedPackage,
                venues.get(venuesSpinner.getSelectedItemPosition()), date.getText().toString(), phone);

        showLoader();
        userWriteComplete = false;
        adminWriteComplete = false;

        db.collection(Constants.PATH_USERS)
                .document(FirebaseAuth.getInstance().getUid())
                .collection(Constants.PATH_BOOKINGS)
                .add(booking)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        userWriteComplete = true;
                        if (adminWriteComplete) {
                            Toast.makeText(ConfirmationActivity.this, R.string.booking_success, Toast.LENGTH_SHORT).show();
                            hideLoader();
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        userWriteComplete = true;
                        if (adminWriteComplete) {
                            hideLoader();
                            Toast.makeText(ConfirmationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        db.collection(Constants.PATH_BOOKINGS)
                .add(booking)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        adminWriteComplete = true;
                        if (userWriteComplete) {
                            Toast.makeText(ConfirmationActivity.this, R.string.booking_success, Toast.LENGTH_SHORT).show();
                            hideLoader();
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        adminWriteComplete = true;
                        if (userWriteComplete) {
                            hideLoader();
                            Toast.makeText(ConfirmationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void fetchVenues() {
        db.collection(selectedPackage.id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            venues.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Venue venue = new Venue(document);
                                venues.add(venue);
                                venuesStrings.add(venue.name);
                            }
                        }
                        updateViews();
                    }
                });
    }

    private void updateViews() {
        hideLoader();
        ArrayAdapter<String> venuesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, venuesStrings);
        venuesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        venuesSpinner.setAdapter(venuesAdapter);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.YEAR, year);
        date.setText(String.format(getString(R.string.date), getDateString(year, monthOfYear + 1, dayOfMonth)));
    }

    String getDateString(int year, int month, int day) {
        return (day < 10 ? "0" + day : day) + "-" + (month < 10 ? "0" + month : month) + "-" + year;
    }
}
