/* (C) 2022 */
package com.example.simpill;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.appcompat.content.res.AppCompatResources;

public class SnoozeDialog {
    private final Context context;
    private final int primaryKey;
    private final int[] snoozeIntervals = {5, 10, 15, 30, 45, 60, 120};

    public SnoozeDialog(Context context, int primaryKey) {
        this.context = context;
        this.primaryKey = primaryKey;
    }

    public Dialog create() {
        Dialog dialog = new Dialog(context);
        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.dialog_snooze, null);
        dialog.setContentView(dialogLayout);

        LinearLayout buttonContainer = dialogLayout.findViewById(R.id.snooze_buttons_container);
        EditText customMinutesInput = dialogLayout.findViewById(R.id.custom_minutes_input);
        Button customSnoozeButton = dialogLayout.findViewById(R.id.custom_snooze_button);

        // Add preset interval buttons
        for (int interval : snoozeIntervals) {
            Button button = new Button(context);
            button.setText(context.getString(R.string.snooze_minutes, interval));
            button.setOnClickListener(v -> handleSnooze(interval));

            // Apply theme-appropriate styling
            if (isDarkMode(context)) {
                button.setBackground(
                        AppCompatResources.getDrawable(context, R.drawable.dialog_bottom_btn_dark));
            } else {
                button.setBackground(
                        AppCompatResources.getDrawable(
                                context, R.drawable.dialog_bottom_btn_purple));
            }

            buttonContainer.addView(button);
        }

        // Handle custom interval
        customSnoozeButton.setOnClickListener(
                v -> {
                    try {
                        int minutes = Integer.parseInt(customMinutesInput.getText().toString());
                        if (minutes > 0) {
                            handleSnooze(minutes);
                        }
                    } catch (NumberFormatException ignored) {
                        // Invalid input, do nothing
                    }
                });

        return dialog;
    }

    private void handleSnooze(int minutes) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        Pill pill = databaseHelper.getPill(primaryKey);
        long snoozeTime = System.currentTimeMillis() + (minutes * 60 * 1000L);
        pill.scheduleSnoozeNotification(context, snoozeTime);
    }

    private boolean isDarkMode(Context context) {
        SharedPrefs sharedPrefs = new SharedPrefs(context);
        return sharedPrefs.getDarkModePref();
    }
}
