package com.ef;

import com.ef.dao.BlockedIPDAO;
import com.ef.dao.LogDAO;
import com.ef.enums.Duration;
import com.ef.enums.ValidArgs;
import com.ef.model.BlockedIP;
import com.ef.model.Log;
import com.ef.utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * This application is responsible for taking a log file and find requests based on given parameters
 * @author Wesley Diniz
 * 
 */
public class Parser {
    
    public static void main(String[] args) {
        try {
            System.out.println("Welcome to Mysql Log Parser");
            // Map all arguments
            Map<String,String> parsedArgs = Utils.parseArgs(args);

            // Validate arguments    
            Utils.validateArgs(parsedArgs);
            
            final String PATH = parsedArgs.get(ValidArgs.ACCESS_LOG.getValue());
            final Date START_DATE = Utils.parseDate(parsedArgs.get(ValidArgs.START_DATE.getValue()));
            final Integer THRESHOLD = Integer.valueOf(parsedArgs.get(ValidArgs.THRESHOLD.getValue()));
            final Duration DURATION = Duration.findByValue(parsedArgs.get(ValidArgs.DURATION.getValue()));
            final Date END_DATE = defineEndDate(START_DATE,DURATION);

            // Persist all logs to database
            LogDAO logDAO = new LogDAO();
            logDAO.saveLogs(loadLogs(PATH));

            // Find Ips that fits the filters            
            List<Log> result = logDAO.findIps(START_DATE,END_DATE,THRESHOLD);

            if (!result.isEmpty()) {
                System.out.println("\nIps found: ");
                result.forEach(l -> System.out.println(l.getIpAddress()));
                BlockedIPDAO blockedIPDAO = new BlockedIPDAO();
                blockedIPDAO.saveBlockedIps(convertLogsToBlockedIP(result, START_DATE,END_DATE,THRESHOLD));
            }

            System.out.println("\n\nParser done! Bye! :)");
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Convert a Log Object to a BlockedIP object 
     * @param logs
     * @param startDate
     * @param endDate
     * @param threshold
     * @return List of BlockedIP
     */
    private static List<BlockedIP> convertLogsToBlockedIP(List<Log> logs, Date startDate, Date endDate, Integer threshold) {
        List<BlockedIP> blockedIPS = new ArrayList<>();
        String startDateString = Utils.parseDateToString(startDate);
        String endDateString = Utils.parseDateToString(endDate);
        logs.forEach(l -> blockedIPS.add(new BlockedIP(l.getIpAddress()
                ,startDateString
                ,endDateString
                ,String.format("Ip blocked due more than %s requests between %s and %s",threshold.toString(),startDateString,endDateString))));
        
        return blockedIPS;
    }

    /**
     * Read an entire log file and return a list o Log object
     * @param path path to log file
     * @return list o Log object
     */
    private static List<Log> loadLogs(String path) {
        List<Log> logs = new ArrayList<>();
        try {
            Stream<String> lines = Files.lines(Paths.get(path));
            System.out.println("\nLoading log...");
            lines.forEach(l -> {
                String[] obj = l.split("[|]");
                logs.add(new Log(obj[0],obj[1],obj[2],obj[3],obj[4]));
            });
            lines.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logs;
    }

    /**
     * Define an end date based on StartDate and Duration
     * @param startDate
     * @param duration
     * @return End Date 
     */
    private static Date defineEndDate(Date startDate, Duration duration) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTime(startDate);
        
        switch (duration) {
            case DAILY:
                calendar.add(Calendar.HOUR,24);
                return calendar.getTime();
            case HOURLY:
                calendar.add(Calendar.HOUR,1);
                return calendar.getTime();
            default:
                return startDate;
        }
        
    }
    
}
