package com.example.project0719.admin;

import android.content.Intent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.project0719.Preferences;
import com.example.project0719.R;
import com.example.project0719.user.BookingsActivity;
import com.example.project0719.user.OrdersActivity;
import com.example.project0719.user.ProductsActivity;

public class AdminHome extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preferences.INSTANCE.put(getApplicationContext(), Preferences.IS_ADMIN, true);

        setContentView(R.layout.activity_admin_home);
        findViewById(R.id.products).setOnClickListener(this);
        findViewById(R.id.packages).setOnClickListener(this);
        findViewById(R.id.orders).setOnClickListener(this);
        findViewById(R.id.venues).setOnClickListener(this);
        findViewById(R.id.bookings).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.products:
                startActivity(new Intent(this, ProductsActivity.class));
                break;
            case R.id.packages:
                startActivity(new Intent(this, PackagesActivity.class));
                break;
            case R.id.orders:
                startActivity(new Intent(this, OrdersActivity.class));
                break;
            case R.id.venues:
                Intent intent = new Intent(this, PackagesActivity.class);
                intent.putExtra("for_venues", true);
                startActivity(intent);
                break;
            case R.id.bookings:
                startActivity(new Intent(this, BookingsActivity.class));
                break;
        }
    }
}
