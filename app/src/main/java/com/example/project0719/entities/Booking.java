package com.example.project0719.entities;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class Booking implements Parcelable {
    public String eventType;
    public Package pack;
    public Venue venue;
    public String date;
    public String id;
    public String phone;


    protected Booking(Parcel in) {
        eventType = in.readString();
        pack = in.readParcelable(Package.class.getClassLoader());
        venue = in.readParcelable(Venue.class.getClassLoader());
        date = in.readString();
        id = in.readString();
        phone = in.readString();
    }

    public Booking(QueryDocumentSnapshot document) {
        eventType = (String) document.getData().get("event_type");
        pack = Package.get((HashMap)document.getData().get("package")) ;
        venue = Venue.get((HashMap) document.getData().get("venue"));
        date = (String) document.getData().get("date");
        phone = (String) document.getData().get("phone");
        id = document.getId();
    }

    public static Map<String, Object> get(String eventType, Package pack, Venue venue, String date, String phone) {
        Map<String, Object> booking = new HashMap<>();
        booking.put("event_type", eventType);
        booking.put("package", pack);
        booking.put("phone", phone);
        booking.put("venue", venue);
        booking.put("date", date);
        return booking;
    }

    public static final Creator<Booking> CREATOR = new Creator<Booking>() {
        @Override
        public Booking createFromParcel(Parcel in) {
            return new Booking(in);
        }

        @Override
        public Booking[] newArray(int size) {
            return new Booking[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(eventType);
        parcel.writeParcelable(pack, i);
        parcel.writeParcelable(venue, i);
        parcel.writeString(date);
        parcel.writeString(id);
        parcel.writeString(phone);
    }
}
