package uk.ac.hope.mcse.android.coursework.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.ac.hope.mcse.android.coursework.R;
import uk.ac.hope.mcse.android.coursework.adapters.HabitSelectionAdapter;
import uk.ac.hope.mcse.android.coursework.models.Challenge;
import uk.ac.hope.mcse.android.coursework.models.Habit;
import uk.ac.hope.mcse.android.coursework.repositories.ChallengeRepository;
import uk.ac.hope.mcse.android.coursework.repositories.HabitRepository;

public class
CreateChallengeFragment extends Fragment {

    private EditText nameEdit, descriptionEdit, daysRequiredEdit;
    private TextView startDateText, endDateText;
    private Button startDateButton, endDateButton, saveButton;
    private RecyclerView habitsRecyclerView;
    private HabitSelectionAdapter habitAdapter;

    private ChallengeRepository challengeRepository;
    private HabitRepository habitRepository;

    private Date startDate;
    private Date endDate;
    private ArrayList<String> selectedHabitIds = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        challengeRepository = new ChallengeRepository(requireActivity().getApplication());
        habitRepository = new HabitRepository(requireActivity().getApplication());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_challenge, container, false);

        // Initialize UI components
        nameEdit = view.findViewById(R.id.challengeNameEdit);
        descriptionEdit = view.findViewById(R.id.challengeDescriptionEdit);
        daysRequiredEdit = view.findViewById(R.id.daysRequiredEdit);
        startDateText = view.findViewById(R.id.startDateText);
        endDateText = view.findViewById(R.id.endDateText);
        startDateButton = view.findViewById(R.id.startDateButton);
        endDateButton = view.findViewById(R.id.endDateButton);
        saveButton = view.findViewById(R.id.saveButton);
        habitsRecyclerView = view.findViewById(R.id.habitsRecyclerView);

        // Set default dates
        Calendar calendar = Calendar.getInstance();
        startDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 7);  // Default one week challenge
        endDate = calendar.getTime();

        // Update date displays
        startDateText.setText(dateFormat.format(startDate));
        endDateText.setText(dateFormat.format(endDate));

        // Setup RecyclerView
        habitsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        habitAdapter = new HabitSelectionAdapter(new ArrayList<>(), this::toggleHabitSelection);
        habitsRecyclerView.setAdapter(habitAdapter);

        // Load habits
        loadHabits();

        // Setup listeners
        startDateButton.setOnClickListener(v -> showDatePicker(true));
        endDateButton.setOnClickListener(v -> showDatePicker(false));
        saveButton.setOnClickListener(v -> saveChallenge());

        return view;
    }

    private void loadHabits() {
        habitRepository.getAllHabits(habits -> {
            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    habitAdapter.updateHabits(habits);
                });
            }
        });
    }

    private void toggleHabitSelection(Habit habit, boolean isSelected) {
        if (isSelected) {
            if (!selectedHabitIds.contains(habit.getId())) {
                selectedHabitIds.add(habit.getId());
            }
        } else {
            selectedHabitIds.remove(habit.getId());
        }
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        if (isStartDate && startDate != null) {
            calendar.setTime(startDate);
        } else if (!isStartDate && endDate != null) {
            calendar.setTime(endDate);
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay);

                    if (isStartDate) {
                        startDate = selectedCalendar.getTime();
                        startDateText.setText(dateFormat.format(startDate));
                    } else {
                        endDate = selectedCalendar.getTime();
                        endDateText.setText(dateFormat.format(endDate));
                    }
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void saveChallenge() {
        String name = nameEdit.getText().toString().trim();
        String description = descriptionEdit.getText().toString().trim();
        String daysRequiredStr = daysRequiredEdit.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a challenge name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedHabitIds.isEmpty()) {
            Toast.makeText(getContext(), "Please select at least one habit", Toast.LENGTH_SHORT).show();
            return;
        }

        if (startDate == null || endDate == null) {
            Toast.makeText(getContext(), "Please select start and end dates", Toast.LENGTH_SHORT).show();
            return;
        }

        if (startDate.after(endDate)) {
            Toast.makeText(getContext(), "Start date must be before end date", Toast.LENGTH_SHORT).show();
            return;
        }

        int daysRequired;
        try {
            daysRequired = Integer.parseInt(daysRequiredStr);
            if (daysRequired <= 0) {
                Toast.makeText(getContext(), "Days required must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter a valid number for days required", Toast.LENGTH_SHORT).show();
            return;
        }

        Challenge newChallenge = new Challenge(
                name, description, new ArrayList<>(selectedHabitIds),
                startDate, endDate, daysRequired);

        challengeRepository.addChallenge(newChallenge);
        Toast.makeText(getContext(), "Challenge created successfully!", Toast.LENGTH_SHORT).show();

        // Navigate back
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}