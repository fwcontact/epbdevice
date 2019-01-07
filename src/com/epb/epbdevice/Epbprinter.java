package com.epb.epbdevice;

import com.epb.epbdevice.beans.PrintPool;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Epbprinter {    
    
    private static final String EMPTY = "";
    private static final BigDecimal PRINTER_LINE = new BigDecimal("-1");
    
    public static Map<String, String> printFile(final Connection conn, final String actionType, final String shopId, final String recKey, final String userId) {
        final Map<String, String> returnMap = new HashMap<String, String>();
        try {
            List<PrintPool> printPoolList = getPrintPoolList(conn, actionType, shopId, recKey, userId);
            if (printPoolList == null || printPoolList.isEmpty()) {
                returnMap.put(Epbdevice.MSG_ID, "error");
                returnMap.put(Epbdevice.MSG, "Failed to call procedure");
                return returnMap;
            }
            
            List<PrintPool> printerPrintPoolList = new ArrayList<PrintPool>();
//            boolean opened;
            String printPort;
            PrintPool pp;
            int size = printPoolList.size();
            for (int index = 0; index < size; index++) {
                pp = printPoolList.get(index);
                if (PRINTER_LINE.compareTo(pp.getLineNo()) != 0) {
                    printerPrintPoolList.add(pp);
                }
                if (PRINTER_LINE.compareTo(pp.getLineNo()) == 0 || index == size - 1) {
                    if (!printerPrintPoolList.isEmpty()) {
                        printPort = printerPrintPoolList.get(0).getPrintPort();
                        if (printPort != null 
                                && (printPort.toUpperCase().startsWith("COM") || printPort.toUpperCase().startsWith("LPT") || !Epbnetprinter.checkNetPort(printPort))) { 
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
                                returnMap.put(Epbdevice.MSG_ID, "error");
                                returnMap.put(Epbdevice.MSG, returnMsg);
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
                            final String returnMsg = Epbnetprinter.printPosReceipt(printPort, printerPrintPoolList);
                            if (!EMPTY.equals(returnMsg)) {
                                returnMap.put(Epbdevice.MSG_ID, "error");
                                returnMap.put(Epbdevice.MSG, returnMsg);
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
            returnMap.put(Epbdevice.MSG, thr.getMessage());
            return returnMap;
        }
    }
    
    private static List<PrintPool> getPrintPoolList(final Connection conn, final String actionType, final String shopId, final String recKey, final String userId) {
        final List<PrintPool> list = new ArrayList<PrintPool>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            if (conn == null) {
                return list;
            }

            //调用函数
            CallableStatement stmt = (CallableStatement ) conn.prepareCall("call EP_BISTRO.get_pos_print_file(?,?,?,?,?,?,?)");
            stmt.registerOutParameter(1, java.sql.Types.VARCHAR);
            stmt.registerOutParameter(2, java.sql.Types.VARCHAR);
            stmt.setString(3, actionType);
            stmt.setString(4, shopId);
            stmt.setString(5, recKey);
            stmt.setString(6, userId);
            stmt.registerOutParameter(7, java.sql.Types.VARCHAR); // printKey
            stmt.execute();
            String strRtn = stmt.getString(1);
            String strRtnPrintKey = stmt.getString(7);
            if (strRtn.equals("OK")) {
                StringBuilder sb = new StringBuilder();
                sb.append("SELECT * FROM POS_PRINTER_FILE WHERE REC_KEY_REF = '");
                sb.append(strRtnPrintKey);
                sb.append("'ORDER BY PRINT_PORT, LINE_NO, ORDER_NO ASC");
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
            }
            return list;
        } catch (Exception e) {
            return list;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Throwable thr) {
            }
        }
    }
}
