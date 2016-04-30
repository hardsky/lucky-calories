package com.hardskygames.luckycalories.calories;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import com.hardskygames.luckycalories.BaseFragment;
import com.hardskygames.luckycalories.R;
import com.hardskygames.luckycalories.calories.models.CalorieModel;
import com.hardskygames.luckycalories.calories.models.DailyCalorie;
import com.hardskygames.luckycalories.calories.models.IColorSubscriber;
import com.hardskygames.luckycalories.calories.models.OrderingCalorie;
import com.mobandme.android.transformer.Transformer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NavigableSet;
import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.ButterKnife;
import io.swagger.client.model.Calorie;
import retrofit2.Call;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 29.04.16.
 */
public class BaseCalorieListFragment extends BaseFragment {

    protected static final int MEAL_ITEM = 1;
    protected static final int DAILY_ITEM = 2;

    protected TreeMultimap<Date, CalorieModel> calories
            = TreeMultimap.create(Ordering.<Date>natural().reverse(), new OrderingCalorie());
    protected SortedMap<Date, DailyCalorie> dailies
            = new TreeMap<>(Ordering.<Date>natural().reverse());

    protected long lastDate = 0;
    protected Call<List<Calorie>> callList;
    protected BaseCaloriesAdapter adapter;

    protected Transformer caloriesTransformer = new Transformer
            .Builder()
            .build(io.swagger.client.model.Calorie.class);

    protected float calorieAlertLevel;


    public BaseCalorieListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onPause() {
        super.onPause();

        if(callList != null){
            callList.cancel();
            callList = null;
        }
    }

    protected int getCaloriePosition(CalorieModel calorie){
        SortedMap<Date, DailyCalorie> prevDailies = dailies.headMap(calorie.getEatDate());
        int count = getContainedEntriesCount(prevDailies);
        count += 1; //sub-header
        NavigableSet<CalorieModel> dailyCalories = calories.get(calorie.getEatDate());
        count +=  dailyCalories.headSet(calorie, true).size();

        return count - 1;
    }

    protected int getSubHeaderPosition(DailyCalorie dailyCalorie) {
        return getContainedEntriesCount(dailies.headMap(dailyCalorie.getDate()));
    }

    protected int getSubHeaderPosition(Date day) {
        return getContainedEntriesCount(dailies.headMap(day));
    }

    protected int getContainedEntriesCount(SortedMap<Date, DailyCalorie> dailies){
        int count = 0;
        for(Date day: dailies.keySet()){
            ++count;
            count += calories.get(day).size();
        }

        return count;
    }

    protected boolean isSameDate(Date prev, Date changed){
        Calendar cl1 = Calendar.getInstance();
        cl1.setTime(prev);

        Calendar cl2 = Calendar.getInstance();
        cl1.setTime(changed);

        return cl1.get(Calendar.YEAR) == cl2.get(Calendar.YEAR)
                && cl1.get(Calendar.MONTH) == cl2.get(Calendar.MONTH)
                && cl1.get(Calendar.DAY_OF_MONTH) == cl2.get(Calendar.DAY_OF_MONTH);
    }

    protected Date getDate(Date time){
        Calendar cl = Calendar.getInstance();
        cl.setTime(time);
        cl.set(Calendar.HOUR_OF_DAY, 0);
        cl.set(Calendar.MINUTE, 0);
        cl.set(Calendar.SECOND, 0);
        cl.set(Calendar.MILLISECOND, 0);

        return cl.getTime();
    }

    protected void onPageLoaded(List<io.swagger.client.model.Calorie> list){
        if(list != null && !list.isEmpty()) {
            int entryCount = adapter.getItemCount();

            for (io.swagger.client.model.Calorie respCl : list) {
                CalorieModel calorie = caloriesTransformer.transform(respCl, CalorieModel.class);
                if(!dailies.containsKey(calorie.getEatDate())){
                    DailyCalorie dailyCalorie = new DailyCalorie(calorieAlertLevel);
                    dailyCalorie.setDate(calorie.getEatDate());

                    dailies.put(dailyCalorie.getDate(), dailyCalorie);
                }

                dailies.get(calorie.getEatDate()).add(calorie);
                calories.put(calorie.getEatDate(), calorie);
            }

            lastDate = list.get(list.size() - 1).getEatTime();
            adapter.notifyItemRangeInserted(entryCount, list.size());
        }
    }

    protected CalorieModel getCalorie(int position){

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

    protected DailyCalorie getDaily(int position){

        int count = 0;
        for(Date day: dailies.keySet()){
            ++count;

            if(count - 1 == position)
                return dailies.get(day);

            count += calories.get(day).size();
        }

        return null;
    }

    protected class BaseCaloriesAdapter extends RecyclerView.Adapter {

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
                return onCreateMealViewHolder(parent, viewType);
            }
            else{
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_calorie_daily, parent, false);
                return new DayItemViewHolder(v);
            }
        }

        protected RecyclerView.ViewHolder onCreateMealViewHolder(ViewGroup parent, int viewType){
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_calorie_item, parent, false);
            return new MealItemViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if (dailies.isEmpty())
                return;

            if(getItemViewType(position) == MEAL_ITEM){
                onBindMealViewHolder(holder, position);
            }
            else{
                DayItemViewHolder viewHolder = (DayItemViewHolder) holder;
                viewHolder.setData(getDaily(position));
            }
        }

        protected void onBindMealViewHolder(RecyclerView.ViewHolder holder, int position){
            MealItemViewHolder viewHolder = (MealItemViewHolder) holder;
            viewHolder.setData(getCalorie(position));
        }

        @Override
        public int getItemCount() {
            return dailies.size() + calories.size();
        }

    }

    protected class DayItemViewHolder extends RecyclerView.ViewHolder implements IColorSubscriber {

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

    protected class MealItemViewHolder extends RecyclerView.ViewHolder implements IColorSubscriber {

        public TextView txtMeal;
        public TextView txtKCal;
        public TextView txtTime;
        public TextView txtComment;

        protected CalorieModel calorie;

        public MealItemViewHolder(View itemView) {
            super(itemView);

            txtMeal = ButterKnife.findById(itemView, R.id.txtMeal);
            txtKCal = ButterKnife.findById(itemView, R.id.txtKCal);
            txtTime = ButterKnife.findById(itemView, R.id.txtTime);
            txtComment = ButterKnife.findById(itemView, R.id.txtComment);

            notifyGreen();
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
        public void notifyGreen() {
            itemView.setBackgroundResource(R.color.md_green_400);
        }

        @Override
        public void notifyRed() {
            itemView.setBackgroundResource(R.color.md_red_400);
        }
    }

}
