package com.spark.newapp2;   // change to your package name

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DevicePagerAdapter extends RecyclerView.Adapter<DevicePagerAdapter.ViewHolder> {

    private Context context;
    private List<Integer> images;
    private List<String> names;

    public DevicePagerAdapter(Context context, List<Integer> images, List<String> names) {
        this.context = context;
        this.images = images;
        this.names = names;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.model_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImageResource(images.get(position));
        holder.textView.setText(names.get(position));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.modelImage);
            textView = itemView.findViewById(R.id.modelName);
        }
    }
}