package com.example.project0719.user;

import androidx.annotation.NonNull;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project0719.BaseActivity;
import com.example.project0719.Constants;
import com.example.project0719.Preferences;
import com.example.project0719.R;
import com.example.project0719.entities.Booking;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class BookingsActivity extends BaseActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    BookingsAdapter adapter = new BookingsAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);

        RecyclerView list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        if (Preferences.INSTANCE.get(getApplicationContext(), Preferences.IS_ADMIN)) {
            fetchOrders();
        } else {
            fetchUserOrders();
        }
    }

    private void fetchOrders() {
        showLoader();
        db.collection(Constants.PATH_BOOKINGS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        hideLoader();
                        if (task.isSuccessful()) {

                            adapter.bookings.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Booking booking = new Booking(document);
                                adapter.bookings.add(booking);
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void fetchUserOrders() {
        showLoader();
        db.collection(Constants.PATH_USERS)
                .document(FirebaseAuth.getInstance().getUid())
                .collection(Constants.PATH_BOOKINGS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        hideLoader();
                        if (task.isSuccessful()) {

                            adapter.bookings.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Booking booking = new Booking(document);
                                adapter.bookings.add(booking);
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
