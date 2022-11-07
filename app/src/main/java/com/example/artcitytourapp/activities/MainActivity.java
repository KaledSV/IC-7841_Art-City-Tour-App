package com.example.artcitytourapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.artcitytourapp.R;
import com.example.artcitytourapp.fragments.SubPlanningFragment;
import com.example.artcitytourapp.fragments.SubPlanningMyRouteListFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Objects;
import java.util.Set;

import Ruta.RutaPersonalizada;
import Sitio.Sitio;
import Usuario.VisitanteSingleton;

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

        //Todo change variable names
        FirebaseAuth fAuth = FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() != null) {
            if (uri != null) //Display the URI for parsing
            {
                Set<String> args = uri.getQueryParameterNames(); // Retrieves all arguments usable with the URI
                String fragment = uri.getFragment();
                switch (fragment) {
                    case "Planear": {
                        String sharedRouteId = uri.getQueryParameter("id_ruta_personalizada");
                        VisitanteSingleton.getInstance().bdUpdateSharedRouteId(sharedRouteId);
                        RutaPersonalizada.getInstance().setIdSharedRoute(sharedRouteId);
                        navController.navigate(R.id.planningFragment);
                        changeSharedRoute();
                    }
                    break;
                    case "Sitios": {
                        String idSitio = uri.getQueryParameter("id");
                        getSiteData(idSitio);
                    }
                    break;

                    default:
                        break;
                }
            }
        }
        else
        {
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }


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

    protected void changeSharedRoute(){
        if (SubPlanningFragment.getContainer() != null){
            SubPlanningFragment.getSwitch().setChecked(true);
        }
    }
}


