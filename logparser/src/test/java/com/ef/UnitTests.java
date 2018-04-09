package com.ef;

import com.ef.dao.LogDAO;
import com.ef.exception.InvalidArgsException;
import com.ef.model.Log;
import com.ef.utils.Utils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class UnitTests {
 
    private static final String VALID_DATE = "2018-01-15 00:00:00";
    private static final String INVALID_DATE = "NOT A DATe";
    private static final Map<String,String> VALID_ARGS = new HashMap<>();
    
    @Spy
    private LogDAO logDAO;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        fillArgsWithDefaultValues();
    }
    
    private void fillArgsWithDefaultValues() {
        VALID_ARGS.put("startDate",VALID_DATE);
        VALID_ARGS.put("duration","hourly");
        VALID_ARGS.put("accesslog","some path");
        VALID_ARGS.put("threshold","100");
    }
    
    @Test
    public void testParseDate() throws ParseException{
        Assert.assertThat(Utils.parseDate(VALID_DATE), CoreMatchers.notNullValue());
    }
    
    @Test
    public void isValidDateRegex() {
        Assert.assertTrue(Utils.isValidDate(VALID_DATE));
        Assert.assertFalse(Utils.isValidDate(INVALID_DATE));
    }
    
    @Test(expected = ParseException.class)
    public void testParseDateException() throws ParseException{
        Utils.parseDate("NOT A DATE");
    }
    
    @Test
    public void testArgsValidation() throws InvalidArgsException {
        Utils.validateArgs(VALID_ARGS);
        
        VALID_ARGS.remove("startDate");
        try {
            Utils.validateArgs(VALID_ARGS);
        }catch(InvalidArgsException e) {
            Assert.assertThat(e.getMessage(),CoreMatchers.containsString("Invalid start date"));
            fillArgsWithDefaultValues();
        }

        VALID_ARGS.remove("duration");
        try {
            Utils.validateArgs(VALID_ARGS);
        }catch(InvalidArgsException e) {
            Assert.assertThat(e.getMessage(),CoreMatchers.containsString("Invalid duration"));
            fillArgsWithDefaultValues();
        }

        VALID_ARGS.remove("threshold");
        try {
            Utils.validateArgs(VALID_ARGS);
        }catch(InvalidArgsException e) {
            Assert.assertThat(e.getMessage(),CoreMatchers.containsString("Invalid threshold"));
            fillArgsWithDefaultValues();
        }

        VALID_ARGS.remove("accesslog");
        try {
            Utils.validateArgs(VALID_ARGS);
        }catch(InvalidArgsException e) {
            Assert.assertThat(e.getMessage(),CoreMatchers.containsString("Invalid access log path"));
            fillArgsWithDefaultValues();
        }

        VALID_ARGS.clear();
        try {
            Utils.validateArgs(VALID_ARGS);
        }catch(InvalidArgsException e) {
            Assert.assertThat(e.getMessage(),CoreMatchers.containsString("No Args were informed"));
            fillArgsWithDefaultValues();
        }
    }
    
    @Test
    public void testCreateInsertStatment() {
        Log log = new Log();
        log.setLogDate(VALID_DATE);
        log.setUserAgent("Chrome");
        log.setProtocol("HTTP");
        log.setIpAddress("192.168.0.1");
        log.setStatusCode("200");
        String statement = logDAO.buildInsertStatement(log);

        Assert.assertThat(statement, CoreMatchers.equalTo(String.format("INSERT INTO log ( logDate,ipAddress,protocol,statusCode,userAgent ) VALUES ( '%s','%s','%s','%s','%s' )", log.getLogDate(),log.getIpAddress(),log.getProtocol(),log.getStatusCode(),log.getUserAgent())));
        
    }
    
}
