package com.spark.newapp2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        Object[] pdus = (Object[]) bundle.get("pdus");
        String format = bundle.getString("format"); // Needed for API 23+

        if (pdus == null || pdus.length == 0) return;

        StringBuilder fullMessage = new StringBuilder();
        String sender = null;

        for (Object pdu : pdus) {
            SmsMessage sms;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                sms = SmsMessage.createFromPdu((byte[]) pdu, format);
            } else {
                sms = SmsMessage.createFromPdu((byte[]) pdu);
            }

            if (sms == null) continue;

            if (sender == null) sender = sms.getOriginatingAddress();
            fullMessage.append(sms.getMessageBody());
        }

        if (sender != null) {
            // Show SMS content
            Toast.makeText(context, "SMS from: " + sender + "\n" + fullMessage.toString(), Toast.LENGTH_LONG).show();

            // TODO: Pass SMS to your app logic
            // Example:
            // if (fullMessage.toString().contains("ARM")) { ... }
        }
    }
}
