package com.example.project0719.entities;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class Cart implements Parcelable {
    public String productId;
    public String name;
    public String quantity;
    public String price;
    public String id;

    protected Cart(Parcel in) {
        productId = in.readString();
        name = in.readString();
        quantity = in.readString();
        price = in.readString();
        id = in.readString();
    }

    public Cart(QueryDocumentSnapshot document) {
        name = (String) document.getData().get("name");
        productId = (String) document.getData().get("product_id");
        quantity = (String) document.getData().get("quantity");
        price = (String) document.getData().get("price");
        id = document.getId();
    }

    public static Map<String, Object> get(String productId, String name, String quantity, String price) {
        Map<String, Object> venue = new HashMap<>();
        venue.put("product_id", productId);
        venue.put("name", name);
        venue.put("quantity", quantity);
        venue.put("price", price);
        return venue;
    }

    public static final Creator<Cart> CREATOR = new Creator<Cart>() {
        @Override
        public Cart createFromParcel(Parcel in) {
            return new Cart(in);
        }

        @Override
        public Cart[] newArray(int size) {
            return new Cart[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(productId);
        parcel.writeString(name);
        parcel.writeString(quantity);
        parcel.writeString(price);
        parcel.writeString(id);
    }
}
