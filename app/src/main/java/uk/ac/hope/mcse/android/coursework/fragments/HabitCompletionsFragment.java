package uk.ac.hope.mcse.android.coursework.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import uk.ac.hope.mcse.android.coursework.R;
import uk.ac.hope.mcse.android.coursework.adapters.HabitCompletionAdapter;
import uk.ac.hope.mcse.android.coursework.models.HabitCompletion;
import uk.ac.hope.mcse.android.coursework.viewmodels.HabitViewModel;

public class HabitCompletionsFragment extends Fragment {
    private RecyclerView recyclerView;
    private HabitCompletionAdapter adapter;
    private HabitViewModel viewModel;
    private TextView emptyView;
    private String habitId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            habitId = getArguments().getString("habitId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_habit_completions, container, false);

        recyclerView = view.findViewById(R.id.recyclerview_completions);
        emptyView = view.findViewById(R.id.empty_view);

        setupRecyclerView();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);
        loadCompletions();
    }

    private void setupRecyclerView() {
        adapter = new HabitCompletionAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadCompletions() {
        if (habitId != null) {
            viewModel.getHabitCompletions(habitId).observe(getViewLifecycleOwner(), completions -> {
                if (completions != null && !completions.isEmpty()) {
                    adapter.setHabitCompletions(completions);
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}