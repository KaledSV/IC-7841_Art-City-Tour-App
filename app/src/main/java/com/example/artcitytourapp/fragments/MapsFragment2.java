package com.example.artcitytourapp.fragments;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Ruta.RutaPersonalizada;
import Sitio.Sitio;
import Sitio.SitioPersonalizado;

public class MapsFragment2 extends Fragment implements GoogleMap.OnMarkerClickListener  {
    GoogleMap mMap;
    JSONObject jso;
    public List<Marker> markers = new ArrayList<>();
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            LocationManager locationManager = (LocationManager) MapsFragment2.this.getActivity().getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    LatLng miUbicacion = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(miUbicacion).title("ubicacion actual"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(miUbicacion,17)); // Mueve la cámara y hace zoom al punto del usuario ak7
                    displaySitesCoordinates(location);
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
            /*mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    Toast.makeText(getApplicationContext(), marker.toString(), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });*/
        }
    };

    private void displaySitesCoordinates(Location curr_location) {
        List<String> sitesIds = RutaPersonalizada.getInstance().getMyRouteSitesIds(); // Changed to work with the actual site id's, instead of the personalized site id's
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Sitios").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Sitio site = document.toObject(Sitio.class);
                    if (sitesIds.contains(document.getId())){
                        site.setCoordenadas((GeoPoint) Objects.requireNonNull(document.get("coordenadas")));
                        LatLng coordenada = new LatLng(site.getCoordenadas().getLatitude(), site.getCoordenadas().getLongitude());
                        mMap.setOnMarkerClickListener(this);
                        Marker newMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)).position(coordenada).title(site.getNombre()));
                        this.markers.add(newMarker);
                        trazarRuta(curr_location,String.valueOf(coordenada.latitude),String.valueOf(coordenada.longitude));
                        curr_location.setLatitude(coordenada.latitude);
                        curr_location.setLongitude(coordenada.longitude);
                    }
                }
            } else {
                Log.w("res", "Error getting documents.", task.getException());
            }
        });
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
        String url ="https://maps.googleapis.com/maps/api/directions/json?origin="+l1.getLatitude()+","+l1.getLongitude()+"&destination="+lat+","+lng+"&mode=drive"+"&key=AIzaSyCZlQBg07B2uDEW3B-Ym7p3kKOM8JcuNio";
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                jso = new JSONObject(response);
                trazarRutaAux(jso);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> Log.d("errorJson",error.toString()));
        queue.add(stringRequest);
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
                        List<LatLng> list = PolyUtil.decode(polyline);
                        mMap.addPolyline(new PolylineOptions().addAll(list).color(Color.MAGENTA).width(5));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps2, container, false);
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


    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Toast.makeText(getApplicationContext(), marker.toString(), Toast.LENGTH_SHORT).show();
        return false;
    }
}