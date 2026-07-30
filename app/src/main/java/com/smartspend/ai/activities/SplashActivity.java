package com.smartspend.ai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smartspend.ai.R;
import com.smartspend.ai.utils.SecurityUtils;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        animateSplash();

        new Handler(Looper.getMainLooper()).postDelayed(this::navigateNext, SPLASH_DURATION);
    }

    private void animateSplash() {
        ImageView logo = findViewById(R.id.iv_logo);
        TextView appName = findViewById(R.id.tv_app_name);
        TextView tagline = findViewById(R.id.tv_tagline);

        // Scale + fade animation for logo
        AnimationSet logoAnim = new AnimationSet(true);
        ScaleAnimation scale = new ScaleAnimation(0.3f, 1f, 0.3f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(800);
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(800);
        logoAnim.addAnimation(scale);
        logoAnim.addAnimation(fadeIn);
        logo.startAnimation(logoAnim);

        // Fade in text with delay
        AlphaAnimation textFade = new AlphaAnimation(0f, 1f);
        textFade.setDuration(600);
        textFade.setStartOffset(500);
        textFade.setFillAfter(true);
        appName.startAnimation(textFade);

        AlphaAnimation taglineFade = new AlphaAnimation(0f, 1f);
        taglineFade.setDuration(600);
        taglineFade.setStartOffset(800);
        taglineFade.setFillAfter(true);
        tagline.startAnimation(taglineFade);
    }

    private void navigateNext() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && SecurityUtils.isBiometricEnabled(this)) {
            authenticateBeforeOpening();
        } else {
            open(user != null ? MainActivity.class : AuthActivity.class);
        }
    }
    private void authenticateBeforeOpening() {
        BiometricPrompt prompt = new BiometricPrompt(this, ContextCompat.getMainExecutor(this),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        open(MainActivity.class);
                    }
                    @Override public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        FirebaseAuth.getInstance().signOut();
                        open(AuthActivity.class);
                    }
                });
        BiometricPrompt.PromptInfo info = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Unlock SmartSpend AI")
                .setSubtitle("Authenticate to access your financial data")
                .setNegativeButtonText("Use account password")
                .build();
        prompt.authenticate(info);
    }

    private void open(Class<?> destination) {
        startActivity(new Intent(this, destination));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
