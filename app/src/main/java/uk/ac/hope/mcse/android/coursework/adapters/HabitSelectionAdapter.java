package uk.ac.hope.mcse.android.coursework.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.function.BiConsumer;

import uk.ac.hope.mcse.android.coursework.R;
import uk.ac.hope.mcse.android.coursework.models.Habit;

public class HabitSelectionAdapter extends RecyclerView.Adapter<HabitSelectionAdapter.HabitViewHolder> {
    private List<Habit> habits;
    private BiConsumer<Habit, Boolean> onSelectionChanged;

    public HabitSelectionAdapter(List<Habit> habits, BiConsumer<Habit, Boolean> onSelectionChanged) {
        this.habits = habits;
        this.onSelectionChanged = onSelectionChanged;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_habit_selection, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habits.get(position);
        holder.titleTextView.setText(habit.getTitle());
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(false);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            onSelectionChanged.accept(habit, isChecked);
        });

        holder.itemView.setOnClickListener(v -> {
            holder.checkBox.setChecked(!holder.checkBox.isChecked());
        });
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    public void updateHabits(List<Habit> habits) {
        this.habits = habits;
        notifyDataSetChanged();
    }

    static class HabitViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView titleTextView;

        HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.habitCheckbox);
            titleTextView = itemView.findViewById(R.id.habitTitleText);
        }
    }
}