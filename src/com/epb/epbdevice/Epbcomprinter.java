package com.epb.epbdevice;

import com.epb.epbdevice.beans.PrintPool;
import com.epb.epbdevice.utl.Epbescpos;
import com.epb.epbdevice.utl.StringParser;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

class Epbcomprinter {
    
    private static final String EMPTY = "";
    private static final String COM = "COM";
    private static final String LPT = "LPT";
    private static final String CUT_PAPER_DEFAULT_CMD = "27,105";
    private static final String OPEN_DRAWER_DEFAULT_CMD = "27,112,0,48,255";
    private static final String SPACE = " ";
    private static final String COMMAND_QR = "qr";
    private static final String COMMAND_QR115200 = "qr115200";
    private static final String COMMA = ",";
    private static final String COMMAND_IMAGE = "pi";
    private static final String COMMAND_IMAGE115200 = "pi115200";
    private static final String COMMAND_BARCODE128 = "b128";
    private static final String COMMAND_BARCODE128_115200 = "b128_115200";
    private static final String COMMAND_BARCODEF128 = "bf128";
    private static final String COMMAND_BARCODEF128_115200 = "bf128_115200";
    private static final String COMMAND_HIDE_IF_NUMBER_IS_ZERO = "hnz";  // for number
    private static final String COMMAND_HIDE_IF_STRING_IS_EMPTY = "hse";  // for string
    private static final byte[] CTL_LF = ("\r\n").getBytes();          // Print and line feed
    private FileOutputStream ioPrint;
    
    public static String printPosReceipt(final String printPort, final List<PrintPool> printPoolList) {
        return new Epbcomprinter().doPrintPosReceipt(printPort, printPoolList);
    }
    
    public static void openDrawer(final String serialParallelPort, final String openCashDrawerCmd) {
        new Epbcomprinter().doOpenDrawer(serialParallelPort, openCashDrawerCmd);
    }
    
    public static void cutPaper() {
        new Epbcomprinter().doCutPaper();
    }

    //
    // private
    //
    
