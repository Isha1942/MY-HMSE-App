package com.spark.newapp2;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.spark.newapp2.database.AppDatabase;
import com.spark.newapp2.database.UserDevice;
import com.spark.newapp2.database.UsersAdapter;

import java.util.ArrayList;
import java.util.List;

public class DeviceSelectionFragment extends Fragment {

    private EditText etUserName, etSiteName, etDeviceSim, etSearchSite;
    private TextView tvSelectedDevice;
    private Button btnSaveDevice;
    private RecyclerView rvUsers;
    private ViewPager2 deviceViewPager;
    private LinearLayout dotsLayout;

    private AppDatabase db;
    private UsersAdapter usersAdapter;
    private UserDevice editingUser = null;

    private final int[] deviceImages = {R.drawable.spyg2, R.drawable.spyg4, R.drawable.spyg8};
    private final String[] deviceNames = {"SPYG2", "SPYG4", "SPYG8"};
    private TextView[] dots;
    private String selectedDeviceModel = "SPYG2";

    private List<UserDevice> allUsers = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_device_selection_fragment, container, false);

        // Bind UI
        etUserName = view.findViewById(R.id.et_user_name);
        etSiteName = view.findViewById(R.id.et_site_name);
        etDeviceSim = view.findViewById(R.id.et_device_sim);
        etSearchSite = view.findViewById(R.id.et_search_site);
        tvSelectedDevice = view.findViewById(R.id.tv_selected_device);
        btnSaveDevice = view.findViewById(R.id.btn_save_device);
        rvUsers = view.findViewById(R.id.rv_users_list);
        deviceViewPager = view.findViewById(R.id.deviceViewPager);
        dotsLayout = view.findViewById(R.id.dotsLayout);

        db = AppDatabase.getInstance(requireContext());

        setupDeviceCarousel();
        setupRecyclerView();
        loadUsers();


        setSimNumberInputFilter();

        btnSaveDevice.setOnClickListener(v -> saveUser());


        etSearchSite.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsersBySite(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        return view;
    }


    private void setupDeviceCarousel() {
        DeviceSliderAdapter adapter = new DeviceSliderAdapter(deviceImages, deviceNames);
        deviceViewPager.setAdapter(adapter);
        tvSelectedDevice.setText("Selected Device: " + selectedDeviceModel);

        deviceViewPager.post(() -> setupDots(deviceViewPager.getCurrentItem()));

        deviceViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                selectedDeviceModel = deviceNames[position];
                tvSelectedDevice.setText("Selected Device: " + selectedDeviceModel);
                setupDots(position);

                SharedPreferences prefs = requireContext().getSharedPreferences("MySecuronPrefs", requireContext().MODE_PRIVATE);
                prefs.edit()
                        .putString("selected_device_model", selectedDeviceModel)
                        .putInt("device_type", mapModelToType(selectedDeviceModel))
                        .apply();

                MainActivity main = (MainActivity) getActivity();
                if (main != null) main.updateDrawerHeaderDeviceOnly(selectedDeviceModel);
            }
        });
    }

    private void setupDots(int currentPosition) {
        dotsLayout.removeAllViews();
        dots = new TextView[deviceImages.length];
        for (int i = 0; i < deviceImages.length; i++) {
            dots[i] = new TextView(getContext());
            dots[i].setText("\u2022");
            dots[i].setTextSize(35);
            dots[i].setTextColor(i == currentPosition ? Color.DKGRAY : Color.LTGRAY);
            dotsLayout.addView(dots[i]);
        }
    }

    private int mapModelToType(String model) {
        if ("SPYG2".equalsIgnoreCase(model)) return 2;
        if ("SPYG4".equalsIgnoreCase(model)) return 4;
        if ("SPYG8".equalsIgnoreCase(model)) return 8;
        return 2;
    }

    private void setSimNumberInputFilter() {
        etDeviceSim.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        etDeviceSim.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    private void setupRecyclerView() {
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        usersAdapter = new UsersAdapter(new ArrayList<>(), new UsersAdapter.OnUserClickListener() {
            @Override
            public void onUserClicked(UserDevice selectedUser) {
                highlightSelectedUser(selectedUser);
            }

            @Override
            public void onEdit(UserDevice user) {
                editingUser = user;
                etUserName.setText(user.getUserName());
                etSiteName.setText(user.getSiteName());
                etDeviceSim.setText(user.getSimNumber());
                selectedDeviceModel = user.getDeviceModel();
                tvSelectedDevice.setText("Selected Device: " + selectedDeviceModel);

                SharedPreferences prefs = requireContext().getSharedPreferences("MySecuronPrefs", requireContext().MODE_PRIVATE);
                prefs.edit()
                        .putString("selected_device_model", selectedDeviceModel)
                        .putInt("device_type", mapModelToType(selectedDeviceModel))
                        .apply();
            }

            @Override
            public void onDelete(UserDevice user) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete User")
                        .setMessage("Are you sure you want to delete " + user.getUserName() + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            new Thread(() -> {
                                db.userDeviceDao().deleteUser(user);
                                loadUsers();
                            }).start();
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });
        rvUsers.setAdapter(usersAdapter);
    }

    private void loadUsers() {
        new Thread(() -> {
            allUsers = db.userDeviceDao().getAllUsers();
            requireActivity().runOnUiThread(() -> usersAdapter.updateList(allUsers));
        }).start();
    }


    private void filterUsersBySite(String siteQuery) {
        List<UserDevice> filtered = new ArrayList<>();
        for (UserDevice u : allUsers) {
            if (u.getSiteName().toLowerCase().contains(siteQuery.toLowerCase())) {
                filtered.add(u);
            }
        }
        usersAdapter.updateList(filtered);
    }

    private void highlightSelectedUser(UserDevice user) {

        for (UserDevice u : usersAdapter.getUsers()) {
            u.setSelected(u.getId() == user.getId());
        }
        usersAdapter.notifyDataSetChanged();


        SharedPreferences prefs = requireContext().getSharedPreferences("MySecuronPrefs", requireContext().MODE_PRIVATE);
        prefs.edit()
                .putString("customer_name", user.getUserName())
                .putString("selected_device_model", user.getDeviceModel())
                .putString("device_number", user.getSimNumber())
                .putInt("device_type", mapModelToType(user.getDeviceModel()))
                .apply();


        MainActivity main = (MainActivity) getActivity();
        if (main != null) main.updateDrawerHeader(user);
    }

    private void saveUser() {
        String userName = etUserName.getText().toString().trim();
        String siteName = etSiteName.getText().toString().trim();
        String simNumber = etDeviceSim.getText().toString().trim();

        if (userName.isEmpty() || siteName.isEmpty() || simNumber.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (simNumber.length() != 10) {
            Toast.makeText(getContext(), "Enter 10-digit SIM number", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            if (editingUser != null) {
                editingUser.setUserName(userName);
                editingUser.setSiteName(siteName);
                editingUser.setSimNumber(simNumber);
                editingUser.setDeviceModel(selectedDeviceModel);
                db.userDeviceDao().updateUser(editingUser);
            } else {
                UserDevice user = new UserDevice(userName, siteName, simNumber, selectedDeviceModel, simNumber, false);
                db.userDeviceDao().insertUser(user);
            }

            requireActivity().runOnUiThread(() -> {
                loadUsers();
                etUserName.setText("");
                etSiteName.setText("");
                etDeviceSim.setText("");
                editingUser = null;
                Toast.makeText(getContext(), "User saved successfully!", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }
}
