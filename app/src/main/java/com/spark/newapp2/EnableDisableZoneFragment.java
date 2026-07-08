package com.spark.newapp2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EnableDisableZoneFragment extends Fragment {

    private Spinner spinnerZone;
    private Button btnEnableZone, btnDisableZone, btnEnableSounder, btnDisableSounder;
    private String deviceNumber = "";
    private String deviceModel = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_enable_disable_zone_fragment, container, false);

        spinnerZone = view.findViewById(R.id.spinner_zone);
        btnEnableZone = view.findViewById(R.id.btn_enable_zone);
        btnDisableZone = view.findViewById(R.id.btn_disable_zone);
        btnEnableSounder = view.findViewById(R.id.btn_enable_sounder);
        btnDisableSounder = view.findViewById(R.id.btn_disable_sounder);

        SharedPreferences prefs = requireContext().getSharedPreferences("MySecuronPrefs", Context.MODE_PRIVATE);
        deviceNumber = prefs.getString("device_number", "");
        deviceModel = prefs.getString("selected_device_model", "SPYG2");

        String[] zones = getZonesForDevice(deviceModel);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, zones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerZone.setAdapter(adapter);

        btnEnableZone.setOnClickListener(v -> sendSelectedZoneCommand(true));
        btnDisableZone.setOnClickListener(v -> sendSelectedZoneCommand(false));
        btnEnableSounder.setOnClickListener(v -> sendSounderCommand(true));
        btnDisableSounder.setOnClickListener(v -> sendSounderCommand(false));

        return view;
    }

    private String[] getZonesForDevice(String model) {
        switch (model) {
            case "SPYG2": return new String[]{"Zone 1", "Zone 2"};
            case "SPYG4": return new String[]{"Shutter / Door Zone", "Panic Zone", "Motion Zone", "Smoke Zone"};
            case "SPYG8": return new String[]{"Zone 1", "Zone 2", "Zone 3", "Zone 4", "Motion Zone", "Panic Zone", "Smoke/Fire Zone"};
            default: return new String[]{"Zone 1", "Zone 2"};
        }
    }

    private void sendSelectedZoneCommand(boolean enable) {
        if (!checkDeviceNumber()) return;
        String zone = spinnerZone.getSelectedItem().toString();
        String command = getZoneCommand(zone, enable, deviceModel);
        String historyText = (enable ? "Enabled " : "Disabled ") + zone;
        sendSMS(command, enable ? "Enable Zone" : "Disable Zone", historyText);
    }

    private String getZoneCommand(String zone, boolean enable, String model) {
        switch (model) {
            case "SPYG2":
                if (zone.equals("Zone 1")) return enable ? "*ZZA#" : "*ZZD#";
                if (zone.equals("Zone 2")) return enable ? "*ZPA#" : "*ZPD#";
                break;
            case "SPYG4":
                if (zone.equals("Shutter / Door Zone")) return enable ? "*ZZA#" : "*ZZD#";
                if (zone.equals("Panic Zone")) return enable ? "*ZPA#" : "*ZPD#";
                if (zone.equals("Motion Zone")) return enable ? "*ZMA#" : "*ZMD#";
                if (zone.equals("Smoke Zone")) return enable ? "*ZSA#" : "*ZSD#";
                break;
            case "SPYG8":
                switch (zone) {
                    case "Zone 1": return enable ? "*Z1A#" : "*Z1D#";
                    case "Zone 2": return enable ? "*Z2A#" : "*Z2D#";
                    case "Zone 3": return enable ? "*Z3A#" : "*Z3D#";
                    case "Zone 4": return enable ? "*Z4A#" : "*Z4D#";
                    case "Motion Zone": return enable ? "*ZMA#" : "*ZMD#";
                    case "Panic Zone": return enable ? "*ZPA#" : "*ZPD#";
                    case "Smoke/Fire Zone": return enable ? "*ZSA#" : "*ZSD#";
                }
                break;
        }
        return "";
    }

    private void sendSounderCommand(boolean enable) {
        String command = enable ? "*ON#" : "*OF#";
        String historyText = enable ? "Enabled Sounder" : "Disabled Sounder";
        sendSMS(command, enable ? "Enable Sounder" : "Disable Sounder", historyText);
    }

    private boolean checkDeviceNumber() {
        if (deviceNumber.isEmpty()) {
            Toast.makeText(getContext(), "Set Device Number first!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void sendSMS(String command, String toastMessage, String historyText) {
        if (!checkDeviceNumber()) return;

        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + Uri.encode(deviceNumber)));
            intent.putExtra("sms_body", command);

            startActivity(intent);
            Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();


            String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    .format(new Date());
            String historyMessage = timestamp + " => " + historyText;

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
}
