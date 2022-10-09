package com.example.artcitytourapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.artcitytourapp.R;

public class PlanningFragment extends Fragment {
    View view;
    int window = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_planning, container, false);

        final TextView fragmetTitle = view.findViewById(R.id.lblPlanningTitle);
        final TextView myRouteBtn = (TextView) view.findViewById(R.id.myRouteLbl);
        final TextView favBtn = (TextView) view.findViewById(R.id.favLbl);
        SubPlanningFragment subPlanning = new SubPlanningFragment();
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

        return view;
    }
}