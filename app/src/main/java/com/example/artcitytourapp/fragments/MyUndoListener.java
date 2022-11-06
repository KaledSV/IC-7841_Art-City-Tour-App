package com.example.artcitytourapp.fragments;

import android.content.Intent;
import android.view.View;

import androidx.navigation.Navigation;

import com.example.artcitytourapp.R;

public class MyUndoListener implements View.OnClickListener {
    View view;

    public MyUndoListener(View view) {
        this.view = view;
    }

    @Override
    public void onClick(View v) {

        // Code to undo the user's last action
        Navigation.findNavController(view).navigate(R.id.planningFragment);
    }
}
