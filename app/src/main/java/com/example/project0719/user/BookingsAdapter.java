package com.example.project0719.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project0719.Preferences;
import com.example.project0719.R;
import com.example.project0719.entities.Booking;

import java.util.ArrayList;
import java.util.Calendar;

public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.VH> {

    ArrayList<Booking> bookings = new ArrayList<>();
    Calendar today = Calendar.getInstance();

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.title.setText(bookings.get(position).venue.name);
        holder.address.setText(bookings.get(position).venue.address);
        holder.event.setText(bookings.get(position).eventType);
        holder.date.setText(bookings.get(position).date);

        if (Preferences.INSTANCE.get(holder.itemView.getContext().getApplicationContext(), Preferences.IS_ADMIN)) {
            holder.contact.setVisibility(View.VISIBLE);
            holder.contact.setText(String.format(holder.itemView.getContext().getString(R.string.contact), bookings.get(position).phone));
        } else  {
            holder.contact.setVisibility(View.INVISIBLE);
        }
        holder.pack.setText(bookings.get(position).pack.name);
        String[] dateSplit = bookings.get(position).date.split("-");
        Calendar now = Calendar.getInstance();
        now.set(Integer.valueOf(dateSplit[2]), Integer.valueOf(dateSplit[1]), Integer.valueOf(dateSplit[0]));

        if (now.getTimeInMillis() < today.getTimeInMillis()) {
            holder.status.setText(holder.itemView.getContext().getString(R.string.status_complete));
        } else {
            holder.status.setText(holder.itemView.getContext().getString(R.string.status_pending));
        }
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    class VH extends RecyclerView.ViewHolder {

        TextView title;
        TextView address;
        TextView event;
        TextView date;
        TextView pack;
        TextView contact;
        TextView status;

        public VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            address = itemView.findViewById(R.id.address);
            event = itemView.findViewById(R.id.event);
            date = itemView.findViewById(R.id.date);

            pack = itemView.findViewById(R.id.pack);
            contact = itemView.findViewById(R.id.contact);
            status = itemView.findViewById(R.id.status);
        }
    }
}
