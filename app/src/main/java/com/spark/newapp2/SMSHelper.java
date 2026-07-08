package com.spark.newapp2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class SMSHelper {

    public static void openSMSApp(Activity activity, String command, String deviceNumber) {
        if (deviceNumber == null || deviceNumber.isEmpty()) {
            Toast.makeText(activity, "No device number found!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (command == null || command.isEmpty()) {
            Toast.makeText(activity, "No command to send!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + deviceNumber)); // do NOT encode
            intent.putExtra("sms_body", command);

            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivity(intent);
                return;
            }


            Intent fallback = new Intent(Intent.ACTION_VIEW);
            fallback.setData(Uri.parse("sms:" + deviceNumber));
            fallback.putExtra("sms_body", command);

            if (fallback.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivity(fallback);
            } else {
                Toast.makeText(activity, "No SMS app found on this device", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "Failed to open SMS app: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
