package com.waben.stock.applayer.strategist.wrapper.formatter;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatter implements Formatter<Date> {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public Date parse(String text, Locale locale) throws ParseException {
            return formatter.parse(text);
        }

        @Override
        public String print(Date date, Locale locale) {
            return formatter.format(date);
        }
    }