package com.example.project0719.admin;

import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project0719.Constants;
import com.example.project0719.R;
import com.example.project0719.entities.Package;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminPackages extends AppCompatActivity implements PackagesAdapter.Callback {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    PackagesAdapter adapter = new PackagesAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_packages);
        RecyclerView list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchPackages();
    }

    private void fetchPackages() {
        db.collection(Constants.PATH_PACKAGES)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            adapter.packages.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                adapter.packages.add(new Package(document));
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onPackageSelected(Package pack) {
        Intent intent = new Intent(this, AdminPackageActivity.class);
        intent.putExtra("package",pack);
        startActivity(intent);
    }

    @Override
    public void addMore() {
        startActivity(new Intent(this, AddPackageActivity.class));
    }
}
