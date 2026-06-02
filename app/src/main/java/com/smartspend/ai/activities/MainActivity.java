package com.smartspend.ai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.smartspend.ai.R;
import com.smartspend.ai.databinding.ActivityMainBinding;
import com.smartspend.ai.fragments.AnalyticsFragment;
import com.smartspend.ai.fragments.BudgetFragment;
import com.smartspend.ai.fragments.DashboardFragment;
import com.smartspend.ai.fragments.ExpensesFragment;
import com.smartspend.ai.fragments.InsightsFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check auth
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }

        setupBottomNavigation();
        setupFab();

        // Check if navigated from notification
        String navigateTo = getIntent().getStringExtra("navigate_to");
        if ("analytics".equals(navigateTo)) {
            binding.bottomNav.setSelectedItemId(R.id.nav_analytics);
        } else {
            loadFragment(new DashboardFragment());
        }
    }

    private void setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                loadFragment(new DashboardFragment());
            } else if (id == R.id.nav_expenses) {
                loadFragment(new ExpensesFragment());
            } else if (id == R.id.nav_analytics) {
                loadFragment(new AnalyticsFragment());
            } else if (id == R.id.nav_budget) {
                loadFragment(new BudgetFragment());
            } else if (id == R.id.nav_insights) {
                loadFragment(new InsightsFragment());
            }
            return true;
        });
    }

    private void setupFab() {
        binding.fabAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddExpenseActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_up, R.anim.fade_out_slow);
        });
    }

    private void loadFragment(Fragment fragment) {
        if (currentFragment != null && currentFragment.getClass() == fragment.getClass()) return;
        currentFragment = fragment;
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in_fast, R.anim.fade_out_fast)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (binding.bottomNav.getSelectedItemId() != R.id.nav_home) {
            binding.bottomNav.setSelectedItemId(R.id.nav_home);
        } else {
            super.onBackPressed();
        }
    }
}
