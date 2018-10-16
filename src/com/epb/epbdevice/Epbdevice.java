package com.epb.epbdevice;

import java.io.InputStream;

public class Epbdevice {
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

//    private void runAppWait(String sCommand) {
//        Process child = null;
//        try {
//            child = Runtime.getRuntime().exec(sCommand);
//            InputStream in = child.getInputStream();
//            int c;
//            while ((c = in.read()) != -1) {
//                char cc = (char) c;
//                System.out.print(cc);//如果你不需要看输出，这行可以注销掉
//
//            }
//            in.close();
//            try {
//                child.waitFor();
//            } catch (InterruptedException ex) {
//                System.out.println("runAppWait error!" + ex.getMessage());
//            }
//        } catch (IOException ex) {
//            System.out.println("runAppWait error!" + ex.getMessage());
//        } finally {
//            try {
//                child.destroy();
//            } catch (Exception ex) {
//                System.out.println("runAppWait error!" + ex.getMessage());
//            }
//        }
//    }
}
