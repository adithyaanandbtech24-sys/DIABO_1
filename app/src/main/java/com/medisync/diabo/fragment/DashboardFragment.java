package com.medisync.diabo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.medisync.diabo.adapter.GoalAdapter;
import com.medisync.diabo.adapter.MedicationAdapter;
import com.medisync.diabo.adapter.VitalAdapter;
import com.medisync.diabo.databinding.FragmentDashboardBinding;
import com.medisync.diabo.db.AppDatabase;
import com.medisync.diabo.model.LabResult;
import com.medisync.diabo.model.Medication;
import com.medisync.diabo.model.UserProfile;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private AppDatabase db;
    private GoalAdapter goalAdapter;
    private MedicationAdapter medicationAdapter;
    private VitalAdapter vitalAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = com.medisync.diabo.db.AppDatabase.getInstance(requireContext());

        // Goals – static demo data for now
        List<GoalAdapter.Goal> demoGoals = new ArrayList<>();
        demoGoals.add(new GoalAdapter.Goal("HbA1c", "< 7%", "#A68CF2"));
        demoGoals.add(new GoalAdapter.Goal("Steps", "8000", "#66B2F2"));
        demoGoals.add(new GoalAdapter.Goal("Weight", "70kg", "#FFB266"));
        goalAdapter = new GoalAdapter(demoGoals);
        binding.rvGoals.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvGoals.setAdapter(goalAdapter);

        // Vitals – static demo data
        List<VitalAdapter.Vital> demoVitals = new ArrayList<>();
        demoVitals.add(new VitalAdapter.Vital("Heart Rate", "72 bpm", "Normal", com.medisync.diabo.R.drawable.heart_icon));
        demoVitals.add(new VitalAdapter.Vital("Blood Glucose", "110 mg/dL", "Normal", com.medisync.diabo.R.drawable.heart_icon));
        demoVitals.add(new VitalAdapter.Vital("Blood Pressure", "120/80", "Normal", com.medisync.diabo.R.drawable.heart_icon));
        demoVitals.add(new VitalAdapter.Vital("Weight", "70 kg", "Normal", com.medisync.diabo.R.drawable.heart_icon));
        vitalAdapter = new VitalAdapter(demoVitals);
        binding.rvVitals.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvVitals.setAdapter(vitalAdapter);

        // Calendar Strip
        List<CalendarAdapter.Day> demoDays = new ArrayList<>();
        demoDays.add(new CalendarAdapter.Day("18", "Sat", false));
        demoDays.add(new CalendarAdapter.Day("19", "Sun", true));
        demoDays.add(new CalendarAdapter.Day("20", "Mon", false));
        demoDays.add(new CalendarAdapter.Day("21", "Tue", false));
        demoDays.add(new CalendarAdapter.Day("22", "Wed", false));
        demoDays.add(new CalendarAdapter.Day("23", "Thu", false));
        CalendarAdapter calendarAdapter = new CalendarAdapter(demoDays);
        binding.rvCalendar.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvCalendar.setAdapter(calendarAdapter);

        // Medications – observe from DB
        medicationAdapter = new MedicationAdapter(new ArrayList<>());
        binding.rvMedications.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvMedications.setAdapter(medicationAdapter);
        db.appDao().getActiveMedications().observe(getViewLifecycleOwner(), new Observer<List<Medication>>() {
            @Override
            public void onChanged(List<Medication> meds) {
                medicationAdapter.updateData(meds);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
