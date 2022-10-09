package com.example.artcitytourapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.artcitytourapp.R;
import com.example.artcitytourapp.fragments.CloseSitesFragment;
import com.example.artcitytourapp.fragments.PlanningFragment;
import com.example.artcitytourapp.fragments.ReviewFragment;
import com.example.artcitytourapp.fragments.SitesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    SitesFragment sites = new SitesFragment();
    CloseSitesFragment close = new CloseSitesFragment();
    PlanningFragment planning = new PlanningFragment();
    ReviewFragment review = new ReviewFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FragmentContainerView
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.contentContainer);
        assert navHostFragment != null;
        bottomNavigationView = findViewById(R.id.bottom_nav);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem sitesItem = menu.findItem(R.id.sitesFragment);
        MenuItem closeItem = menu.findItem(R.id.closeFragment);
        MenuItem planningItem = menu.findItem(R.id.planningFragment);
        MenuItem reviewItem = menu.findItem(R.id.reviewFragment);

        sitesItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                navController.navigate(R.id.sitesFragment);
                return true;
            }
        });
        closeItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                navController.navigate(R.id.closeFragment);
                return true;
            }
        });
        planningItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                navController.navigate(R.id.planningFragment);
                return true;
            }
        });
        reviewItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                navController.navigate(R.id.reviewFragment);
                return true;
            }
        });

        // Fragment
        /*bottomNavigationView = findViewById(R.id.bottom_nav);
        NavController navController = Navigation.findNavController(this, R.id.contentContainer);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);*/



    }
}


