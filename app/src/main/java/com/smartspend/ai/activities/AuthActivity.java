package com.smartspend.ai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smartspend.ai.R;
import com.smartspend.ai.databinding.ActivityAuthBinding;
import com.smartspend.ai.models.User;

public class AuthActivity extends AppCompatActivity {

    private ActivityAuthBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private boolean isLoginMode = true;
    private boolean isForgotMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        setupClickListeners();
        updateUI();
    }

    private void setupClickListeners() {
        binding.btnAuth.setOnClickListener(v -> {
            if (isForgotMode) {
                handleForgotPassword();
            } else if (isLoginMode) {
                handleLogin();
            } else {
                handleSignup();
            }
        });

        binding.tvToggleAuth.setOnClickListener(v -> {
            if (isForgotMode) {
                isForgotMode = false;
                isLoginMode = true;
            } else {
                isLoginMode = !isLoginMode;
            }
            updateUI();
        });

        binding.tvForgotPassword.setOnClickListener(v -> {
            isForgotMode = true;
            updateUI();
        });
    }

    private void updateUI() {
        if (isForgotMode) {
            binding.tvAuthTitle.setText("Reset Password");
            binding.tvAuthSubtitle.setText("Enter your email to receive a reset link");
            binding.tilName.setVisibility(View.GONE);
            binding.tilPassword.setVisibility(View.GONE);
            binding.tvForgotPassword.setVisibility(View.GONE);
            binding.btnAuth.setText("Send Reset Link");
            binding.tvToggleAuth.setText("Back to Login");
        } else if (isLoginMode) {
            binding.tvAuthTitle.setText("Welcome Back");
            binding.tvAuthSubtitle.setText("Sign in to your SmartSpend account");
            binding.tilName.setVisibility(View.GONE);
            binding.tilPassword.setVisibility(View.VISIBLE);
            binding.tvForgotPassword.setVisibility(View.VISIBLE);
            binding.btnAuth.setText("Sign In");
            binding.tvToggleAuth.setText("Don't have an account? Sign Up");
        } else {
            binding.tvAuthTitle.setText("Create Account");
            binding.tvAuthSubtitle.setText("Start your smart spending journey");
            binding.tilName.setVisibility(View.VISIBLE);
            binding.tilPassword.setVisibility(View.VISIBLE);
            binding.tvForgotPassword.setVisibility(View.GONE);
            binding.btnAuth.setText("Create Account");
            binding.tvToggleAuth.setText("Already have an account? Sign In");
        }
    }

    private void handleLogin() {
        String email = getEmail();
        String password = getPassword();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please fill all fields");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Enter a valid email address");
            return;
        }

        showLoading(true);
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    showLoading(false);
                    navigateToMain();
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    showError(e.getMessage());
                });
    }

    private void handleSignup() {
        String name = getName();
        String email = getEmail();
        String password = getPassword();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Please fill all fields");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Enter a valid email address");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }

        showLoading(true);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser firebaseUser = result.getUser();
                    if (firebaseUser != null) {
                        // Update display name
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();
                        User user = new User(firebaseUser.getUid(), name, email);
                        com.google.android.gms.tasks.Tasks.whenAll(
                                firebaseUser.updateProfile(profileUpdates),
                                firestore.collection("users").document(firebaseUser.getUid()).set(user)
                        ).addOnSuccessListener(ignored -> {
                            showLoading(false);
                            navigateToMain();
                        }).addOnFailureListener(error -> {
                            auth.signOut();
                            showLoading(false);
                            showError("Account created, but profile setup failed. Please sign in again.");
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    showError(e.getMessage());
                });
    }

    private void handleForgotPassword() {
        String email = getEmail();
        if (email.isEmpty()) {
            showError("Please enter your email");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Enter a valid email address");
            return;
        }

        showLoading(true);
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> {
                    showLoading(false);
                    Toast.makeText(this, "Reset link sent to " + email, Toast.LENGTH_LONG).show();
                    isForgotMode = false;
                    isLoginMode = true;
                    updateUI();
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    showError(e.getMessage());
                });
    }

    private void navigateToMain() {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private String getName() {
        return binding.etName.getText() != null ? binding.etName.getText().toString().trim() : "";
    }

    private String getEmail() {
        return binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
    }

    private String getPassword() {
        return binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnAuth.setEnabled(!loading);
        binding.btnAuth.setText(loading ? "" : (isForgotMode ? "Send Reset Link" : isLoginMode ? "Sign In" : "Create Account"));
    }
}
