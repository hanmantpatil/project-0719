package com.example.project0719.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project0719.R;

import java.util.ArrayList;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.VH> {

    ArrayList<Order> orders = new ArrayList<>();

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.summary.setText(orders.get(position).orderSummary);
        holder.address.setText("Address: "+ orders.get(position).deliveryAddress);
        holder.contact.setText(String.format(holder.itemView.getContext().getString(R.string.contact), orders.get(position).deliveryPhone));
        holder.totalAmount.setText("Total: " + String.format(holder.itemView.getContext().getString(R.string.inr), orders.get(position).totalAmount));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class VH extends RecyclerView.ViewHolder {

        TextView address;
        TextView summary;
        TextView contact;
        TextView totalAmount;

        public VH(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
            contact = itemView.findViewById(R.id.contact);
            summary = itemView.findViewById(R.id.order_summary);
            totalAmount = itemView.findViewById(R.id.amount);
        }
    }
}
