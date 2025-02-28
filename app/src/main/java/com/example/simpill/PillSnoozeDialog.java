/* (C) 2025 */
package com.example.simpill;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;

public class PillSnoozeDialog extends Dialog {
    private static final int[] SNOOZE_DURATIONS = {5, 10, 15, 30, 60}; // in minutes
    private final Pill pill;
    private final Context context;

    public PillSnoozeDialog(@NonNull Context context, Pill pill) {
        super(context);
        this.context = context;
        this.pill = pill;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_snooze);

        LinearLayout snoozeButtonsContainer = findViewById(R.id.snooze_buttons_container);
        EditText customMinutesInput = findViewById(R.id.custom_minutes_input);
        Button customSnoozeButton = findViewById(R.id.custom_snooze_button);

        // Add predefined snooze duration buttons
        for (int duration : SNOOZE_DURATIONS) {
            Button button = new Button(context);
            button.setText(context.getString(R.string.snooze_minutes, duration));
            button.setOnClickListener(v -> handleSnooze(duration));
            snoozeButtonsContainer.addView(button);
        }

        // Handle custom snooze duration
        customSnoozeButton.setOnClickListener(
                v -> {
                    String input = customMinutesInput.getText().toString();
                    if (!TextUtils.isEmpty(input)) {
                        try {
                            int customDuration = Integer.parseInt(input);
                            if (customDuration > 0) {
                                handleSnooze(customDuration);
                            }
                        } catch (NumberFormatException e) {
                            // Handle invalid input
                            customMinutesInput.setError("Please enter a valid number");
                        }
                    }
                });
    }

    private void handleSnooze(int durationMinutes) {
        long snoozeTime = System.currentTimeMillis() + (durationMinutes * 60 * 1000);
        pill.scheduleSnoozeNotification(context, snoozeTime);
        dismiss();
    }
}
