package com.example.artcitytourapp.fragments;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

public class MapsFragment2 extends Fragment {
    GoogleMap mMap;
    JSONObject jso;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            /*LatLng sydney = new LatLng(-34, 151);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
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
                    /*
                    LatLng sanjose = new LatLng(9.93333, -84.08333);
                    LatLng cartago = new LatLng(9.86444, -83.91944);
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)).position(sanjose).title("San Jose"));
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)).position(cartago).title("Cartago"));
                    */

                    //Funcion agregarMarcadores(listaSitiosPlanUser)
                    //for i:lista{
                    //      addMarker(Sitio.coordenada)
                    //}
                    //Funcion planRuta(location,listaSitiosPlanUser)
                    //for i:lista{
                    //      trazarRuta(location = i-1,i.lat,i.lng);
                    //      location = i
                    //
                    //}
                    /*
                    trazarRuta(location,String.valueOf(sanjose.latitude),String.valueOf(sanjose.longitude));
                    Location locationCopia = location;
                    locationCopia.setLatitude(sanjose.latitude);
                    locationCopia.setLongitude(sanjose.longitude);
                    trazarRuta(locationCopia,String.valueOf(cartago.latitude),String.valueOf(cartago.longitude));

                     */
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

    private ArrayList<String> getUserPersonalSitesIds(){
        if (RutaPersonalizada.getInstance().getMyRoute().size() > 0)
        {
            ArrayList<SitioPersonalizado> sites = (ArrayList<SitioPersonalizado>) RutaPersonalizada.getInstance().getMyRoute();
            ArrayList<String> idSitios = new ArrayList<>();

            for (SitioPersonalizado site : sites)
            {
                idSitios.add(site.getIdSitio());
            }


            return idSitios;
        }

        return null;
    }

    private void displaySitesCoordinates(Location curr_location) {
        ArrayList<String> sitesIds = getUserPersonalSitesIds();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Sitios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Sitio site = document.toObject(Sitio.class);
                        assert site != null;
                        if (sitesIds.contains(document.getId())){
                            site.setCoordenadas((GeoPoint) Objects.requireNonNull(document.get("coordenadas")));
                            LatLng coordenada = new LatLng(site.getCoordenadas().getLatitude(), site.getCoordenadas().getLongitude());
                            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)).position(coordenada).title(site.getNombre()));
                            trazarRuta(curr_location,String.valueOf(coordenada.latitude),String.valueOf(coordenada.longitude));
                            curr_location.setLatitude(coordenada.latitude);
                            curr_location.setLongitude(coordenada.longitude);
                        }
                    }
                } else {
                    Log.w("res", "Error getting documents.", task.getException());
                }
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
}