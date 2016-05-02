package com.hardskygames.luckycalories.calories;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.hardskygames.luckycalories.R;
import com.hardskygames.luckycalories.calories.models.FilterModel;
import com.hardskygames.luckycalories.common.DatePickerFragment;
import com.hardskygames.luckycalories.common.EndlessRecyclerOnScrollListener;
import com.hardskygames.luckycalories.common.TimePickerFragment;
import com.hardskygames.luckycalories.users.models.UserModel;
import com.mobandme.android.transformer.Transformer;
import com.squareup.otto.Bus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.swagger.client.api.LuckyCaloriesApi;
import io.swagger.client.model.Calorie;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class CaloriesFilterListFragment extends BaseCalorieListFragment {

    private class RequestParams{
        public long formDate;
        public long toDate;
        public long formTime;
        public long toTime;
    }

    @Inject
    LuckyCaloriesApi api;
    @Inject
    UserModel user;
    @Inject
    Bus bus;

    @Bind(R.id.listCalories)
    RecyclerView listLayout;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.txtFromDate)
    TextView txtFromDate;
    @Bind(R.id.txtToDate)
    TextView txtToDate;

    @Bind(R.id.txtFromTime)
    TextView txtFromTime;
    @Bind(R.id.txtToTime)
    TextView txtToTime;

    private LinearLayoutManager layoutManager;
    private EndlessRecyclerOnScrollListener scrollListener;

    FilterModel filterModel = new FilterModel();
    RequestParams requestParams;

    private static final String dateFormat = "dd/MM/yyyy";
    private static final String timeFormat = "HH:mm";

    public CaloriesFilterListFragment() {
        // Required empty public constructor

        //last month, from 12:00 to 15:00

        Calendar cl = Calendar.getInstance();
        filterModel.setToDate(cl.getTime());

        cl.add(Calendar.MONTH, -1);
        filterModel.setFromDate(cl.getTime());

        cl.set(Calendar.HOUR_OF_DAY, 12);
        cl.set(Calendar.MINUTE, 0);
        cl.set(Calendar.SECOND, 0);
        filterModel.setFromTime(cl.getTime());

        cl.set(Calendar.HOUR_OF_DAY, 15);
        filterModel.setToTime(cl.getTime());
    }


    @OnClick(R.id.btnFromDate)
    public void onclickFromDate(){
        FragmentManager fm = getChildFragmentManager();
        DatePickerFragment dateDialog = new DatePickerFragment();
        dateDialog.setListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar cl = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                filterModel.setFromDate(cl.getTime());
                txtFromDate.setText(new SimpleDateFormat(dateFormat).format(filterModel.getFromDate()));
            }
        });
        dateDialog.show(fm, "fragment_edit_date");

    }

    @OnClick(R.id.btnToDate)
    public void onclickToDate(){
        FragmentManager fm = getChildFragmentManager();
        DatePickerFragment dateDialog = new DatePickerFragment();
        dateDialog.setListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar cl = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                filterModel.setToDate(cl.getTime());
                txtToDate.setText(new SimpleDateFormat(dateFormat).format(filterModel.getToDate()));
            }
        });
        dateDialog.show(fm, "fragment_edit_date");
    }

    @OnClick(R.id.btnFromTime)
    public void onclickFromTime(){
        FragmentManager fm = getChildFragmentManager();
        TimePickerFragment timeDialog = new TimePickerFragment();
        timeDialog.setListener(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar cl = Calendar.getInstance();
                cl.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cl.set(Calendar.MINUTE, minute);

                filterModel.setFromTime(cl.getTime());
                txtFromTime.setText(new SimpleDateFormat(timeFormat).format(filterModel.getFromTime()));
            }
        });
        timeDialog.show(fm, "fragment_edit_time");
    }

    @OnClick(R.id.btnToTime)
    public void onclickToTime(){
        FragmentManager fm = getChildFragmentManager();
        TimePickerFragment timeDialog = new TimePickerFragment();
        timeDialog.setListener(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar cl = Calendar.getInstance();
                cl.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cl.set(Calendar.MINUTE, minute);

                filterModel.setToTime(cl.getTime());
                txtToTime.setText(new SimpleDateFormat(timeFormat).format(filterModel.getToTime()));
            }
        });
        timeDialog.show(fm, "fragment_edit_time");
    }

    @OnClick(R.id.btnFilter)
    public void applyFilter(){
        if(callList != null){
            callList.cancel();
            callList = null;
        }
        requestParams = new RequestParams();
        requestParams.formDate = filterModel.getFromDate().getTime();
        requestParams.toDate = filterModel.getToDate().getTime();
        requestParams.formTime = filterModel.getFromTime().getTime();
        requestParams.toTime = filterModel.getToTime().getTime();

        lastDate = 0;
        dailies.clear();
        calories.clear();

        adapter.notifyDataSetChanged();

        loadPage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_calories_filter_list, container, false);

        ButterKnife.bind(this, layout);

        layoutManager = new LinearLayoutManager(getActivity());
        listLayout.setLayoutManager(layoutManager);

        lastDate = 0;
        calories.clear();
        dailies.clear();

        adapter = new BaseCaloriesAdapter();
        listLayout.setAdapter(adapter);

        caloriesTransformer = new Transformer
                .Builder()
                .build(io.swagger.client.model.Calorie.class);

        scrollListener = new EndlessRecyclerOnScrollListener(layoutManager, 5) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadPage();
            }
        };

        txtFromDate.setText(new SimpleDateFormat(dateFormat).format(filterModel.getFromDate()));
        txtToDate.setText(new SimpleDateFormat(dateFormat).format(filterModel.getToDate()));
        txtFromTime.setText(new SimpleDateFormat(timeFormat).format(filterModel.getFromTime()));
        txtToTime.setText(new SimpleDateFormat(timeFormat).format(filterModel.getToTime()));

        applyFilter();

        return layout;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();

        listLayout.addOnScrollListener(scrollListener);
        bus.register(this);
    }

    @Override
    public void onPause() {
        bus.unregister(this);

        super.onPause();

        listLayout.removeOnScrollListener(scrollListener);
        if(callList != null){
            callList.cancel();
            callList = null;
        }
    }

    private void loadPage(){
        progressBar.setVisibility(View.VISIBLE);
        callList = api.getUserCaloriesFilter(user.getId(), lastDate,
                requestParams.formDate, requestParams.toDate,
                requestParams.formTime, requestParams.toTime);

        listLayout.removeOnScrollListener(scrollListener);

        callList.enqueue(new Callback<List<Calorie>>() {
            @Override
            public void onResponse(Call<List<Calorie>> call, Response<List<Calorie>> response) {
                if(response.isSuccessful()) {
                    onPageLoaded(response.body());
                }
                else{
                    Timber.e("Error on request calories list: %s", response.errorBody());
                }

                listLayout.addOnScrollListener(scrollListener);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<List<io.swagger.client.model.Calorie>> call, Throwable t) {
                Timber.e(t, "Error on request calories list.");
                listLayout.addOnScrollListener(scrollListener);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

}
