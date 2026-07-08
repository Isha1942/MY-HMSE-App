package com.spark.newapp2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DeviceDataCheckFragment extends Fragment {

    private LinearLayout btnCheckMobileNumbers, btnCheckDailySMS, btnCheckCustomerName,
            btnCheckArmDisarm, btnCheckZoneStatus, btnCheckAutoTimer,
            btnCheckSimSMSCall, btnCheckSimBalance, btnCheckDeviceHistory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_device_data_check_fragment, container, false);

        btnCheckMobileNumbers = view.findViewById(R.id.btn_check_mobile_numbers);
        btnCheckDailySMS = view.findViewById(R.id.btn_check_daily_sms);
        btnCheckCustomerName = view.findViewById(R.id.btn_check_customer_name);
        btnCheckArmDisarm = view.findViewById(R.id.btn_check_arm_disarm);
        btnCheckZoneStatus = view.findViewById(R.id.btn_check_zone_status);
        btnCheckAutoTimer = view.findViewById(R.id.btn_check_auto_timer);
        btnCheckSimSMSCall = view.findViewById(R.id.btn_check_sim_sms_call);
        btnCheckSimBalance = view.findViewById(R.id.btn_check_sim_balance);
        btnCheckDeviceHistory = view.findViewById(R.id.btn_check_device_history);


        btnCheckMobileNumbers.setOnClickListener(v -> sendCommand("**N1#", "Check Mobile Numbers"));
        btnCheckDailySMS.setOnClickListener(v -> sendCommand("**N2#", "Check Daily SMS"));
        btnCheckCustomerName.setOnClickListener(v -> sendCommand("**M#", "Check Customer Name"));
        btnCheckArmDisarm.setOnClickListener(v -> sendCommand("*SC#", "Check Arm/Disarm Status"));
        btnCheckZoneStatus.setOnClickListener(v -> sendCommand("*SO#", "Check Zone Status"));
        btnCheckAutoTimer.setOnClickListener(v -> sendCommand("*TC#", "Check Auto Timer"));
        btnCheckSimSMSCall.setOnClickListener(v -> sendCommand("*CC#", "Check SIM SMS/Call"));
        btnCheckDeviceHistory.setOnClickListener(v -> sendCommand("*F#", "Check Device History"));


        btnCheckSimBalance.setOnClickListener(v ->
                showInputDialog("Enter USSD code for SIM balance", input ->
                        sendCommand("*B*" + input + "#", "Check SIM Balance"))
        );

        return view;
    }

    private String getDeviceNumber() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("MySecuronPrefs", Context.MODE_PRIVATE);
        return prefs.getString("device_number", "");
    }

    private void sendCommand(String command, String description) {
        String deviceNumber = getDeviceNumber();
        if (deviceNumber == null || deviceNumber.isEmpty()) {
            Toast.makeText(getActivity(), "Please select a device first!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + Uri.encode(deviceNumber)));
            intent.putExtra("sms_body", command);
            startActivity(intent);

            Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();


            String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    .format(new Date());
            String historyMessage = timestamp + " => " + description + " (Command: " + command + ")";

            saveCommandToHistory(historyMessage);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Failed to open SMS app: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

    private void showInputDialog(String title, InputCallback callback) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_input, null);
        TextView tvTitle = dialogView.findViewById(R.id.dialog_title);
        EditText etInput = dialogView.findViewById(R.id.dialog_input);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnOk = dialogView.findViewById(R.id.btn_ok);

        tvTitle.setText(title);
        etInput.setFilters(new android.text.InputFilter[]{}); // allow all input
        etInput.setInputType(InputType.TYPE_CLASS_TEXT);

        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnOk.setOnClickListener(v -> {
            String input = etInput.getText().toString().trim();
            if (input.isEmpty()) {
                Toast.makeText(getContext(), "Input cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            callback.onInput(input);
            dialog.dismiss();
        });

        dialog.show();
    }

    interface InputCallback {
        void onInput(String value);
    }
}
