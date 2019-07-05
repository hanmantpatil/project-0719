package com.example.project0719.admin;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project0719.BaseActivity;
import com.example.project0719.Constants;
import com.example.project0719.R;
import com.example.project0719.entities.Venue;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class VenuesActivity extends BaseActivity implements ListAdapter.Callback {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ListAdapter adapter;
    ArrayList<Venue> venues = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ListAdapter(this, getApplicationContext());

        setContentView(R.layout.activity_venues);
        RecyclerView list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchVenues();
    }

    private void fetchVenues() {
        db.collection(Constants.PATH_VENUES)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            venues.clear();
                            adapter.items.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Venue venue = new Venue(document);
                                venues.add(venue);
                                adapter.items.add(venue.name);
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onItemSelected(int position) {
        Intent intent = new Intent(this, VenueActivity.class);
        intent.putExtra("venue", venues.get(position));
        startActivity(intent);
    }

    @Override
    public void addMore() {
        startActivity(new Intent(this, AddVenueActivity.class));
    }
}
