/* (C) 2022 */
package com.example.simpill;

import static com.example.simpill.Pill.PRIMARY_KEY_INTENT_KEY_STRING;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverPillSnooze extends BroadcastReceiver {
    public static final String SNOOZE_MINUTES_EXTRA = "snooze_minutes";
    private DatabaseHelper databaseHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        int primaryKey = intent.getIntExtra(PRIMARY_KEY_INTENT_KEY_STRING, -1);
        int snoozeMinutes = intent.getIntExtra(SNOOZE_MINUTES_EXTRA, 5); // Default 5 minutes

        if (primaryKey != -1) {
            if (databaseHelper == null) {
                databaseHelper = new DatabaseHelper(context);
            }
            Pill pill = databaseHelper.getPill(primaryKey);

            // Schedule a new notification after snooze interval
            long snoozeTime = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000L);
            pill.scheduleSnoozeNotification(context, snoozeTime);
        }
    }

    // For testing purposes only
    void setDatabaseHelper(DatabaseHelper helper) {
        this.databaseHelper = helper;
    }
}
