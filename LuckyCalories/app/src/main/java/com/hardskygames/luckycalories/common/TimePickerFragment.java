package com.hardskygames.luckycalories.common;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 27.04.16.
 * see https://guides.codepath.com/android/Using-DialogFragment#displaying-date-or-time-picker-dialogs
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private Calendar calendar;
    private TimePickerDialog.OnTimeSetListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = calendar != null ? calendar : Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onDestroyView() {
        this.calendar = null;
        super.onDestroyView();
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public  void setListener(TimePickerDialog.OnTimeSetListener listener){
        this.listener = listener;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.listener.onTimeSet(view, hourOfDay, minute);
    }
}
