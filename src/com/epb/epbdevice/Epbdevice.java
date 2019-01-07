package com.epb.epbdevice;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;

public class Epbdevice {
    public static final String MSG_ID = "msgId";
    public static final String MSG = "msg";
    public static final String RETURN_OK = "OK";
    
    public static void initBat(final String intiFilePathName) {  //D:\\EPBrowser\\EPB\\init.bat
        try {
            int osType = new Epbdevice().getOsType();
            if (osType == 0) {//windows
                new Epbdevice().runAppNoWait("cmd.exe /C start /min " + intiFilePathName); //D:\\EPBrowser\\EPB\\init.bat
            } else if (osType == 1) {//mac
                new Epbdevice().runAppNoWait("sh " + intiFilePathName); // /EPBrowser/EPB/init.command
            }
        } catch (Exception ex) {
            System.out.println("Initial print parameter error!" + ex.getMessage());
        }
    }
    
    public static Map<String, String> printFile(final Connection conn, final String actionType, final String shopId, final String recKey, final String userId) {
        return Epbprinter.printFile(conn, actionType, shopId, recKey, userId);
    }
    
    //
    // private
    //
    
    private int getOsType() {
        int iOS_TYPE = 0;
        String osName = System.getProperty("os.name");
        int iPosition = 0;
        iPosition = osName.indexOf("Windows");
        if (iPosition >= 0) {
            return 0;
        }
        iPosition = osName.indexOf("Mac OS X");
        if (iPosition >= 0) {
            return 1;
        }
        iPosition = osName.indexOf("Linux");
        if (iPosition >= 0) {
            return 2;
        }
        return iOS_TYPE;
    }

    private void runAppNoWait(String sCommand) throws Exception {
        Process child = null;
        try {
            child = Runtime.getRuntime().exec(sCommand);
            InputStream in = child.getInputStream();
            in.close();
        } catch (Exception ex) {
            System.out.println("runAppNoWait error!" + ex.getMessage());
        } finally {
        }
    }
}
