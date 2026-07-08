package com.spark.newapp2;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.spark.newapp2.database.UserDevice;
import android.content.Intent;
import android.net.Uri;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int SMS_PERMISSION_CODE = 100;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private TextView toolbarTitle;
    private ImageView toolbarLogoCenter;
    private ImageView toolbarLogoRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarLogoCenter = toolbar.findViewById(R.id.toolbar_logo_center);
        toolbarLogoRight = toolbar.findViewById(R.id.toolbar_logo_right);


        showCenterLogo();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        setupDrawerHeader();

        if (savedInstanceState == null) {
            loadFragment(new DeviceSelectionFragment());
            navigationView.setCheckedItem(R.id.nav_device_selection);
        }
    }

    private void showCenterLogo() {
        toolbarLogoCenter.setVisibility(View.VISIBLE);
        toolbarTitle.setVisibility(View.GONE);
        toolbarLogoRight.setVisibility(View.GONE);
    }

    private void showTitleWithRightLogo(String title) {
        toolbarLogoCenter.setVisibility(View.GONE);
        toolbarTitle.setVisibility(View.VISIBLE);
        toolbarLogoRight.setVisibility(View.VISIBLE);
        toolbarTitle.setText(title);
    }

    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupDrawerHeader();
    }

    public void setupDrawerHeader() {
        View headerView = navigationView.getHeaderView(0);
        if (headerView == null) return;

        TextView tvCustomerName = headerView.findViewById(R.id.tv_customer_name);
        TextView tvDeviceName = headerView.findViewById(R.id.tv_device_name);
        TextView tvSimNo = headerView.findViewById(R.id.tv_sim_number);

        SharedPreferences prefs = getSharedPreferences("MySecuronPrefs", MODE_PRIVATE);
        if (tvCustomerName != null) tvCustomerName.setText(prefs.getString("customer_name", "Select User"));
        if (tvDeviceName != null) tvDeviceName.setText(prefs.getString("selected_device_model", "No Device"));
        if (tvSimNo != null) tvSimNo.setText(prefs.getString("device_number", "No SIM"));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        String title = "My HMSE"; // default

        if (item.getItemId() == R.id.nav_device_selection) {
            selectedFragment = new DeviceSelectionFragment();
            title = "Device Selection";
        } else if (item.getItemId() == R.id.nav_device_settings) {
            selectedFragment = new DeviceSettingFragment();
            title = "Device Settings";
        } else if (item.getItemId() == R.id.nav_entry_exit) {
            selectedFragment = new EntryExitTimersFragment();
            title = "Entry/Exit Timers";
        } else if (item.getItemId() == R.id.nav_sounder_timing) {
            selectedFragment = new SounderTimingFragment();
            title = "Sounder Timing";
        } else if (item.getItemId() == R.id.nav_auto_timer) {
            selectedFragment = new AutoTimerFragment();
            title = "Auto Timer";
        } else if (item.getItemId() == R.id.nav_arm_disarm) {
            selectedFragment = new ArmDisarmFragment();
            title = "Arm / Disarm";
        } else if (item.getItemId() == R.id.nav_enable_disable) {
            selectedFragment = new EnableDisableZoneFragment();
            title = "Enable / Disable Zone";
        } else if (item.getItemId() == R.id.nav_device_data) {
            selectedFragment = new DeviceDataCheckFragment();
            title = "Device Data Check";
        } else if (item.getItemId() == R.id.nav_notes) {
            selectedFragment = new NotesFragment();
            title = "Notes";
        } else if (item.getItemId() == R.id.nav_history) {
            selectedFragment = new HistoryFragment();
            title = "History";
        } else if (item.getItemId() == R.id.nav_about) {
            selectedFragment = new AboutUsFragment();
            title = "About Us";
        }

        loadFragment(selectedFragment);
        showTitleWithRightLogo(title);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (current instanceof DeviceSelectionFragment) {
                showCenterLogo();
            }
            super.onBackPressed();
        }
    }

    public void updateDrawerHeader(UserDevice user) {
        if (user == null) return;
        SharedPreferences prefs = getSharedPreferences("MySecuronPrefs", MODE_PRIVATE);
        prefs.edit()
                .putString("customer_name", user.getUserName())
                .putString("selected_device_model", user.getDeviceModel())
                .putString("device_number", user.getSimNumber())
                .putString("device_state", "disarmed")
                .apply();
        Toast.makeText(this, "Mobile number saved", Toast.LENGTH_SHORT).show();
        setupDrawerHeader();
    }

    public void updateDrawerHeaderDeviceOnly(String deviceModel) {
        SharedPreferences prefs = getSharedPreferences("MySecuronPrefs", MODE_PRIVATE);
        prefs.edit()
                .putString("selected_device_model", deviceModel)
                .putString("device_state", "disarmed")
                .apply();
        Toast.makeText(this, "Device updated", Toast.LENGTH_SHORT).show();
        setupDrawerHeader();
    }

    public void clearDrawerHeader() {
        SharedPreferences prefs = getSharedPreferences("MySecuronPrefs", MODE_PRIVATE);
        prefs.edit().clear().apply();
        Toast.makeText(this, "Cleared", Toast.LENGTH_SHORT).show();
        setupDrawerHeader();
    }

    public void sendCommandFromFragment(String command, String deviceNumber) {
        if (deviceNumber == null || deviceNumber.isEmpty()) {
            Toast.makeText(this, "Set device number first!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
            Toast.makeText(this, "SMS permission required", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + deviceNumber));
            intent.putExtra("sms_body", command);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                saveCommandToHistory(command);
            } else {
                Toast.makeText(this, "No SMS app found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void saveCommandToHistory(String command) {
        new Thread(() -> {
            try {
                CommandHistory history = new CommandHistory(command, System.currentTimeMillis());
                HistoryDatabase.getInstance(this)
                        .historyDao()
                        .insert(history);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "SMS permission granted. Please retry the action.", Toast.LENGTH_SHORT).show();
        }
    }
}
