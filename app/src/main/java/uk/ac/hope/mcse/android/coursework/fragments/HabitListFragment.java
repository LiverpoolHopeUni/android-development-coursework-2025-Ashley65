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

import uk.ac.hope.mcse.android.coursework.R;
import uk.ac.hope.mcse.android.coursework.adapters.HabitAdapter;
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

        // Initialize repository with application context
        repository = new HabitRepository(requireActivity().getApplication());

        // Initialize views
        recyclerView = view.findViewById(R.id.habit_list_recyclerview);
        emptyView = view.findViewById(R.id.empty_view);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HabitAdapter(getContext(),
                // Habit completion callback
                habit -> {
                    repository.completeHabit(habit.getId());
                    updateUI();
                },
                // Habit click callback
                habit -> {
                    Bundle args = new Bundle();
                    args.putString("habitId", habit.getId());
                    Navigation.findNavController(view).navigate(R.id.action_habitListFragment_to_habitDetailFragment, args);

                }
        );
        recyclerView.setAdapter(adapter);



        // Load data
        updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when returning to this fragment
        updateUI();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParentFragmentManager().setFragmentResultListener("habitUpdated", this,
                (requestKey, result) -> updateUI());
    }

    private void updateUI() {
        // Get habits and update adapter
        repository.getAllHabits(habits -> {
            requireActivity().runOnUiThread(() -> {
                adapter.setHabits(habits);

                // Toggle visibility based on whether there are habits
                if (habits.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
            });
        });
    }
}
