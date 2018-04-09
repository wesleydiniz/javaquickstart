package com.ef.enums;

import java.util.Arrays;

public enum Duration {
    
    HOURLY("hourly"),DAILY("daily");
   
    private String value;

    Duration(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
    
    public static Duration findByValue(String value) {
        return Arrays.asList(Duration.values())
                .stream()
                .filter(d -> d.getValue().equalsIgnoreCase(value))
                .findAny()
                .get();
    }
}
