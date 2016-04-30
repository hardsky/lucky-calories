package com.hardskygames.luckycalories.calories;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.hardskygames.luckycalories.BaseActivity;
import com.hardskygames.luckycalories.R;
import com.hardskygames.luckycalories.common.DatePickerFragment;
import com.hardskygames.luckycalories.common.TimePickerFragment;
import com.hardskygames.luckycalories.calories.events.EditCalorieEvent;
import com.hardskygames.luckycalories.calories.models.CalorieModel;
import com.squareup.otto.Bus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditCalorieFragment extends DialogFragment implements Toolbar.OnMenuItemClickListener,
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener{

    @Inject
    Bus bus;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.txtMeal)
    AppCompatEditText txtMeal;
    @Bind(R.id.txtKCal)
    AppCompatEditText txtKCal;
    @Bind(R.id.txtDate)
    AppCompatEditText txtDate;
    @Bind(R.id.txtTime)
    AppCompatEditText txtTime;
    @Bind(R.id.txtNote)
    AppCompatEditText txtNote;

    private CalorieModel model = new CalorieModel();
    private Calendar calendar = Calendar.getInstance();

    private Date origEatTime;


    public EditCalorieFragment() {
        // Required empty public constructor
    }

    public void setModel(CalorieModel model){
        this.model = model;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_edit_calorie, container, false);
        ButterKnife.bind(this, layout);

        toolbar.setTitle(model.getId() > 0 ? "Edit" : "New");
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.inflateMenu(R.menu.dialog_toolbar_menu);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if(model.getId() > 0) { //existed value; edit mode

            txtMeal.setText(model.getMeal());
            txtKCal.setText(String.format(Locale.US, "%.2f", model.getAmount()));
            txtDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(model.getEatTime()));
            txtTime.setText(new SimpleDateFormat("HH:mm").format(model.getEatTime()));
            txtNote.setText(model.getNote());

            calendar.setTime(model.getEatTime());
        }
        else{
            txtDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
            txtTime.setText(new SimpleDateFormat("HH:mm").format(calendar.getTime()));
        }

        origEatTime = model.getEatTime();

        return layout;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
        ((BaseActivity) getActivity()).inject(this);
        bus.register(this);
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    @OnClick(R.id.btnTime)
    public void onTimeClick(){
        FragmentManager fm = getChildFragmentManager();
        TimePickerFragment timeDialog = new TimePickerFragment();
        timeDialog.setCalendar(calendar);
        timeDialog.setListener(this);
        timeDialog.show(fm, "fragment_edit_time");
    }

    @OnClick(R.id.btnDate)
    public void onDateClick(){
        FragmentManager fm = getChildFragmentManager();
        DatePickerFragment dateDialog = new DatePickerFragment();
        dateDialog.setCalendar(calendar);
        dateDialog.setListener(this);
        dateDialog.show(fm, "fragment_edit_date");
    }

    /**
     * click on SAVE button
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        try{
            model.setMeal(txtMeal.getText().toString());
            model.setAmount(Float.parseFloat(txtKCal.getText().toString()));
            model.setEatTime(calendar.getTime());
            model.setNote(txtNote.getText().toString());

            EditCalorieEvent ev = new EditCalorieEvent();
            ev.model = model;
            ev.origEatTime = origEatTime;

            model = null;

            bus.post(ev);

            dismiss();
            return true;
        }
        catch (Exception ex){
            Timber.e(ex, "Error on input calorie's values.");
        }

        return false;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        txtDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        txtTime.setText(timeFormat.format(calendar.getTime()));
    }

}
