package uk.ac.hope.mcse.android.coursework.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import uk.ac.hope.mcse.android.coursework.R;
import uk.ac.hope.mcse.android.coursework.adapters.HabitAdapter;
import uk.ac.hope.mcse.android.coursework.models.Habit;
import uk.ac.hope.mcse.android.coursework.repositories.HabitRepository;

public class HabitListFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyView;
    private HabitAdapter adapter;
    private HabitRepository repository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_habit_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new HabitRepository(); // In a real app, this would be injected or retrieved from a singleton

        // Initialize views
        recyclerView = view.findViewById(R.id.habit_list_recyclerview);
        emptyView = view.findViewById(R.id.empty_view);
        FloatingActionButton fabAddHabit = view.findViewById(R.id.fab_add_habit);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HabitAdapter(getContext(), habits -> {
            // Handle habit completion
            repository.completeHabit(habits.getId());
            updateUI();
        }, habit -> {
            // Navigate to habit detail
            Bundle args = new Bundle();
            args.putString("habitId", habit.getId());
            Navigation.findNavController(view).navigate(R.id.action_habitListFragment_to_habitDetailFragment, args);
        });
        recyclerView.setAdapter(adapter);

        // Set up FAB click listener
        fabAddHabit.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_habitListFragment_to_habitDetailFragment)
        );

        // Load data
        updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        List<Habit> habits = repository.getAllHabits();
        adapter.setHabits(habits);

        if (habits.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}