package com.example.fitnesstracker.ai;

import com.example.fitnesstracker.models.FitnessMetrics;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GeminiAiClient {
    private final GenerativeModelFutures model;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public interface AiResponseCallback {
        void onSuccess(String response);
        void onError(Throwable t);
    }

    public GeminiAiClient(String apiKey) {
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", apiKey);
        this.model = GenerativeModelFutures.from(gm);
    }

    public void getWorkoutSuggestion(FitnessMetrics metrics, AiResponseCallback callback) {
        String prompt = String.format(
            "Based on my fitness data: %d steps, %.1f calories burned, and %d quest points earned today. " +
            "Provide a tailored short workout suggestion (max 20 words) and a motivational message (max 15 words). " +
            "Format: Suggestion | Message", 
            metrics.steps, metrics.calories, metrics.points
        );

        Content content = new Content.Builder()
            .addText(prompt)
            .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                callback.onSuccess(result.getText());
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onError(t);
            }
        }, executor);
    }
}
