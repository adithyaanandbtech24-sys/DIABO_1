package com.medisync.diabo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;
import com.medisync.diabo.databinding.FragmentSetupBinding;
import com.medisync.diabo.db.AppDatabase;
import com.medisync.diabo.model.UserProfile;
import com.google.android.material.chip.Chip;

public class SetupFragment extends Fragment {

    private FragmentSetupBinding binding;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSetupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = AppDatabase.getInstance(requireContext());

        // Setup Dropdown
        String[] types = {"Type 1", "Type 2", "Gestational", "Prediabetes"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, types);
        binding.spinnerDiabetesType.setAdapter(adapter);

        binding.btnCompleteSetup.setOnClickListener(v -> {
            saveUserProfile();
        });
    }

    private void saveUserProfile() {
        String name = binding.etName.getText().toString().trim();
        String ageStr = binding.etAge.getText().toString().trim();
        String type = binding.spinnerDiabetesType.getText().toString();
        
        if (name.isEmpty() || ageStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please provide name and age", Toast.LENGTH_SHORT).show();
            return;
        }

        UserProfile profile = new UserProfile();
        profile.name = name;
        profile.age = Integer.parseInt(ageStr);
        profile.diabetesType = type;
        
        String heightStr = binding.etHeight.getText().toString();
        if (!heightStr.isEmpty()) profile.height = Double.parseDouble(heightStr);
        
        String weightStr = binding.etWeight.getText().toString();
        if (!weightStr.isEmpty()) profile.weight = Double.parseDouble(weightStr);

        int selectedChipId = binding.chipGroupTreatment.getCheckedChipId();
        Chip selectedChip = binding.getRoot().findViewById(selectedChipId);
        if (selectedChip != null) {
            profile.treatmentType = selectedChip.getText().toString();
        }

        new Thread(() -> {
            db.appDao().insertUserProfile(profile);
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Setup Complete!", Toast.LENGTH_SHORT).show();
                androidx.navigation.Navigation.findNavController(binding.getRoot()).navigate(R.id.action_setup_to_main);
            });
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
