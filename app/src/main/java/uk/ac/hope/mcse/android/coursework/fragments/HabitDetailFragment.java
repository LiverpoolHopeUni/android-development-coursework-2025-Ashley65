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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_habit_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize repository
        repository = new HabitRepository(requireActivity().getApplication());

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
