package com.hardskygames.luckycalories.calories.models;

import java.util.Date;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 30.04.16.
 */
public class FilterModel {
    Date fromDate;
    Date toDate;
    Date fromTime;
    Date toTime;

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Date getFromTime() {
        return fromTime;
    }

    public void setFromTime(Date fromTime) {
        this.fromTime = fromTime;
    }

    public Date getToTime() {
        return toTime;
    }

    public void setToTime(Date toTime) {
        this.toTime = toTime;
    }
}
