package uk.ac.hope.mcse.android.coursework.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import uk.ac.hope.mcse.android.coursework.R;
import uk.ac.hope.mcse.android.coursework.models.Habit;
import uk.ac.hope.mcse.android.coursework.repositories.HabitRepository;
import uk.ac.hope.mcse.android.coursework.utils.ReminderReceiver;

public class HabitDetailFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_habit_detail, container, false);
    }
    private uk.ac.hope.mcse.android.coursework.utils.ReminderReceiver ReminderReceiver = new uk.ac.hope.mcse.android.coursework.utils.ReminderReceiver();


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize repository
        repository = new HabitRepository(requireActivity().getApplication());


        statsDetailCard = view.findViewById(R.id.stats_detail_card);
        streakText = view.findViewById(R.id.streak_text);
        adherenceRateText = view.findViewById(R.id.adherence_rate_text);
        adherenceProgressBar = view.findViewById(R.id.adherence_progress);
        completionChart = view.findViewById(R.id.completion_chart);


        // Initialize UI components
        titleEdit = view.findViewById(R.id.title_edit);
        descriptionEdit = view.findViewById(R.id.description_edit);
        frequencyEdit = view.findViewById(R.id.frequency_edit);
        frequencyTypeDropdown = view.findViewById(R.id.frequency_type_dropdown);
        createdDateText = view.findViewById(R.id.created_date_text);
        completedCountText = view.findViewById(R.id.completed_count_text);
        lastCompletedText = view.findViewById(R.id.last_completed_text);
        saveButton = view.findViewById(R.id.save_button);
        deleteButton = view.findViewById(R.id.delete_button);

        // Set up frequency type dropdown
        String[] frequencyTypes = {"Daily", "Weekly"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                frequencyTypes
        );
        frequencyTypeDropdown.setAdapter(adapter);

        Button reminderSettingsButton = view.findViewById(R.id.reminderSettingsButton);
        reminderSettingsButton.setOnClickListener(v -> {
            if (currentHabit != null) {
                Bundle bundle = new Bundle();
                bundle.putString("habitId", currentHabit.getId());
                Navigation.findNavController(v).navigate(
                        R.id.action_habitDetailFragment_to_reminderSettingsFragment, bundle);
            }
        });


        Button testNotificationButton = view.findViewById(R.id.testNotificationButton);
        if (testNotificationButton != null) {
            testNotificationButton.setOnClickListener(v -> testNotification());
        }

        // Get habit ID from arguments if editing an existing habit
        if (getArguments() != null && getArguments().containsKey("habitId")) {
            habitId = getArguments().getString("habitId");
            loadHabit(habitId);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            // New habit, hide delete button
            deleteButton.setVisibility(View.GONE);

            // Hide statistics card for new habits
            view.findViewById(R.id.stats_card).setVisibility(View.GONE);
        }

        // Set up button click listeners
        saveButton.setOnClickListener(v -> saveHabit());
        deleteButton.setOnClickListener(v -> deleteHabit());
    }
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
    private TextInputEditText titleEdit;
    private TextInputEditText descriptionEdit;
    private TextInputEditText frequencyEdit;
    private AutoCompleteTextView frequencyTypeDropdown;
    private TextView createdDateText;
    private TextView completedCountText;
    private TextView lastCompletedText;
    private Button saveButton;
    private Button deleteButton;

    private HabitRepository repository;
    private String habitId;
    private Habit currentHabit;

    private CardView statsDetailCard;
    private TextView streakText;
    private TextView adherenceRateText;
    private LinearProgressIndicator adherenceProgressBar;
    private LineChart completionChart;



    private void testNotification() {
        if (currentHabit == null) {
            Toast.makeText(requireContext(), "Please save the habit first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current time plus 15 seconds
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 15); // Set notification for 15 seconds from now

        // Set reminder properties on the habit
        currentHabit.setReminderEnabled(true);
        currentHabit.setReminderTime(calendar.getTimeInMillis());

        // Save the habit
        repository.updateHabit(currentHabit);

        // Schedule notification
        uk.ac.hope.mcse.android.coursework.utils.ReminderReceiver.scheduleReminder(
                requireContext(),
                currentHabit,
                calendar.getTimeInMillis()
        );

        Toast.makeText(requireContext(),
                "Notification scheduled for 15 seconds from now",
                Toast.LENGTH_LONG).show();
    }
    private void loadHabit(String id) {
        repository.getHabit(id, habit -> {
            if (habit != null) {
                currentHabit = habit;
                requireActivity().runOnUiThread(() -> populateFields(habit));
            }
        });
    }

    private void populateFields(Habit habit) {
        titleEdit.setText(habit.getTitle());
        descriptionEdit.setText(habit.getDescription());
        frequencyEdit.setText(String.valueOf(habit.getFrequency()));
        frequencyTypeDropdown.setText(habit.isDaily() ? "Daily" : "Weekly", false);

        createdDateText.setText("Created on: " + dateFormat.format(habit.getCreatedDate()));
        completedCountText.setText("Completed: " + habit.getCompletedCount() + " times");

        if (habit.getLastCompletedDate() != null) {
            lastCompletedText.setText("Last completed: " + dateFormat.format(habit.getLastCompletedDate()));
            lastCompletedText.setVisibility(View.VISIBLE);
        } else {
            lastCompletedText.setVisibility(View.GONE);
        }

        // Update statistics UI
        updateStatisticsUI(habit);

    }
    private void updateStatisticsUI(Habit habit) {
        // Only show detailed stats if the habit has been completed at least once
        if (habit.getCompletedCount() > 0) {
            statsDetailCard.setVisibility(View.VISIBLE);

            // Update streak information
            streakText.setText("Current streak: " + habit.getCurrentStreak() + " days");

            // Calculate adherence rate
            int adherenceRate = calculateAdherenceRate(habit);
            adherenceRateText.setText("Adherence rate: " + adherenceRate + "%");
            adherenceProgressBar.setProgress(adherenceRate);

            // Set up completion trend chart
            setupCompletionChart(habit);
        } else {
            statsDetailCard.setVisibility(View.GONE);
        }
    }

    private int calculateAdherenceRate(Habit habit) {
        // Calculate days since creation
        Date now = new Date();
        long diffInMillis = now.getTime() - habit.getCreatedDate().getTime();
        long daysSinceCreation = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

        if (daysSinceCreation == 0) return 100; // Just created today

        // Calculate expected completions based on frequency and type (daily/weekly)
        int expectedCompletions;
        if (habit.isDaily()) {
            expectedCompletions = (int) (daysSinceCreation * habit.getFrequency());
        } else {
            // Weekly habit
            long weeksSinceCreation = daysSinceCreation / 7;
            expectedCompletions = (int) (weeksSinceCreation * habit.getFrequency());
        }

        if (expectedCompletions == 0) return 100; // Not enough time has passed for any expected completions

        // Calculate adherence rate
        return Math.min(100, (habit.getCompletedCount() * 100) / expectedCompletions);
    }

    private void setupCompletionChart(Habit habit) {
        // Generate sample data for the last 7 days
        List<Entry> entries = new ArrayList<>();
        List<String> dateLabels = new ArrayList<>();

        // Get completion data (for a real implementation, you would retrieve actual completion history)
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat labelFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());

        // Create sample data for the last 7 days
        for (int i = 6; i >= 0; i--) {
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, -i);
            dateLabels.add(labelFormat.format(cal.getTime()));

            // This is where you would use actual completion data
            // For now, we'll generate a simple pattern based on the current streak
            boolean completed = i % 2 == 0 && i <= habit.getCurrentStreak();
            entries.add(new Entry(6-i, completed ? 1 : 0));
        }

        // Create the dataset
        LineDataSet dataSet = new LineDataSet(entries, "Completions");
        dataSet.setColor(Color.GREEN);
        dataSet.setCircleColor(Color.GREEN);
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawValues(false);
        dataSet.setCircleRadius(4f);

        // Apply the data to the chart
        LineData lineData = new LineData(dataSet);
        completionChart.setData(lineData);

        // Customize the chart appearance
        completionChart.getDescription().setEnabled(false);
        completionChart.getLegend().setEnabled(false);
        completionChart.setTouchEnabled(false);

        // Customize the axes
        XAxis xAxis = completionChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dateLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        completionChart.getAxisRight().setEnabled(false);
        completionChart.getAxisLeft().setAxisMaximum(1.2f);
        completionChart.getAxisLeft().setAxisMinimum(0f);
        completionChart.getAxisLeft().setDrawLabels(false);
        completionChart.getAxisLeft().setDrawGridLines(false);

        completionChart.invalidate(); // Refresh the chart
    }

    private void saveHabit() {
        String title = titleEdit.getText().toString().trim();
        String description = descriptionEdit.getText() != null ? descriptionEdit.getText().toString().trim() : "";
        String frequencyStr = frequencyEdit.getText() != null ? frequencyEdit.getText().toString().trim() : "";
        String frequencyType = frequencyTypeDropdown.getText() != null ? frequencyTypeDropdown.getText().toString().trim() : "";

        // Validate inputs
        if (title.isEmpty() || frequencyStr.isEmpty() || frequencyType.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int frequency;
        try {
            frequency = Integer.parseInt(frequencyStr);
            if (frequency <= 0) {
                Toast.makeText(getContext(), "Frequency must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter a valid number for frequency", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isDaily = frequencyType.equals("Daily");

        if (currentHabit != null) {
            // Update existing habit
            currentHabit.setTitle(title);
            currentHabit.setDescription(description);
            currentHabit.setFrequency(frequency);
            currentHabit.setDaily(isDaily);
            repository.updateHabit(currentHabit);
        } else {
            // Create new habit
            Habit newHabit = new Habit(title, description, frequency, isDaily);
            repository.addHabit(newHabit);
        }

        Toast.makeText(getContext(), "Habit saved", Toast.LENGTH_SHORT).show();

        // Send fragment result to notify list fragment that data has changed
        getParentFragmentManager().setFragmentResult("habitUpdated", new Bundle());

        // Navigate back
        Navigation.findNavController(requireView()).navigateUp();
    }

    private void deleteHabit() {
        if (currentHabit != null) {
            repository.deleteHabit(currentHabit.getId());
            Toast.makeText(getContext(), "Habit deleted", Toast.LENGTH_SHORT).show();

            // Send fragment result to notify list fragment that data has changed
            getParentFragmentManager().setFragmentResult("habitUpdated", new Bundle());

            Navigation.findNavController(requireView()).navigateUp();
        }
    }
}
