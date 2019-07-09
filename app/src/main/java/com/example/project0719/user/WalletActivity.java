package com.example.project0719.user;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.project0719.BaseActivity;
import com.example.project0719.Constants;
import com.example.project0719.R;
import com.example.project0719.entities.Balance;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class WalletActivity extends BaseActivity {

    TextView balanceText;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Balance balance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        balanceText = findViewById(R.id.balance);

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount = ((EditText) findViewById(R.id.amount)).getText().toString();

                if (TextUtils.isEmpty(amount)) {
                    Toast.makeText(WalletActivity.this, R.string.empty_amount_error, Toast.LENGTH_SHORT).show();
                    return;
                }

                addToWallet(amount);
            }
        });
        fetchBalance();
    }

    private void addToWallet(String amount) {
        showLoader();
        if (balance == null) {
            Map<String, Object> obj = Balance.get(amount);
            db.collection(Constants.PATH_USERS)
                    .document(FirebaseAuth.getInstance().getUid())
                    .collection(Constants.PATH_WALLET)
                    .add(obj)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            hideLoader();
                            if (getIntent().getBooleanExtra("finish", false)) {
                                finish();
                            } else {
                                fetchBalance();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideLoader();
                            Toast.makeText(WalletActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            db.collection(Constants.PATH_USERS)
                    .document(FirebaseAuth.getInstance().getUid())
                    .collection(Constants.PATH_WALLET)
                    .document(balance.id).update("amount", String.valueOf(Float.valueOf(balance.amount) + Float.valueOf(amount)))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            hideLoader();
                            if (getIntent().getBooleanExtra("finish", false)) {
                                finish();
                            } else {
                                fetchBalance();
                            }
                        }
                    });
        }
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

                        String balanceAmount = balance == null ? "0.0" : balance.amount;
                        balanceText.setText(String.format(getString(R.string.inr), balanceAmount));
                    }
                });
    }

}