    private boolean doOpenEpbprinter(final String serialParallelPort) {
        try {
            if (ioPrint == null) {
                ioPrint = new FileOutputStream(serialParallelPort);
                return true;
            }
            return true;
        } catch (IOException ex) {
            ioPrint = null;
//            System.out.println("com.epb.epbdevice.Epbprinter.Epbprinter()" + ":" + ex.getMessage());
            return false;
        }
    }
    
//    public static void printPosReceipt(final FileOutputStream ioprint, final List<PrintPool> printPoolList) {
//        printPosReceipt(false, ioprint, null, printPoolList);
//    }
    
//    public static void printNetPosReceipt(PrintWriter socketWriter, final List<PrintPool> printPoolList) {
//        printPosReceipt(true, null, socketWriter, printPoolList);
//    }
    
//    private static void printPosReceipt(final boolean isNetPrint, final FileOutputStream ioprint, PrintWriter socketWriter, final List<PrintPool> printPoolList) {
//        try {
//            String output;
//            int position;
//            int qrSize;
//            String imagePath;
//            String[] qrArray;
//            boolean skipThisLine = false;
//            BigDecimal poolLineNo = null; 
//            String line;     
//            for (PrintPool linePrintPool : printPoolList) {
//                if (poolLineNo != null && poolLineNo.compareTo(linePrintPool.getLineNo()) == 0) {
//                    continue;
//                }
//                line = EMPTY;
//                for (PrintPool printPool : printPoolList) {
//                    final BigDecimal lineNo = printPool.getLineNo();
//                    if (lineNo.compareTo(poolLineNo) != 0) {
//                        continue;
//                    }
//                    poolLineNo = linePrintPool.getLineNo();
//
//                    String printCommand = printPool.getPrintCommand() == null ? EMPTY : printPool.getPrintCommand();
//                    final String const1 = printPool.getConst1() == null ? EMPTY : printPool.getConst1();
//                    final String const2 = printPool.getConst2() == null ? EMPTY : printPool.getConst2();
////                    final String format = printPool.getFormat() == null ? EMPTY : printPool.getFormat();
//                    final Integer lineLength = printPool.getLength() == null ? 0 : printPool.getLength().intValue();
//                    final String align = printPool.getAlign().toString() == null ? EMPTY : printPool.getAlign().toString();
//                    final String breakFlg = printPool.getBreakFlg() == null ? EMPTY : printPool.getBreakFlg().toString();
//                    final String fillBlankFlg = printPool.getFillBlankFlg() == null ? "N" : printPool.getFillBlankFlg().toString();
//                    output = EMPTY;
//                    if (!(printCommand == null || EMPTY.equals(printCommand))) {
//                        //输出
//                        line += StringParser.getSplitString(printCommand);
//                    }
//                    output = printPool.getVal();
//
//                    if (printCommand != null && printCommand.length() != 0
//                            && (printCommand.startsWith(COMMAND_QR) || printCommand.startsWith(COMMAND_QR115200))) {
//                        qrArray = printCommand.split(COMMA);
//                        if (qrArray.length >= 2) {
//                            try {
//                                position = Integer.parseInt(qrArray[1]);
//                            } catch (Throwable ex) {
//                                position = 0;
//                            }
//                            try {
//                                qrSize = Integer.parseInt(qrArray[2]);
//                            } catch (Throwable ex) {
//                                qrSize = 150;
//                            }
//                            if (printCommand.startsWith(COMMAND_QR115200)) {
//                                Epbescpos.printQRCodeBaudrate115200(ioprint, position, output, qrSize);
//                            } else {
//                                Epbescpos.printQRCode(ioprint, position, output, qrSize);
//                            }
//                            line = EMPTY;
//                            continue;
//                        }
//                    } else if (printCommand != null && printCommand.length() != 0
//                            && (printCommand.startsWith(COMMAND_IMAGE) || printCommand.startsWith(COMMAND_IMAGE115200))) {
//                        qrArray = printCommand.split(COMMA);
////                        LOG.debug("----printCommand:" + printCommand);
//                        if (qrArray.length == 3) {
//                            try {
//                                position = Integer.parseInt(qrArray[1]);
//                            } catch (Throwable ex) {
//                                position = 0;
//                            }
//                            try {
//                                imagePath = qrArray[2];
//                            } catch (Throwable ex) {
//                                imagePath = EMPTY;
//                            }
//                            if (imagePath != null && imagePath.length() != 0) {
//                                if (printCommand.startsWith(COMMAND_IMAGE115200)) {
//                                    Epbescpos.printImageBaudrate115200(ioprint, position, imagePath);
//                                } else {
//                                    Epbescpos.printImage(ioprint, position, imagePath);
//                                }
//                                line = EMPTY;
//                                continue;
//                            }
//                        }
//                    } else if (printCommand != null && printCommand.length() != 0
//                            && (printCommand.startsWith(COMMAND_BARCODE128) || printCommand.startsWith(COMMAND_BARCODE128_115200))) {
//                        //eg:b128,position
//                        qrArray = printCommand.split(COMMA);
//                        if (qrArray.length >= 2) {
//                            try {
//                                position = Integer.parseInt(qrArray[1]);
//                            } catch (Throwable ex) {
//                                position = 0;
//                            }
//                            String barcodeImgPath = Epbescpos.generateBarcode128(output);
//                            if (barcodeImgPath != null && barcodeImgPath.length() != 0) {
//                                if (printCommand.startsWith(COMMAND_BARCODE128_115200)) {
//                                    Epbescpos.printImageBaudrate115200(ioprint, position, barcodeImgPath);
//                                } else {
//                                    Epbescpos.printImage(ioprint, position, barcodeImgPath);
//                                }
//                            }
//                            line = EMPTY;
//                            continue;
//                        }
//                    }
//
//                    if ((!(const1 == null || EMPTY.equals(const1)) || !(const2 == null || EMPTY.equals(const2)))
//                            && (output == null || EMPTY.equals(output))) {
//                        output = EMPTY;
//                    } else {
//                        output = const1 + output + const2;
//                    }
//
//                    //如果有Command命令，没有内容，不需要补空格
//                    if (!(!(printCommand == null || printCommand.equals(EMPTY)) && (output == null || output.equals(EMPTY)))) {
//                        output = StringParser.setStringAlignment(output, align, lineLength, breakFlg, fillBlankFlg);
//                    }
//
//                    line += output;
//
//                    if (poolLineNo == null) {
//                    }
//                }
//                
//                if (!skipThisLine) {
//                    if (isNetPrint) {
//                        if (line == null || line.length() == 0) {
//                            socketWriter.flush();
//                        } else {
//                            socketWriter.println((line));// 打印完毕自动走纸
//                            socketWriter.flush();
//                        }
//                    } else if (line == null || line.length() == 0) {
//                        ioprint.write(CTL_LF);
//                    } else {
//                        ioprint.write((line).getBytes());
//                        ioprint.write(CTL_LF);
//                    }
//                }
//                skipThisLine = false;                
//            }
//        } catch (Throwable ex) {
//            System.out.println("Print receipt head Failed!" + ex);
//        }
//    }
    
