package com.ef.dao;

import com.ef.model.Log;
import com.ef.utils.Utils;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class LogDAO extends BaseDAO<Log>{

    @Override
    protected Class<Log> getEntityClass() {
        return Log.class;
    }
    
    public void saveLogs(List<Log> logs) {
        if (logs != null) {
            Long begin = System.currentTimeMillis();
            System.out.println("Saving requests to database...");
            List<List<Log>> partitions = Lists.partition(logs,10000);
            partitions.forEach(this::bulkInsert);
            Long end = System.currentTimeMillis();
            System.out.println("All requests were saved. Total time: " + (end - begin)/1000 + " seconds.");
        }
    }
    
    public List<Log> findIps(Date startDate, Date endDate, Integer threshold) {
        System.out.println(String.format("\nSearching for ips that made more than %s requests between %s and %s ...", threshold,Utils.parseDateToString(startDate),Utils.parseDateToString(endDate)));
        StringBuilder query = new StringBuilder();
        query.append("SELECT log.ipAddress, count(log.id) as total ")
                .append("FROM logparser.log log ")
                .append("WHERE log.logDate BETWEEN %s AND %s ")
                .append("GROUP BY log.ipAddress ")
                .append("HAVING total > %s ");
                
        String finalQuery = String.format(query.toString(),
                Utils.parseDateToQuotedString(startDate), 
                Utils.parseDateToQuotedString(endDate),threshold);
        
        return find(finalQuery);
    } 
    
}
