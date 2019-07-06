package com.example.project0719.entities;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class Cart implements Parcelable {

    public Product product;
    public String quantity;
    public String id;

    protected Cart(Parcel in) {
        product = in.readParcelable(Product.class.getClassLoader());
        quantity = in.readString();
        id = in.readString();
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

    public Cart(QueryDocumentSnapshot document) {
        product = Product.get((HashMap)document.get("product"));
        quantity = (String) document.getData().get("quantity");
        id = document.getId();
    }

    public static Map<String, Object> get(Product product, String quantity) {
        Map<String, Object> booking = new HashMap<>();
        booking.put("product", product);
        booking.put("quantity", quantity);
        return booking;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(product, i);
        parcel.writeString(quantity);
        parcel.writeString(id);
    }
}
