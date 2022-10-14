package com.example.artcitytourapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.artcitytourapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Objects;
import java.util.Set;

import Sitio.Sitio;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

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

        //URI with site data for sharing
        Uri uri = getIntent().getData();

        if (uri != null) //Display the URI for parsing
        {
            Set<String> args = uri.getQueryParameterNames();
            String idSitio = uri.getQueryParameter("id");  //will return "V-Maths-Addition "
            getSiteData(idSitio);
        }

        // Fragment
        /*bottomNavigationView = findViewById(R.id.bottom_nav);
        NavController navController = Navigation.findNavController(this, R.id.contentContainer);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);*/



    }

    protected void getSiteData(String siteId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Sitios").document(siteId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) { //Creates site object
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Sitio site = document.toObject(Sitio.class);
                        assert site != null;
                        site.setCoordenadas((GeoPoint) Objects.requireNonNull(document.get("coordenadas")));
                        site.setIdSite(siteId);
                        Bundle b = new Bundle();
                        b.putSerializable("Sitio", site);
                        //There has to be a better way to do this
                        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.contentContainer);
                        NavController navController = navHostFragment.getNavController();
                        navController.navigate(R.id.siteFragment, b);
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }
}


