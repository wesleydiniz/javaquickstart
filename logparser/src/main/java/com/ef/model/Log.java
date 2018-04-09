package com.ef.model;

import com.ef.utils.IgnoreField;

public class Log {
    
    @IgnoreField
    private Long id;
    
    private String logDate;
    
    private String ipAddress;
    
    private String protocol;
    
    private String statusCode;
    
    private String userAgent;

    public Log(){
        
    }
    
    public Log(String logDate, String ipAddress, String protocol, String statusCode, String userAgent) {
        this.logDate = logDate;
        this.ipAddress = ipAddress;
        this.protocol = protocol;
        this.userAgent = userAgent;
        this.statusCode = statusCode;
    }


    public String getLogDate() {
        return logDate;
    }

    public void setLogDate(String logDate) {
        this.logDate = logDate;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
