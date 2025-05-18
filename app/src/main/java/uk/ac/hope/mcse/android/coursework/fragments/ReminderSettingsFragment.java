package uk.ac.hope.mcse.android.coursework.fragments;

import android.app.AlarmManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import uk.ac.hope.mcse.android.coursework.R;
import uk.ac.hope.mcse.android.coursework.models.Habit;
import uk.ac.hope.mcse.android.coursework.repositories.HabitRepository;
import uk.ac.hope.mcse.android.coursework.utils.ReminderReceiver;

public class ReminderSettingsFragment extends Fragment {
    private Habit currentHabit;
    private String habitId;
    private HabitRepository repository;

    private Switch reminderSwitch;
    private TextView timeTextView;
    private Button setTimeButton;
    private Button saveButton;

    private Calendar reminderCalendar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new HabitRepository(requireActivity().getApplication());

        if (getArguments() != null) {
            habitId = getArguments().getString("habitId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder_settings, container, false);

        reminderSwitch = view.findViewById(R.id.reminderSwitch);
        timeTextView = view.findViewById(R.id.timeTextView);
        setTimeButton = view.findViewById(R.id.setTimeButton);
        saveButton = view.findViewById(R.id.saveButton);

        reminderCalendar = Calendar.getInstance();

        if (habitId != null) {
            loadHabitDetails();
        }

        setupListeners();

        return view;
    }

    private void loadHabitDetails() {
        repository.getHabit(habitId, habit -> {
            if (habit != null && isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    currentHabit = habit;

                    // Set UI based on habit reminder settings
                    reminderSwitch.setChecked(habit.isReminderEnabled());

                    if (habit.getReminderTime() > 0) {
                        reminderCalendar.setTimeInMillis(habit.getReminderTime());
                        updateTimeDisplay();
                    }

                    updateUIState();
                });
            }
        });
    }

    private void setupListeners() {
        reminderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> updateUIState());

        setTimeButton.setOnClickListener(v -> showTimePickerDialog());

        saveButton.setOnClickListener(v -> saveReminderSettings());
    }

    private void updateUIState() {
        boolean enabled = reminderSwitch.isChecked();
        setTimeButton.setEnabled(enabled);
        timeTextView.setEnabled(enabled);
    }

    private void showTimePickerDialog() {
        int hour = reminderCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = reminderCalendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, selectedMinute) -> {
                    reminderCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    reminderCalendar.set(Calendar.MINUTE, selectedMinute);
                    reminderCalendar.set(Calendar.SECOND, 0);

                    updateTimeDisplay();
                },
                hour, minute, true);

        timePickerDialog.show();
    }

    private void updateTimeDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        timeTextView.setText(sdf.format(reminderCalendar.getTime()));
    }

    private void saveReminderSettings() {
        if (currentHabit == null) return;

        boolean enabled = reminderSwitch.isChecked();
        currentHabit.setReminderEnabled(enabled);

        if (enabled) {
            // Get current time for setting the next alarm
            Calendar now = Calendar.getInstance();
            reminderCalendar.set(Calendar.YEAR, now.get(Calendar.YEAR));
            reminderCalendar.set(Calendar.MONTH, now.get(Calendar.MONTH));
            reminderCalendar.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));

            // If time for today has already passed, set for tomorrow
            if (reminderCalendar.before(now)) {
                reminderCalendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            currentHabit.setReminderTime(reminderCalendar.getTimeInMillis());

            // Schedule the reminder
            ReminderReceiver.scheduleReminder(
                    requireContext(),
                    currentHabit,
                    reminderCalendar.getTimeInMillis());
        } else {
            // Cancel existing reminder
            ReminderReceiver.cancelReminder(requireContext(), currentHabit.getId());
        }

        // Save habit with updated reminder settings
        repository.updateHabit(currentHabit);

        Toast.makeText(requireContext(), "Reminder settings saved", Toast.LENGTH_SHORT).show();
    }

    // Check if exact alarms are allowed
    private boolean canScheduleExactAlarms() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
            return alarmManager.canScheduleExactAlarms();
        }
        return true;
    }
}