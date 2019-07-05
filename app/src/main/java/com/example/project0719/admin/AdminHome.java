package com.example.project0719.admin;

import android.content.Intent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.project0719.R;

public class AdminHome extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        findViewById(R.id.products).setOnClickListener(this);
        findViewById(R.id.packages).setOnClickListener(this);
        findViewById(R.id.orders).setOnClickListener(this);
        findViewById(R.id.venues).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.products:
                break;
            case R.id.packages:
                startActivity(new Intent(this, AdminPackages.class));
                break;
            case R.id.orders:
                break;
            case R.id.venues:
                break;
        }
    }
}
