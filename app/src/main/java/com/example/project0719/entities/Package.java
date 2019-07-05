package com.example.project0719.entities;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class Package implements Parcelable {

    public String name;
    public String description;
    public String id;

    public Package(QueryDocumentSnapshot documentSnapshot) {
        name = (String) documentSnapshot.getData().get("name");
        description = (String) documentSnapshot.getData().get("description");
        id = documentSnapshot.getId();
    }

    protected Package(Parcel in) {
        name = in.readString();
        description = in.readString();
        id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Package> CREATOR = new Creator<Package>() {
        @Override
        public Package createFromParcel(Parcel in) {
            return new Package(in);
        }

        @Override
        public Package[] newArray(int size) {
            return new Package[size];
        }
    };

    public static Map<String, Object> get(String packageName, String packageDescription) {
        Map<String, Object> pack = new HashMap<>();
        pack.put("name", packageName);
        pack.put("description", packageDescription);

        return pack;
    }
}
