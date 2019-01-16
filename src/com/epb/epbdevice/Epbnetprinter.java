package com.epb.epbdevice;

import com.epb.epbdevice.beans.PrintPool;
import com.epb.epbdevice.utl.Epbescpos;
import com.epb.epbdevice.utl.StringParser;
import java.io.IOException;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Epbnetprinter {
    private static final String EMPTY = "";
//    private static final String COM = "COM";
//    private static final String LPT = "LPT";
    private static final String CUT_PAPER_DEFAULT_CMD = "27,105";
//    private static final String OPEN_DRAWER_DEFAULT_CMD = "27,112,0,48,255";
//    private static final byte[] CTL_LF = ("\r\n").getBytes();          // Print and line feed
    private static final String SPACE = " ";
    private static final String COMMAND_QR = "qr";
    private static final String COMMAND_QR115200 = "qr115200";
    private static final String COMMA = ",";
    private static final String COMMAND_IMAGE = "pi";
    private static final String COMMAND_IMAGE115200 = "pi115200";
    private static final String COMMAND_BARCODE128 = "b128";
    private static final String COMMAND_BARCODE128_115200 = "b128_115200";
    private Socket client;
    private PrintWriter socketWriter;
    
    public static String printPosReceipt(final String ipAddr, final List<PrintPool> printPoolList) {
        return new Epbnetprinter().doPrintPosReceipt(ipAddr, printPoolList);
    }
    
    public static String printText(final String ipAddr, final String text) {
        return new Epbnetprinter().doPrintText(ipAddr, text);
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
    
    private String doPrintPosReceipt(final String ipAddr, final List<PrintPool> printPoolList) {
        try {
            boolean opened = doOpenEpbNetPrinter(ipAddr);
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
        boolean opened = doOpenEpbNetPrinter(ipAddr);
        if (opened) {
            doPrintText(text);
            doCloseNetPrinter();
            return EMPTY;
        } else {
            return "Failed to open net printer port" + "->" + ipAddr;
        }
    }
    
    private boolean doOpenEpbNetPrinter(final String ip) {
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
    
    private void doPrintPosReceipt(final List<PrintPool> printPoolList) {
        try {
            String output;
            int position;
            int qrSize;
            String imagePath;
            String[] qrArray;
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
                    final String align = printPool.getAlign() == null ? EMPTY : printPool.getAlign().toString();
                    final String breakFlg = printPool.getBreakFlg() == null ? EMPTY : printPool.getBreakFlg().toString();
                    final String fillBlankFlg = printPool.getFillBlankFlg() == null ? "N" : printPool.getFillBlankFlg().toString();
                    output = EMPTY;
                    if (!(printCommand == null || EMPTY.equals(printCommand))) {
                        //输出
                        line += StringParser.getSplitString(printCommand);
                    }
                    output = printPool.getVal();

                    if (printCommand != null && printCommand.length() != 0
                            && (printCommand.startsWith(COMMAND_QR) || printCommand.startsWith(COMMAND_QR115200))) {
                        qrArray = printCommand.split(COMMA);
                        if (qrArray.length >= 2) {
                            try {
                                position = Integer.parseInt(qrArray[1]);
                            } catch (Throwable ex) {
                                position = 0;
                            }
                            try {
                                qrSize = Integer.parseInt(qrArray[2]);
                            } catch (Throwable ex) {
                                qrSize = 150;
                            }
                            if (printCommand.startsWith(COMMAND_QR115200)) {
                                Epbescpos.printQRCodeBaudrate115200(socketWriter, position, output, qrSize);
                            } else {
                                Epbescpos.printQRCode(socketWriter, position, output, qrSize);
                            }
                            line = EMPTY;
                            continue;
                        }
                    } else if (printCommand != null && printCommand.length() != 0
                            && (printCommand.startsWith(COMMAND_IMAGE) || printCommand.startsWith(COMMAND_IMAGE115200))) {
                        qrArray = printCommand.split(COMMA);
//                        LOG.debug("----printCommand:" + printCommand);
                        if (qrArray.length == 3) {
                            try {
                                position = Integer.parseInt(qrArray[1]);
                            } catch (Throwable ex) {
                                position = 0;
                            }
                            try {
                                imagePath = qrArray[2];
                            } catch (Throwable ex) {
                                imagePath = EMPTY;
                            }
                            if (imagePath != null && imagePath.length() != 0) {
                                if (printCommand.startsWith(COMMAND_IMAGE115200)) {
                                    Epbescpos.printImageBaudrate115200(socketWriter, position, imagePath);
                                } else {
                                    Epbescpos.printImage(socketWriter, position, imagePath);
                                }
                                line = EMPTY;
                                continue;
                            }
                        }
                    } else if (printCommand != null && printCommand.length() != 0
                            && (printCommand.startsWith(COMMAND_BARCODE128) || printCommand.startsWith(COMMAND_BARCODE128_115200))) {
                        //eg:b128,position
                        qrArray = printCommand.split(COMMA);
                        if (qrArray.length >= 2) {
                            try {
                                position = Integer.parseInt(qrArray[1]);
                            } catch (Throwable ex) {
                                position = 0;
                            }
                            String barcodeImgPath = Epbescpos.generateBarcode128(output);
                            if (barcodeImgPath != null && barcodeImgPath.length() != 0) {
                                if (printCommand.startsWith(COMMAND_BARCODE128_115200)) {
                                    Epbescpos.printImageBaudrate115200(socketWriter, position, barcodeImgPath);
                                } else {
                                    Epbescpos.printImage(socketWriter, position, barcodeImgPath);
                                }
                            }
                            line = EMPTY;
                            continue;
                        }
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
                        socketWriter.flush();
                    } else {
                        socketWriter.println((line));// 打印完毕自动走纸
                        socketWriter.flush();
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
    
    private void doPrintCmd(final String printerCmd) {
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
    
//    private void cutPaper() {
//        printCmd(CUT_PAPER_DEFAULT_CMD);
//    }

    private void doCloseNetPrinter() {
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
