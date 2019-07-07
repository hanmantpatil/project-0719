package com.example.project0719.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.project0719.BaseActivity;
import com.example.project0719.Constants;
import com.example.project0719.R;
import com.example.project0719.entities.Balance;
import com.example.project0719.entities.Cart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class PlaceOrderActivity extends BaseActivity {

    ArrayList<Cart> cartItems;
    Balance balance = null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final ArrayList<Cart> deletedItems = new ArrayList<>();

    private EditText phone;
    private EditText address;
    private TextView orders;
    private TextView prices;

    private float totalAmount;

    boolean userWriteComplete = false;
    boolean adminWriteComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        orders = findViewById(R.id.orders);
        prices = findViewById(R.id.prices);

        cartItems = getIntent().getParcelableArrayListExtra("cart_items");

        setUpPriceAndOrders();
        fetchBalance();

        findViewById(R.id.place_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (balance == null || Float.valueOf(balance.amount) < totalAmount) {
                    Toast.makeText(PlaceOrderActivity.this, R.string.low_on_balance, Toast.LENGTH_SHORT).show();
                    return;
                }
                placeOrder();
            }
        });
    }

    private void placeOrder() {
        String deliveryAddress = address.getText().toString();
        String deliveryPhone = phone.getText().toString();

        if (TextUtils.isEmpty(deliveryAddress)) {
            Toast.makeText(this, getString(R.string.empty_address_error), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(deliveryPhone)) {
            Toast.makeText(this, getString(R.string.empty_phone_error), Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> order = Order.get(cartItems, totalAmount, deliveryAddress, deliveryPhone);

        placeOrder(order);
    }

    private void placeOrder(Map<String, Object> order) {
        showLoader();
        userWriteComplete = false;
        adminWriteComplete = false;

        db.collection(Constants.PATH_USERS)
                .document(FirebaseAuth.getInstance().getUid())
                .collection(Constants.PATH_ORDERS)
                .add(order)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        userWriteComplete = true;
                        if (adminWriteComplete) {
                            deductFromWallet();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        userWriteComplete = true;
                        if (adminWriteComplete) {
                            hideLoader();
                            Toast.makeText(PlaceOrderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        db.collection(Constants.PATH_ORDERS)
                .add(order)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        adminWriteComplete = true;
                        if (userWriteComplete) {
                            deductFromWallet();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        adminWriteComplete = true;
                        if (userWriteComplete) {
                            hideLoader();
                            Toast.makeText(PlaceOrderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void deductFromWallet() {

        db.collection(Constants.PATH_USERS)
                .document(FirebaseAuth.getInstance().getUid())
                .collection(Constants.PATH_WALLET)
                .document(balance.id).update("amount", String.valueOf(Float.valueOf(balance.amount) - totalAmount))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        clearCart();
                    }
                });
    }

    private void clearCart() {
        for (final Cart item : cartItems) {
            db.collection(Constants.PATH_USERS)
                    .document(FirebaseAuth.getInstance().getUid())
                    .collection(Constants.PATH_CART)
                    .document(item.id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            deletedItems.add(item);
                            if (deletedItems.size() == cartItems.size()) {
                                Toast.makeText(PlaceOrderActivity.this, R.string.order_placed, Toast.LENGTH_SHORT).show();
                                hideLoader();
                                finish();
                            }
                        }
                    });
        }
    }

    private void setUpPriceAndOrders() {
        StringBuilder priceBuilder = new StringBuilder();
        StringBuilder orderBuilder = new StringBuilder();
        totalAmount = 0;

        for (Cart item : cartItems) {
            priceBuilder.append(String.format(getString(R.string.inr), item.product.price));
            priceBuilder.append("\n");

            totalAmount += Float.valueOf(item.product.price);

            orderBuilder.append(item.product.name);
            orderBuilder.append(" * ");
            orderBuilder.append(item.quantity);
            orderBuilder.append("\n");
        }
        orderBuilder.append("\n");
        priceBuilder.append("\n");

        orderBuilder.append(getString(R.string.total));
        priceBuilder.append(String.format(getString(R.string.inr), String.valueOf(totalAmount)));

        prices.setText(priceBuilder.toString());
        orders.setText(orderBuilder.toString());
    }

    private void fetchBalance() {
        showLoader();
        db.collection(Constants.PATH_USERS)
                .document(FirebaseAuth.getInstance().getUid())
                .collection(Constants.PATH_WALLET)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        hideLoader();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                balance = new Balance(document);
                            }
                        }
                    }
                });
    }
}
