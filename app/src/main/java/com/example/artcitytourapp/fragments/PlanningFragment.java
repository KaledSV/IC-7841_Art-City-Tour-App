package com.example.artcitytourapp.fragments;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.artcitytourapp.R;

import Ruta.RutaPersonalizada;

public class PlanningFragment extends Fragment {
    static View view;
    int window = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_planning, container, false);

        final TextView fragmetTitle = view.findViewById(R.id.lblPlanningTitle);
        final TextView myRouteBtn = (TextView) view.findViewById(R.id.myRouteLbl);
        final TextView favBtn = (TextView) view.findViewById(R.id.favLbl);
        final ImageView shareBtn = view.findViewById(R.id.planning_share_button);
        final ImageView notifyBtn = view.findViewById(R.id.planning_notification_button);

        SubPlanningFragment subPlanning = new SubPlanningFragment();
        //MapsFragment subPlanning = new MapsFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.planningContainer, subPlanning, "subPlanningFragment")
                .addToBackStack(null)
                .commit();

        fragmetTitle.setText(getResources().getString(R.string.planning));
        myRouteBtn.setClickable(true);
        myRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(window != 0){
                    int pLRoute = myRouteBtn.getPaddingLeft();
                    int pTRoute = myRouteBtn.getPaddingTop();
                    int pRRoute = myRouteBtn.getPaddingRight();
                    int pBRoute = myRouteBtn.getPaddingBottom();
                    myRouteBtn.setBackgroundResource(R.drawable.selected_button_border);
                    myRouteBtn.setTextColor(getResources().getColor(R.color.positive_600));
                    myRouteBtn.setPadding(pLRoute, pTRoute, pRRoute, pBRoute);

                    int pLFav = favBtn.getPaddingLeft();
                    int pTFav = favBtn.getPaddingTop();
                    int pRFav = favBtn.getPaddingRight();
                    int pBFav = favBtn.getPaddingBottom();
                    favBtn.setBackgroundResource(R.drawable.unselected_button_border);
                    favBtn.setTextColor(getResources().getColor(R.color.grey_400));
                    favBtn.setPadding(pLFav, pTFav, pRFav, pBFav);

                    window = 0;
                    SubPlanningFragment subPlanning = new SubPlanningFragment();
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.planningContainer, subPlanning, "subPlanningFragment")
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        favBtn.setClickable(true);
        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(window != 1) {
                    int pLFav = favBtn.getPaddingLeft();
                    int pTFav = favBtn.getPaddingTop();
                    int pRFav = favBtn.getPaddingRight();
                    int pBFav = favBtn.getPaddingBottom();
                    favBtn.setBackgroundResource(R.drawable.selected_button_border);
                    favBtn.setTextColor(getResources().getColor(R.color.positive_600));
                    favBtn.setPadding(pLFav, pTFav, pRFav, pBFav);

                    int pLRoute = myRouteBtn.getPaddingLeft();
                    int pTRoute = myRouteBtn.getPaddingTop();
                    int pRRoute = myRouteBtn.getPaddingRight();
                    int pBRoute = myRouteBtn.getPaddingBottom();
                    myRouteBtn.setBackgroundResource(R.drawable.unselected_button_border);
                    myRouteBtn.setTextColor(getResources().getColor(R.color.grey_400));
                    myRouteBtn.setPadding(pLRoute, pTRoute, pRRoute, pBRoute);

                    window = 1;
                    SubFavoriteFragment subFavorite = new SubFavoriteFragment();
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.planningContainer, subFavorite, "subFavoriteFragment")
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        shareBtn.setOnClickListener(view ->
                share_plan()
        );

        notifyBtn.setOnClickListener(view ->
                update_user_plan()
        );

        return view;


    }

    private void share_plan(){
        Intent sendIntent = new Intent();
        String base = "Mirá que chiva esta ruta personalizada: ";
        Uri.Builder builder = new Uri.Builder(); //Query builder URI for easier parsing
        builder.scheme("https")
                .authority("act.navigation.app")
                .appendPath("Plan")
                .appendQueryParameter("id_ruta_personal",RutaPersonalizada.getInstance().getIdMyRoute()) //Saca el id de la ruta personalizada
                .fragment("Planear");
        String Uri = builder.build().toString();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,base + RutaPersonalizada.getInstance().getName() + "\n Mas informacion en: "+Uri);
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    @SuppressLint("RestrictedApi")
    private void update_user_plan() {
        RutaPersonalizada.alterRutaPersonalizada(RutaPersonalizada.getInstance().getIdMyRoute(),RutaPersonalizada.getInstance().getIdSharedRoute() );
        Toast.makeText(getApplicationContext(),"Sincronización completa!",Toast.LENGTH_SHORT).show();
        }
}