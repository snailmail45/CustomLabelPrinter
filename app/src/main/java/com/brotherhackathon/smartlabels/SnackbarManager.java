package com.brotherhackathon.smartlabels;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class SnackbarManager {
    public static Snackbar createAnchoredSnackbar(View anchor, String msg) {
        Snackbar snackbar = Snackbar.make(anchor, msg, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        snackbar.setAnchorView(anchor);
        return snackbar;
    }
}
