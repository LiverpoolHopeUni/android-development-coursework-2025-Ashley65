package uk.ac.hope.mcse.android.coursework.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import uk.ac.hope.mcse.android.coursework.R;
import uk.ac.hope.mcse.android.coursework.models.Habit;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private List<Habit> habits = new ArrayList<>();
    private final Context context;
    private final OnHabitCompleteListener completeListener;
    private final OnHabitClickListener clickListener;

    public interface OnHabitCompleteListener {
        void onHabitComplete(Habit habit);
    }

    public interface OnHabitClickListener {
        void onHabitClick(Habit habit);
    }

    public HabitAdapter(Context context, OnHabitCompleteListener completeListener, OnHabitClickListener clickListener) {
        this.context = context;
        this.completeListener = completeListener;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_habit, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habits.get(position);
        holder.bind(habit);
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    public void setHabits(List<Habit> habits) {
        this.habits = habits;
        notifyDataSetChanged();
    }

    class HabitViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView descriptionTextView;
        private final TextView frequencyTextView;
        private final ProgressBar progressBar;
        private final Button completeButton;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.habit_title);
            descriptionTextView = itemView.findViewById(R.id.habit_description);
            frequencyTextView = itemView.findViewById(R.id.habit_frequency);
            progressBar = itemView.findViewById(R.id.progress_bar);
            completeButton = itemView.findViewById(R.id.complete_button);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    clickListener.onHabitClick(habits.get(position));
                }
            });

            completeButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    completeListener.onHabitComplete(habits.get(position));
                }
            });
        }

        public void bind(Habit habit) {
            titleTextView.setText(habit.getTitle());
            descriptionTextView.setText(habit.getDescription());

            String frequencyText = habit.getFrequency() + " times " + (habit.isDaily() ? "daily" : "weekly");
            frequencyTextView.setText(frequencyText);

            // Update progress bar
            progressBar.setMax(habit.getFrequency());
            progressBar.setProgress(habit.getCompletedCount());
        }
    }
}