package com.example.project0719.entities;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class Venue implements Parcelable {

    public String name;
    public String description;
    public String address;
    public String phoneNo;
    public String id;

    public Venue(Parcel in) {
        name = in.readString();
        description = in.readString();
        address = in.readString();
        phoneNo = in.readString();
        id = in.readString();
    }

    public Venue(QueryDocumentSnapshot document) {
        name = (String) document.getData().get("name");
        description = (String) document.getData().get("description");
        address = (String) document.getData().get("address");
        phoneNo = (String) document.getData().get("phone");
        id = document.getId();
    }

    private Venue(HashMap map) {
        name = (String) map.get("name");
        description = (String) map.get("description");
        address = (String) map.get("address");
        phoneNo = (String) map.get("phone");
    }

    public static Map<String, Object> get(String name, String description, String phoneNo, String address) {
        Map<String, Object> venue = new HashMap<>();
        venue.put("name", name);
        venue.put("description", description);
        venue.put("address", address);
        venue.put("phone", phoneNo);
        return venue;
    }

    public static Venue get(HashMap map) {
        return new Venue(map);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(address);
        dest.writeString(phoneNo);
        dest.writeString(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Venue) {
            return ((Venue) obj).id.equals(this.id);
        } else {
            return super.equals(obj);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Venue> CREATOR = new Creator<Venue>() {
        @Override
        public Venue createFromParcel(Parcel in) {
            return new Venue(in);
        }

        @Override
        public Venue[] newArray(int size) {
            return new Venue[size];
        }
    };
}
