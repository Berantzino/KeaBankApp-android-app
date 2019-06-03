package com.lasseberantzino.keabankapp.service;

import android.app.AlertDialog;
import android.content.Context;
import android.util.SparseIntArray;

import java.util.Random;

public class UtilityService {

    public UtilityService() {
    }


    // Populates the NemId SpareIntArray
    // Key is between 1.000-9.999
    // Value is between 100.000-999.999
    public SparseIntArray populateNemId() {

        Random random = new Random();
        SparseIntArray nemIdCodes = new SparseIntArray();

        for (int i = 0; i < 10; i++) {
            int key = random.nextInt(9000) + 1000;
            int value = random.nextInt(899999) + 100000;
            nemIdCodes.put(key, value);
        }

        return nemIdCodes;
    }


    // Creates an AlertDialog with the provided context and message
    // and returns it to the called
    public AlertDialog getErrorDialog(Context context, String message) {

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

        return alertDialog;
    }
}
