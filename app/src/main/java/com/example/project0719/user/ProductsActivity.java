package com.example.project0719.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.example.project0719.BaseActivity;
import com.example.project0719.Constants;
import com.example.project0719.Preferences;
import com.example.project0719.R;
import com.example.project0719.admin.AddPackageActivity;
import com.example.project0719.admin.AddProductActivity;
import com.example.project0719.entities.Cart;
import com.example.project0719.entities.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductsActivity extends BaseActivity {

    LinearLayout list;
    private ArrayList<Product> products = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        list = findViewById(R.id.list);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchProducts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Preferences.INSTANCE.get(getApplicationContext(), Preferences.IS_ADMIN)) {
            getMenuInflater().inflate(R.menu.products_activity_menu, menu);
            return true;
        } else {
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_product) {
            startActivity(new Intent(this, AddProductActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchProducts() {
        FirebaseFirestore.getInstance().collection(Constants.PATH_PRODUCTS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            list.removeAllViews();
                            products.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = new Product(document);
                                products.add(product);
                            }

                            populateViews();
                        }
                    }
                });
    }

    private void populateViews() {
        for (final Product product : products) {
            View view = LayoutInflater.from(this).inflate(R.layout.product_list_item, list, false);
            ((TextView) view.findViewById(R.id.title)).setText(product.name);
            ((TextView) view.findViewById(R.id.description)).setText(product.description);
            ((TextView) view.findViewById(R.id.price)).setText(String.format(getString(R.string.inr), product.price));
            final ImageView imageView = view.findViewById(R.id.image);
            Button button = view.findViewById(R.id.button);

            FirebaseStorage.getInstance().getReferenceFromUrl(product.image).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Glide.with(ProductsActivity.this).load(task.getResult()).into(imageView);
                }
            });

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Preferences.INSTANCE.get(getApplicationContext(), Preferences.IS_ADMIN)) {
                        removeProduct(product);
                    } else {
                        addToCart(product);
                    }
                }
            });
            button.setText(Preferences.INSTANCE.get(getApplicationContext(), Preferences.IS_ADMIN)
                    ? getString(R.string.remove) : getString(R.string.add_to_cart));
            list.addView(view);
        }
    }

    private void addToCart(Product product) {
        Map<String, Object> map = Cart.get(product, "1");

        showLoader();

        FirebaseFirestore.getInstance()
                .collection(Constants.PATH_USERS)
                .document(FirebaseAuth.getInstance().getUid())
                .collection(Constants.PATH_CART)
                .add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        hideLoader();
                        Toast.makeText(ProductsActivity.this, R.string.added_to_cart, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideLoader();
                        Toast.makeText(ProductsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeProduct(Product product) {

    }
}
