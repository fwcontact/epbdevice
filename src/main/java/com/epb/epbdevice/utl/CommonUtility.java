/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epb.epbdevice.utl;

import com.epb.epbdevice.Epbdevice;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 *
 * @author sim_liang
 */
public class CommonUtility {
    
    private static final java.text.SimpleDateFormat DATETIMEFORMAT = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String SPACE = " ";
    
    public static void printVersion() {
        try {
            Properties propertis = new Properties();
            propertis.load(Epbdevice.class.getResourceAsStream("/META-INF/maven/com.epb/epbdevice/pom.properties"));
            System.out.println("epbdevice " + propertis.getProperty("version"));
            propertis.clear();
        } catch (IOException thr) {
            System.out.println("com.epb.epbdevice.Epbdevice.printVersion():" + thr.getMessage());
        }
    }
    
    public static void printLog(final String message) {
        final String displayMessage = DATETIMEFORMAT.format(new Date()) 
                + SPACE + SPACE + SPACE + SPACE + SPACE + SPACE + SPACE + SPACE 
                + message;
        System.out.println(displayMessage);
    }
}
