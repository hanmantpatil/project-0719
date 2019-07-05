package com.example.project0719.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project0719.R;
import com.example.project0719.entities.Package;

import java.util.ArrayList;

public class PackagesAdapter extends RecyclerView.Adapter<PackagesAdapter.VH> {

    ArrayList<Package> packages = new ArrayList<>();
    private Callback callback;

    PackagesAdapter(Callback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (position == packages.size()) {
            holder.textView.setText(holder.textView.getContext().getString(R.string.add_more));
        } else {
            holder.textView.setText(packages.get(position).name);
        }
    }

    @Override
    public int getItemCount() {
        return packages.size() + 1;
    }

    class VH extends RecyclerView.ViewHolder {
        TextView textView;

        VH(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.list_item_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (packages.size() == getAdapterPosition()) {
                        callback.addMore();
                    } else {
                        callback.onPackageSelected(packages.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    interface Callback {
        void onPackageSelected(Package pack);

        void addMore();
    }
}
