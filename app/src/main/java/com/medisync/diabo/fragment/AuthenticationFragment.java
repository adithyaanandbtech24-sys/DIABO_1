package com.medisync.diabo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.medisync.diabo.R;
import com.medisync.diabo.databinding.ActivityAuthenticationBinding;

public class AuthenticationFragment extends Fragment {

    private ActivityAuthenticationBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityAuthenticationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            // Mock authentication always succeeds
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_auth_to_setup);
        });
        binding.btnGuest.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_auth_to_setup);
        });
        binding.tvSwitchAuth.setOnClickListener(v -> {
            if (binding.authTitle.getText().equals("Welcome Back")) {
                binding.authTitle.setText("Join MediSync");
                binding.btnLogin.setText("Sign Up");
                binding.tvSwitchAuth.setText("Already have an account? Login");
            } else {
                binding.authTitle.setText("Welcome Back");
                binding.btnLogin.setText("Login");
                binding.tvSwitchAuth.setText("Don't have an account? Sign Up");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
