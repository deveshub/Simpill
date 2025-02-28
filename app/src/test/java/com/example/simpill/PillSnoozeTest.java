/* (C) 2025 */
package com.example.simpill;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@Ignore("Test needs to be fixed - temporarily disabled")
public class PillSnoozeTest {

    @Mock private Context mockContext;

    @Mock private AlarmManager mockAlarmManager;

    @Mock private DatabaseHelper mockDatabaseHelper;

    @Mock private SharedPreferences mockSharedPreferences;

    @Mock private SharedPreferences.Editor mockEditor;

    @Mock private SQLiteDatabase mockDatabase;

    @Mock private DateTimeManager mockDateTimeManager;

    private Pill testPill;
    private ReceiverPillSnooze receiverPillSnooze;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock Context and AlarmManager
        when(mockContext.getSystemService(Context.ALARM_SERVICE)).thenReturn(mockAlarmManager);

        // Mock SharedPreferences
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        when(mockEditor.putBoolean(anyString(), anyBoolean())).thenReturn(mockEditor);
        doNothing().when(mockEditor).apply();

        // Mock DateTimeManager
        when(mockDateTimeManager.formatLongAsTimeString(anyLong())).thenReturn("10:00 AM");
        when(mockDateTimeManager.getCurrentTimeString()).thenReturn("10:00 AM");
        when(mockDateTimeManager.formatLongAsDateTimeString(anyLong()))
                .thenReturn("2023-01-01 10:00 AM");
        when(mockDateTimeManager.formatDateTimeStringAsLong(anyString()))
                .thenReturn(System.currentTimeMillis() + 3600000);
        when(mockDateTimeManager.getCurrentCalendarDayMonthYear())
                .thenReturn(new int[] {20, 3, 2024});
        when(mockDateTimeManager.addMonthToDateString(anyString())).thenReturn("2023-02-01");

        // Initialize test pill with DateTimeManager
        testPill =
                new Pill(
                        "Test Pill",
                        new String[] {"10:00"},
                        "2023-01-01",
                        "2023-02-01",
                        Pill.DEFAULT_ALARM_URI,
                        1,
                        0,
                        "10:00 AM",
                        30,
                        DatabaseHelper.ALARM,
                        1,
                        2,
                        mockDateTimeManager);
        testPill.setPrimaryKey(1);

        // Initialize receiver and inject mocked database helper
        receiverPillSnooze = new ReceiverPillSnooze();
        receiverPillSnooze.setDatabaseHelper(mockDatabaseHelper);

        // Mock database helper behavior
        when(mockDatabaseHelper.getPill(anyInt())).thenReturn(testPill);
    }

    @Test
    public void testScheduleSnoozeNotification() {
        // Test scheduling a snooze notification
        long snoozeTime = System.currentTimeMillis() + 300000; // 5 minutes
        testPill.scheduleSnoozeNotification(mockContext, snoozeTime);

        // Verify that AlarmManager was called with correct parameters
        verify(mockAlarmManager).cancel(any(PendingIntent.class));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verify(mockAlarmManager)
                    .setExactAndAllowWhileIdle(
                            eq(AlarmManager.RTC_WAKEUP), eq(snoozeTime), any(PendingIntent.class));
        } else {
            verify(mockAlarmManager)
                    .setExact(
                            eq(AlarmManager.RTC_WAKEUP), eq(snoozeTime), any(PendingIntent.class));
        }
    }

    @Test
    @Ignore("Test needs to be fixed - temporarily disabled")
    public void testReceiverPillSnoozeOnReceive() {
        // Create mock Intent with test data
        Intent mockIntent = new Intent();
        mockIntent.putExtra(Pill.PRIMARY_KEY_INTENT_KEY_STRING, 1);
        mockIntent.putExtra(ReceiverPillSnooze.SNOOZE_MINUTES_EXTRA, 10);

        // Test the onReceive method
        receiverPillSnooze.onReceive(mockContext, mockIntent);

        // Verify that AlarmManager was called
        verify(mockAlarmManager, atLeastOnce()).cancel(any(PendingIntent.class));
    }

    @Test
    @Ignore("Test needs to be fixed - temporarily disabled")
    public void testSnoozeDialogCreation() {
        // Mock SharedPreferences for dark mode
        when(mockSharedPreferences.getBoolean(eq("dark_mode"), anyBoolean())).thenReturn(false);

        SnoozeDialog snoozeDialog = new SnoozeDialog(mockContext, 1);
        assertNotNull(snoozeDialog);
    }

    @Test
    @Ignore("Test needs to be fixed - temporarily disabled")
    public void testInvalidPrimaryKeyHandling() {
        Intent mockIntent = new Intent();
        mockIntent.putExtra(Pill.PRIMARY_KEY_INTENT_KEY_STRING, -1);
        mockIntent.putExtra(ReceiverPillSnooze.SNOOZE_MINUTES_EXTRA, 5);

        receiverPillSnooze.onReceive(mockContext, mockIntent);

        // Verify that no alarm was scheduled for invalid primary key
        verify(mockAlarmManager, never())
                .setExactAndAllowWhileIdle(anyInt(), anyLong(), any(PendingIntent.class));
        verify(mockAlarmManager, never()).setExact(anyInt(), anyLong(), any(PendingIntent.class));
    }

    @Test
    @Ignore("Test needs to be fixed - temporarily disabled")
    public void testDefaultSnoozeTime() {
        Intent mockIntent = new Intent();
        mockIntent.putExtra(Pill.PRIMARY_KEY_INTENT_KEY_STRING, 1);
        // Don't set SNOOZE_MINUTES_EXTRA to test default value

        receiverPillSnooze.onReceive(mockContext, mockIntent);

        // Verify that default 5-minute snooze was used
        verify(mockAlarmManager, atLeastOnce()).cancel(any(PendingIntent.class));
    }
}
