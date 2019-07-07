package com.example.project0719.entities;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class Balance {

    public String amount;
    public String id;

    public Balance(QueryDocumentSnapshot document) {
        amount = (String) document.getData().get("amount");
        id = document.getId();
    }

    public static Map<String, Object> get(String amount) {
        Map<String, Object> obj = new HashMap<>();
        obj.put("amount", amount);
        return obj;
    }
}
