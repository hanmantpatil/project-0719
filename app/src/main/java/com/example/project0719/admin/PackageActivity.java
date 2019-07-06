package com.example.project0719.admin;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import android.os.Bundle;
import com.example.project0719.BaseActivity;
import com.example.project0719.Constants;
import com.example.project0719.Preferences;
import com.example.project0719.R;
import com.example.project0719.entities.Cart;
import com.example.project0719.entities.Package;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class PackageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_package);

        final Package pack = getIntent().getParcelableExtra("package");

        ((TextView) findViewById(R.id.name)).setText(pack.name);
        ((TextView) findViewById(R.id.description)).setText(pack.description);

        Button delete = findViewById(R.id.delete_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePackage(pack);
            }
        });

        Button addToCart = findViewById(R.id.add_to_cart);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart(pack);
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

    private void addToCart(Package pack) {

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
