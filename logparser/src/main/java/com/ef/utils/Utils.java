package com.ef.utils;

import com.ef.enums.Duration;
import com.ef.enums.ValidArgs;
import com.ef.exception.InvalidArgsException;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Parse args[] to an Map to be easily handled
     * @param args
     * @return Map of arguments
     */
    public static Map<String, String> parseArgs(String[] args) {
        final Map<String, String> parsedArgs = new HashMap<>();
        if (args != null && args.length > 0) {
            Arrays.asList(args).forEach(
                    a -> parsedArgs.put(a.split("=")[0].replaceAll("-",""),
                            a.split("=")[1])
            );
        }
        return parsedArgs;
    }

    /**
     *  Validates a given Map of arguments and launches an Exception in case of any invalid value
     * @param args
     * @throws InvalidArgsException
     */
    public static void validateArgs(Map<String,String> args) throws InvalidArgsException {
        if (args.isEmpty()) {
            throw new InvalidArgsException("No Args were informed");
        }
        
        if (args.get(ValidArgs.ACCESS_LOG.getValue()) == null) {
            throw new InvalidArgsException("Invalid access log path");
        }

        if (args.get(ValidArgs.DURATION.getValue()) == null 
                || (!args.get(ValidArgs.DURATION.getValue()).equals(Duration.DAILY.getValue()) 
                    && !args.get(ValidArgs.DURATION.getValue()).equals(Duration.HOURLY.getValue()))) {
            throw new InvalidArgsException("Invalid duration. It must be daily or hourly");
        }

        if (args.get(ValidArgs.THRESHOLD.getValue()) == null
                || !StringUtils.isNumeric(args.get(ValidArgs.THRESHOLD.getValue()))
                ) {
            throw new InvalidArgsException("Invalid threshold. It must be an integer");
        }

        if (args.get(ValidArgs.START_DATE.getValue()) == null || !isValidDate(args.get(ValidArgs.START_DATE.getValue()))) {
            throw new InvalidArgsException("Invalid start date. Date format must be yyyy-MM-dd HH:mm:ss");
        }
    }

    /**
     * Check if a date has a correct format
     * @param date
     * @return
     */
    public static boolean isValidDate(String date) {
        date = date.replaceAll("[.]"," ");
        return date.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
    }

    /**
     * Parse a String to a Date Object
     * @param date
     * @return
     */
    public static Date parseDate(String date) throws ParseException{
        try {
            date = date.replaceAll("[.]"," ");
            return formatter.parse(date);
        } catch (ParseException e) {
            throw e;
        }
    }

    /**
     * Parse a date object to a quoted string
     * @param date
     * @return
     */
    public static String parseDateToQuotedString(Date date) {
        return "'"+formatter.format(date)+"'";
    }

    /**
     * Parse a date object to a quoted string
     * @param date
     * @return
     */
    public static String parseDateToString(Date date) {
        return formatter.format(date);
    }
}
