package uk.ac.hope.mcse.android.coursework.fragments;

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
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Locale;

import uk.ac.hope.mcse.android.coursework.R;
import uk.ac.hope.mcse.android.coursework.models.Habit;
import uk.ac.hope.mcse.android.coursework.repositories.HabitRepository;

public class HabitDetailFragment extends Fragment {

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
    private Habit currentHabit;
    private String habitId;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_habit_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new HabitRepository(); // In a real app, this would be injected or retrieved from a singleton

        // Initialize views
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
        String[] frequencyTypes = new String[]{"Daily", "Weekly"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, frequencyTypes);
        frequencyTypeDropdown.setAdapter(adapter);

        // Get habit ID from arguments if editing an existing habit
        if (getArguments() != null && getArguments().containsKey("habitId")) {
            habitId = getArguments().getString("habitId");
            currentHabit = repository.getHabit(habitId);

            if (currentHabit != null) {
                populateFields(currentHabit);
                deleteButton.setVisibility(View.VISIBLE);
            } else {
                // Handle error - habit not found
                Toast.makeText(getContext(), "Habit not found", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).navigateUp();
            }
        } else {
            // New habit
            deleteButton.setVisibility(View.GONE);
            createdDateText.setText("Not created yet");
            completedCountText.setText("Completed: 0 times");
            lastCompletedText.setText("Not completed yet");
        }

        // Set up button click listeners
        saveButton.setOnClickListener(v -> saveHabit());
        deleteButton.setOnClickListener(v -> deleteHabit());
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
        } else {
            lastCompletedText.setText("Not completed yet");
        }
    }

    private void saveHabit() {
        String title = titleEdit.getText() != null ? titleEdit.getText().toString().trim() : "";
        String description = descriptionEdit.getText() != null ? descriptionEdit.getText().toString().trim() : "";
        String frequencyStr = frequencyEdit.getText() != null ? frequencyEdit.getText().toString().trim() : "";
        String frequencyType = frequencyTypeDropdown.getText() != null ? frequencyTypeDropdown.getText().toString() : "";

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

        // Navigate back
        Navigation.findNavController(requireView()).navigateUp();
        Toast.makeText(getContext(), "Habit saved", Toast.LENGTH_SHORT).show();
    }

    private void deleteHabit() {
        if (currentHabit != null) {
            repository.deleteHabit(currentHabit.getId());
            Navigation.findNavController(requireView()).navigateUp();
            Toast.makeText(getContext(), "Habit deleted", Toast.LENGTH_SHORT).show();
        }
    }
}