package com.example.project0719.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project0719.BaseActivity;
import com.example.project0719.Constants;
import com.example.project0719.R;
import com.example.project0719.entities.Cart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class CartActivity extends BaseActivity implements CartAdapter.Callback {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CartAdapter adapter = new CartAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        RecyclerView list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchCart();
    }

    private void fetchCart() {
        showLoader();
        db.collection(Constants.PATH_USERS)
                .document(FirebaseAuth.getInstance().getUid())
                .collection(Constants.PATH_CART)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        hideLoader();
                        if (task.isSuccessful()) {
                            adapter.cartItems.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Cart cart = new Cart(document);
                                adapter.cartItems.add(cart);
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void removeItem(final Cart cartItem) {
        showLoader();
        FirebaseFirestore.getInstance()
                .collection(Constants.PATH_USERS)
                .document(FirebaseAuth.getInstance().getUid())
                .collection(Constants.PATH_CART)
                .document(cartItem.id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CartActivity.this, R.string.cart_removed, Toast.LENGTH_SHORT).show();
                        hideLoader();
                        adapter.cartItems.remove(cartItem);
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideLoader();
                        Toast.makeText(CartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_ativity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.place_order) {
            placeOrder();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void placeOrder() {
        if (!adapter.cartItems.isEmpty()) {
            Intent intent = new Intent(this, PlaceOrderActivity.class);
            intent.putParcelableArrayListExtra("cart_items", adapter.cartItems);
            startActivity(intent);
        }
    }
}
