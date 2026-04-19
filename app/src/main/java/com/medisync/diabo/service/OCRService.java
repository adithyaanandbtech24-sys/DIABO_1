package com.medisync.diabo.service;

import android.content.Context;
import android.net.Uri;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import java.io.IOException;

public class OCRService {

    public interface OCRCallback {
        void onSuccess(String text);
        void onFailure(Exception e);
    }

    public static void extractText(Context context, Uri imageUri, OCRCallback callback) {
        try {
            InputImage image = InputImage.fromFilePath(context, imageUri);
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

            recognizer.process(image)
                    .addOnSuccessListener(visionText -> {
                        callback.onSuccess(visionText.getText());
                    })
                    .addOnFailureListener(callback::onFailure);
        } catch (IOException e) {
            callback.onFailure(e);
        }
    }
}
