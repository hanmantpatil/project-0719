package com.example.project0719.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project0719.Preferences;
import com.example.project0719.R;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.VH> {

    ArrayList<String> items = new ArrayList<>();
    private Callback callback;
    private Context context;

    ListAdapter(Callback callback, Context context) {
        this.callback = callback;
        this.context = context;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (position == items.size()) {
            holder.textView.setText(holder.textView.getContext().getString(R.string.add_more));
        } else {
            holder.textView.setText(items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return Preferences.INSTANCE.get(context, Preferences.IS_ADMIN) ? items.size() + 1 : items.size();
    }

    class VH extends RecyclerView.ViewHolder {
        TextView textView;

        VH(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.list_item_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (items.size() == getAdapterPosition()) {
                        callback.addMore();
                    } else {
                        callback.onItemSelected(getAdapterPosition());
                    }
                }
            });
        }
    }

    interface Callback {
        void onItemSelected(int position);

        void addMore();
    }
}
