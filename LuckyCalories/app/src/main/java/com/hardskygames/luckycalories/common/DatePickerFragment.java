package com.hardskygames.luckycalories.common;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 28.04.16.
 */
public class DatePickerFragment extends DialogFragment {
    private Calendar calendar;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = calendar != null ? calendar : Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Activity has to implement this interface
        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) getParentFragment();

        // Create a new instance of TimePickerDialog and return it
        return new DatePickerDialog(getActivity(), listener, year, month, day);
    }

    @Override
    public void onDestroyView() {
        this.calendar = null;
        super.onDestroyView();
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
}
