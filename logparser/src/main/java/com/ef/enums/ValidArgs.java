package com.ef.enums;

public enum ValidArgs {
    
    ACCESS_LOG("accesslog"),
    START_DATE("startDate"),
    DURATION("duration"),
    THRESHOLD("threshold");
    
    private String value;
    
    ValidArgs(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
}
