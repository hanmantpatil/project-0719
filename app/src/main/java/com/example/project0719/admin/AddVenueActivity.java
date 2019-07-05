package com.example.project0719.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.project0719.BaseActivity;
import com.example.project0719.Constants;
import com.example.project0719.R;
import com.example.project0719.entities.Venue;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class AddVenueActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_venue);

        findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVenue();
            }
        });
    }

    private void addVenue() {
        String name = ((EditText) findViewById(R.id.name)).getText().toString();
        String description = ((EditText) findViewById(R.id.description)).getText().toString();
        String phoneNo = ((EditText) findViewById(R.id.phone)).getText().toString();
        String address = ((EditText) findViewById(R.id.address)).getText().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(AddVenueActivity.this, R.string.empty_venue_name_error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(AddVenueActivity.this, R.string.empty_venue_description_error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(phoneNo)) {
            Toast.makeText(AddVenueActivity.this, R.string.empty_phone_error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(address)) {
            Toast.makeText(AddVenueActivity.this, R.string.empty_address_error, Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> pack = Venue.get(name, description, phoneNo, address);

        showLoader();

        FirebaseFirestore.getInstance().collection(Constants.PATH_VENUES)
                .add(pack)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddVenueActivity.this, R.string.venue_added, Toast.LENGTH_SHORT).show();
                        hideLoader();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideLoader();
                        Toast.makeText(AddVenueActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
