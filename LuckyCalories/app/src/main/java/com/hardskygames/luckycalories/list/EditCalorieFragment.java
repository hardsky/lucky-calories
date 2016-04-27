package com.hardskygames.luckycalories.list;


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
import android.widget.TimePicker;

import com.hardskygames.luckycalories.BaseActivity;
import com.hardskygames.luckycalories.R;
import com.hardskygames.luckycalories.common.TimePickerFragment;
import com.hardskygames.luckycalories.list.events.EditCalorieEvent;
import com.hardskygames.luckycalories.models.CalorieModel;
import com.squareup.otto.Bus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditCalorieFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener,
        Toolbar.OnMenuItemClickListener{

    @Inject
    Bus bus;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.txtMeal)
    AppCompatEditText txtMeal;
    @Bind(R.id.txtKCal)
    AppCompatEditText txtKCal;
    @Bind(R.id.txtTime)
    AppCompatEditText txtTime;
    @Bind(R.id.txtNote)
    AppCompatEditText txtNote;

    private CalorieModel model = new CalorieModel();

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

        toolbar.setTitle("Edit");
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        toolbar.inflateMenu(R.menu.dialog_toolbar_menu);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });



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
        timeDialog.show(fm, "fragment_edit_time");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar cl = Calendar.getInstance();
        cl.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cl.set(Calendar.MINUTE, minute);

        model.setEatTime(cl.getTime());

        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        txtTime.setText(timeFormat.format(model.getEatTime()));
    }

    /**
     * click on SAVE button
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        try{
            model.setMeal(txtMeal.getText().toString());
            model.setAmount(Float.parseFloat(txtKCal.getText().toString()));
            model.setEatTime(new SimpleDateFormat("HH:mm").parse(txtTime.getText().toString()));
            model.setNote(txtNote.getText().toString());

            EditCalorieEvent ev = new EditCalorieEvent();
            ev.model = model;

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
}
