package com.example.project0719.user;

import com.example.project0719.entities.Cart;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Order {

    public String orderSummary;
    public String deliveryAddress;
    public String deliveryPhone;
    public String totalAmount;
    String id;

    public Order(QueryDocumentSnapshot document) {
        orderSummary = (String) document.getData().get("order_summary");
        deliveryAddress = (String) document.getData().get("address");
        deliveryPhone = (String) document.getData().get("phone");
        totalAmount = String.valueOf(document.getData().get("total"));
        id = document.getId();
    }

    public static Map<String, Object> get(ArrayList<Cart> cartItems, float totalAmount, String deliveryAddress, String deliveryPhone) {

        Map<String, Object> pack = new HashMap<>();
        StringBuilder orderSummary = new StringBuilder();

        for (Cart item : cartItems) {
            orderSummary.append(item.product.name);
            orderSummary.append(" * ");
            orderSummary.append(item.quantity);
            orderSummary.append("\n");
        }


        pack.put("order_summary", orderSummary.toString());
        pack.put("address", deliveryAddress);
        pack.put("phone", deliveryPhone);
        pack.put("total", totalAmount);

        return pack;
    }
}
