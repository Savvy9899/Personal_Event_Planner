package com.example.personaleventplanner.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personaleventplanner.R;
import com.example.personaleventplanner.data.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final List<Event> eventList = new ArrayList<>();
    private final OnEventActionListener listener;

    public EventAdapter(OnEventActionListener listener) {
        this.listener = listener;
    }

    public void setEvents(List<Event> events) {
        eventList.clear();
        if (events != null) {
            eventList.addAll(events);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;
        private final TextView tvCategory;
        private final TextView tvLocation;
        private final TextView tvDateTime;
        private final ImageButton btnEdit;
        private final ImageButton btnDelete;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Event event) {
            tvTitle.setText(event.getTitle());
            tvCategory.setText("Category: " + event.getCategory());
            tvLocation.setText("Location: " + event.getLocation());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
            tvDateTime.setText("Date: " + sdf.format(new Date(event.getEventDateTime())));

            itemView.setOnClickListener(v -> listener.onEdit(event));
            btnEdit.setOnClickListener(v -> listener.onEdit(event));
            btnDelete.setOnClickListener(v -> listener.onDelete(event));
        }
    }

    public interface OnEventActionListener {
        void onEdit(Event event);
        void onDelete(Event event);
    }
}