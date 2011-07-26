package org.mycore.oai.pmh.dataprovider;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.mycore.oai.pmh.Granularity;

/**
 * Contains util methods to support the ISO8601 UTC date format.
 * 
 * @author Matthias Eichner
 */
public abstract class DateUtils {

    private static Logger LOGGER = Logger.getLogger(DateUtils.class);

    private static Calendar CALENDAR = Calendar.getInstance();

    public static Granularity currentGranularity = Granularity.YYYY_MM_DD;

    public static DateFormat utcDay;

    public static DateFormat utcSecond;

    static {
        utcDay = new SimpleDateFormat("yyyy-MM-dd");
        utcSecond = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    }

    /**
     * Sets a new static granularity.
     * 
     * @param granularity the granularity to set
     */
    public static void setGranularity(Granularity granularity) {
        currentGranularity = granularity;
    }

    public static Granularity getGranularity() {
        return currentGranularity;
    }

    /**
     * Formats a Date into a date/time string.
     * 
     * @param date
     *            the time value to be formatted into a time string.
     * @return the formatted time string. In YYYY-MM-DDThh:mm:ssZ or YYYY-MM-DD
     */
    public static String formatUTC(Date date) {
        if (date == null)
            return null;
        if (Granularity.YYYY_MM_DD.equals(currentGranularity)) {
            return utcDay.format(date);
        } else {
            return utcSecond.format(date);
        }
    }

    /**
     * Parses text from the beginning of the given string to produce a date.
     * 
     * @param date
     *            a string to parse
     * @return A Date parsed from the string.
     */
    public static Date parseUTC(String date) {
        try {
            return parse(date);
        } catch (ParseException pExc) {
            LOGGER.error("while parsing date " + date, pExc);
        }
        return null;
    }

    public static Date parse(String date) throws ParseException {
        if (date == null)
            return null;
        if (date.contains("T") && date.endsWith("Z"))
            return utcSecond.parse(date);
        return utcDay.parse(date);
    }

    /**
     * Returns the most likely granularity of a date. If hour, minute, second and milisecond of the date are zero, the day granularity is returned.
     * 
     * @param date
     *            date to check
     * @return the most likely granularity of the date
     */
    public static Granularity getGranularity(Date date) {
        Calendar calendar = CALENDAR;
        synchronized (calendar) {
            calendar.setTime(date);
            if (calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 0 && calendar.get(Calendar.SECOND) == 0
                    && calendar.get(Calendar.MILLISECOND) == 0) {
                return Granularity.YYYY_MM_DD;
            }
            return Granularity.YYYY_MM_DD_THH_MM_SS_Z;
        }
    }

    /**
     * Returns the granularity of a date.
     * 
     * @param date
     * @return
     * @throws ParseException
     */
    public static Granularity getGranularity(String date) throws ParseException {
        parse(date);
        if (date.contains("T") && date.contains("Z")) {
            return Granularity.YYYY_MM_DD_THH_MM_SS_Z;
        }
        return Granularity.YYYY_MM_DD;
    }

    /**
     * Returns the current date.
     * 
     * @return date at current time
     */
    public static Date getCurrentDate() {
        Calendar cal = CALENDAR;
        synchronized (cal) {
            cal.setTime(new Date(System.currentTimeMillis()));
            return cal.getTime();
        }
    }

}