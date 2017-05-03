package com.macjiji.marcus.agenda;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
 *
 * @author Marcus
 * @version 1.0
 * @see AjouterEvenement
 * @see ModifierEvenement
 *
 * Classe permettant de créer la boite de dialog avec calendrier
 *
 */

public class DatePickerFragment extends DialogFragment {

    /**
     * Méthode onCreateDialog permettant de créer la boîte de dialogue contenant le DatePicker
     * @param savedInstanceState
     */
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener)getActivity(), year, month, day);
    }

}
