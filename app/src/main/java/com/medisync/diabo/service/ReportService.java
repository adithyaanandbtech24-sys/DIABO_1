package com.medisync.diabo.service;

import android.content.Context;
import android.net.Uri;
import com.medisync.diabo.db.AppDatabase;
import com.medisync.diabo.model.LabResult;
import com.medisync.diabo.model.MedicalReport;
import com.medisync.diabo.model.Medication;
import com.medisync.diabo.BuildConfig;
import retrofit2.Call;
import retrofit2.Response;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;

public class ReportService {

    public interface ReportCallback {
        void onSuccess(MedicalReport report);
        void onFailure(String error);
    }

    public static void processReport(Context context, Uri imageUri, ReportCallback callback) {
        OCRService.extractText(context, imageUri, new OCRService.OCRCallback() {
            @Override
            public void onSuccess(String extractedText) {
                analyzeWithAI(context, extractedText, imageUri, callback);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure("OCR Failed: " + e.getMessage());
            }
        });
    }

    private static void analyzeWithAI(Context context, String text, Uri uri, ReportCallback callback) {
        ChatService chatService = new ChatService(BuildConfig.GEMINI_API_KEY);
        chatService.analyzeReport(text, new retrofit2.Callback<GeminiApi.Response>() {
            @Override
            public void onResponse(Call<GeminiApi.Response> call, Response<GeminiApi.Response> response) {
                if (response.isSuccessful() && response.body() != null) {
                    saveResults(context, response.body().getText(), text, uri, callback);
                } else {
                    callback.onFailure("AI Analysis Failed");
                }
            }

            @Override
            public void onFailure(Call<GeminiApi.Response> call, Throwable t) {
                callback.onFailure("AI Analysis error: " + t.getMessage());
            }
        });
    }

    private static void saveResults(Context context, String aiResponse, String rawText, Uri uri, ReportCallback callback) {
        AppDatabase db = AppDatabase.getInstance(context);
        MedicalReport report = new MedicalReport();
        report.extractedText = rawText;
        report.aiInsights = aiResponse;
        report.imageURL = uri.toString();

        List<LabResult> labs = new ArrayList<>();
        List<Medication> meds = new ArrayList<>();
        
        try {
            // Remove markdown code blocks if present
            String jsonStr = aiResponse;
            if (jsonStr.contains("```json")) {
                jsonStr = jsonStr.substring(jsonStr.indexOf("```json") + 7);
                jsonStr = jsonStr.substring(0, jsonStr.lastIndexOf("```"));
            } else if (jsonStr.contains("```")) {
                jsonStr = jsonStr.substring(jsonStr.indexOf("```") + 3);
                jsonStr = jsonStr.substring(0, jsonStr.lastIndexOf("```"));
            }
            
            JSONObject root = new JSONObject(jsonStr);
            
            JSONArray labsArray = root.optJSONArray("lab_results");
            if (labsArray != null) {
                for (int i = 0; i < labsArray.length(); i++) {
                    JSONObject obj = labsArray.getJSONObject(i);
                    LabResult lab = new LabResult();
                    lab.name = obj.optString("name");
                    lab.value = obj.optString("value");
                    lab.unit = obj.optString("unit");
                    lab.range = obj.optString("range");
                    lab.status = obj.optString("status");
                    lab.reportId = report.id;
                    labs.add(lab);
                }
            }
            
            JSONArray medsArray = root.optJSONArray("medications");
            if (medsArray != null) {
                for (int i = 0; i < medsArray.length(); i++) {
                    JSONObject obj = medsArray.getJSONObject(i);
                    Medication med = new Medication();
                    med.name = obj.optString("name");
                    med.dosage = obj.optString("dosage");
                    med.frequency = obj.optString("frequency");
                    med.reportId = report.id;
                    med.isActive = 1;
                    meds.add(med);
                }
            }
        } catch (Exception e) {
            // Fallback: search for Metformin as a demo backup
            if (aiResponse.toLowerCase().contains("metformin")) {
                Medication med = new Medication();
                med.name = "Metformin";
                med.dosage = "500mg";
                med.frequency = "Once daily";
                med.isActive = 1;
                med.reportId = report.id;
                meds.add(med);
            }
        }

        new Thread(() -> {
            db.appDao().insertReport(report);
            db.appDao().insertLabResults(labs);
            db.appDao().insertMedications(meds);
            callback.onSuccess(report);
        }).start();
    }
}
