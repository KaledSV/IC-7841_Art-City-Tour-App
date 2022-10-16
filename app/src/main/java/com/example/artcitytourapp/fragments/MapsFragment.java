package com.example.artcitytourapp.fragments;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.artcitytourapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

import Sitio.Sitio;
import Sitio.Coordenada;

public class MapsFragment extends Fragment {
    View view;
    GoogleMap mMap;
    JSONObject jso;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            LocationManager locationManager = (LocationManager) MapsFragment.this.getActivity().getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    obtenerLugaresCercaDeTi(location);
                    LatLng miUbicacion = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(miUbicacion).title("ubicacion actual"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(miUbicacion));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo( 17.0f ));
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1000, locationListener);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_maps, container, false);
        View viewSearchFragment = SearchFragment.getSearchVista();
        if (CloseSitesFragment.getArgumento1() != 0) {
            switch(CloseSitesFragment.getArgumento1()){
                case 1:
                    Button botonf1 = (Button) viewSearchFragment.findViewById(R.id.botonff1);
                    mostrarResultadoFiltro(botonf1);
                    break;
                case 2:
                    Button botonf2 = (Button) viewSearchFragment.findViewById(R.id.botonff2);
                    mostrarResultadoFiltro(botonf2);
                    break;
                case 3:
                    Button botonf3 = (Button) viewSearchFragment.findViewById(R.id.botonff3);
                    mostrarResultadoFiltro(botonf3);
                    break;
                case 4:
                    Button botonf4 = (Button) viewSearchFragment.findViewById(R.id.botonff4);
                    mostrarResultadoFiltro(botonf4);
                    break;
            }
        }
        CloseSitesFragment.setArgumento1(0);
        view.setId(View.generateViewId());
        return view;
    }
    public void obtenerLugaresCercaDeTi(Location location){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Sitios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<Coordenada> listaCoordenadas = new ArrayList<>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        GeoPoint punto = document.getGeoPoint("coordenadas");
                        double lat = punto.getLatitude();
                        double lng = punto.getLongitude();
                        LatLng coordenada = new LatLng(lat,lng);
                        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)).position(coordenada).title(document.get("nombre").toString()));
                        Coordenada c = new Coordenada(document.get("nombre").toString(),lat,lng);
                        listaCoordenadas.add(c);
                    }
                    for (int i=0;i<listaCoordenadas.size();i++){
                        obtenerDistancia(location,String.valueOf(listaCoordenadas.get(i).latitud),String.valueOf(listaCoordenadas.get(i).longitud),listaCoordenadas.get(i).nombre);
                    }
                } else {
                    Log.w("res", "Error getting documents.", task.getException());
                }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void mostrarResultadoFiltro(Button b){
        View viewSearchFragment = SearchFragment.getSearchVista();
        ObjectAnimator animation = ObjectAnimator.ofFloat(viewSearchFragment.findViewById(R.id.includeLugares), "translationY", -1000f);
        animation.setDuration(2000);
        animation.start();
        b.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(204, 0, 150)));
        b.setForegroundTintList(ColorStateList.valueOf(Color.rgb(0, 255, 0)));
        b.setHintTextColor(Color.WHITE);
    }

    public void getLocalizacion() {
        int permiso = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        if(permiso == PackageManager.PERMISSION_DENIED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)){
            }

            else{
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }
    private void trazarRuta(Location l1,String lat,String lng){
        String url ="https://maps.googleapis.com/maps/api/directions/json?origin="+l1.getLatitude()+","+l1.getLongitude()+"&destination="+lat+","+lng+"&destination=9.99074,-83.03596"+ "&mode=walking"+"&key=AIzaSyCZlQBg07B2uDEW3B-Ym7p3kKOM8JcuNio";
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    jso = new JSONObject(response);
                    trazarRutaAux(jso);
                    Log.i("jsonRuta: ",""+response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("errorJson",error.toString());

            }
        });
        queue.add(stringRequest);
    }
    private void obtenerDistancia(Location l1, String lat,String lng, String nombre){
        View viewSearchFragment = SearchFragment.getSearchVista();
        LinearLayout layoutVertical = (LinearLayout) viewSearchFragment.findViewById(R.id.layoutCerca);
        String url ="https://maps.googleapis.com/maps/api/directions/json?origin="+l1.getLatitude()+","+l1.getLongitude()+"&destination="+lat+","+lng+"&mode=drive"+"&key=AIzaSyCZlQBg07B2uDEW3B-Ym7p3kKOM8JcuNio";
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    jso = new JSONObject(response);
                    JSONArray jRoutes1 = jso.getJSONArray("routes");
                    JSONArray jLegs1 = ((JSONObject)(jRoutes1.get(0))).getJSONArray("legs");
                    View results = getLayoutInflater().inflate(R.layout.result_layout, null);
                    results.setId(View.generateViewId());
                    TextView titulo = new TextView(getContext());
                    TextView distancia = new TextView(getContext());
                    TextView address = new TextView(getContext());
                    titulo = results.findViewById(R.id.site_name);
                    titulo.setText(nombre);
                    distancia = results.findViewById(R.id.site_distance);
                    distancia.setText(((JSONObject)jLegs1.get(0)).getJSONObject("distance").getString("text"));
                    address = results.findViewById(R.id.site_address);
                    address.setText(((JSONObject)jLegs1.get(0)).getString("end_address").substring(9,40));
                    /*((JSONObject)jLegs1.get(0)).getJSONObject("duration").getString("text");
                    ((JSONObject)jLegs1.get(0)).getJSONObject("end_address").toString()*/;
                    results.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            viewSearchFragment.findViewById(R.id.includeLugares).setVisibility(View.INVISIBLE);
                            ConstraintLayout cly = viewSearchFragment.findViewById(R.id.cl);
                            View direccion1 = getLayoutInflater().inflate(R.layout.fragment_direccion1, null);
                            direccion1.setId(View.generateViewId());
                            TextView txtTitulo = direccion1.findViewById(R.id.titL);
                            txtTitulo.setText(nombre);
                            TextView txtDis = direccion1.findViewById(R.id.textView4);
                            TextView dis = view.findViewById(R.id.site_distance);
                            txtDis.setText(dis.getText());
                            Button btnDir = direccion1.findViewById(R.id.button3);
                            btnDir.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    viewSearchFragment.findViewById(direccion1.getId()).setVisibility(View.INVISIBLE);
                                    ConstraintLayout cly2 = viewSearchFragment.findViewById(R.id.cl);
                                    View direccion2 = getLayoutInflater().inflate(R.layout.fragment_direccion2, null);
                                    direccion2.setId(View.generateViewId());
                                    TextView titulo = direccion2.findViewById(R.id.textView8);
                                    titulo.setText(nombre);
                                    TextView dis2 = direccion2.findViewById(R.id.textView6);
                                    TextView txtDis1 = txtDis;
                                    dis2.setText(txtDis1.getText());
                                    TextView addressBanner2 = direccion2.findViewById(R.id.textView9);
                                    TextView DurationBanner2 = direccion2.findViewById(R.id.textView7);
                                    TextView wazeLink = direccion2.findViewById(R.id.textView10);
                                    TextView googleMapsLink = direccion2.findViewById(R.id.textView11);
                                    try {
                                        DurationBanner2.setText(((JSONObject)jLegs1.get(0)).getJSONObject("duration").getString("text"));
                                        addressBanner2.setText(((JSONObject)jLegs1.get(0)).getString("end_address").substring(9));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    googleMapsLink.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://maps.google.com/?q="+lat+","+lng));
                                            startActivity(browserIntent);

                                        }
                                    });
                                    wazeLink.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q="+lat+","+lng));
                                            startActivity(browserIntent);
                                        }
                                    });
                                    cly2.addView(direccion2);
                                    ajustarPosicionBanner(direccion2,cly2);
                                    trazarRuta(l1,lat,lng);
                                }
                            });
                            cly.addView(direccion1);
                            ajustarPosicionBanner(direccion1,cly);
                        }
                    });
                    layoutVertical.addView(results);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("errorJson",error.toString());

            }
        });
        queue.add(stringRequest);
    }
    public void ajustarPosicionBanner(View bannerDireccion,ConstraintLayout layoutBanner){
        ConstraintSet set = new ConstraintSet();
        set.clone(layoutBanner);
        set.connect(bannerDireccion.getId(), ConstraintSet.TOP, layoutBanner.getId(), ConstraintSet.TOP, 0);
        set.connect(bannerDireccion.getId(), ConstraintSet.BOTTOM, layoutBanner.getId(), ConstraintSet.BOTTOM, 0);
        set.connect(bannerDireccion.getId(), ConstraintSet.LEFT, layoutBanner.getId(), ConstraintSet.LEFT, 0);
        set.connect(bannerDireccion.getId(), ConstraintSet.RIGHT, layoutBanner.getId(), ConstraintSet.RIGHT, 0);
        set.setVerticalBias(bannerDireccion.getId(), 0.95f);
        set.applyTo(layoutBanner);
    }
    private void trazarRutaAux(JSONObject jso) {
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        try {
            jRoutes = jso.getJSONArray("routes");
            for (int i=0; i<jRoutes.length();i++){
                jLegs = ((JSONObject)(jRoutes.get(i))).getJSONArray("legs");
                for (int j=0; j<jLegs.length();j++){
                    jSteps = ((JSONObject)jLegs.get(j)).getJSONArray("steps");
                    for (int k = 0; k<jSteps.length();k++){
                        String polyline = ""+((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        Log.i("end",""+polyline);
                        List<LatLng> list = PolyUtil.decode(polyline);
                        mMap.addPolyline(new PolylineOptions().addAll(list).color(Color.MAGENTA).width(5));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
            getLocalizacion();
        }
    }
}