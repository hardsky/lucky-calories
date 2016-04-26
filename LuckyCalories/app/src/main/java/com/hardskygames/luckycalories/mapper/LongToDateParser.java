package com.hardskygames.luckycalories.mapper;

import com.mobandme.android.transformer.parser.AbstractParser;

import java.util.Date;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 25.04.16.
 */
public class LongToDateParser extends AbstractParser<Long, Date> {
    @Override
    protected Date onParse(Long value) {
        return new Date(value);
    }
}
