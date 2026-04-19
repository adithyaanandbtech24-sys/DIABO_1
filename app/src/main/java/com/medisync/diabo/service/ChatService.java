package com.medisync.diabo.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;

public class ChatService {

    private static final String BASE_URL = "https://generativelanguage.googleapis.com/";
    private final GeminiApi api;
    private final String apiKey;

    public ChatService(String apiKey) {
        this.apiKey = apiKey;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.api = retrofit.create(GeminiApi.class);
    }

    public void analyzeReport(String extractedText, Callback<GeminiApi.Response> callback) {
        String prompt = "You are a clinical assistant. Analyze the following medical report text and extract lab results and medications. " +
                "Return ONLY a JSON object with two arrays: 'lab_results' (each with 'name', 'value', 'unit', 'range', 'status', 'testDate') " +
                "and 'medications' (each with 'name', 'dosage', 'frequency', 'reason'). " +
                "Text: " + extractedText;
        GeminiApi.Request request = new GeminiApi.Request(prompt);
        api.generateContent(apiKey, request).enqueue(callback);
    }
}
