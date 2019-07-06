package com.example.project0719.entities;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class Product implements Parcelable {
    public String name;
    public String description;
    public String price;
    public String image;
    public String id;


    protected Product(Parcel in) {
        name = in.readString();
        description = in.readString();
        price = in.readString();
        image = in.readString();
        id = in.readString();
    }

    public Product(QueryDocumentSnapshot document) {
        name = (String) document.getData().get("name");
        description = (String) document.getData().get("description");
        price = (String) document.getData().get("price");
        image = (String) document.getData().get("image");
        id = document.getId();
    }

    private Product(HashMap product) {
        name = (String) product.get("name");
        description = (String) product.get("description");
        price = (String) product.get("price");
        image = (String) product.get("image");
    }

    public static Map<String, Object> get(String name, String price, String description, String image) {
        Map<String, Object> booking = new HashMap<>();
        booking.put("name", name);
        booking.put("description", description);
        booking.put("price", price);
        booking.put("image", image);
        return booking;
    }

    public static Product get(HashMap product) {
        return new Product(product);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(price);
        dest.writeString(image);
        dest.writeString(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
