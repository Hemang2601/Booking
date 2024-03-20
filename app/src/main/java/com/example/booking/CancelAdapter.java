package com.example.booking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CancelAdapter extends RecyclerView.Adapter<CancelAdapter.AppointmentViewHolder> {

    private static List<Appointment> appointments;
    private static CancelListener cancelListener;

    public CancelAdapter(List<Appointment> appointments, CancelListener cancelListener) {
        this.appointments = appointments;
        this.cancelListener = cancelListener;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cancel, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.bind(appointment);
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {

        private TextView appointmentIdValueTextView;
        private TextView dateValueTextView;
        private TextView timeValueTextView;
        private TextView bankValueTextView;
        private TextView transactionTypesValueTextView;
        private TextView statusValueTextView;
        private TextView tokenValueTextView;
        private Button cancelButton;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            appointmentIdValueTextView = itemView.findViewById(R.id.appointmentIdValueTextView);
            dateValueTextView = itemView.findViewById(R.id.dateValueTextView);
            timeValueTextView = itemView.findViewById(R.id.timeValueTextView);
            bankValueTextView = itemView.findViewById(R.id.bankValueTextView);
            transactionTypesValueTextView = itemView.findViewById(R.id.transactionTypesValueTextView);
            statusValueTextView = itemView.findViewById(R.id.statusValueTextView);
            tokenValueTextView = itemView.findViewById(R.id.tokenValueTextView);
            cancelButton = itemView.findViewById(R.id.cancelButton);

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Appointment appointment = appointments.get(position);
                        // Call cancel listener
                        cancelListener.onCancel(appointment.getAppointmentId());
                    }
                }
            });
        }

        public void bind(Appointment appointment) {
            appointmentIdValueTextView.setText(String.valueOf(appointment.getAppointmentId()));
            dateValueTextView.setText(appointment.getSelectedDate());
            timeValueTextView.setText(appointment.getSelectedTime());
            bankValueTextView.setText(appointment.getSelectedBank());
            transactionTypesValueTextView.setText(appointment.getTransactionTypes());
            statusValueTextView.setText(appointment.getStatus());
            tokenValueTextView.setText(appointment.getToken());
        }
    }

    // Interface to handle cancel event
    public interface CancelListener {
        void onCancel(int appointmentId);
    }
}