package com.example.booking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<History> histories;

    public HistoryAdapter(List<History> histories) {
        this.histories = histories;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        History history = histories.get(position);
        holder.bind(history);
    }

    @Override
    public int getItemCount() {
        return histories.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        private TextView appointmentIdValueTextView;
        private TextView dateValueTextView;
        private TextView timeValueTextView;
        private TextView bankValueTextView;
        private TextView transactionTypesValueTextView;
        private TextView statusValueTextView;
        private TextView tokenValueTextView;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            appointmentIdValueTextView = itemView.findViewById(R.id.appointmentIdValueTextView);
            dateValueTextView = itemView.findViewById(R.id.dateValueTextView);
            timeValueTextView = itemView.findViewById(R.id.timeValueTextView);
            bankValueTextView = itemView.findViewById(R.id.bankValueTextView);
            transactionTypesValueTextView = itemView.findViewById(R.id.transactionTypesValueTextView);
            statusValueTextView = itemView.findViewById(R.id.statusValueTextView);
            tokenValueTextView = itemView.findViewById(R.id.tokenValueTextView);
        }

        public void bind(History history) {
            appointmentIdValueTextView.setText(String.valueOf(history.getAppointmentId()));
            dateValueTextView.setText(history.getSelectedDate());
            timeValueTextView.setText(history.getSelectedTime());
            bankValueTextView.setText(history.getSelectedBank());
            transactionTypesValueTextView.setText(history.getTransactionTypes());
            statusValueTextView.setText(history.getStatus());
            tokenValueTextView.setText(history.getToken());
        }
    }
}
