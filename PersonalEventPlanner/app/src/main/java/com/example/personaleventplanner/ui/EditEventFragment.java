package com.example.personaleventplanner.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.personaleventplanner.R;
import com.example.personaleventplanner.data.Event;
import com.example.personaleventplanner.databinding.FragmentEditEventBinding;
import com.example.personaleventplanner.viewmodel.EventViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditEventFragment extends Fragment {

    private FragmentEditEventBinding binding;
    private EventViewModel eventViewModel;
    private Event currentEvent;
    private final Calendar selectedCalendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEditEventBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);

        String[] categories = {"Work", "Social", "Travel", "Study", "Personal"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategoryEdit.setAdapter(adapter);

        if (getArguments() != null) {
            int eventId = getArguments().getInt("eventId");
            eventViewModel.getEventById(eventId, event -> {
                if (event != null) {
                    currentEvent = event;
                    populateFields();
                }
            });
        }

        binding.btnPickDateEdit.setOnClickListener(v -> openDatePicker());
        binding.btnPickTimeEdit.setOnClickListener(v -> openTimePicker());
        binding.btnUpdateEvent.setOnClickListener(v -> updateEvent());
        binding.btnDeleteEvent.setOnClickListener(v -> deleteEvent());
    }

    private void populateFields() {
        binding.etTitleEdit.setText(currentEvent.getTitle());
        binding.etLocationEdit.setText(currentEvent.getLocation());
        selectedCalendar.setTimeInMillis(currentEvent.getEventDateTime());
        updateDateTimeLabel();

        String[] categories = {"Work", "Social", "Travel", "Study", "Personal"};
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(currentEvent.getCategory())) {
                binding.spinnerCategoryEdit.setSelection(i);
                break;
            }
        }
    }

    private void openDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedCalendar.set(Calendar.YEAR, year);
                    selectedCalendar.set(Calendar.MONTH, month);
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateTimeLabel();
                },
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH),
                selectedCalendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void openTimePicker() {
        TimePickerDialog dialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedCalendar.set(Calendar.MINUTE, minute);
                    selectedCalendar.set(Calendar.SECOND, 0);
                    selectedCalendar.set(Calendar.MILLISECOND, 0);
                    updateDateTimeLabel();
                },
                selectedCalendar.get(Calendar.HOUR_OF_DAY),
                selectedCalendar.get(Calendar.MINUTE),
                false
        );
        dialog.show();
    }

    private void updateDateTimeLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        binding.tvSelectedDateTimeEdit.setText(sdf.format(selectedCalendar.getTime()));
    }

    private void updateEvent() {
        if (currentEvent == null) {
            return;
        }

        String title = binding.etTitleEdit.getText().toString().trim();
        String category = binding.spinnerCategoryEdit.getSelectedItem().toString();
        String location = binding.etLocationEdit.getText().toString().trim();
        long eventTime = selectedCalendar.getTimeInMillis();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(requireContext(), "Title is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (eventTime < System.currentTimeMillis()) {
            Toast.makeText(requireContext(), "Please choose a future date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        currentEvent.setTitle(title);
        currentEvent.setCategory(category);
        currentEvent.setLocation(location);
        currentEvent.setEventDateTime(eventTime);

        eventViewModel.update(currentEvent);
        Toast.makeText(requireContext(), "Event updated successfully", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(requireView()).navigateUp();
    }

    private void deleteEvent() {
        if (currentEvent == null) {
            return;
        }
        eventViewModel.delete(currentEvent);
        Toast.makeText(requireContext(), "Event deleted successfully", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(requireView()).navigateUp();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}