    private String doPrintPosReceipt(final String printPort, final List<PrintPool> printPoolList) {
        try {
            boolean opened = doOpenEpbprinter(printPort);
            if (opened) {
                doPrintPosReceipt(printPoolList);
                doClosePrinter();
                return EMPTY;
            } else {
                return "Failed to open printer port" + "->" + printPort;
            }
        } catch (Throwable thr) {
            return "Failed to print receipt" + "->" + thr.getMessage();
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
                    if (lineNo.compareTo(poolLineNo) != 0) {
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
                                Epbescpos.printQRCodeBaudrate115200(ioPrint, position, output, qrSize);
                            } else {
                                Epbescpos.printQRCode(ioPrint, position, output, qrSize);
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
                                    Epbescpos.printImageBaudrate115200(ioPrint, position, imagePath);
                                } else {
                                    Epbescpos.printImage(ioPrint, position, imagePath);
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
                                    Epbescpos.printImageBaudrate115200(ioPrint, position, barcodeImgPath);
                                } else {
                                    Epbescpos.printImage(ioPrint, position, barcodeImgPath);
                                }
                            }
                            line = EMPTY;
                            continue;
                        }
                    }

                    if ((!(const1 == null || EMPTY.equals(const1)) || !(const2 == null || EMPTY.equals(const2)))
                            && (output == null || EMPTY.equals(output))) {
                        output = EMPTY;
                    } else {
                        output = const1 + output + const2;
                    }

                    //如果有Command命令，没有内容，不需要补空格
                    if ((printCommand != null && !EMPTY.equals(printCommand) && (output == null || EMPTY.equals(output)))) {
                        // do nothing
                    } else {    
                        output = StringParser.setStringAlignment(output, align, lineLength, breakFlg, fillBlankFlg);
                    }

                    line += output;
                }
                
                if (!skipThisLine) {
                    if (line == null || line.length() == 0) {
                        ioPrint.write(CTL_LF);
                    } else {
                        ioPrint.write((line).getBytes());
                        ioPrint.write(CTL_LF);
                    }
                }
                skipThisLine = false;                
            }
        } catch (Throwable ex) {
            System.out.println("Print receipt Failed!" + ex);
        }
    }

    private void doPrintText(final String lineText) {
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

    private void doPrintCmd(final String printerCmd) {
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

    private void doClosePrinter() {
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

    private void doCutPaper() {
        doPrintCmd(CUT_PAPER_DEFAULT_CMD);
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
    private void doOpenDrawer(final String serialParallelPort, final String openCashDrawerCmd) {
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
