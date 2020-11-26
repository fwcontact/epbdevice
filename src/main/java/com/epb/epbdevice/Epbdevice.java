package com.epb.epbdevice;

import com.epb.epbdevice.printer.Epbprinter;
import com.epb.epbdevice.printer.Epbnetprinter;
import com.epb.epbdevice.printer.Epbcomprinter;
import com.epb.epbdevice.utl.CommonUtility;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Epbdevice {
    public static final String MSG_ID = "msgId";
    public static final String MSG = "msg";
    public static final String RETURN_OK = "OK";
    public static final String RETURN_FAIL = "Fail";
    
    private static final String COM = "COM";
    private static final String LPT = "LPT";
    private static final String EMPTY = "";
    private static final Log LOG = LogFactory.getLog(Epbdevice.class);
//    private static final Log LOG = LogFactory.getLog(Epbescpos.class);
    
    public synchronized static void initBat(final String intiFilePathName) {  //D:\\EPBrowser\\EPB\\init.bat
        try {
            int osType = new Epbdevice().getOsType();
            if (osType == 0) {//windows
                new Epbdevice().runAppNoWait("cmd.exe /C start /min " + intiFilePathName); //D:\\EPBrowser\\EPB\\init.bat
            } else if (osType == 1) {//mac
                new Epbdevice().runAppNoWait("sh " + intiFilePathName); // /EPBrowser/EPB/init.command
            }
        } catch (Exception ex) {
            LOG.error("Initial print parameter error!" , ex);
        }
    }
    
    public synchronized static Map<String, String> printFile(final Connection conn, final String recKey, final String userId) {
        // log version
        CommonUtility.printVersion();
        
        return Epbprinter.printFile(conn, recKey, userId);
    }
    
    /**
     * Message queue to trigger printer in LAN
     *
     * @param printQueueMap  Map<String, Object>
     * @return Map<String, String> 
     */
    public synchronized static Map<String, String> printFile(final Map<String, Object> printQueueMap) {
        // log version
        CommonUtility.printVersion();
        
        return Epbprinter.printFileMQ(printQueueMap);
    }
    
    public synchronized static Map<String, String> testConnectPrinter(final String printPort) {
        Map<String, String> returnMap = new HashMap<>();
        if (printPort != null
                && (printPort.toUpperCase().startsWith(COM) || printPort.toUpperCase().startsWith(LPT) || !Epbnetprinter.checkNetPort(printPort))) {
            String returnMsg = Epbcomprinter.testConnectPrinter(printPort);
            if (returnMsg == null || EMPTY.equals(returnMsg)) {
                returnMap.put(Epbdevice.MSG_ID, Epbdevice.RETURN_OK);
                returnMap.put(Epbdevice.MSG, EMPTY);
            } else {
                returnMap.put(Epbdevice.MSG_ID, RETURN_FAIL);
                returnMap.put(Epbdevice.MSG, returnMsg);
            }
            return returnMap;
        } else {
            String returnMsg = Epbnetprinter.testConnectPrinter(printPort);
            if (returnMsg == null || EMPTY.equals(returnMsg)) {
                returnMap.put(Epbdevice.MSG_ID, Epbdevice.RETURN_OK);
                returnMap.put(Epbdevice.MSG, EMPTY);
            } else {
                returnMap.put(Epbdevice.MSG_ID, RETURN_FAIL);
                returnMap.put(Epbdevice.MSG, returnMsg);
            }
            return returnMap;
        }
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
        try {
            Process child = Runtime.getRuntime().exec(sCommand);
            InputStream in = child.getInputStream();
            in.close();
        } catch (IOException ex) {
            LOG.error("runAppNoWait error!", ex);
        } finally {
        }
    }
    
    //
    // test
    //
    
    public static void main(String args[]) {
//        Epbnetprinter.openEpbNetPrinter("192.168.1.68");
//        Epbnetprinter.printText("test 1");
//        Epbnetprinter.printText("test 2");
//        Epbnetprinter.printText("test 3");        
//        Epbnetprinter.closeNetPrinter();
//        Epbnetprinter.openEpbNetPrinter("192.168.1.68");
//        Epbnetprinter.printText("test 4"); 
//        Epbnetprinter.printText("test 5"); 
//        Epbnetprinter.printText("test 6"); 
//        Epbnetprinter.printText("test 7"); 
//        Epbnetprinter.printText("test 8"); 
//        Epbnetprinter.printText("test 9");        
//        Epbnetprinter.closeNetPrinter();  
//        Epbnetprinter.printText("192.168.1.68", "text1");
//        Epbnetprinter.printText("192.168.1.68", "text2");
//        Epbnetprinter.printText("192.168.1.68", "text3");
//        Epbnetprinter.printText("192.168.1.68", "text4");
        try {
            String driver = "oracle.jdbc.driver.OracleDriver"; 
            String url = "jdbc:oracle:thin:@192.168.1.11:1521:orcl";
            String user = "EPBSH";
            String pwd = "EPBSH";
            Class.forName(driver);
            System.out.println("driver is ok");

            Connection conn = DriverManager.getConnection(url, user, pwd);
            System.out.println("conection is ok");
            String recKey = "123";        // PRINTMAS.rec_key
            String userId = "Admin";      // Waitier OR Cashier
            final Map<String, String> returnMap = Epbdevice.printFile(conn, recKey, userId);
            if ("OK".equals(returnMap.get("msgId"))) {
                // printer OK
            } else {
                // error
                System.out.println(returnMap.get("msg"));
            }
        } catch (ClassNotFoundException | SQLException thr) {
            System.out.println("thr:" + thr.getMessage());
        }
    }
}
