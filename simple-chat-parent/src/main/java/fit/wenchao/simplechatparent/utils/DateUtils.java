package fit.wenchao.simplechatparent.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils
{

    /**
     * Constants represent common date format string
     */
    public enum DateFormatEnum {
        UGLY_DEFAULT_ONE("EEE MMM dd HH:mm:ss zzz yyyy"),

        DATE_TIME("yyyy-MM-dd hh:mm:ss"),
        DATE_ONLY("yyyy-MM-dd");

        String formatString;

        DateFormatEnum(String formatString) {
            this.formatString = formatString;
        }

        @Override
        public String toString() {
            return formatString;
        }
    }

    /**
     * transform Date obj to specific format String
     * @param format format string
     * @param date Date obj 2 be formatted
     * @return formatted date string
     */
    public static String formatDateWith(String format, Date date) {
        DateFormat df = new SimpleDateFormat(format, Locale.US);
        return df.format(date);
    }

    /**
     * transform Date obj to String using format 'yyyy-MM-dd hh:mm:ss'
     * @param date Date obj 2 be formatted
     * @return formatted date string with format 'yyyy-MM-dd hh:mm:ss'
     */
    public static String formatDate(Date date) {
        return formatDateWith(DateFormatEnum.DATE_TIME.toString(), date);
    }

    /**
     * produce a Date obj from a dateString with specific format
     * @param format the format of dataString 2 be parsed
     * @param dateStr dataString to be parsed
     * @return a Date obj represent the raw dateString
     */
    public static Date parseDateWith(String format, String dateStr) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.US);
        return df.parse(dateStr);
    }

    /**
     * produce a Date obj from a dateString with format string 'yyyy-MM-dd hh:mm:ss'
     * @param dateStr dataString to be parsed. It must conform to format: 'yyyy-MM-dd hh:mm:ss'.
     * @return a Date obj represent the raw dateString
     */
    public static Date parseDateWith(String dateStr) throws ParseException {
        return parseDateWith(DateFormatEnum.DATE_TIME.toString(), dateStr);
    }

    /**
     * transform a date string to another format
     * @param fromFormat format raw date string conforms to
     * @param toFormat target format
     * @param rawDateString date string to be transformed
     * @return transformed date string conforming to format toFormat
     * @throws ParseException if the fromFormat is not compatible with rawDateString, throw ParseException.
     */
    public static String formatDateString(String fromFormat, String toFormat, String rawDateString) throws ParseException {
        Date date = parseDateWith(fromFormat, rawDateString);
        return formatDateWith(toFormat, date);
    }

    public static void main(String[] args) throws ParseException {
        String s = formatDateWith(DateFormatEnum.DATE_ONLY.toString(), new Date());
        System.out.println(s);

        String s1 = formatDate(new Date());
        System.out.println(s1);

        Date date = parseDateWith("2021-11-24 11:14:03");
        System.out.println(date);

        Date date1 = parseDateWith("EEE MMM dd HH:mm:ss zzz yyyy", "Mon Nov 24 11:14:03 CST 2021");
        System.out.println(date1);

        String s2 = formatDateString(DateFormatEnum.UGLY_DEFAULT_ONE.formatString,
                DateFormatEnum.DATE_ONLY.toString(), new Date().toString());
        System.out.println(s2);
    }

}
