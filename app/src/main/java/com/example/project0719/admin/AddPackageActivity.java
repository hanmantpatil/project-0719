package com.example.project0719.admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddPackageActivity extends BaseActivity {

    ImageView imageView;
    static final int REQUEST_IMAGE_GET = 1;
    Uri imageUri = null;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_package);

        imageView = findViewById(R.id.image);

        findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPackage();
            }
        });
    }

    private void uploadImage(final Map<String, Object> pack) {
        showLoader();
        StorageReference ref = storageReference.child(Constants.PATH_VENUES + "/" + UUID.randomUUID().toString());

        ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storeInToDB(pack, taskSnapshot.getStorage().toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideLoader();
                Toast.makeText(AddPackageActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storeInToDB(Map<String, Object> pack, String downloadLink) {

        pack.put("image", downloadLink);

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

    private void addPackage() {

        String packageName = ((EditText) findViewById(R.id.package_name)).getText().toString();
        String packageDescription = ((EditText) findViewById(R.id.package_description)).getText().toString();
        String price = ((EditText) findViewById(R.id.package_price)).getText().toString();

        if (TextUtils.isEmpty(packageName)) {
            Toast.makeText(AddPackageActivity.this, R.string.empty_package_name_error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(packageDescription)) {
            Toast.makeText(AddPackageActivity.this, R.string.empty_package_description_error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(price)) {
            Toast.makeText(AddPackageActivity.this, R.string.empty_package_price_error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(AddPackageActivity.this, R.string.empty_product_images_error, Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> pack = Package.get(packageName, packageDescription, price);

        uploadImage(pack);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.products_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_product) {
            getPictureFromGallery();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getPictureFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            Uri fullPhotoUri = data.getData();
            imageUri = fullPhotoUri;
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fullPhotoUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
