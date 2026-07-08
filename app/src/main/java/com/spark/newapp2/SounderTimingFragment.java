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

public class SounderTimingFragment extends Fragment {

    private Spinner spSounder;
    private Button btnSet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_sounder_timing_fragment, container, false);

        spSounder = view.findViewById(R.id.sp_sounder_timing);
        btnSet = view.findViewById(R.id.btn_set_sounder);

        setupSpinner();

        btnSet.setOnClickListener(v -> openSmsApp());

        return view;
    }

    private void setupSpinner() {
        ArrayList<String> sounderTimes = new ArrayList<>();
        for (int i = 2; i <= 28; i += 2) sounderTimes.add(i + " min");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, sounderTimes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSounder.setAdapter(adapter);
    }

    private String convertTimeToCommand(int minutes) {
        int index = (minutes / 2) - 1;
        return String.valueOf((char) ('A' + index));
    }

    private void openSmsApp() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MySecuronPrefs", Context.MODE_PRIVATE);
        String deviceNumber = prefs.getString("device_number", "");

        if (deviceNumber == null || deviceNumber.isEmpty()) {
            Toast.makeText(getActivity(), "Please select a device first", Toast.LENGTH_SHORT).show();
            return;
        }

        int minutes = Integer.parseInt(spSounder.getSelectedItem().toString().replace(" min", ""));
        String commandLetter = convertTimeToCommand(minutes);
        String command = "**0" + commandLetter + "#";

        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + Uri.encode(deviceNumber)));
            intent.putExtra("sms_body", command);
            startActivity(intent);  // opens SMS app
            Toast.makeText(getActivity(), "Sounder timing set", Toast.LENGTH_SHORT).show();


            saveCommandToHistory(command, minutes);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "No SMS app found", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveCommandToHistory(String command, int minutes) {
        new Thread(() -> {
            try {
                String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                        .format(new Date());


                String historyMessage = timestamp + " => Sounder timing set to " + minutes + " minutes";

                CommandHistory history = new CommandHistory(historyMessage, System.currentTimeMillis());
                HistoryDatabase.getInstance(requireContext())
                        .historyDao()
                        .insert(history);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
