package com.opion.knowlio.Utilities;

import android.text.format.DateUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeAgoConverter {

    public static String getTimeAgo(String dateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a, dd/MM/yyyy", Locale.getDefault());
        try {
            Date date = sdf.parse(dateTime);
            long timeInMillis = date.getTime();

            long currentTime = System.currentTimeMillis();
            long timeDifference = currentTime - timeInMillis;

            // Using Android's DateUtils to format time difference
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                    timeInMillis,
                    currentTime,
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE);

            return timeAgo.toString();

        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid date format";
        }
    }
}
