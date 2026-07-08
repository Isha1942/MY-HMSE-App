package com.spark.newapp2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class DeviceSettingFragment extends Fragment {

    private LinearLayout btnSaveCustomerName, btnSaveMobileNumber, btnArmDisarmSMS,
            btnSetControlNumber, btnSetSMSCallOrder, btnSaveRFID, btnSetDateTime;

    private String deviceNumber = "";
    private int deviceType = 2;
    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_device_setting_fragment, container, false);

        bindViews(view);

        prefs = requireContext().getSharedPreferences("MySecuronPrefs", Context.MODE_PRIVATE);
        deviceNumber = prefs.getString("device_number", "");
        deviceType = prefs.getInt("device_type", 2);

        String model = prefs.getString("selected_device_model", "");
        if (deviceType != 8 && "SPYG8".equalsIgnoreCase(model)) {
            deviceType = 8;
        }

        if (deviceNumber.isEmpty()) {
            Toast.makeText(requireContext(), "Please select a device first", Toast.LENGTH_LONG).show();
            disableAllButtons();
        } else {
            setupClickListeners();
        }

        return view;
    }

    private void bindViews(View view) {
        btnSaveCustomerName = view.findViewById(R.id.btn_save_customer_name_card);
        btnSaveMobileNumber = view.findViewById(R.id.btn_save_mobile_number_card);
        btnArmDisarmSMS = view.findViewById(R.id.btn_arm_disarm_sms_card);
        btnSetControlNumber = view.findViewById(R.id.btn_set_control_number_card);
        btnSetSMSCallOrder = view.findViewById(R.id.btn_set_sms_call_order_card);
        btnSaveRFID = view.findViewById(R.id.btn_save_rfid_card);
        btnSetDateTime = view.findViewById(R.id.btn_set_date_time_card);
    }

    private void disableAllButtons() {
        btnSaveCustomerName.setEnabled(false);
        btnSaveMobileNumber.setEnabled(false);
        btnArmDisarmSMS.setEnabled(false);
        btnSetControlNumber.setEnabled(false);
        btnSetSMSCallOrder.setEnabled(false);
        btnSaveRFID.setEnabled(false);
        btnSetDateTime.setEnabled(false);
    }

    private void setupClickListeners() {
        btnSaveCustomerName.setOnClickListener(v -> showInputDialogWithCommand("Set Customer Name", "*_"));
        btnSaveMobileNumber.setOnClickListener(v -> showSaveMobileNumberDialog());
        btnArmDisarmSMS.setOnClickListener(v -> showNumberInputDialog("Set Arm/Disarm SMS Numbers", "*M", 5));
        btnSetControlNumber.setOnClickListener(v -> showNumberInputDialog("Set Controlling Numbers", "*N", 5));
        btnSetSMSCallOrder.setOnClickListener(v -> showSMSCallOrderDialog());
        btnSaveRFID.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Continue ??")
                    .setMessage("Do you want to save RFID card ?")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, which) -> {
                        sendCommand("*SS#", "Save RFID Card");
                    })
                    .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss())
                    .show();
        });
        btnSetDateTime.setOnClickListener(v -> setDeviceDateTime());
    }


    private void sendCommand(String command, String toastMessage) {
        if (deviceNumber.isEmpty()) {
            Toast.makeText(requireContext(), "Device not selected!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + Uri.encode(deviceNumber)));
            intent.putExtra("sms_body", command);

            startActivity(intent);
            Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();


            String timestamp = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                    .format(new java.util.Date());

            String historyMessage = timestamp + " => " + toastMessage;
            saveCommandToHistory(historyMessage);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Failed to open SMS app: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }



    private void saveCommandToHistory(String command) {
        new Thread(() -> {
            try {
                CommandHistory history = new CommandHistory(command, System.currentTimeMillis());
                HistoryDatabase.getInstance(requireContext())
                        .historyDao()
                        .insert(history);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showInputDialogWithCommand(String title, String commandPrefix) {
        showInputDialog(title, value -> sendCommand(commandPrefix + value + "#", title));
    }

    private void showInputDialog(String title, InputCallback callback) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_input, null);
        TextView tvTitle = dialogView.findViewById(R.id.dialog_title);
        EditText etInput = dialogView.findViewById(R.id.dialog_input);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnOk = dialogView.findViewById(R.id.btn_ok);

        tvTitle.setText(title);

        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnOk.setOnClickListener(v -> {
            String input = etInput.getText().toString().trim();
            if (!input.isEmpty()) {
                callback.onInput(input);
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Input cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showSaveMobileNumberDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_save_mobile, null);
        EditText etMobile = dialogView.findViewById(R.id.et_mobile_number);
        Spinner spinner = dialogView.findViewById(R.id.spinner_number_count);

        boolean isSpyg8 = (deviceType == 8) || "SPYG8".equalsIgnoreCase(prefs.getString("selected_device_model", ""));
        etMobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)}); // max 10 digits
        etMobile.setInputType(InputType.TYPE_CLASS_NUMBER);

        Integer[] numbers = new Integer[isSpyg8 ? 15 : 10];
        for (int i = 0; i < numbers.length; i++) numbers[i] = i + 1;

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, numbers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        String savedMobile = prefs.getString("mobile_number", "");
        int savedCount = prefs.getInt("max_numbers", 1);
        if (!savedMobile.isEmpty()) etMobile.setText(savedMobile);
        if (savedCount > 0 && savedCount <= numbers.length) spinner.setSelection(savedCount - 1);

        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        Button btnOk = dialogView.findViewById(R.id.btn_ok);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnOk.setOnClickListener(v -> {
            String mobile = etMobile.getText().toString().trim();
            if (mobile.length() != 10) {
                Toast.makeText(getContext(), "Enter a valid 10-digit mobile number.", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedNo = (int) spinner.getSelectedItem();
            String formattedNo = String.format("%02d", selectedNo);

            prefs.edit()
                    .putString("mobile_number", mobile)
                    .putInt("max_numbers", selectedNo)
                    .apply();

            sendCommand("*" + mobile + "*" + formattedNo + "#", "Saved Mobile Number");
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showNumberInputDialog(String title, String commandPrefix, int maxDigits) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_input, null);
        TextView tvTitle = dialogView.findViewById(R.id.dialog_title);
        EditText etInput = dialogView.findViewById(R.id.dialog_input);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnOk = dialogView.findViewById(R.id.btn_ok);

        tvTitle.setText(title);

        etInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        etInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxDigits)});

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

            while (input.length() < maxDigits) input += "*";
            sendCommand(commandPrefix + input + "#", title);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showSMSCallOrderDialog() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16,16,16,16);

        layout.addView(createOrderButton("Call All Then SMS All", "*OC#"));
        layout.addView(createOrderButton("SMS and Call Each Mobile Number", "*OB#"));
        layout.addView(createOrderButton("SMS All Then Call All", "*OA#"));

        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(getContext())
                .setView(layout)
                .create();
        dialog.show();
    }

    private Button createOrderButton(String text, String command) {
        Button btn = new Button(getContext());
        btn.setText(text);
        btn.setBackgroundTintList(getResources().getColorStateList(R.color.dark_blue));
        btn.setTextColor(getResources().getColor(android.R.color.white));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,8,0,8);
        btn.setLayoutParams(params);

        btn.setOnClickListener(v -> sendCommand(command, text));
        return btn;
    }

    private void setDeviceDateTime() {
        Calendar calendar = Calendar.getInstance(); // Mobile date & time


        String formatted = String.format("%02d%02d%02d%02d%02d",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR) % 100,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE));

        String command = "*CS" + formatted + "#";
        sendCommand(command, "Set Date/Time");
    }

    interface InputCallback {
        void onInput(String value);
    }
}
