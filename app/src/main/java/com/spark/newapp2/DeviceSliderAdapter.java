package com.spark.newapp2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DeviceSliderAdapter extends RecyclerView.Adapter<DeviceSliderAdapter.DeviceViewHolder> {

    private int[] deviceImages;
    private String[] deviceNames;

    public DeviceSliderAdapter(int[] deviceImages, String[] deviceNames) {
        this.deviceImages =deviceImages;
        this.deviceNames =deviceNames;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_slider_item, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        holder.imageDevice.setImageResource(deviceImages[position]);
        holder.tvDeviceName.setText(deviceNames[position]);
    }

    @Override
    public int getItemCount() {
        return deviceImages.length;
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {
        ImageView imageDevice;
        TextView tvDeviceName;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            imageDevice = itemView.findViewById(R.id.imageDevice);
            tvDeviceName = itemView.findViewById(R.id.tvDeviceName);
        }
    }
}
