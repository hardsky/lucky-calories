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

import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import com.hardskygames.luckycalories.BaseFragment;
import com.hardskygames.luckycalories.R;
import com.hardskygames.luckycalories.common.EndlessRecyclerOnScrollListener;
import com.hardskygames.luckycalories.list.events.AddCalorieEvent;
import com.hardskygames.luckycalories.list.events.EditCalorieEvent;
import com.hardskygames.luckycalories.list.models.CalorieModel;
import com.hardskygames.luckycalories.list.models.DailyCalorie;
import com.hardskygames.luckycalories.list.models.IColorSubscriber;
import com.hardskygames.luckycalories.list.models.OrderingCalorie;
import com.hardskygames.luckycalories.models.User;
import com.mobandme.android.transformer.Transformer;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NavigableSet;
import java.util.SortedMap;
import java.util.TreeMap;

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

    private static final int MEAL_ITEM = 1;
    private static final int DAILY_ITEM = 2;

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

    private TreeMultimap<Date, CalorieModel> calories
            = TreeMultimap.create(Ordering.<Date>natural().reverse(), new OrderingCalorie());
    private SortedMap<Date, DailyCalorie> dailies
            = new TreeMap<>(Ordering.<Date>natural().reverse());

    private LinearLayoutManager layoutManager;
    private CaloriesAdapter adapter;
    private EndlessRecyclerOnScrollListener scrollListener;

    private long lastDate = 0;
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
                    CalorieModel model = ((MealItemViewHolder)viewHolder).getData();
                    Date eatDate = model.getEatDate();

                    int position = viewHolder.getAdapterPosition();
                    if(calories.get(eatDate).isEmpty()){//remove item + day sub-header

                        calories.remove(eatDate, model);
                        dailies.remove(eatDate);

                        adapter.notifyItemRangeRemoved(position - 1, 2);
                    }
                    else{ //remove item
                        int dailyPos = getSubHeaderPosition(eatDate);
                        dailies.get(eatDate).remove(model);
                        calories.remove(eatDate, model);
                        adapter.notifyItemChanged(dailyPos);
                        adapter.notifyItemRemoved(position);
                    }

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

    int getCaloriePosition(CalorieModel calorie){
        SortedMap<Date, DailyCalorie> prevDailies = dailies.headMap(calorie.getEatDate());
        int count = getContainedEntriesCount(prevDailies);
        count += 1; //sub-header
        NavigableSet<CalorieModel> dailyCalories = calories.get(calorie.getEatDate());
        count +=  dailyCalories.headSet(calorie, true).size();

        return count - 1;
    }

    int getSubHeaderPosition(DailyCalorie dailyCalorie) {
        return getContainedEntriesCount(dailies.headMap(dailyCalorie.getDate()));
    }

    int getSubHeaderPosition(Date day) {
        return getContainedEntriesCount(dailies.headMap(day));
    }

    private int getContainedEntriesCount(SortedMap<Date, DailyCalorie> dailies){
        int count = 0;
        for(Date day: dailies.keySet()){
            ++count;
            count += calories.get(day).size();
        }

        return count;
    }

    private boolean isSameDate(Date prev, Date changed){
        Calendar cl1 = Calendar.getInstance();
        cl1.setTime(prev);

        Calendar cl2 = Calendar.getInstance();
        cl1.setTime(changed);

        return cl1.get(Calendar.YEAR) == cl2.get(Calendar.YEAR)
                && cl1.get(Calendar.MONTH) == cl2.get(Calendar.MONTH)
                && cl1.get(Calendar.DAY_OF_MONTH) == cl2.get(Calendar.DAY_OF_MONTH);
    }

    private Date getDate(Date time){
        Calendar cl = Calendar.getInstance();
        cl.setTime(time);
        cl.set(Calendar.HOUR_OF_DAY, 0);
        cl.set(Calendar.MINUTE, 0);
        cl.set(Calendar.SECOND, 0);
        cl.set(Calendar.MILLISECOND, 0);

        return cl.getTime();
    }

    @Subscribe
    public void onCalorieEdit(EditCalorieEvent ev){
        final CalorieModel calorieModel = ev.model;
        ev.model = null;

        if(calorieModel.getId() == 0L){//create

            if(!dailies.containsKey(calorieModel.getEatDate())){ //add item + sub-header
                DailyCalorie dailyCalorie = new DailyCalorie(user.getDailyCalories());
                dailyCalorie.setDate(calorieModel.getEatDate());
                dailyCalorie.add(calorieModel);

                dailies.put(dailyCalorie.getDate(), dailyCalorie);
                calories.put(dailyCalorie.getDate(), calorieModel);
                int dailyPosition = getSubHeaderPosition(dailyCalorie);

                adapter.notifyItemRangeInserted(dailyPosition, 2);
            }
            else{ //add item
                DailyCalorie dailyCalorie = dailies.get(calorieModel.getEatDate());
                dailyCalorie.add(calorieModel);
                int dailyPos = getSubHeaderPosition(calorieModel.getEatDate());

                calories.put(calorieModel.getEatDate(), calorieModel);
                adapter.notifyItemInserted(getCaloriePosition(calorieModel));
                adapter.notifyItemChanged(dailyPos);
            }

            //lastDate for get paged list of calories
            //list sorted from current time to past
            //but calories during day sorted from morning to evening
            lastDate = calories.get(dailies.lastKey()).first().getEatTime().getTime();

            Call<Calorie> calorieCall = api.createUserCalorie(user.getId(),
                    caloriesTransformer.transform(calorieModel, Calorie.class));
            calorieCall.enqueue(new Callback<Calorie>() {
                @Override
                public void onResponse(Call<Calorie> call, Response<Calorie> response) {
                    if(response.isSuccessful()) {
                        calorieModel.setId(response.body().getId());
                    }
                    else{
                        try {
                            Timber.e(response.errorBody().string());
                        } catch (IOException e) {
                            Timber.e("Error on creating calorie.");
                        }
                    }
                }

                @Override
                public void onFailure(Call<Calorie> call, Throwable t) {
                    Timber.e(t, "Error on adding calorie entry.");
                }
            });
        }
        else{//update

            if(isSameDate(ev.origEatTime, calorieModel.getEatTime())){
                int caloriePosition = getCaloriePosition(calorieModel);
                int dailyPosition = getSubHeaderPosition(calorieModel.getEatDate());
                dailies.get(calorieModel.getEatDate()).change();
                adapter.notifyItemChanged(caloriePosition);
                adapter.notifyItemChanged(dailyPosition);
            }
            else{
                dailies.get(getDate(ev.origEatTime)).remove(calorieModel);
                if(!dailies.containsKey(calorieModel.getEatDate())){ //add item + sub-header
                    DailyCalorie dailyCalorie = new DailyCalorie(user.getDailyCalories());
                    dailyCalorie.setDate(calorieModel.getEatDate());
                    dailyCalorie.add(calorieModel);

                    dailies.put(dailyCalorie.getDate(), dailyCalorie);
                    calories.put(dailyCalorie.getDate(), calorieModel);
                    int dailyPosition = getSubHeaderPosition(dailyCalorie);

                    adapter.notifyItemRangeInserted(dailyPosition, 2);
                }
                else{ //add item
                    DailyCalorie dailyCalorie = dailies.get(calorieModel.getEatDate());
                    float prevTotal = dailyCalorie.getTotal();
                    dailies.get(calorieModel.getEatDate()).add(calorieModel);
                    calories.put(calorieModel.getEatDate(), calorieModel);
                    adapter.notifyItemChanged(getSubHeaderPosition(calorieModel.getEatDate()));
                    adapter.notifyItemInserted(getCaloriePosition(calorieModel));
                }

                lastDate = calories.get(dailies.lastKey()).first().getEatTime().getTime();
            }

            Call<Calorie> calorieCall = api.updateUserCalorie(user.getId(), caloriesTransformer.transform(calorieModel, Calorie.class));
            calorieCall.enqueue(new Callback<Calorie>() {
                @Override
                public void onResponse(Call<Calorie> call, Response<Calorie> response) {
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
                        CalorieModel calorie = caloriesTransformer.transform(respCl, CalorieModel.class);
                        if(!dailies.containsKey(calorie.getEatDate())){
                            DailyCalorie dailyCalorie = new DailyCalorie(user.getDailyCalories());
                            dailyCalorie.setDate(calorie.getEatDate());

                            dailies.put(dailyCalorie.getDate(), dailyCalorie);
                        }

                        dailies.get(calorie.getEatDate()).add(calorie);
                        calories.put(calorie.getEatDate(), calorie);
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

    private CalorieModel getCalorie(int position){

        int count = 0;
        for(Date day: dailies.keySet()){
            ++count;
            if(count + calories.get(day).size() > position) {
                for(CalorieModel calorie: calories.get(day)){
                    if(count == position)
                        return calorie;

                    ++count;
                }
            }
            count += calories.get(day).size();
        }

        return null;
    }

    private DailyCalorie getDaily(int position){

        int count = 0;
        for(Date day: dailies.keySet()){
            ++count;

            if(count - 1 == position)
                return dailies.get(day);

            count += calories.get(day).size();
        }

        return null;
    }

    private class CaloriesAdapter extends RecyclerView.Adapter {

        @Override
        public int getItemViewType(int position) {
            if(dailies.isEmpty())
                return 0;

            int count = 0;
            for(Date day: dailies.keySet()){
                ++count;
                if(position == count - 1)
                    return DAILY_ITEM;

                count += calories.get(day).size();
                if(count > position)
                    return MEAL_ITEM;
            }

            return MEAL_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == MEAL_ITEM){
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_calorie_item, parent, false);
                return new MealItemViewHolder(v);
            }
            else{
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_calorie_daily, parent, false);
                return new DayItemViewHolder(v);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if (dailies.isEmpty())
                return;

            if(getItemViewType(position) == MEAL_ITEM){
                MealItemViewHolder viewHolder = (MealItemViewHolder) holder;
                viewHolder.setData(getCalorie(position));
            }
            else{
                DayItemViewHolder viewHolder = (DayItemViewHolder) holder;
                viewHolder.setData(getDaily(position));
            }
        }

        @Override
        public int getItemCount() {
            return dailies.size() + calories.size();
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
            txtTotal.setText(String.format("%.0f kcal", data.getTotal()));

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

    private class MealItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnLongClickListener, IColorSubscriber {

        public TextView txtMeal;
        public TextView txtKCal;
        public TextView txtTime;
        public TextView txtComment;

        private CalorieModel calorie;

        public MealItemViewHolder(View itemView) {
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
