package com.example.project0719.admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.project0719.BaseActivity;
import com.example.project0719.Constants;
import com.example.project0719.R;
import com.example.project0719.entities.Venue;
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

public class AddVenueActivity extends BaseActivity {

    static final int REQUEST_IMAGE_GET = 1;
    private ImageView imageView;
    private Uri image;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_venue);
        imageView = findViewById(R.id.image);

        findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVenue();
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
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fullPhotoUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addVenue() {
        String name = ((EditText) findViewById(R.id.name)).getText().toString();
        String description = ((EditText) findViewById(R.id.description)).getText().toString();
        String phoneNo = ((EditText) findViewById(R.id.phone)).getText().toString();
        String address = ((EditText) findViewById(R.id.address)).getText().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(AddVenueActivity.this, R.string.empty_venue_name_error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(AddVenueActivity.this, R.string.empty_venue_description_error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(phoneNo)) {
            Toast.makeText(AddVenueActivity.this, R.string.empty_phone_error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(address)) {
            Toast.makeText(AddVenueActivity.this, R.string.empty_address_error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (image == null) {
            Toast.makeText(AddVenueActivity.this, R.string.empty_product_images_error, Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> pack = Venue.get(name, description, phoneNo, address);

        uploadImage(pack);
    }

    private void uploadImage(final Map<String, Object> venue) {
        showLoader();
        StorageReference ref = storageReference.child(Constants.PATH_VENUES + "/" + UUID.randomUUID().toString());

        ref.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storeInToDB(venue, taskSnapshot.getStorage().toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideLoader();
                Toast.makeText(AddVenueActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storeInToDB(Map<String, Object> venue, String image) {
        venue.put("image", image);
        FirebaseFirestore.getInstance().collection(Constants.PATH_VENUES)
                .add(venue)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddVenueActivity.this, R.string.venue_added, Toast.LENGTH_SHORT).show();
                        hideLoader();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideLoader();
                        Toast.makeText(AddVenueActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
