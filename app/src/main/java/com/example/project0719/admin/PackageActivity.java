package com.example.project0719.admin;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import android.os.Bundle;
import com.example.project0719.BaseActivity;
import com.example.project0719.Constants;
import com.example.project0719.R;
import com.example.project0719.entities.Package;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class PackageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_package);

        final Package pack = getIntent().getParcelableExtra("package");

        ((TextView) findViewById(R.id.name)).setText(pack.name);
        ((TextView) findViewById(R.id.description)).setText(pack.description);

        findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePackage(pack);
            }
        });
    }

    private void deletePackage(Package pack) {
        showLoader();
        FirebaseFirestore.getInstance()
                .collection(Constants.PATH_PACKAGES)
                .document(pack.id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PackageActivity.this, R.string.package_deleted, Toast.LENGTH_SHORT).show();
                        hideLoader();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideLoader();
                        Toast.makeText(PackageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
