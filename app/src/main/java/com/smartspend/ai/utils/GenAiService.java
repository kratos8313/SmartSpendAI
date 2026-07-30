package com.smartspend.ai.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeBackend;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;

public final class GenAiService {
    public interface Callback {
        void onComplete(String narrative, boolean generatedByAi);
    }

    private GenAiService() {}

    public static void generateMonthlyNarrative(Context context, MonthlyReportSummary summary, Callback callback) {
        String fallback = summary.deterministicNarrative();
        if (FirebaseApp.getApps(context).isEmpty()) {
            callback.onComplete(fallback, false);
            return;
        }
        try {
            GenerativeModel model = FirebaseAI.getInstance(GenerativeBackend.googleAI())
                    .generativeModel("gemini-3.5-flash");
            GenerativeModelFutures futures = GenerativeModelFutures.from(model);
            Content prompt = new Content.Builder().addText(summary.toGroundingPrompt()).build();
            ListenableFuture<GenerateContentResponse> request = futures.generateContent(prompt);
            Futures.addCallback(request, new FutureCallback<>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    String text = result == null ? null : result.getText();
                    if (text == null || text.isBlank()) callback.onComplete(fallback, false);
                    else callback.onComplete(text.trim().substring(0, Math.min(text.trim().length(), 1600)), true);
                }
                @Override
                public void onFailure(@NonNull Throwable error) {
                    callback.onComplete(fallback, false);
                }
            }, ContextCompat.getMainExecutor(context));
        } catch (RuntimeException error) {
            callback.onComplete(fallback, false);
        }
    }
}
