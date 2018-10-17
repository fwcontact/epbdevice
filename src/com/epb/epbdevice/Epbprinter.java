package com.epb.epbdevice;

import java.io.FileOutputStream;
import java.io.IOException;

import com.epb.epbdevice.utl.StringParser;

public class Epbprinter {
//	    private static final String EMPTY = "";

    private static final String COM = "COM";
    private static final String LPT = "LPT";
    private static final String CUT_PAPER_DEFAULT_CMD = "27,105";
    private static final String OPEN_DRAWER_DEFAULT_CMD = "27,112,0,48,255";
    private static final byte[] CTL_LF = ("\r\n").getBytes();          // Print and line feed
    private static FileOutputStream ioPrint;

    public static boolean OpenEpbprinter(final String serialParallelPort) {
        try {
            if (ioPrint == null) {
                ioPrint = new FileOutputStream(serialParallelPort);
                return true;
            }
            return true;
        } catch (IOException ex) {
            ioPrint = null;
            System.out.println("com.epb.epbdevice.Epbprinter.Epbprinter()" + ":" + ex.getMessage());
            return false;
        }
    }

    public static void printText(final String lineText) {
        try {
            if (ioPrint == null) {
                // do nothing
                System.out.println("please open printer first");
                return;
            }
            ioPrint.write(lineText.getBytes());
            ioPrint.write(CTL_LF);
        } catch (IOException ex) {
//            ioPrint = null;
            System.out.println("com.epb.epbdevice.Epbprinter.printText()" + ":" + ex.getMessage());
        }
    }

    public static void printCmd(final String printerCmd) {
        try {
            if (ioPrint == null) {
                // do nothing
                System.out.println("please open printer first");
                return;
            }
            if (printerCmd == null || printerCmd.trim().length() == 0) {
                // do nothing
                return;
            }

            final String commandSpilt = StringParser.getSplitString(printerCmd);
            ioPrint.write(commandSpilt.getBytes());
        } catch (IOException ex) {
//            ioPrint = null;
            System.out.println("com.epb.epbdevice.Epbprinter.printCmd()" + ":" + ex.getMessage());
        }
    }

    public static void closePrinter() {
        try {
            if (ioPrint == null) {
                // do nothing
                return;
            }
            ioPrint.flush();
            ioPrint.close();
        } catch (IOException ex) {
            ioPrint = null;
            System.out.println("com.epb.epbdevice.Epbprinter.closePrinter()" + ":" + ex.getMessage());
        }
    }

    public static void cutPaper() {
        printCmd(CUT_PAPER_DEFAULT_CMD);
    }

//	    public static void openDrawer() {
//	        printCmd(OPEN_DRAWER_DEFAULT_CMD);
//	    }
    /**
     * @param serialParallelPort cash drawer serialParallelPort as same as
     * printer serialParallelPort
     * @param openCashDrawerCmd open cash drawer command, default
     * 27,112,0,48,255
     */
    public static void openDrawer(final String serialParallelPort, final String openCashDrawerCmd) {
        try {
            // openCashDrawerCmd default 27,112,0,48,255
            if (serialParallelPort.toUpperCase().startsWith(COM) || serialParallelPort.toUpperCase().startsWith(LPT)) {
                if (ioPrint == null) {
                    FileOutputStream drawerIoPrint;
                    drawerIoPrint = null;
                    try {
                        drawerIoPrint = new FileOutputStream(serialParallelPort);
                        final String commandSpilt = StringParser.getSplitString(
                                openCashDrawerCmd == null || openCashDrawerCmd.trim().length() == 0
                                ? OPEN_DRAWER_DEFAULT_CMD
                                : openCashDrawerCmd);
                        drawerIoPrint.write(commandSpilt.getBytes());
                    } catch (IOException ex) {
                        drawerIoPrint = null;
                    } finally {
                        if (drawerIoPrint != null) {
                            drawerIoPrint.close();
                        }
                    }
                } else {
                    final String commandSpilt = StringParser.getSplitString(
                            openCashDrawerCmd == null || openCashDrawerCmd.trim().length() == 0
                            ? OPEN_DRAWER_DEFAULT_CMD
                            : openCashDrawerCmd);
                    ioPrint.write(commandSpilt.getBytes());
                }
            } else {
                // DO NOTHING
            }
        } catch (IOException ex) {
            System.out.println("com.epb.epbdevice.Epbprinter.openDrawer()" + ":" + ex.getMessage());
        }
    }
}
