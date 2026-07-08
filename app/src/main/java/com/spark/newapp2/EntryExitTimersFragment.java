package com.spark.newapp2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EntryExitTimersFragment extends Fragment {

    private Spinner spEntry, spExit;
    private Button btnSet;
    private String deviceNumber = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_entry_exit_timers_fragment, container, false);

        spEntry = view.findViewById(R.id.sp_entry_time);
        spExit = view.findViewById(R.id.sp_exit_time);
        btnSet = view.findViewById(R.id.btn_set_time);

        setupSpinners();

        SharedPreferences prefs = requireContext().getSharedPreferences("MySecuronPrefs", Context.MODE_PRIVATE);
        deviceNumber = prefs.getString("device_number", "");

        btnSet.setOnClickListener(v -> openSmsApp());

        return view;
    }

    private void setupSpinners() {
        ArrayList<String> entryTimes = new ArrayList<>();
        for (int i = 0; i <= 120; i += 10) entryTimes.add(String.valueOf(i));
        ArrayAdapter<String> entryAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, entryTimes);
        entryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEntry.setAdapter(entryAdapter);

        ArrayList<String> exitTimes = new ArrayList<>();
        for (int i = 60; i <= 180; i += 10) exitTimes.add(String.valueOf(i));
        ArrayAdapter<String> exitAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, exitTimes);
        exitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spExit.setAdapter(exitAdapter);
    }

    private String convertEntryTimeToCommand(String timeStr) {
        int val = Integer.parseInt(timeStr);
        if (val == 0) return "0";
        int index = (val / 10) - 1;
        return String.valueOf((char) ('A' + index));
    }

    private String convertExitTimeToCommand(String timeStr) {
        int val = Integer.parseInt(timeStr);
        if (val == 60) return "0";
        int index = (val - 70) / 10;
        return String.valueOf((char) ('A' + index));
    }

    private void openSmsApp() {
        if (deviceNumber == null || deviceNumber.isEmpty()) {
            Toast.makeText(getActivity(), "Please select a device first", Toast.LENGTH_SHORT).show();
            return;
        }

        String entryTime = spEntry.getSelectedItem().toString();
        String exitTime = spExit.getSelectedItem().toString();

        String entryCommand = convertEntryTimeToCommand(entryTime);
        String exitCommand = convertExitTimeToCommand(exitTime);
        String command = "*0" + entryCommand + exitCommand + "#";

        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + Uri.encode(deviceNumber)));
            intent.putExtra("sms_body", command);
            startActivity(intent);  // opens SMS app
            Toast.makeText(getActivity(), "Entry/Exit times set", Toast.LENGTH_SHORT).show();


            String historyMessage = getTimestamp() +
                    " => Entry time " + entryTime + " sec, Exit time " + exitTime + " sec set";
            saveCommandToHistory(historyMessage);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "No SMS app found", Toast.LENGTH_SHORT).show();
        }
    }


    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
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
