package com.epb.epbdevice;

import com.epb.epbdevice.beans.PrintPool;
import com.epb.epbdevice.utl.CommonUtility;
import com.epb.epbdevice.utl.QrCode2;
import com.epb.epbdevice.utl.StringParser;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Epbnetprinter2 {
    private static final String EMPTY = "";
    private static final String COMMAND_QR = "qr";
    private Socket client;
    private BufferedInputStream bis = null;
    private BufferedOutputStream bos = null;
    
    public static String printPosReceipt(final String ipAddr, final List<PrintPool> printPoolList, final String encoding) {
        return new Epbnetprinter2().doPrintPosReceipt(ipAddr, printPoolList, encoding);
    }
    
    public static String printText(final String ipAddr, final String text) {
        return new Epbnetprinter2().doPrintText(ipAddr, text);
    }
    
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
    
    //
    // private
    //
    
    private String doPrintPosReceipt(final String ipAddr, final List<PrintPool> printPoolList, final String encoding) {
        try {
            CommonUtility.printLog("connecting:" + ipAddr);
            boolean opened = doOpenEpbNetPrinter(ipAddr, encoding);
            CommonUtility.printLog("connected:" + ipAddr);
            if (opened) {
                doPrintPosReceipt(printPoolList);
                doCloseNetPrinter();
                return EMPTY;
            } else {
                return "Failed to open net printer port" + "->" + ipAddr;
            }
        } catch (Throwable thr) {
            return "Failed to print receipt" + "->" + thr.getMessage();
        }
    }
    
    private String doPrintText(final String ipAddr, final String text) {
        boolean opened = doOpenEpbNetPrinter(ipAddr, null);
        if (opened) {
            doPrintText(text);
            doCloseNetPrinter();
            return EMPTY;
        } else {
            return "Failed to open net printer port" + "->" + ipAddr;
        }
    }
    
    private boolean doOpenEpbNetPrinter(final String ip, final String encoding) {
        try {
            if (client == null) {
                client = new java.net.Socket();
                client.connect(new InetSocketAddress(ip, 9100), 1000); // 创建一个 socket
                bis = new BufferedInputStream(client.getInputStream()); 
                bos = new BufferedOutputStream(client.getOutputStream()); 
            }
            return true;
        } catch (IOException ex) {
            if (bos != null) {
                try {
                    bos.close();
                    bos = null;
                } catch (Throwable thr) {
                    System.out.println("com.epb.epbdevice.Epbnetprinter2.openEpbNetPrinter()" + ":" + thr.getMessage());
                }
            }
            
            if (bis != null) {
                try {
                    bis.close();
                } catch (Throwable thr) {
                    System.out.println("com.epb.epbdevice.Epbnetprinter2.openEpbNetPrinter()" + ":" + thr.getMessage());
                }
            }
            
            if (client != null) {
                try {
                    client.close();
                    client = null;
                } catch (IOException ioe) {
                    System.out.println("com.epb.epbdevice.Epbnetprinter2.openEpbNetPrinter()" + ":" + ioe.getMessage());
                }
            }
            System.out.println("com.epb.epbdevice.Epbnetprinter2.openEpbNetPrinter()" + ":" + ex.getMessage());
            return false;
        }
    }
    
    private void doPrintPosReceipt(final List<PrintPool> printPoolList) {
        try {
            String output;
            boolean skipThisLine = false;
            BigDecimal poolLineNo = null;
            String line;
            for (PrintPool linePrintPool : printPoolList) {
                if (poolLineNo != null && poolLineNo.compareTo(linePrintPool.getLineNo()) == 0) {
                    continue;
                }
                poolLineNo = linePrintPool.getLineNo();
                line = EMPTY;
                for (PrintPool printPool : printPoolList) {
                    final BigDecimal lineNo = printPool.getLineNo();
                    if (poolLineNo != null && lineNo.compareTo(poolLineNo) != 0) {
                        continue;
                    }
//                    poolLineNo = linePrintPool.getLineNo();

                    String printCommand = printPool.getPrintCommand() == null ? EMPTY : printPool.getPrintCommand();
                    final String const1 = printPool.getConst1() == null ? EMPTY : printPool.getConst1();
                    final String const2 = printPool.getConst2() == null ? EMPTY : printPool.getConst2();
//                    final String format = printPool.getFormat() == null ? EMPTY : printPool.getFormat();
                    final Integer lineLength = printPool.getLength() == null ? 0 : printPool.getLength().intValue();
                    final String align = printPool.getAlign() == null ? EMPTY : printPool.getAlign();
                    final String breakFlg = printPool.getBreakFlg() == null ? EMPTY : printPool.getBreakFlg();
                    final String fillBlankFlg = printPool.getFillBlankFlg() == null ? "N" : printPool.getFillBlankFlg();
                    output = EMPTY;
                    if (printCommand != null && !EMPTY.equals(printCommand) && !COMMAND_QR.equals(printCommand)) {
                        //输出
                        line += StringParser.getSplitString(printCommand);
                    }
                    output = printPool.getVal();

                    if (printCommand != null && printCommand.length() != 0 && COMMAND_QR.equals(printCommand)) {
                        String qrDate = (const1 == null ? EMPTY : const1.trim()) + (output == null ? EMPTY : output.trim()) + (const2 == null ? EMPTY : const2.trim());
                        if (qrDate != null && !EMPTY.equals(qrDate)) {
                            QrCode2.printQrCode(bos, qrDate);
                        }     
                        continue;
                    }

                    if ((const1 == null || EMPTY.equals(const1)) 
                            && (const2 == null || EMPTY.equals(const2))
                            && (output == null || EMPTY.equals(output))) {
                        output = EMPTY;
                    } else {
                        output = (const1 == null ? EMPTY : const1) + (output == null ? EMPTY : output) + (const2 == null ? EMPTY : const2);
                    }

                    //如果有Command命令，没有内容，不需要补空格
                    if ((printCommand != null && !EMPTY.equals(printCommand) && (output == null || EMPTY.equals(output)))) {
                        // do nothing
                    } else {    
                        output = StringParser.setStringAlignment(output, align, lineLength, breakFlg, fillBlankFlg);
                    }

                    line += output;

                    if (poolLineNo == null) {
                    }
                }

                if (!skipThisLine) {
                    if (line == null || line.length() == 0) {
                        bos.flush();
                    } else {
                        bos.write(line.getBytes());
                        bos.write("\r\n".getBytes());// 打印完毕自动走纸
                        bos.flush();
                    }
                }
                skipThisLine = false;
            }
        } catch (Throwable ex) {
            System.out.println("Print receipt head Failed!" + ex);
        }
    }

    private void doPrintText(final String lineText) {
        try {
            if (bos == null) {
                // do nothing
                System.out.println("please open net printer first");
                return;
            }
            bos.write(lineText.getBytes());// 打印完毕自动走纸
            bos.flush();
        } catch (Throwable ex) {
            System.out.println("com.epb.epbdevice.Epbnetprinter2.printText()" + ":" + ex.getMessage());
        }
    }
    
//    private void doPrintCmd(final String printerCmd) {
//        try {
//            if (socketWriter == null) {
//                // do nothing
//                System.out.println("please open net printer first");
//                return;
//            }
//            if (printerCmd == null || printerCmd.trim().length() == 0) {
//                // do nothing
//                return;
//            }
//
//            final String commandSpilt = StringParser.getSplitString(printerCmd);
//            socketWriter.write(commandSpilt);
//        } catch (Throwable ex) {
//            System.out.println("com.epb.epbdevice.Epbnetprinter.printCmd()" + ":" + ex.getMessage());
//        }
//    }
    
//    private void cutPaper() {
//        printCmd(CUT_PAPER_DEFAULT_CMD);
//    }
    private void doCloseNetPrinter() {
        try {
            if (bos != null) {
                try {
                    bos.close();
                    bos = null;
                } catch (Throwable thr) {
                    System.out.println("com.epb.epbdevice.Epbnetprinter2.doCloseNetPrinter()" + ":" + thr.getMessage());
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (Throwable thr) {
                    System.out.println("com.epb.epbdevice.Epbnetprinter2.doCloseNetPrinter()" + ":" + thr.getMessage());
                }

            }
            if (client != null) {
                try {
                    client.close();
                    client = null;
                } catch (IOException ioe) {
                    System.out.println("com.epb.epbdevice.Epbnetprinter2.doCloseNetPrinter()" + ":" + ioe.getMessage());
                }
            }
        } catch (Throwable ex) {
            System.out.println("com.epb.epbdevice.Epbnetprinter2.doCloseNetPrinter()" + ":" + ex.getMessage());
        } finally {
            CommonUtility.printLog("disconnect");
        }
    }
}
