package com.example.artcitytourapp.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {

            /*LatLng sydney = new LatLng(9.933230174080439, -84.0727511011615);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            //googleMap.setMinZoomPreference(3000);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            googleMap.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );*/
            mMap = googleMap;
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            LocationManager locationManager = (LocationManager) MapsFragment.this.getActivity().getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    /*View viewSearchFragment = SearchFragment.getSearchVista();
                    LinearLayout layoutVertical = (LinearLayout) viewSearchFragment.findViewById(R.id.layoutCerca);*/
                    //aqui empieza
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Sitios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                HashMap<Float, Coordenada> sitiosDistancia = new HashMap<Float, Coordenada>();
                                float[] distance = new float[1];
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("res", document.getId() + " => " +document.get("nombre")+"=>" +document.get("coordenadas"));
                                    GeoPoint punto = document.getGeoPoint("coordenadas");
                                    double lat = punto.getLatitude();
                                    double lng = punto.getLongitude();
                                    LatLng coordenada = new LatLng(lat,lng);
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)).position(coordenada).title(document.get("nombre").toString()));
                                    Location.distanceBetween(lat,lng,location.getLatitude(),location.getLongitude(),distance);
                                    Coordenada c = new Coordenada(document.get("nombre").toString(),lat,lng);
                                    sitiosDistancia.put(distance[0], c);
                                    //trazarRuta(location,String.valueOf(lat),String.valueOf(lng));
                                    //obtenerDistancia(location,String.valueOf(lat),String.valueOf(lng));

                                    /*TextView txt = new TextView(getContext());
                                    txt.setText(document.get("nombre").toString() +"-"+Float.toString(distance[0]));
                                    layoutVertical.addView(txt);*/
                                }
                                TreeMap<Float, Coordenada> t = new TreeMap<>();
                                t.putAll(sitiosDistancia);
                                for (Float key : t.keySet()) {
                                    System.out.println("Clave: " + key + ", Valor: " + t.get(key).nombre);
                                    obtenerDistancia(location,String.valueOf(t.get(key).latitud),String.valueOf(t.get(key).longitud),t.get(key).nombre);
                                }
                            } else {
                                Log.w("res", "Error getting documents.", task.getException());
                            }
                        }
                    });
                    //qui termina
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_maps, container, false);
        return view;
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
        String url ="https://maps.googleapis.com/maps/api/directions/json?origin="+l1.getLatitude()+","+l1.getLongitude()+"&destination="+lat+","+lng+"&mode=walking"+"&key=AIzaSyCZlQBg07B2uDEW3B-Ym7p3kKOM8JcuNio";
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    jso = new JSONObject(response);
                    JSONArray jRoutes1 = jso.getJSONArray("routes");
                    JSONArray jLegs1 = ((JSONObject)(jRoutes1.get(0))).getJSONArray("legs");
                    //View results = new View(getContext());
                    //results = getLayoutInflater().inflate(R.layout.result_layout, null);
                    //TextView txt = new TextView(getContext());
                    //txt.setText(nombre+"-"+((JSONObject)jLegs1.get(0)).getJSONObject("distance").getString("text"));
                    View results = getLayoutInflater().inflate(R.layout.result_layout, null);
                    TextView titulo = new TextView(getContext());
                    TextView distancia = new TextView(getContext());
                    //TextView imagen = new ImageView(getContext());
                    titulo = results.findViewById(R.id.site_name);
                    titulo.setText(nombre);
                    distancia = results.findViewById(R.id.site_distance);
                    distancia.setText(((JSONObject)jLegs1.get(0)).getJSONObject("distance").getString("text"));
                    results.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            viewSearchFragment.findViewById(R.id.includeLugares).setVisibility(View.INVISIBLE);
                            ConstraintLayout cly = viewSearchFragment.findViewById(R.id.cl);
                            View direccion1 = getLayoutInflater().inflate(R.layout.fragment_direccion1, null);
                            cly.addView(direccion1);
                            //View dir = getLayoutInflater().inflate(R.layout.fragment_direccion1, null);
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