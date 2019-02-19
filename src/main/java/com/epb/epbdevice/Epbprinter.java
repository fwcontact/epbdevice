package com.epb.epbdevice;

import com.epb.epbdevice.beans.PrintPool;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Epbprinter {    
    private static final String MSG_ID = "msgId";
    private static final String MSG = "msg";
    private static final String PRINT_LIST = "printList";
    private static final String OK = "OK";
    
    private static final String EMPTY = "";
    private static final BigDecimal PRINTER_LINE = new BigDecimal("-1");
    private static final String COM = "COM";
    private static final String LPT = "LPT";
    
    public static Map<String, String> printFile(final Connection conn, final String recKey, final String userId) {
        final Map<String, String> returnMap = new HashMap<>();
        try {
            final Map<String, Object> printMap = getPrintPoolList(conn, recKey, userId);
//            List<PrintPool> printPoolList = getPrintPoolList(conn, recKey, userId);
            if (!OK.equals(printMap.get(MSG_ID))) {
                returnMap.put(Epbdevice.MSG_ID, (String) printMap.get(MSG_ID));
                returnMap.put(Epbdevice.MSG, (String) printMap.get(MSG));
            }
            List<PrintPool> printPoolList = (List<PrintPool>) printMap.get(PRINT_LIST);
            if (printPoolList == null || printPoolList.isEmpty()) {
                returnMap.put(Epbdevice.MSG_ID, "nodatafound");
                returnMap.put(Epbdevice.MSG, "No printer data generated, Print key is " + recKey);
                return returnMap;
            }
            String printEncoding = EMPTY;
            
            List<PrintPool> printerPrintPoolList = new ArrayList<>();
//            boolean opened;
            String printPort;
            PrintPool pp;
            int size = printPoolList.size();
            for (int index = 0; index < size; index++) {
                pp = printPoolList.get(index);
                if (PRINTER_LINE.compareTo(pp.getLineNo()) != 0) {
                    printerPrintPoolList.add(pp);
                } else {
                    printEncoding = pp.getVal();
                }
                if (PRINTER_LINE.compareTo(pp.getLineNo()) == 0 || index == size - 1) {
                    if (!printerPrintPoolList.isEmpty()) {
                        printPort = printerPrintPoolList.get(0).getPrintPort();
                        if (printPort != null 
                                && (printPort.toUpperCase().startsWith(COM) || printPort.toUpperCase().startsWith(LPT) || !Epbnetprinter.checkNetPort(printPort))) { 
//                            opened = Epbcomprinter.OpenEpbprinter(printPort);
//                            if (opened) {
//                                Epbcomprinter.printPosReceipt(printPoolList);
//                                Epbcomprinter.closePrinter();
//                            } else {
//                                returnMap.put(Epbdevice.MSG_ID, "error");
//                                returnMap.put(Epbdevice.MSG, "Failed to open printer port" + "->" + printPort);
//                                return returnMap;
//                            }     
                            final String returnMsg = Epbcomprinter.printPosReceipt(printPort, printerPrintPoolList);
                            if (!EMPTY.equals(returnMsg)) {
                                returnMap.put(Epbdevice.MSG_ID, "printerror");
                                returnMap.put(Epbdevice.MSG, returnMsg + ", Print key is " + recKey);
                                return returnMap;
                            }
                        } else {
//                            opened = Epbnetprinter.openEpbNetPrinter(printPort);
//                            if (opened) {
//                                Epbnetprinter.printPosReceipt(printPoolList);
//                                Epbnetprinter.closeNetPrinter();
//                            } else {
//                                returnMap.put(Epbdevice.MSG_ID, "error");
//                                returnMap.put(Epbdevice.MSG, "Failed to open net printer port" + "->" + printPort);
//                                return returnMap;
//                            }
                            final String returnMsg = Epbnetprinter.printPosReceipt(printPort, printerPrintPoolList, printEncoding);
                            if (!EMPTY.equals(returnMsg)) {
                                returnMap.put(Epbdevice.MSG_ID, "printerror");
                                returnMap.put(Epbdevice.MSG, returnMsg + ", Print key is " + recKey);
                                return returnMap;
                            }
                        }
                    }
                    printerPrintPoolList.clear();
                }
            }
            
            returnMap.put(Epbdevice.MSG_ID, Epbdevice.RETURN_OK);
            returnMap.put(Epbdevice.MSG, EMPTY);
            return returnMap;
        } catch (Throwable thr) {
            returnMap.put(Epbdevice.MSG_ID, "unhandle exception");
            returnMap.put(Epbdevice.MSG, thr.getMessage() + ", Print key is " + recKey);
            return returnMap;
        }
    }
    
    private static Map<String, Object> getPrintPoolList(final Connection conn, final String recKey, final String userId) {
        final List<PrintPool> list = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final Map<String, Object> returnMap = new HashMap<>();
        try {
            if (conn == null) {
                returnMap.put(MSG_ID, "failed");
                returnMap.put(MSG, "Connection is null, Print key is " + recKey);
                return returnMap;
            }

            //调用函数
            CallableStatement stmt = (CallableStatement ) conn.prepareCall("call EP_BISTRO.get_pos_print_file(?,?,?,?)");
            stmt.registerOutParameter(1, java.sql.Types.VARCHAR);
            stmt.registerOutParameter(2, java.sql.Types.VARCHAR);
            stmt.setString(3, recKey);
            stmt.setString(4, userId);
            stmt.execute();
            String strRtn = stmt.getString(1);
            String strMsg = stmt.getString(2);
            if (OK.equals(strRtn)) {
                StringBuilder sb = new StringBuilder();
                sb.append("SELECT * FROM POS_PRINTER_FILE WHERE REC_KEY_REF = '");
                sb.append(recKey);
                sb.append("' ORDER BY PRINT_SEQ, PRINT_PORT, LINE_NO, ORDER_NO ASC");
                pstmt = conn.prepareStatement(sb.toString());
                rs = pstmt.executeQuery();
                ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                PrintPool printPool;
                while (rs.next()) {
                    printPool = new PrintPool();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        Object value = rs.getObject(columnName);
                        // PRINT_PORT, LINE_NO, ORDER_NO, PRINT_COMMAND, CONST1, CONST2, FORMAT, LENGTH, ALIGN, BREAK_FLG, FILL_BLANK_FLG, VAL
                        if ("PRINT_PORT".equals(columnName.toUpperCase())) {
                            printPool.setPrintPort((String) value);
                        } else if ("LINE_NO".equals(columnName.toUpperCase())) {
                            printPool.setLineNo(value instanceof BigDecimal ? (BigDecimal) value : new BigDecimal(value + EMPTY));
                        } else if ("ORDER_NO".equals(columnName.toUpperCase())) {
                            printPool.setOrderNo(value instanceof BigInteger ? (BigInteger) value 
                                    : value == null ? null
                                    : new BigDecimal(value + EMPTY).toBigInteger());
                        } else if ("PRINT_COMMAND".equals(columnName.toUpperCase())) {
                            printPool.setPrintCommand((String) value);
                        } else if ("CONST1".equals(columnName.toUpperCase())) {
                            printPool.setConst1((String) value);
                        } else if ("CONST2".equals(columnName.toUpperCase())) {
                            printPool.setConst2((String) value);
                        } else if ("FORMAT".equals(columnName.toUpperCase())) {
                            printPool.setFormat((String) value);
                        } else if ("LENGTH".equals(columnName.toUpperCase())) {
                            printPool.setLength(value instanceof BigInteger ? (BigInteger) value 
                                    : value == null ? null
                                    : new BigDecimal(value + EMPTY).toBigInteger());
                        } else if ("ALIGN".equals(columnName.toUpperCase())) {
                            printPool.setAlign((String) value);
                        } else if ("BREAK_FLG".equals(columnName.toUpperCase())) {
                            printPool.setBreakFlg((String) value);
                        } else if ("FILL_BLANK_FLG".equals(columnName.toUpperCase())) {
                            printPool.setFillBlankFlg((String) value);
                        } else if ("VAL".equals(columnName.toUpperCase())) {
                            printPool.setVal((String) value);
                        }
                    }
                    list.add(printPool);
                }
                returnMap.put(MSG_ID, OK);
                returnMap.put(MSG, EMPTY);
                returnMap.put(PRINT_LIST, list);
                return returnMap;
            } else {
                returnMap.put(MSG_ID, strRtn);
                returnMap.put(MSG, strMsg);
                return returnMap;
            }
        } catch (SQLException e) {
            returnMap.put(MSG_ID, "Failed to execute get_pos_print_file");
            returnMap.put(MSG, "Failed to execute get_pos_print_file:" + e.getMessage() + ", Print key is " + recKey);
            return returnMap;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException thr) {
                // DO NOTHING
            }
        }
    }
}
