package com.hardskygames.luckycalories.mapper;

import com.mobandme.android.transformer.parser.AbstractParser;

import java.util.Date;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 25.04.16.
 */
public class DateToLongParser extends AbstractParser<Date, Long> {
    @Override
    protected Long onParse(Date value) {
        if(value == null)
            return 0L;

        return value.getTime();
    }
}
