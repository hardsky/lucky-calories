package com.hardskygames.luckycalories.list;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.hardskygames.luckycalories.BaseFragment;
import com.hardskygames.luckycalories.R;
import com.hardskygames.luckycalories.common.EndlessRecyclerOnScrollListener;
import com.hardskygames.luckycalories.common.Utils;
import com.hardskygames.luckycalories.list.events.AddCalorieEvent;
import com.hardskygames.luckycalories.list.events.EditCalorieEvent;
import com.hardskygames.luckycalories.list.models.CalorieModel;
import com.hardskygames.luckycalories.list.models.DailyCalorie;
import com.hardskygames.luckycalories.list.models.IColorSubscriber;
import com.hardskygames.luckycalories.models.User;
import com.mobandme.android.transformer.Transformer;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
public class CaloriesListFragment extends BaseFragment {

    @Inject
    LuckyCaloriesApi api;
    @Inject
    User user;
    @Inject
    Bus bus;

    @Bind(R.id.listCalories)
    RecyclerView listLayout;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private List<ICalorieListItem> calorieList = new ArrayList<>(20);

    private LinearLayoutManager layoutManager;
    private CaloriesAdapter adapter;
    private EndlessRecyclerOnScrollListener scrollListener;

    private long lastDate = 0;
    private DailyCalorie daily = null;
    private Call<List<io.swagger.client.model.Calorie>> callList;

    private Transformer caloriesTransformer;

    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                @Override
                public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                    if(viewHolder instanceof DayItemViewHolder)
                        return 0;

