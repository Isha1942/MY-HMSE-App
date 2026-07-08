package com.spark.newapp2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import com.spark.newapp2.database.AppDatabase;
import com.spark.newapp2.database.UserDevice;

public class SavedUserDetailFragment extends Fragment {

    private TextView tvName, tvSite, tvSim, tvDevice;
    private ImageButton btnEdit, btnDelete;
    private UserDevice user;
    private AppDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.item_user, container, false);


        tvName = view.findViewById(R.id.tv_name);
        tvSite = view.findViewById(R.id.tv_site);
        tvSim = view.findViewById(R.id.tv_sim);
        tvDevice = view.findViewById(R.id.tv_device);
        btnEdit = view.findViewById(R.id.btn_edit_user);
        btnDelete = view.findViewById(R.id.btn_delete_user);

        db = AppDatabase.getInstance(requireContext());


        if (getArguments() != null) {
            user = new UserDevice(
                    getArguments().getString("userName", ""),
                    getArguments().getString("siteName", ""),
                    getArguments().getString("simNumber", ""),
                    getArguments().getString("deviceModel", ""),
                    "",   // extra field if needed
                    false
            );
            displayUser(user);
        }


        btnEdit.setOnClickListener(v -> editUser());


        btnDelete.setOnClickListener(v -> deleteUser());

        return view;
    }

    private void displayUser(UserDevice user) {
        if (user != null) {
            tvName.setText("Username: " + user.getUserName());
            tvSite.setText("Site: " + user.getSiteName());
            tvSim.setText("SIM: " + user.getSimNumber());
            tvDevice.setText("Device: " + user.getDeviceModel());
        }
    }

    private void editUser() {
        if (user == null) return;

        DeviceSelectionFragment fragment = new DeviceSelectionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userName", user.getUserName());
        bundle.putString("siteName", user.getSiteName());
        bundle.putString("simNumber", user.getSimNumber());
        bundle.putString("deviceModel", user.getDeviceModel());
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void deleteUser() {
        if (user == null) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete this user?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    new Thread(() -> {
                        db.userDeviceDao().deleteUser(user);

                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "User deleted!", Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        });
                    }).start();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
}
