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
import uk.ac.hope.mcse.android.coursework.models.HabitCompletion;

public class HabitCompletionAdapter extends RecyclerView.Adapter<HabitCompletionAdapter.HabitCompletionViewHolder> {

    private List<HabitCompletion> habitCompletions;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    public HabitCompletionAdapter(List<HabitCompletion> habitCompletions) {
        this.habitCompletions = habitCompletions;
    }

    @NonNull
    @Override
    public HabitCompletionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_habit_completion, parent, false);
        return new HabitCompletionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitCompletionViewHolder holder, int position) {
        HabitCompletion completion = habitCompletions.get(position);
        holder.dateTextView.setText(dateFormat.format(completion.getCompletedDate()));

        if (completion.getNotes() != null && !completion.getNotes().isEmpty()) {
            holder.notesTextView.setText(completion.getNotes());
            holder.notesTextView.setVisibility(View.VISIBLE);
        } else {
            holder.notesTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return habitCompletions.size();
    }

    public void setHabitCompletions(List<HabitCompletion> habitCompletions) {
        this.habitCompletions = habitCompletions;
        notifyDataSetChanged();
    }

    static class HabitCompletionViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView notesTextView;

        HabitCompletionViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.tv_completed_date);
            notesTextView = itemView.findViewById(R.id.tv_notes);
        }
    }
}