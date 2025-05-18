package uk.ac.hope.mcse.android.coursework.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import uk.ac.hope.mcse.android.coursework.R;
import uk.ac.hope.mcse.android.coursework.adapters.ChallengeAdapter;
import uk.ac.hope.mcse.android.coursework.repositories.ChallengeRepository;

public class ChallengesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChallengeAdapter adapter;
    private ChallengeRepository challengeRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenges, container, false);

        recyclerView = view.findViewById(R.id.challengesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChallengeAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabAddChallenge = view.findViewById(R.id.fabAddChallenge);
        fabAddChallenge.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_challengesFragment_to_createChallengeFragment)
        );

        challengeRepository = new ChallengeRepository(requireActivity().getApplication());
        loadChallenges();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadChallenges();
    }

    private void loadChallenges() {
        challengeRepository.getAllChallenges(challenges -> {
            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    adapter.updateChallenges(challenges);
                });
            }
        });
    }
}