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
import com.example.personaleventplanner.databinding.FragmentAddEventBinding;
import com.example.personaleventplanner.viewmodel.EventViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEventFragment extends Fragment {

    private FragmentAddEventBinding binding;
    private EventViewModel eventViewModel;
    private final Calendar selectedCalendar = Calendar.getInstance();
    private boolean dateSelected = false;
    private boolean timeSelected = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddEventBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);

        String[] categories = {"Work", "Social", "Travel", "Study", "Personal"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(adapter);

        binding.btnPickDate.setOnClickListener(v -> openDatePicker());
        binding.btnPickTime.setOnClickListener(v -> openTimePicker());
        binding.btnSaveEvent.setOnClickListener(v -> saveEvent());
    }
    private void openDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedCalendar.set(Calendar.YEAR, year);
                    selectedCalendar.set(Calendar.MONTH, month);
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    dateSelected = true;
                    updateDateTimeLabel();
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void openTimePicker() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedCalendar.set(Calendar.MINUTE, minute);
                    selectedCalendar.set(Calendar.SECOND, 0);
                    selectedCalendar.set(Calendar.MILLISECOND, 0);
                    timeSelected = true;
                    updateDateTimeLabel();
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        dialog.show();
    }
    private void updateDateTimeLabel() {
        if (dateSelected || timeSelected) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
            binding.tvSelectedDateTime.setText(sdf.format(selectedCalendar.getTime()));
        }
    }

    private void saveEvent() {
        String title = binding.etTitle.getText().toString().trim();
        String category = binding.spinnerCategory.getSelectedItem().toString();
        String location = binding.etLocation.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(requireContext(), "Title is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!dateSelected || !timeSelected) {
            Toast.makeText(requireContext(), "Please select date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        long eventTime = selectedCalendar.getTimeInMillis();
        if (eventTime < System.currentTimeMillis()) {
            Toast.makeText(requireContext(), "Please choose a future date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        Event event = new Event(title, category, location, eventTime);
        eventViewModel.insert(event);
        Toast.makeText(requireContext(), "Event saved successfully", Toast.LENGTH_SHORT).show();

        binding.etTitle.setText("");
        binding.etLocation.setText("");
        binding.tvSelectedDateTime.setText("No date selected");
        dateSelected = false;
        timeSelected = false;
        selectedCalendar.setTimeInMillis(System.currentTimeMillis());

        Navigation.findNavController(requireView()).navigateUp();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
