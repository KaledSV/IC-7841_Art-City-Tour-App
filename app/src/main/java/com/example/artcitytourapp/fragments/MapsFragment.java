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
import androidx.navigation.Navigation;

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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

import Resenna.Resenna;
import Sitio.Sitio;
import Sitio.Coordenada;
import Usuario.VisitanteSingleton;

public class MapsFragment extends Fragment {
    View view;
    GoogleMap mMap;
    JSONObject jso;
    int filtroNum = 0;
    int distanceSites = 0;

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
                    //En colocar filtro se comprueba si existe un filtro o no existe
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
        SearchView sv = viewSearchFragment.findViewById(R.id.searchView);
        sv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.searchFragment2);
            }
        });
        sv.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.searchFragment2);
            }
        });
        Button botonf1 = (Button) viewSearchFragment.findViewById(R.id.botonff1);
        Button botonf2 = (Button) viewSearchFragment.findViewById(R.id.botonff2);
        Button botonf3 = (Button) viewSearchFragment.findViewById(R.id.botonff3);
        Button botonf4 = (Button) viewSearchFragment.findViewById(R.id.botonff4);
        ArrayList<Button> listaBotonesFiltro = new ArrayList<>();
        listaBotonesFiltro.add(botonf1);
        listaBotonesFiltro.add(botonf2);
        listaBotonesFiltro.add(botonf3);
        listaBotonesFiltro.add(botonf4);
        for(int i=1;i<=listaBotonesFiltro.size();i++){
            recargarFiltro(listaBotonesFiltro.get(i-1),i);
        }
        if (CloseSitesFragment.getArgumento1() != 0) {
            switch(CloseSitesFragment.getArgumento1()){
                case 1:
                    mostrarResultadoFiltro(botonf1);
                    filtroNum = 1;
                    break;
                case 2:
                    mostrarResultadoFiltro(botonf2);
                    filtroNum = 2;
                    break;
                case 3:
                    mostrarResultadoFiltro(botonf3);
                    filtroNum = 3;
                    break;
                case 4:
                    mostrarResultadoFiltro(botonf4);
                    filtroNum = 4;
                    break;
            }
        }
        CloseSitesFragment.setArgumento1(0);
        view.setId(View.generateViewId());
        return view;
    }
    public void recargarFiltro(Button varButton,int tipoFiltro2){
        varButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putSerializable("tipoFiltro", tipoFiltro2);
                Navigation.findNavController(view).navigate(R.id.closeFragment,b);
            }
        });
    }
    public void obtenerLugaresCercaDeTi(Location location){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Sitios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<Sitio> sites = new ArrayList<>();
                if (task.isSuccessful()) {
                    distanceSites = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Sitio site = document.toObject(Sitio.class);
                        assert site != null;
                        site.setCoordenadas((GeoPoint) Objects.requireNonNull(document.get("coordenadas")));
                        site.setIdSite(document.getId().toString());
                        LatLng coordenada = new LatLng(site.getCoordenadas().getLatitude(),site.getCoordenadas().getLongitude());
                        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)).position(coordenada).title(site.getNombre()));
                        sites.add(site);
                        obtenerDistancia(location, site, sites);
                    }

                } else {
                    Log.w("res", "Error getting documents.", task.getException());
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void orderAfterDistances(Location location, ArrayList<Sitio> sites){
        if (distanceSites == sites.size()){
            //ordenarlos ascendente
            if(filtroNum==0 || filtroNum==1 || filtroNum==2){
                sites = OrdenarAscendenteXDistancia(sites);
            }
            else if(filtroNum==3){
                sites = OrdenarAscendenteXTiempo(sites);
            }
            else{
                sites = OrdenarAscendenteXAccesibilidad(sites);
            }
            desplegarCarrusel(location,sites);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected  ArrayList<Sitio> OrdenarAscendenteXTiempo(ArrayList<Sitio> A){
        A.sort(Comparator.comparing(Sitio::getTiempoEspera));
        return A;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected  ArrayList<Sitio> OrdenarAscendenteXAccesibilidad(ArrayList<Sitio> A){
        A.sort(Comparator.comparing(Sitio::getAccRuedas));
        Collections.reverse(A);
        return A;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected  ArrayList<Sitio> OrdenarAscendenteXDistancia(ArrayList<Sitio> A){
        A.sort(Comparator.comparing(Sitio::getDistancia));
        return A;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void mostrarResultadoFiltro(Button b){
        View viewSearchFragment = SearchFragment.getSearchVista();
        ConstraintLayout bt_sheet = viewSearchFragment.findViewById(R.id.includeLugares);
        if (BottomSheetBehavior.from(bt_sheet).getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            BottomSheetBehavior.from(bt_sheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        /*ObjectAnimator animation = ObjectAnimator.ofFloat(viewSearchFragment.findViewById(R.id.includeLugares), "translationY", -1000f,0);
        ObjectAnimator animation2 = ObjectAnimator.ofFloat(viewSearchFragment.findViewById(R.id.includeLugares), "translationY", 0,-1000f);
        animation.setDuration(2000);
        animation.start();*/
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
    public void obtenerDistancia(Location l1, Sitio s, ArrayList<Sitio> sites){
        String url ="https://maps.googleapis.com/maps/api/directions/json?origin="+l1.getLatitude()+","+l1.getLongitude()+"&destination="+String.valueOf(s.getCoordenadas().getLatitude())+","+String.valueOf(s.getCoordenadas().getLongitude())+ "&mode=drive"+"&key=AIzaSyCZlQBg07B2uDEW3B-Ym7p3kKOM8JcuNio";
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(String response) {
                try {
                    jso = new JSONObject(response);
                    JSONArray jRoutes1 = jso.getJSONArray("routes");
                    JSONArray jLegs1 = ((JSONObject)(jRoutes1.get(0))).getJSONArray("legs");
                    String distancia = ((JSONObject)jLegs1.get(0)).getJSONObject("distance").getString("text");
                    String notacion = distancia.substring(distancia.length()-2,distancia.length());
                    distancia = distancia.substring(0,distancia.length()-3);
                    distancia = distancia.replace(",", "");
                    Log.d("Prueba3",s.getNombre());
                    Log.d("Prueba3",distancia);
                    s.setDistancia(Float.parseFloat(distancia));
                    Log.d("Prueba3",notacion);
                    if(!notacion.equals("km")){
                        float var = Float.parseFloat(distancia);
                        var = (float) (var * 0.001);
                        s.setDistancia(var);
                    }
                    else{
                        s.setDistancia(Float.parseFloat(distancia));
                    }
                    distanceSites++;
                    orderAfterDistances(l1, sites);
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
    private void desplegarCarrusel(Location l1, ArrayList<Sitio> sites){
        if (sites.size()>0){
            Sitio s = sites.get(0);
            View viewSearchFragment = SearchFragment.getSearchVista();
            LinearLayout layoutVertical = (LinearLayout) viewSearchFragment.findViewById(R.id.layoutCerca);
            String url ="https://maps.googleapis.com/maps/api/directions/json?origin="+l1.getLatitude()+","+l1.getLongitude()+"&destination="+String.valueOf(s.getCoordenadas().getLatitude())+","+String.valueOf(s.getCoordenadas().getLongitude())+"&mode=drive"+"&key=AIzaSyCZlQBg07B2uDEW3B-Ym7p3kKOM8JcuNio";
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
                        TextView titulo = results.findViewById(R.id.site_name);
                        TextView distancia = results.findViewById(R.id.site_distance);
                        TextView address = results.findViewById(R.id.site_address);
                        ImageView imagen = results.findViewById(R.id.siteImageViewResult);
                        bdGetSiteFoto(imagen, s.getIdFotoPredeterminada());
                        titulo.setText(s.getNombre());
                        //distancia.setText(((JSONObject)jLegs1.get(0)).getJSONObject("distance").getString("text"));
                        //String distanciaTexto = ((JSONObject)jLegs1.get(0)).getJSONObject("distance").getString("text");
                        colocarFiltro(viewSearchFragment.findViewById(R.id.tituloLugares), distancia,s.getDistancia(),s.getTiempoEspera(),s.getAccRuedas());
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
                                txtTitulo.setText(s.getNombre());
                                TextView txtDis = direccion1.findViewById(R.id.textView4);
                                TextView dis = view.findViewById(R.id.site_distance);
                                try {
                                    txtDis.setText(((JSONObject)jLegs1.get(0)).getJSONObject("distance").getString("text"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Button btnDir = direccion1.findViewById(R.id.button3);
                                btnDir.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        viewSearchFragment.findViewById(direccion1.getId()).setVisibility(View.INVISIBLE);
                                        ConstraintLayout cly2 = viewSearchFragment.findViewById(R.id.cl);
                                        View direccion2 = getLayoutInflater().inflate(R.layout.fragment_direccion2, null);
                                        direccion2.setId(View.generateViewId());
                                        TextView titulo = direccion2.findViewById(R.id.textView8);
                                        titulo.setText(s.getNombre());
                                        TextView dis2 = direccion2.findViewById(R.id.textView6);
                                        TextView txtDis1 = txtDis;
                                        try {
                                            dis2.setText(((JSONObject)jLegs1.get(0)).getJSONObject("distance").getString("text"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
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
                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://maps.google.com/?q="+String.valueOf(s.getCoordenadas().getLatitude())+","+String.valueOf(s.getCoordenadas().getLongitude())));
                                                startActivity(browserIntent);

                                            }
                                        });
                                        wazeLink.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q="+String.valueOf(s.getCoordenadas().getLatitude())+","+String.valueOf(s.getCoordenadas().getLongitude())));
                                                startActivity(browserIntent);
                                            }
                                        });
                                        cly2.addView(direccion2);
                                        ajustarPosicionBanner(direccion2,cly2);
                                        trazarRuta(l1,String.valueOf(s.getCoordenadas().getLatitude()),String.valueOf(s.getCoordenadas().getLongitude()));
                                    }
                                });

                                ImageView photoView = direccion1.findViewById(R.id.imageView3);
                                photoView.setImageDrawable(imagen.getDrawable());

                                VisitanteSingleton user = VisitanteSingleton.getInstance();
                                ImageView favBtn = direccion1.findViewById(R.id.favImageMap);
                                setFavoriteImage(user.siteFavoriteStatus(s.getIdSite()), favBtn);
                                favBtn.setClickable(true); //Favoritos
                                favBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (user.siteFavoriteStatus(s.getIdSite())){
                                            user.bdRemoveFavorite(s.getIdSite(), view);
                                            setFavoriteImage(false, favBtn);
                                        }
                                        else{
                                            user.bdAddFavorite(s.getIdSite(), view);
                                            setFavoriteImage(true, favBtn);
                                        }
                                    }
                                });

                                cly.addView(direccion1);
                                ajustarPosicionBanner(direccion1,cly);
                            }
                        });
                        layoutVertical.addView(results);
                        sites.remove(0);
                        desplegarCarrusel(l1, sites);
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
    }

    public void colocarFiltro(TextView titulo,TextView label,float distanciaTexto,int tiempoEspera,Boolean accesible){
        View viewSearchFragment = SearchFragment.getSearchVista();
        switch (filtroNum){
            case 0:
                titulo.setText("Lugares cerca de ti");
                label.setText(String.valueOf(distanciaTexto) + "km");
                break;
            case 1:
                /*LinearLayout ly = viewSearchFragment.findViewById(R.id.layoutCerca);
                View filtros = getLayoutInflater().inflate(R.layout.fragment_filtros, null);
                ly.addView(filtros);*/
                titulo.setText("Filtro");
                label.setText(String.valueOf("nada"));
                break;
            case 2:
                titulo.setText("Lugares cerca de ti");
                label.setText(String.valueOf(distanciaTexto) + "km");
                break;
            case 3:
                titulo.setText("Tiempo de espera");
                label.setText(String.valueOf(tiempoEspera) + "Min");
                break;
            case 4:
                titulo.setText("Accesibilidad para silla de ruedas");
                if(accesible){
                    label.setText("ACC");
                }else{
                    label.setText("NO ACC");
                }
                break;
        }
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

    protected void bdGetSiteFoto(ImageView iv, String idFoto){
        if (idFoto == null){
            imageSite(iv, "Imagenes Interfaz/notFoundImage.png");
        }
        else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("Fotografia").document(idFoto);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            if (document.get("foto") == null) {
                                imageSite(iv, "Imagenes Interfaz/notFoundImage.png");
                            } else {
                                imageSite(iv, (String) Objects.requireNonNull(document.get("foto")));
                            }
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

    protected void imageSite(ImageView iv, String imgPath){
        StorageReference pathReference  = FirebaseStorage.getInstance().getReference(imgPath);
        try {
            File localFile = File.createTempFile("tempFile", imgPath.substring(imgPath.lastIndexOf(".")));
            pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    iv.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 125, 125, false));
                }
            });
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    protected void setFavoriteImage(Boolean status, ImageView iv){
        if (status){
            iv.setImageResource(R.drawable.favorite_on);
        }
        else{
            iv.setImageResource(R.drawable.favorite_off);
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