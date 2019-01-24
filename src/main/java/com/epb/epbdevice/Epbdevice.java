package com.epb.epbdevice;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.Properties;

public class Epbdevice {
    public static final String MSG_ID = "msgId";
    public static final String MSG = "msg";
    public static final String RETURN_OK = "OK";
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
            System.out.println("Initial print parameter error!" + ex.getMessage());
        }
    }
    
    public synchronized static Map<String, String> printFile(final Connection conn, final String recKey, final String userId) {
        // log version
        printVersion();
        
        return Epbprinter.printFile(conn, recKey, userId);
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
    
    private static void printVersion() {
        try {
            Properties propertis = new Properties();
            propertis.load(Epbdevice.class.getResourceAsStream("/META-INF/maven/com.epb/epbdevice/pom.properties"));
            System.out.println("epbdevice " + propertis.getProperty("version"));
            propertis.clear();
        } catch (IOException thr) {
            System.out.println("com.epb.epbdevice.Epbdevice.printVersion():" + thr.getMessage());
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
            if (Epbdevice.RETURN_OK.equals(returnMap.get(Epbdevice.MSG_ID))) {
                // printer OK
            } else {
                // error
                System.out.println(returnMap.get(Epbdevice.MSG));
            }
        } catch (Throwable thr) {
            System.out.println("thr:" + thr.getMessage());
        }
    }
}
