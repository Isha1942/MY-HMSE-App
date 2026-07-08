package com.spark.newapp2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ArmDisarmFragment extends Fragment {

    private Button btnArm, btnDisarm, btnReset;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_arm_disarm_fragment, container, false);

        btnArm = view.findViewById(R.id.btn_arm);
        btnDisarm = view.findViewById(R.id.btn_disarm);
        btnReset = view.findViewById(R.id.btn_reset_device);

        Button[] buttons = {btnArm, btnDisarm, btnReset};
        for (Button btn : buttons) {
            btn.setBackgroundTintList(null);
            btn.setBackgroundResource(R.drawable.rounded_button);
        }

        btnArm.setOnClickListener(v -> openSmsApp("*A#", "Device Armed", "Device Armed"));
        btnDisarm.setOnClickListener(v -> openSmsApp("*D#", "Device Disarmed", "Device Disarmed"));
        btnReset.setOnClickListener(v -> openSmsApp("*R#", "Device Reset", "Device Reset"));

        return view;
    }

    private String getDeviceNumber() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("MySecuronPrefs", Context.MODE_PRIVATE);
        return prefs.getString("device_number", "");
    }

    private void openSmsApp(String command, String toastMessage, String historyText) {
        String deviceNumber = getDeviceNumber();
        if (deviceNumber == null || deviceNumber.isEmpty()) {
            Toast.makeText(getActivity(), "Please select a device first", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + Uri.encode(deviceNumber))); // encode number
            intent.putExtra("sms_body", command);

            startActivity(intent);
            Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();


            String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    .format(new Date());
            String historyMessage = timestamp + " => " + historyText;

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
