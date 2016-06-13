package org.stargazerlan.alphastock;

import android.util.Log;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Lan Baoping on 2016/5/31.
 */
public class UpdatePeriod {
    public static boolean isInPeriod() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek != Calendar.SATURDAY || dayOfWeek != Calendar.SUNDAY) {
            if (hour >= 12 && hour < 13) return false;
            if (hour >= 9 && hour <= 15) return true;
        }
        return false;
    }
}
