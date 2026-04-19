package com.medisync.diabo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.medisync.diabo.BuildConfig;
import com.medisync.diabo.adapter.ChatAdapter;
import com.medisync.diabo.databinding.FragmentChatBinding;
import com.medisync.diabo.service.ChatService;
import com.medisync.diabo.service.GeminiApi;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private ChatAdapter adapter;
    private final List<ChatAdapter.Message> messages = new ArrayList<>();
    private ChatService chatService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatService = new ChatService(BuildConfig.GEMINI_API_KEY);
        
        adapter = new ChatAdapter(messages);
        binding.rvChat.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvChat.setAdapter(adapter);

        // Welcome message
        addMessage("Hello! I'm your MediSync Health Assistant. How can I help you today?", false);

        binding.btnSend.setOnClickListener(v -> {
            String text = binding.etMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                sendMessageToAI(text);
                binding.etMessage.setText("");
            }
        });
    }

    private void sendMessageToAI(String userText) {
        addMessage(userText, true);
        
        chatService.analyzeReport(userText, new retrofit2.Callback<GeminiApi.Response>() {
            @Override
            public void onResponse(Call<GeminiApi.Response> call, Response<GeminiApi.Response> response) {
                if (response.isSuccessful() && response.body() != null) {
                    addMessage(response.body().getText(), false);
                } else {
                    addMessage("I'm sorry, I encountered an error processing that.", false);
                }
            }

            @Override
            public void onFailure(Call<GeminiApi.Response> call, Throwable t) {
                addMessage("Connection error: " + t.getMessage(), false);
            }
        });
    }

    private void addMessage(String text, boolean isUser) {
        messages.add(new ChatAdapter.Message(text, isUser));
        adapter.notifyItemInserted(messages.size() - 1);
        binding.rvChat.scrollToPosition(messages.size() - 1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
