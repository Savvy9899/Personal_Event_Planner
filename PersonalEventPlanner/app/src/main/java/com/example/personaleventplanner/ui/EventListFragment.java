package com.example.personaleventplanner.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.personaleventplanner.R;
import com.example.personaleventplanner.adapter.EventAdapter;
import com.example.personaleventplanner.data.Event;
import com.example.personaleventplanner.databinding.FragmentEventListBinding;
import com.example.personaleventplanner.viewmodel.EventViewModel;
import com.google.android.material.snackbar.Snackbar;

public class EventListFragment extends Fragment implements EventAdapter.OnEventActionListener {

    private FragmentEventListBinding binding;
    private EventViewModel eventViewModel;
    private EventAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEventListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new EventAdapter(this);
        binding.recyclerViewEvents.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewEvents.setAdapter(adapter);

        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        eventViewModel.getUpcomingEvents().observe(getViewLifecycleOwner(), events -> {
            adapter.setEvents(events);
            binding.tvEmpty.setVisibility(events == null || events.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onEdit(Event event) {
        Bundle bundle = new Bundle();
        bundle.putInt("eventId", event.getId());
        Navigation.findNavController(requireView()).navigate(R.id.action_eventListFragment_to_editEventFragment, bundle);
    }

    @Override
    public void onDelete(Event event) {
        eventViewModel.delete(event);
        Snackbar.make(binding.getRoot(), "Event deleted successfully", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}