                    return super.getSwipeDirs(recyclerView, viewHolder);
                }

                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    CalorieModel model = ((CommentItemViewHolder)viewHolder).getData();
                    model.remove();

                    int idx = calorieList.indexOf(model);
                    calorieList.remove(idx);
                    adapter.notifyItemRemoved(idx);
                }
            }
    );

    public CaloriesListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_calories_list, container, false);
        ButterKnife.bind(this, layout);

        layoutManager = new LinearLayoutManager(getActivity());
        listLayout.setLayoutManager(layoutManager);

        adapter = new CaloriesAdapter();
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

        loadPage();

        itemTouchHelper.attachToRecyclerView(listLayout);

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
        addScrollListener();
        bus.register(this);
    }

    @Override
    public void onPause() {
        bus.unregister(this);

        super.onPause();

        listLayout.clearOnScrollListeners();
        if(callList != null){
            callList.cancel();
            callList = null;
        }
    }

    @OnClick(R.id.btnAdd)
    public void addNewCalorie(){
        bus.post(new AddCalorieEvent());
    }

    @Subscribe
    public void onCalorieEdit(EditCalorieEvent ev){
        final CalorieModel calorieModel = ev.model;
        ev.model = null;
        if(calorieModel.getId() == 0L){//create

            adapter.notifyItemInserted(Utils.addToSortedList(calorieList, calorieModel, new Predicate<ICalorieListItem>() {
                @Override
                public boolean apply(ICalorieListItem input) {
                    if(input.getType() == ICalorieListItem.MEAL_CALORIE){
                        return calorieModel.getEatTime().getTime() > ((CalorieModel)input).getEatTime().getTime();
                    }
                    else{
                        return false;
                    }
                }
            }));

            Call<Calorie> calorieCall = api.createUserCalorie(user.getId(),
                    caloriesTransformer.transform(calorieModel, Calorie.class));
            calorieCall.enqueue(new Callback<Calorie>() {
                @Override
                public void onResponse(Call<Calorie> call, Response<Calorie> response) {
                    calorieModel.setId(response.body().getId());
                }

                @Override
                public void onFailure(Call<Calorie> call, Throwable t) {
                    Timber.e(t, "Error on adding calorie entry.");
                }
            });
        }
        else{//update
            int idx = calorieList.indexOf(calorieModel);
            calorieList.remove(idx);
            adapter.notifyItemRemoved(idx);

            Call<Calorie> calorieCall = api.updateUserCalorie(user.getId(), caloriesTransformer.transform(calorieModel, Calorie.class));
            calorieCall.enqueue(new Callback<Calorie>() {
                @Override
                public void onResponse(Call<Calorie> call, Response<Calorie> response) {
                    adapter.notifyItemInserted(Utils.addToSortedList(calorieList, calorieModel, new Predicate<ICalorieListItem>() {
                        @Override
                        public boolean apply(ICalorieListItem input) {
                            if(input.getType() == ICalorieListItem.MEAL_CALORIE){
                                return calorieModel.getEatTime().getTime() > ((CalorieModel)input).getEatTime().getTime();
                            }
                            else{
                                return false;
                            }
                        }
                    }));
                }

                @Override
                public void onFailure(Call<Calorie> call, Throwable t) {
                    Timber.e(t, "Error on adding calorie entry.");
                }
            });
        }
    }

    private void addScrollListener(){
        listLayout.addOnScrollListener(scrollListener);
    }

    private void loadPage(){
        progressBar.setVisibility(View.VISIBLE);
        callList = api.getUserCaloriesList(user.getId(), lastDate);

        //listLayout.clearOnScrollListeners(); //add next, when current will complete
        listLayout.removeOnScrollListener(scrollListener);

        callList.enqueue(new Callback<List<io.swagger.client.model.Calorie>>() {
            @Override
            public void onResponse(Call<List<io.swagger.client.model.Calorie>> call, Response<List<io.swagger.client.model.Calorie>> response) {
                List<io.swagger.client.model.Calorie> list =  response.body();
                if(list != null && !list.isEmpty()) {
                    int entryCount = adapter.getItemCount();

                    for (io.swagger.client.model.Calorie respCl : list) {

                        if(lastDate == 0 || DailyCalorie.isNewDate(lastDate, respCl.getEatTime())){
                            lastDate = respCl.getEatTime();
                            daily = new DailyCalorie(user.getDailyCalories());
                            daily.setDate(new Date(lastDate));

                            calorieList.add(daily);
                        }

                        CalorieModel calorie = caloriesTransformer.transform(respCl, CalorieModel.class);
                        daily.add(calorie);
                        calorieList.add(calorie);
                    }

                    lastDate = list.get(list.size() - 1).getEatTime();
                    adapter.notifyItemRangeInserted(entryCount, list.size());
                }

                addScrollListener();
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<List<io.swagger.client.model.Calorie>> call, Throwable t) {
                Timber.e(t, "Error on request calories list.");
                addScrollListener();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private class CaloriesAdapter extends RecyclerView.Adapter {

        @Override
        public int getItemViewType(int position) {
            if(calorieList.isEmpty()) {
                return 0;
            }

            return calorieList.get(position).getType();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == ICalorieListItem.MEAL_CALORIE){
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_calorie_item, parent, false);
                return new CommentItemViewHolder(v);
            }
            else{
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_calorie_daily, parent, false);
                return new DayItemViewHolder(v);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if (calorieList.isEmpty())
                return;

            if(getItemViewType(position) == ICalorieListItem.MEAL_CALORIE){
                CalorieModel comment = (CalorieModel) calorieList.get(position);
                CommentItemViewHolder viewHolder = (CommentItemViewHolder) holder;
                viewHolder.setData(comment);
            }
            else{
                DailyCalorie comment = (DailyCalorie) calorieList.get(position);
                DayItemViewHolder viewHolder = (DayItemViewHolder) holder;
                viewHolder.setData(comment);
            }
        }

        @Override
        public int getItemCount() {
            return calorieList.size();
        }

    }

    private class DayItemViewHolder extends RecyclerView.ViewHolder implements IColorSubscriber{

        public TextView txtDate;
        public TextView txtTotal;

        private DailyCalorie daily;

        public DayItemViewHolder(View itemView) {
            super(itemView);

            txtDate = ButterKnife.findById(itemView, R.id.txtDate);
            txtTotal = ButterKnife.findById(itemView, R.id.txtTotal);
        }

        public void setData(DailyCalorie data) {
            DateFormat timeFormat = new SimpleDateFormat("dd MMM");

            this.daily = data;

            txtDate.setText(timeFormat.format(data.getDate()));
            txtTotal.setText(String.format("%.0f kcal", data.getCalories()));

            data.register(this);
        }

        @Override
        public void notifyGreen() {
            itemView.setBackgroundResource(R.color.md_green_400);
        }

        @Override
        public void notifyRed() {
            itemView.setBackgroundResource(R.color.md_red_400);
        }
    }

    private class CommentItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnLongClickListener, IColorSubscriber {

        public TextView txtMeal;
        public TextView txtKCal;
        public TextView txtTime;
        public TextView txtComment;

        private CalorieModel calorie;

        public CommentItemViewHolder(View itemView) {
            super(itemView);

            txtMeal = ButterKnife.findById(itemView, R.id.txtMeal);
            txtKCal = ButterKnife.findById(itemView, R.id.txtKCal);
            txtTime = ButterKnife.findById(itemView, R.id.txtTime);
            txtComment = ButterKnife.findById(itemView, R.id.txtComment);

            notifyGreen();

            itemView.setOnLongClickListener(this);
        }

        public void setData(CalorieModel data) {
            DateFormat timeFormat = new SimpleDateFormat("HH:mm");
            //DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());

            this.calorie = data;

            txtMeal.setText(calorie.getMeal());
            txtKCal.setText(String.format("%.0f", calorie.getAmount()));
            txtTime.setText(timeFormat.format(calorie.getEatTime()));
            txtComment.setText(calorie.getNote());

            data.register(this);
        }

        public CalorieModel getData(){
            return calorie;
        }

        @Override
        public boolean onLongClick(View v) {
            AddCalorieEvent ev = new AddCalorieEvent();
            ev.model = calorie;
            bus.post(ev);

            return true;
        }

        @Override
        public void notifyGreen() {
            itemView.setBackgroundResource(R.color.md_green_400);
        }

        @Override
        public void notifyRed() {
            itemView.setBackgroundResource(R.color.md_red_400);
        }
    }

}
