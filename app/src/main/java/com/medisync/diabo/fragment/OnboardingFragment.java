package com.medisync.diabo.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.medisync.diabo.R;
import com.medisync.diabo.databinding.FragmentOnboardingBinding;

public class OnboardingFragment extends Fragment {

    private FragmentOnboardingBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initial states
        binding.iconContainer.setScaleX(0.5f);
        binding.iconContainer.setScaleY(0.5f);
        binding.iconContainer.setAlpha(0f);
        binding.btnContinue.setVisibility(View.INVISIBLE);

        setupFeatures();
        
        // Hide features initially
        binding.feature1.getRoot().setVisibility(View.INVISIBLE);
        binding.feature2.getRoot().setVisibility(View.INVISIBLE);
        binding.feature3.getRoot().setVisibility(View.INVISIBLE);
        binding.feature4.getRoot().setVisibility(View.INVISIBLE);

        startAnimations();

        binding.btnContinue.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_onboarding_to_auth);
        });
    }

    private void setupFeatures() {
        // Feature 1: Clinical Intelligence
        binding.feature1.featureIcon.setText("🧠");
        binding.feature1.featureTitle.setText("Clinical Intelligence");
        binding.feature1.featureSubtitle.setText("AI-powered analysis of your medical reports");

        // Feature 2: Document OCR
        binding.feature2.featureIcon.setText("📄");
        binding.feature2.featureTitle.setText("Document OCR");
        binding.feature2.featureSubtitle.setText("Instantly extract data from health documents");

        // Feature 3: Trends & Insights
        binding.feature3.featureIcon.setText("📈");
        binding.feature3.featureTitle.setText("Trends & Insights");
        binding.feature3.featureSubtitle.setText("Visualize your health journey over time");

        // Feature 4: Doctor Connect
        binding.feature4.featureIcon.setText("👨‍⚕️");
        binding.feature4.featureTitle.setText("Doctor Connect");
        binding.feature4.featureSubtitle.setText("Seamlessly share results with your physician");
    }

    private void startAnimations() {
        // Icon animation
        binding.iconContainer.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .alpha(1.0f)
                .setDuration(600)
                .start();

        // Staggered feature animations
        handler.postDelayed(() -> animateFeatureRow(binding.feature1.getRoot()), 400);
        handler.postDelayed(() -> animateFeatureRow(binding.feature2.getRoot()), 800);
        handler.postDelayed(() -> animateFeatureRow(binding.feature3.getRoot()), 1200);
        handler.postDelayed(() -> animateFeatureRow(binding.feature4.getRoot()), 1600);

        // Continue button animation
        handler.postDelayed(() -> {
            binding.btnContinue.setVisibility(View.VISIBLE);
            AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
            TranslateAnimation translate = new TranslateAnimation(0, 0, 40, 0);
            alpha.setDuration(600);
            translate.setDuration(600);
            binding.btnContinue.startAnimation(alpha);
            binding.btnContinue.startAnimation(translate);
        }, 2000);
    }

    private void animateFeatureRow(View row) {
        row.setVisibility(View.VISIBLE);
        AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
        TranslateAnimation translate = new TranslateAnimation(-40, 0, 0, 0);
        alpha.setDuration(500);
        translate.setDuration(500);
        row.startAnimation(alpha);
        row.startAnimation(translate);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        handler.removeCallbacksAndMessages(null);
    }
}
