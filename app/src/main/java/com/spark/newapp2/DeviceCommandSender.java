package com.spark.newapp2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class DeviceCommandSender {

    public static void sendCommand(Context context, String deviceNumber, String command) {
        if (deviceNumber.isEmpty()) {
            Toast.makeText(context, "Device not selected!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + deviceNumber));
            intent.putExtra("sms_body", command);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
                

                saveCommandToHistory(context, command);
            } else {
                Toast.makeText(context, "No SMS app found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private static void saveCommandToHistory(Context context, String command) {
        new Thread(() -> {
            try {
                CommandHistory history = new CommandHistory(command, System.currentTimeMillis());
                HistoryDatabase.getInstance(context)
                        .historyDao()
                        .insert(history);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
