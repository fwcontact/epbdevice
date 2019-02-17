/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epb.epbdevice.utl;

import com.epb.epbdevice.Epbdevice;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author sim_liang
 */
public class CommonUtility {
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
}
