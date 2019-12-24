package com.example.preschool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReturnMinute {

    public long getMinute(String dateStart, String dateStop){

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        Date d1 = null;

        Date d2 = null;

        try {

            d1 = format.parse(dateStart);

            d2 = format.parse(dateStop);

        } catch (ParseException e) {

        }

        // Get msec from each, and subtract.

        long diff = d2.getTime() - d1.getTime();


        long diffMinutes = diff / (60 * 1000);

        return diffMinutes;

    }
    public long getHour(String dateStart, String dateStop){

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d1 = null;

        Date d2 = null;

        try {

            d1 = format.parse(dateStart);

            d2 = format.parse(dateStop);

        } catch (ParseException e) {

        }

        // Get msec from each, and subtract.

        long diff = d2.getTime() - d1.getTime();

        long diffHours = diff / (60 * 60 * 1000);


        return  diffHours;

    }



}
