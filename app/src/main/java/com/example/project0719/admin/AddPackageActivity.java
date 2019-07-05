package com.example.project0719.admin;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.project0719.BaseActivity;
import com.example.project0719.Constants;
import com.example.project0719.R;
import com.example.project0719.entities.Package;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddPackageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_package);

        findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPackage();
            }
        });
    }

    private void addPackage() {

        String packageName = ((EditText) findViewById(R.id.package_name)).getText().toString();
        String packageDescription = ((EditText) findViewById(R.id.package_description)).getText().toString();
        if (TextUtils.isEmpty(packageName)) {
            Toast.makeText(AddPackageActivity.this, R.string.empty_package_name_error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(packageDescription)) {
            Toast.makeText(AddPackageActivity.this, R.string.empty_package_description_error, Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> pack = Package.get(packageName, packageDescription);

        showLoader();

        FirebaseFirestore.getInstance().collection(Constants.PATH_PACKAGES)
                .add(pack)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddPackageActivity.this, R.string.package_added, Toast.LENGTH_SHORT).show();
                        hideLoader();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideLoader();
                        Toast.makeText(AddPackageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
