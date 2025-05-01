package com.main.blooddonorserch.view.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.main.blooddonorserch.MainActivity;
import com.main.blooddonorserch.R;
import com.main.blooddonorserch.Repository.AuthRepository;
import com.main.blooddonorserch.databinding.ActivityHomeNavigationBinding;
import com.main.blooddonorserch.viewmodel.AuthViewModel;

//signout
import androidx.fragment.app.Fragment;

public class HomeNavigation extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeNavigationBinding binding;
    private AuthViewModel viewModel;
    private FirebaseAuth auth;

    private TextView userGmailId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarHomeNavigation.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        navigationView.setItemIconTintList(null);
        auth = FirebaseAuth.getInstance();


        // Initialize ViewModel & Firebase Auth
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);




        if (auth.getCurrentUser() != null) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer);

            // User is logged in: Show full navigation
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_settings, R.id.nav_profile,R.id.nav_history, R.id.nav_about)
                    .setOpenableLayout(drawer)
                    .build();
        } else {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.menu);

            // User is NOT logged in: Show only Home
            mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home,R.id.nav_about) // Only Home
                    .setOpenableLayout(drawer)
                    .build();
        }




        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_navigation);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



        //Updating Gmail id
        String gmailId = (auth.getCurrentUser() != null) ? auth.getCurrentUser().getEmail() : "Guest User";


        // Get NavigationView header
        View headerView = navigationView.getHeaderView(0);
        TextView userGmailId = headerView.findViewById(R.id.user_gmail_id);

        if (userGmailId != null) {
            userGmailId.setText(gmailId);
        } else {
            //Log.e("HomeNavigation", "TextView user_gmail_id not found in navigation header!");
            //Toast.makeText(this, "TextView user_gmail_id not found in navigation header!", Toast.LENGTH_SHORT).show()
        }



        // Handle Navigation Drawer Menu Item Clicks
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                NavController navController = Navigation.findNavController(HomeNavigation.this, R.id.nav_host_fragment_content_home_navigation);
                int currentDestinationId = navController.getCurrentDestination().getId();


                if (currentDestinationId == id) {
                    drawer.closeDrawers();
                    return true;
                }

                if (id == R.id.nav_home) {
                    navController.navigate(R.id.nav_home,null, new NavOptions.Builder()
                            .setPopUpTo(R.id.nav_home, true) // Clears all previous instances
                            .build());
                } else if (id == R.id.nav_profile) {
                    navController.navigate(R.id.nav_profile, null, new NavOptions.Builder()
                            .setPopUpTo(R.id.nav_profile, true) // Clears all previous instances
                            .build());
                }
                else if (id == R.id.nav_signout) {
                    showLogoutConfirmationDialog();
                }
                else if(id == R.id.nav_history)
                {
                    navController.navigate(R.id.nav_history,null, new NavOptions.Builder()
                            .setPopUpTo(R.id.nav_history, true) // Clears all previous instances
                            .build());
                }
                else if(id == R.id.nav_about)
                {
                    navController.navigate(R.id.nav_about,null, new NavOptions.Builder()
                            .setPopUpTo(R.id.nav_about, true) // Clears all previous instances
                            .build());
                }


                drawer.closeDrawers(); // Close the drawer after clicking
                return true;
            }
        });
    }
    @Override
    public void onBackPressed() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_navigation);
        int currentDestinationId = navController.getCurrentDestination().getId();
        // Check if user is logged in
        if (auth.getCurrentUser() != null) {
            if (currentDestinationId == R.id.nav_home) {
                // ✅ If already on Home, exit the app
                finishAffinity(); // Closes all activities in the task
            } else {
                super.onBackPressed(); // Navigate back normally
            }
        } else {
            // ✅ If user is NOT logged in, allow normal back navigation
            super.onBackPressed();
        }
    }

    /*************************************************Sign out ********************************/
    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Logout")
                .setMessage("Are you sure you want to Sign out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handleSignOut();
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void handleSignOut() {
        if (viewModel.getCurrentUser() != null) {
            viewModel.signOutAndRevokeAccess(new AuthRepository.OnSignOutCompleteListener() {
                @Override
                public void onSignOutSuccess() {
                    Toast.makeText(HomeNavigation.this, "Signed Out Successfully!", Toast.LENGTH_SHORT).show();

                    // Redirect to MainActivity (Login Screen)
                    Intent intent = new Intent(HomeNavigation.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
                    startActivity(intent);
                    finish(); // Close current activity
                }

                @Override
                public void onSignOutFailure(String errorMessage) {
                    Toast.makeText(HomeNavigation.this, "Sign Out Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(HomeNavigation.this, "No user logged in!", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshNavigationMenu() {
        NavigationView navigationView = binding.navView;
        Menu menu = navigationView.getMenu();

        // Clear existing menu and reload based on updated status
        menu.clear();
        navigationView.inflateMenu(R.menu.activity_main_drawer); // Reload menu

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_navigation);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
