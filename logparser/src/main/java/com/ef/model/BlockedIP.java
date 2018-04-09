package com.ef.model;

public class BlockedIP {
    
    private String ipAddress;
    
    private String startDate;
    
    private String endDate;
    
    private String reason;

    public BlockedIP(String ipAddress, String startDate, String endDate, String reason) {
        this.ipAddress = ipAddress;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
