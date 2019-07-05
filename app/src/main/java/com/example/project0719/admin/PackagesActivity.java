package com.example.project0719.admin;

import android.content.Intent;
import androidx.annotation.NonNull;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project0719.BaseActivity;
import com.example.project0719.Constants;
import com.example.project0719.R;
import com.example.project0719.entities.Package;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PackagesActivity extends BaseActivity implements ListAdapter.Callback {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ListAdapter adapter = new ListAdapter(this);
    ArrayList<Package> packages = new ArrayList<>();

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
                            packages.clear();
                            adapter.items.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Package pa = new Package(document);
                                packages.add(pa);
                                adapter.items.add(pa.name);
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onItemSelected(int position) {
        Intent intent = new Intent(this, PackageActivity.class);
        intent.putExtra("package", packages.get(position));
        startActivity(intent);
    }

    @Override
    public void addMore() {
        startActivity(new Intent(this, AddPackageActivity.class));
    }
}
