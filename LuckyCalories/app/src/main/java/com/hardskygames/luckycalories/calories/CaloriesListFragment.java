package com.hardskygames.luckycalories.calories;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.hardskygames.luckycalories.R;
import com.hardskygames.luckycalories.common.EndlessRecyclerOnScrollListener;
import com.hardskygames.luckycalories.calories.events.AddCalorieEvent;
import com.hardskygames.luckycalories.calories.events.EditCalorieEvent;
import com.hardskygames.luckycalories.calories.models.CalorieModel;
import com.hardskygames.luckycalories.calories.models.DailyCalorie;
import com.hardskygames.luckycalories.users.models.UserModel;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.IOException;
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
public class CaloriesListFragment extends BaseCalorieListFragment {

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


    private LinearLayoutManager layoutManager;
    private EndlessRecyclerOnScrollListener scrollListener;

    private ColorMaster colorMaster = new ColorMaster();

    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                @Override
                public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                    if(viewHolder instanceof BaseDayItemViewHolder)
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

                    if(model.getId() > 0) {
                        api.deleteUserCalorie(user.getId(), model.getId()).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(!response.isSuccessful()){
                                    Timber.e("Error on delete calorie entry: %s", response.errorBody());
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Timber.e(t, "Error on delete calorie entry.");
                            }
                        });
                    }

                    int position = viewHolder.getAdapterPosition();
                    if(calories.get(eatDate).isEmpty()){//remove item + day sub-header

                        calories.remove(eatDate, model);
                        dailies.remove(eatDate);

                        adapter.notifyItemRangeRemoved(position - 1, 2);
                    }
                    else{ //remove item
                        dailies.get(eatDate).remove(model);
                        calories.remove(eatDate, model);
                        adapter.notifyItemRemoved(position);

                        int dailyPos = getSubHeaderPosition(eatDate);
                        int count = calories.get(eatDate).size() + 1; //calories + sub-header
                        adapter.notifyItemRangeChanged(dailyPos, count);
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

        lastDate = 0;
        calories.clear();
        dailies.clear();

        adapter = new CaloriesAdapter();
        listLayout.setAdapter(adapter);

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

        listLayout.removeOnScrollListener(scrollListener);
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

            if(!dailies.containsKey(calorieModel.getEatDate())){ //add item + sub-header
                DailyCalorie dailyCalorie = new DailyCalorie();
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

                calories.put(calorieModel.getEatDate(), calorieModel);
                adapter.notifyItemInserted(getCaloriePosition(calorieModel));

                int dailyPos = getSubHeaderPosition(calorieModel.getEatDate());
                int count = calories.get(calorieModel.getEatDate()).size() + 1; //calories + sub-header
                adapter.notifyItemRangeChanged(dailyPos, count);
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
                    DailyCalorie dailyCalorie = new DailyCalorie();
                    dailyCalorie.setDate(calorieModel.getEatDate());
                    dailyCalorie.add(calorieModel);

                    dailies.put(dailyCalorie.getDate(), dailyCalorie);
                    calories.put(dailyCalorie.getDate(), calorieModel);
                    int dailyPosition = getSubHeaderPosition(dailyCalorie);

                    adapter.notifyItemRangeInserted(dailyPosition, 2);
                }
                else{ //add item
                    DailyCalorie dailyCalorie = dailies.get(calorieModel.getEatDate());
                    int prevTotal = dailyCalorie.getTotal();
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
                if(response.isSuccessful()) {
                    onPageLoaded(response.body());
                }
                else{
                    Timber.e("Error on request calories list: %s", response.errorBody());
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

    private class CaloriesAdapter extends BaseCaloriesAdapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == MEAL_ITEM){
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_calorie_item, parent, false);
                return new MealItemClickableViewHolder(v);
            }
            else{
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_calorie_daily, parent, false);
                return new DayItemViewHolder(v);
            }
        }
    }

    private class ColorMaster{

        public int getColor(CalorieModel model){
            return getColor(dailies.get(model.getEatDate()).getTotal());
        }

        public int getColor(DailyCalorie model){
            return getColor(model.getTotal());
        }

        private int getColor(int dayTotal){
            if(dayTotal < user.getDailyCalories())
                return R.color.md_green_400;
            else
                return R.color.md_red_400;
        }
    }

    private class DayItemViewHolder extends BaseDayItemViewHolder{

        public DayItemViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void setData(DailyCalorie data) {
            super.setData(data);

            itemView.setBackgroundResource(colorMaster.getColor(data));
        }
    }

    private class MealItemClickableViewHolder extends MealItemViewHolder
            implements View.OnLongClickListener {

        public MealItemClickableViewHolder(View itemView) {
            super(itemView);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            AddCalorieEvent ev = new AddCalorieEvent();
            ev.model = calorie;
            bus.post(ev);

            return true;
        }

        @Override
        public void setData(CalorieModel data) {
            super.setData(data);

            itemView.setBackgroundResource(colorMaster.getColor(data));
        }

    }

}
