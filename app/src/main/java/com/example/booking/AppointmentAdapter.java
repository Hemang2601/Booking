package com.example.booking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private List<Appointment> appointments;

    public AppointmentAdapter(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
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

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            appointmentIdValueTextView = itemView.findViewById(R.id.appointmentIdValueTextView);
            dateValueTextView = itemView.findViewById(R.id.dateValueTextView);
            timeValueTextView = itemView.findViewById(R.id.timeValueTextView);
            bankValueTextView = itemView.findViewById(R.id.bankValueTextView);
            transactionTypesValueTextView = itemView.findViewById(R.id.transactionTypesValueTextView);
            statusValueTextView = itemView.findViewById(R.id.statusValueTextView);
            tokenValueTextView = itemView.findViewById(R.id.tokenValueTextView);
        }

        public void bind(Appointment appointment) {
            appointmentIdValueTextView.setText(String.valueOf(appointment.getAppointmentId()));
            dateValueTextView.setText(appointment.getSelectedDate());
            timeValueTextView.setText(appointment.getSelectedTime());
            bankValueTextView.setText(appointment.getSelectedBank());
            String transactionTypes = appointment.getTransactionTypes();
            StringBuilder formattedTransactionTypes = new StringBuilder();
            int count = 0;
            for (int i = 0; i < transactionTypes.length(); i++) {
                char c = transactionTypes.charAt(i);
                if (c == ',') {
                    count++;
                    if (count == 3) {
                        formattedTransactionTypes.append("\n");
                        count = 0;
                    } else {
                        formattedTransactionTypes.append(c);
                    }
                } else {
                    formattedTransactionTypes.append(c);
                }
            }
            transactionTypesValueTextView.setText(formattedTransactionTypes.toString());

            statusValueTextView.setText(appointment.getStatus());
            tokenValueTextView.setText(appointment.getToken());
        }
    }
}
