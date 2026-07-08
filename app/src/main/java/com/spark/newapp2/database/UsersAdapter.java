package com.spark.newapp2.database;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.spark.newapp2.R;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    public interface OnUserClickListener {
        void onUserClicked(UserDevice user);
        void onEdit(UserDevice user);
        void onDelete(UserDevice user);
    }

    private final List<UserDevice> allUsers;   // Full list (unchanged)
    private List<UserDevice> filteredUsers;    // Filtered list (shown in RecyclerView)
    private final OnUserClickListener listener;

    public UsersAdapter(List<UserDevice> users, OnUserClickListener listener) {
        this.allUsers = new ArrayList<>(users);
        this.filteredUsers = new ArrayList<>(users);
        this.listener = listener;

        for (UserDevice u : allUsers) u.setSelected(false);
    }

    public void updateList(List<UserDevice> newUsers) {
        allUsers.clear();
        allUsers.addAll(newUsers);

        filteredUsers = new ArrayList<>(newUsers);
        for (UserDevice u : allUsers) u.setSelected(false);

        notifyDataSetChanged();
    }

    /** ✅ Filter users by Site Name */
    public void filterBySite(String query) {
        if (query == null || query.trim().isEmpty()) {
            filteredUsers = new ArrayList<>(allUsers);
        } else {
            List<UserDevice> result = new ArrayList<>();
            for (UserDevice user : allUsers) {
                if (user.getSiteName() != null &&
                        user.getSiteName().toLowerCase().contains(query.toLowerCase())) {
                    result.add(user);
                }
            }
            filteredUsers = result;
        }
        notifyDataSetChanged();
    }

    public List<UserDevice> getUsers() {
        return filteredUsers;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserDevice user = filteredUsers.get(position);

        holder.tvName.setText("Username: " + user.getUserName());
        holder.tvSite.setText("Site: " + user.getSiteName());
        holder.tvSim.setText("SIM: " + user.getSimNumber());
        holder.tvDevice.setText("Device: " + user.getDeviceModel());

        if (user.isSelected()) {
            int[] highlightColors = {
                    Color.parseColor("#B2EBF2"),
                    Color.parseColor("#A0E6F0"),
                    Color.parseColor("#C0F0FF"),
                    Color.parseColor("#D0F4FF")
            };
            holder.cardView.setCardBackgroundColor(highlightColors[position % highlightColors.length]);
        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
        }

        holder.cardView.setOnClickListener(v -> {
            user.setSelected(!user.isSelected());
            notifyItemChanged(position);
            listener.onUserClicked(user);
        });

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(user));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(user));
    }

    @Override
    public int getItemCount() {
        return filteredUsers.size();   // ✅ Correct count
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView tvName, tvSite, tvSim, tvDevice;
        ImageButton btnEdit, btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvSite = itemView.findViewById(R.id.tv_site);
            tvSim = itemView.findViewById(R.id.tv_sim);
            tvDevice = itemView.findViewById(R.id.tv_device);
            btnEdit = itemView.findViewById(R.id.btn_edit_user);
            btnDelete = itemView.findViewById(R.id.btn_delete_user);
        }
    }
}
