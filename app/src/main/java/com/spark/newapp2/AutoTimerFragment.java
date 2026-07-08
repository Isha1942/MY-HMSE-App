package com.spark.newapp2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AutoTimerFragment extends Fragment {

    private EditText etTime;
    private Button btnAlarm, btnDisarm, btnQuickZone, btnDelayedZone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_auto_timer_fragment, container, false);

        etTime = view.findViewById(R.id.et_time);
        btnAlarm = view.findViewById(R.id.btn_set_arm_time);
        btnDisarm = view.findViewById(R.id.btn_set_disarm_time);
        btnQuickZone = view.findViewById(R.id.btn_set_quick_zone);
        btnDelayedZone = view.findViewById(R.id.btn_set_delayed_zone);

        btnAlarm.setOnClickListener(v -> openSmsApp("*TA", "ARM Time"));
        btnDisarm.setOnClickListener(v -> openSmsApp("*TD", "DISARM Time"));
        btnQuickZone.setOnClickListener(v -> openSmsApp("*TQ", "Quick Zone Time"));
        btnDelayedZone.setOnClickListener(v -> openSmsApp("*TT", "Delayed Zone Time"));

        return view;
    }

    private String getDeviceNumber() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("MySecuronPrefs", Context.MODE_PRIVATE);
        return prefs.getString("device_number", "");
    }

    private void openSmsApp(String prefix, String type) {
        String time = etTime.getText().toString().replace(":", "");
        if (TextUtils.isEmpty(time)) {
            Toast.makeText(getActivity(), "Please enter time", Toast.LENGTH_SHORT).show();
            return;
        }

        String deviceNumber = getDeviceNumber();
        if (deviceNumber == null || deviceNumber.isEmpty()) {
            Toast.makeText(getActivity(), "Please select a device first", Toast.LENGTH_SHORT).show();
            return;
        }

        String command = prefix + time + "#";

        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + Uri.encode(deviceNumber)));
            intent.putExtra("sms_body", command);
            startActivity(intent);  // opens SMS app
            Toast.makeText(getActivity(), type + " set: " + time, Toast.LENGTH_SHORT).show();


            String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    .format(new Date());
            String historyMessage = timestamp + " => " + type + " set: " + time;

            saveCommandToHistory(historyMessage);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "No SMS app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveCommandToHistory(String message) {
        new Thread(() -> {
            try {
                CommandHistory history = new CommandHistory(message, System.currentTimeMillis());
                HistoryDatabase.getInstance(requireContext())
                        .historyDao()
                        .insert(history);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
