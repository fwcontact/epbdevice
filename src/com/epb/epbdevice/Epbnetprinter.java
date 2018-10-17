package com.epb.epbdevice;

import com.epb.epbdevice.utl.StringParser;
import java.io.IOException;

import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Epbnetprinter {
    private static final String EMPTY = "";
//    private static final String COM = "COM";
//    private static final String LPT = "LPT";
    private static final String CUT_PAPER_DEFAULT_CMD = "27,105";
//    private static final String OPEN_DRAWER_DEFAULT_CMD = "27,112,0,48,255";
//    private static final byte[] CTL_LF = ("\r\n").getBytes();          // Print and line feed
    private static Socket client;
    private static PrintWriter socketWriter;
    
    public static boolean checkNetPort(final String ip) {
        try {
            if (ip == null || EMPTY.equals(ip)) {
                return false;
            }
            final Pattern patt = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
            final Matcher mat = patt.matcher(ip);
            final boolean isIp = mat.matches();
            return isIp;
        } catch (Throwable throwable) {
            System.out.println("com.epb.epbdevice.Epbnetprinter.checkNetPort()" + ":" + throwable.getMessage());
            return false;
        }
    }

    public static boolean openEpbNetPrinter(final String ip) {
        try {
            if (client == null) {
                client = new java.net.Socket();
                client.connect(new InetSocketAddress(ip, 9100), 1000); // 创建一个 socket
                socketWriter = new PrintWriter(client.getOutputStream());// 创建输入输出数据流
            }
            return true;
        } catch (IOException ex) {
            if (socketWriter != null) {
                try {
                    socketWriter.close();
                    socketWriter = null;
                } catch (Throwable thr) {
                    System.out.println("com.epb.epbdevice.Epbnetprinter.openEpbNetPrinter()" + ":" + thr.getMessage());
                }
            }
            if (client != null) {
                try {
                    client.close();
                    client = null;
                } catch (IOException ioe) {
                    System.out.println("com.epb.epbdevice.Epbnetprinter.openEpbNetPrinter()" + ":" + ioe.getMessage());
                }
            }
            System.out.println("com.epb.epbdevice.Epbnetprinter.openEpbNetPrinter()" + ":" + ex.getMessage());
            return false;
        }
    }

    public static void printText(final String lineText) {
        try {
            if (socketWriter == null) {
                // do nothing
                System.out.println("please open net printer first");
                return;
            }
            socketWriter.println((lineText));// 打印完毕自动走纸
            socketWriter.flush();
        } catch (Throwable ex) {
            System.out.println("com.epb.epbdevice.Epbnetprinter.printText()" + ":" + ex.getMessage());
        }
    }
    
    public static void printCmd(final String printerCmd) {
        try {
            if (socketWriter == null) {
                // do nothing
                System.out.println("please open net printer first");
                return;
            }
            if (printerCmd == null || printerCmd.trim().length() == 0) {
                // do nothing
                return;
            }

            final String commandSpilt = StringParser.getSplitString(printerCmd);
            socketWriter.write(commandSpilt);
        } catch (Throwable ex) {
            System.out.println("com.epb.epbdevice.Epbnetprinter.printCmd()" + ":" + ex.getMessage());
        }
    }
    
    public static void cutPaper() {
        printCmd(CUT_PAPER_DEFAULT_CMD);
    }

    public static void closeNetPrinter() {
        try {
            if (client == null) {
                // do nothing
                return;
            }
            socketWriter.flush();
            socketWriter.close();
            client.close();
        } catch (IOException ex) {
            if (socketWriter != null) {
                try {
                    socketWriter.close();
                    socketWriter = null;
                } catch (Throwable thr) {
                    System.out.println("com.epb.epbdevice.Epbnetprinter.openEpbNetPrinter()" + ":" + thr.getMessage());
                }
            }
            if (client != null) {
                try {
                    client.close();
                    client = null;
                } catch (IOException ioe) {
                    System.out.println("com.epb.epbdevice.Epbnetprinter.closePrinter()" + ":" + ioe.getMessage());
                }
            }
            System.out.println("com.epb.epbdevice.Epbnetprinter.closePrinter()" + ":" + ex.getMessage());
        }
    }
}
