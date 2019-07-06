package com.example.project0719.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project0719.R;
import com.example.project0719.entities.Cart;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH> {

    private Callback callback;
    ArrayList<Cart> cartItems = new ArrayList<>();

    CartAdapter(Callback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.title.setText(cartItems.get(position).product.name);
        holder.price.setText(String.format(holder.itemView.getContext().getString(R.string.inr), cartItems.get(position).product.price) + " * " + cartItems.get(position).quantity);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class VH extends RecyclerView.ViewHolder {
        private Button removeButton;
        private TextView title;
        private TextView price;

        public VH(@NonNull View itemView) {
            super(itemView);
            removeButton = itemView.findViewById(R.id.remove);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.removeItem(cartItems.get(getAdapterPosition()));
                }
            });
        }
    }

    interface Callback {
        void removeItem(Cart cartItem);
    }
}
