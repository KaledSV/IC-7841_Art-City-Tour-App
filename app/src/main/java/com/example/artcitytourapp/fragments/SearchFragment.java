package com.example.artcitytourapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.example.artcitytourapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import Sitio.Sitio;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private static View view;

    private SearchView mSearchview;
    private RecyclerView mRecyclerview;
    private Query mSiteDatabase;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /*FirebaseRecyclerOptions<Sitio> options =
            new FirebaseRecyclerOptions.Builder<Sitio>()
                    //.setQuery(mSiteDatabase)
                    .build();*/

    //Clase que acomoda a los sitios obtenidos de la busqueda
    public class SiteviewHolder extends RecyclerView.ViewHolder{

        View mView;

        public SiteviewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }
    }

    private void firebaseSiteSearch(){/*
        //Necesita un adaptador para mostrar los resultados obtenidos de firebase
        FirebaseRecyclerAdapter<Sitio,SiteviewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Sitio, SiteviewHolder>()
        {

            @NonNull
            @Override
            public SiteviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            protected void onBindViewHolder(@NonNull SiteviewHolder holder, int position, @NonNull Sitio model) {

            }
        };*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);

        mSearchview = view.findViewById(R.id.searchView);
        //mRecyclerview = (RecyclerView) view.findViewById(R.id.result_list);
        mSiteDatabase = FirebaseDatabase.getInstance().getReference("Sitios");



        mSearchview.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseSiteSearch();
            }
        });

        return view;
    }
    public static View getSearchVista(){
        return view;
    }
}