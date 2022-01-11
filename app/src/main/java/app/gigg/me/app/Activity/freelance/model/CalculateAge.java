package app.gigg.me.app.Activity.freelance.model;

import java.util.Calendar;

public class CalculateAge {

    public int calculateAge(long timestamp) {

        Calendar calendar = Calendar.getInstance();

        Calendar calendarnow = Calendar.getInstance();

        calendarnow.getTimeZone();

        calendar.setTimeInMillis(timestamp);

        int getmonth = calendar.get(calendar.MONTH);

        int getyears = calendar.get(calendar.YEAR);

        int currentmonth = calendarnow.get(calendarnow.MONTH);

        int currentyear = calendarnow.get(calendarnow.YEAR);

        return ((currentyear * 12 + currentmonth) - (getyears * 12 + getmonth)) / 12;
    }
}
