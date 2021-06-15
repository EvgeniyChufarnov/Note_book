package com.example.notebook.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class Utils {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

    public static String dateLongToString(long date) {
        return dateFormat.format(date);
    }

    public static long dateStringToLong(String date) {
        try {
            return Objects.requireNonNull(dateFormat.parse(date)).getTime();
        } catch (ParseException | NullPointerException e) {
            return Calendar.getInstance().getTimeInMillis();
        }
    }
}
