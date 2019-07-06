package com.example.project0719.admin;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.project0719.BaseActivity;
import com.example.project0719.Constants;
import com.example.project0719.Preferences;
import com.example.project0719.R;
import com.example.project0719.entities.Package;
import com.example.project0719.entities.Venue;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class VenueActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue);

        final Venue venue = getIntent().getParcelableExtra("venue");

        ((TextView) findViewById(R.id.name)).setText(venue.name);
        ((TextView) findViewById(R.id.description)).setText(venue.description);
        ((TextView) findViewById(R.id.address)).setText(venue.address);
        ((TextView) findViewById(R.id.phone)).setText(venue.phoneNo);

        Button delete = findViewById(R.id.delete_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePackage(venue);
            }
        });

        Button addToCart = findViewById(R.id.add_to_cart);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart(venue);
            }
        });

        if (Preferences.INSTANCE.get(getApplicationContext(), Preferences.IS_ADMIN)) {
            addToCart.setVisibility(View.GONE);
            delete.setVisibility(View.VISIBLE);
        } else {
            addToCart.setVisibility(View.VISIBLE);
            delete.setVisibility(View.GONE);
        }
    }

    private void addToCart(Venue venue) {
        
    }

    private void deletePackage(Venue venue) {
        showLoader();
        FirebaseFirestore.getInstance()
                .collection(Constants.PATH_VENUES)
                .document(venue.id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(VenueActivity.this, R.string.venue_deleted, Toast.LENGTH_SHORT).show();
                        hideLoader();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideLoader();
                        Toast.makeText(VenueActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
