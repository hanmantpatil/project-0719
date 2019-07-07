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
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.os.Bundle;
import com.example.project0719.BaseActivity;
import com.example.project0719.Constants;
import com.example.project0719.R;
import com.example.project0719.entities.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class AddProductActivity extends BaseActivity {

    static final int REQUEST_IMAGE_GET = 1;
    private LinearLayout imagesContainer;
    private Uri image;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        imagesContainer = findViewById(R.id.images_container);

        findViewById(R.id.add_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProduct();
            }
        });
    }

    private void saveProduct() {
        String name = ((EditText) findViewById(R.id.name)).getText().toString();
        String description = ((EditText) findViewById(R.id.description)).getText().toString();
        String price = ((EditText) findViewById(R.id.price)).getText().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, getText(R.string.empty_package_name_error), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, getText(R.string.empty_product_description_error), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, getText(R.string.empty_product_price_error), Toast.LENGTH_SHORT).show();
            return;
        }

        if (image == null) {
            Toast.makeText(this, getText(R.string.empty_product_images_error), Toast.LENGTH_SHORT).show();
            return;
        }

        uploadImage(name, price, description);
    }

    private void uploadImage(final String name, final String price, final String description) {
        showLoader();
        StorageReference ref = storageReference.child(Constants.PATH_PRODUCTS + "/" + UUID.randomUUID().toString());

        ref.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storeInToDB(name, price, description, taskSnapshot.getStorage().toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideLoader();
                Toast.makeText(AddProductActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storeInToDB(String name, String price, String description, String downloadLink) {
        Map<String, Object> product = Product.get(name, price, description, downloadLink);

        FirebaseFirestore.getInstance().collection(Constants.PATH_PRODUCTS)
                .add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddProductActivity.this, R.string.product_added, Toast.LENGTH_SHORT).show();
                        hideLoader();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideLoader();
                        Toast.makeText(AddProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
            image = fullPhotoUri;
            imagesContainer.removeAllViews();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fullPhotoUri);
                View imageView = LayoutInflater.from(this).inflate(R.layout.image, imagesContainer, false);
                ((ImageView) imageView.findViewById(R.id.image)).setImageBitmap(bitmap);
                imagesContainer.addView(imageView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
