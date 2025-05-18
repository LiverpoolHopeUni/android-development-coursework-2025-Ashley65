package uk.ac.hope.mcse.android.coursework.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import uk.ac.hope.mcse.android.coursework.R;
import uk.ac.hope.mcse.android.coursework.models.Challenge;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder> {

    private List<Challenge> challenges;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public ChallengeAdapter(List<Challenge> challenges) {
        this.challenges = challenges;
    }

    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_challenge, parent, false);
        return new ChallengeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        Challenge challenge = challenges.get(position);
        holder.nameTextView.setText(challenge.getName());
        holder.descriptionTextView.setText(challenge.getDescription());
        holder.dateRangeTextView.setText(String.format("%s - %s",
                dateFormat.format(challenge.getStartDate()),
                dateFormat.format(challenge.getEndDate())));
        holder.statusTextView.setText(challenge.isCompleted() ? "Completed" : "In Progress");
    }

    @Override
    public int getItemCount() {
        return challenges.size();
    }

    public void updateChallenges(List<Challenge> challenges) {
        this.challenges = challenges;
        notifyDataSetChanged();
    }

    static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView descriptionTextView;
        TextView dateRangeTextView;
        TextView statusTextView;

        ChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.challengeNameText);
            descriptionTextView = itemView.findViewById(R.id.challengeDescriptionText);
            dateRangeTextView = itemView.findViewById(R.id.challengeDateRangeText);
            statusTextView = itemView.findViewById(R.id.challengeStatusText);
        }
    }
}