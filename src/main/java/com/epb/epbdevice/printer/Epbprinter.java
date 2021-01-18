package com.epb.epbdevice.printer;

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
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.epb.epbdevice.Epbdevice;
import com.epb.epbdevice.beans.PrintPool;
import com.epb.epbdevice.utl.CommonUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings({ "unused", "unchecked" })
public class Epbprinter {
	private static final Log LOG = LogFactory.getLog(Epbprinter.class);
	private static final String MSG_ID = "msgId";
	private static final String MSG = "msg";
	private static final String PRINT_LIST = "printList";
	private static final String OK = "OK";

	private static final String EMPTY = "";
	private static final BigDecimal PRINTER_LINE = new BigDecimal("-1");
	private static final String COM = "COM";
	private static final String LPT = "LPT";
//    private static final int READY = 0;
//    private static final int RUNNING = 1;
	private static final CopyOnWriteArrayList<String> WAITING_QUEUE = new CopyOnWriteArrayList<String>();
	private static final CopyOnWriteArrayList<String> PRINTING_QUEUE = new CopyOnWriteArrayList<String>();
	private static final ConcurrentHashMap<String, List<PrintPool>> FULL_PRINT_POOL_MAPPING = new ConcurrentHashMap<String, List<PrintPool>>();
//    private static final CopyOnWriteArrayList<String> HIS_QUEUE = new CopyOnWriteArrayList<String>();

	public static Map<String, String> printFile(final Connection conn, final String recKey, final String userId) {
		final Map<String, String> returnMap = new HashMap<>();
		try {
			LOG.info("printFile:" + recKey);
			if (recKey == null || recKey.isEmpty()) {
				returnMap.put(Epbdevice.MSG_ID, OK);
				returnMap.put(Epbdevice.MSG, "Print key is null, do nothing, return OK");
				return returnMap;
			}
//            CommonUtility.printLog("print start");
			final Map<String, Object> printMap = getPrintPoolList(conn, recKey, userId);
//            List<PrintPool> printPoolList = getPrintPoolList(conn, recKey, userId);
			if (!OK.equals(printMap.get(MSG_ID))) {
				returnMap.put(Epbdevice.MSG_ID, (String) printMap.get(MSG_ID));
				returnMap.put(Epbdevice.MSG, (String) printMap.get(MSG));
				return returnMap;
			}
			List<PrintPool> printPoolList = (List<PrintPool>) printMap.get(PRINT_LIST);
			LOG.info("printPoolList size:" + (printPoolList == null ? "0" : printPoolList.size()));
//            if (printPoolList == null || printPoolList.isEmpty()) {
//                returnMap.put(Epbdevice.MSG_ID, "nodatafound");
//                returnMap.put(Epbdevice.MSG, "No printer data generated, Print key is " + recKey);
//                return returnMap;
//            }
//            
//            FULL_PRINT_POOL_MAPPING.put(recKey, printPoolList);
//            WAITING_QUEUE.add(recKey);        
////            HIS_QUEUE.add(recKey);   
//            
////            startPrint sell = new startPrint();
////            Thread thread = new Thread(sell);
////            thread.start(); 
//
//            startPrint();
//            
//            returnMap.put(Epbdevice.MSG_ID, Epbdevice.RETURN_OK);
//            returnMap.put(Epbdevice.MSG, EMPTY);
//            return returnMap;
//0032021: BISTRO fnb epbdevice PRINTER no-print events
//            return printFile(recKey, printPoolList);
			return printFileThread(recKey, printPoolList);
		} catch (Throwable ex) {
//            ex.printStackTrace();
			LOG.error("error exec printFile", ex);
			returnMap.put(Epbdevice.MSG_ID, "unhandle exception");
			returnMap.put(Epbdevice.MSG, ex.getMessage() + ", Print key is " + recKey);
			return returnMap;
		} finally {
//            CommonUtility.printLog("print end");
//            for (String key : HIS_QUEUE) {
//                CommonUtility.printLog("his key:" + key);
//            }
		}
	}

	public static Map<String, String> printFileMQ(final Map<String, Object> printQueueMap) {
		final Map<String, String> returnMap = new HashMap<>();
		try {
			LOG.info("call printFileMQ");
			if (!OK.equals(printQueueMap.get(MSG_ID))) {
				returnMap.put(Epbdevice.MSG_ID, (String) printQueueMap.get(MSG_ID));
				returnMap.put(Epbdevice.MSG, (String) printQueueMap.get(MSG));
			}

			// cast
			final List<Map<String, Object>> messageMaps = (List<Map<String, Object>>) printQueueMap.get(PRINT_LIST);
			// convert
			final List<PrintPool> printPoolList = messageMaps
					.stream()
					.map(messageMap -> new ObjectMapper()
							.convertValue(messageMap, PrintPool.class))
					.collect(Collectors.toList());

			if (printPoolList == null || printPoolList.isEmpty()) {
				returnMap.put(Epbdevice.MSG_ID, "nodatafound");
				returnMap.put(Epbdevice.MSG, "No printer data generated");
				return returnMap;
			}
			String recKey = System.currentTimeMillis() + "" + new Random().nextInt(100);
//            0032021: BISTRO fnb epbdevice PRINTER no-print events
//            return printFile(recKey, printPoolList);
			return printFileThread(recKey, printPoolList);
		} catch (Throwable ex) {
//            ex.printStackTrace();
			LOG.error("error exec printFileMQ", ex);
			returnMap.put(Epbdevice.MSG_ID, "unhandle exception");
			returnMap.put(Epbdevice.MSG, ex.getMessage());
			return returnMap;
		} finally {
//            CommonUtility.printLog("print end");
//            for (String key : HIS_QUEUE) {
//                CommonUtility.printLog("his key:" + key);
//            }
		}
	}

	//
	// private
	//
	private static Map<String, String> printFile(final String recKey, final List<PrintPool> printPoolList) {
		final Map<String, String> returnMap = new HashMap<>();
		try {
			if (printPoolList == null || printPoolList.isEmpty()) {
				returnMap.put(Epbdevice.MSG_ID, "nodatafound");
				returnMap.put(Epbdevice.MSG, "No printer data generated, Print key is " + recKey);
				return returnMap;
			}
//            CommonUtility.printLog("printPoolList size is " + printPoolList.size());
//            LOG.info("printPort, lineNo, orderNo, printCommand, const1, const2, format, length, align, breakFlg, fillBlankFlg, val");
//            for (PrintPool pp : printPoolList) {
//                LOG.info(pp.getPrintPort() + ","
//                        + pp.getLineNo() + ","
//                        + pp.getOrderNo() + ","
//                        + pp.getPrintCommand() + ","
//                        + pp.getConst1() + ","
//                        + pp.getConst2() + ","
//                        + pp.getFormat() + ","
//                        + pp.getLength() + ","
//                        + pp.getAlign() + ","
//                        + pp.getBreakFlg() + ","
//                        + pp.getFillBlankFlg() + ","
//                        + pp.getVal());
//            }
			FULL_PRINT_POOL_MAPPING.put(recKey, printPoolList);
			WAITING_QUEUE.add(recKey);
//            HIS_QUEUE.add(recKey);   

//            startPrint sell = new startPrint();
//            Thread thread = new Thread(sell);
//            thread.start(); 
			startPrint();

			returnMap.put(Epbdevice.MSG_ID, Epbdevice.RETURN_OK);
			returnMap.put(Epbdevice.MSG, EMPTY);
			return returnMap;
		} catch (Throwable ex) {
//            ex.printStackTrace();
			LOG.error("error exec printFile", ex);
			returnMap.put(Epbdevice.MSG_ID, "unhandle exception");
			returnMap.put(Epbdevice.MSG, ex.getMessage() + ", Print key is " + recKey);
			return returnMap;
		} finally {
//            CommonUtility.printLog("print end");
//            for (String key : HIS_QUEUE) {
//                CommonUtility.printLog("his key:" + key);
//            }
		}
	}

//    private static class startPrint implements Runnable {
//
//        Lock lock = new ReentrantLock();
//
//        @Override
//        public void run() {
//            try {
//                lock.lock();
//                doStartPrint();
//            } finally {
//                lock.unlock();
//            }
//        }
//    }

	private static void startPrint() {
		// fax report in a thread
		final Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				doStartPrint();
			}
		});

		// start
		thread.start();
	}

	private static Map<String, Object> getPrintPoolList(final Connection conn, final String recKey,
			final String userId) {
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

			// 调用函数
			CallableStatement stmt = (CallableStatement) conn.prepareCall("call EP_BISTRO.get_pos_print_file(?,?,?,?)");
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
						// PRINT_PORT, LINE_NO, ORDER_NO, PRINT_COMMAND, CONST1, CONST2, FORMAT, LENGTH,
						// ALIGN, BREAK_FLG, FILL_BLANK_FLG, VAL
						if ("PRINT_PORT".equals(columnName.toUpperCase())) {
							printPool.setPrintPort((String) value);
						} else if ("LINE_NO".equals(columnName.toUpperCase())) {
							printPool.setLineNo(
									value instanceof BigDecimal ? (BigDecimal) value : new BigDecimal(value + EMPTY));
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
//                    LOG.info("printPool:" + printPool.getPrintPort() + ","
//                            + printPool.getLineNo()+ "," 
//                            + printPool.getOrderNo()+ "," 
//                            + printPool.getPrintCommand()+ "," 
//                            + printPool.getConst1()+ "," 
//                            + printPool.getConst2()+ "," 
//                            + printPool.getFormat()+ "," 
//                            + printPool.getLength()+ "," 
//                            + printPool.getAlign()+ "," 
//                            + printPool.getBreakFlg()+ "," 
//                            + printPool.getFillBlankFlg()+ "," 
//                            + printPool.getVal());
					list.add(printPool);
				}
				returnMap.put(MSG_ID, OK);
				returnMap.put(MSG, EMPTY);
				returnMap.put(PRINT_LIST, list);
				LOG.info("list size:" + list.size());
				return returnMap;
			} else {
				returnMap.put(MSG_ID, strRtn);
				returnMap.put(MSG, strMsg);
				return returnMap;
			}
		} catch (SQLException ex) {
//            ex.printStackTrace();
			LOG.error("error exec getPrintPoolList", ex);
			returnMap.put(MSG_ID, "Failed to execute get_pos_print_file");
			returnMap.put(MSG, "Failed to execute get_pos_print_file:" + ex.getMessage() + ", Print key is " + recKey);
			return returnMap;
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException ex) {
				// DO NOTHING
//                ex.printStackTrace();
				LOG.error("error exec getPrintPoolList", ex);
			}
		}
	}

	private synchronized static void doStartPrint() {
		if (!PRINTING_QUEUE.isEmpty()) {
			// it is busy
			return;
		}
		printQueue();
		if (!PRINTING_QUEUE.isEmpty()) {
			doStartPrint();
		}
	}

	private static void printQueue() {
		CommonUtility.printLog("printQueue");
		// add to print queue
		if (PRINTING_QUEUE.isEmpty()) {
			CommonUtility.printLog("printing queue is empty");
			for (String key : WAITING_QUEUE) {
				PRINTING_QUEUE.add(key);
				CommonUtility.printLog("add to printing queue->" + key);
				WAITING_QUEUE.remove(key);
			}
		}

		if (!PRINTING_QUEUE.isEmpty()) {
			for (String key : PRINTING_QUEUE) {
				CommonUtility.printLog("printing->" + key);
				printFile(key);
				PRINTING_QUEUE.remove(key);
				FULL_PRINT_POOL_MAPPING.remove(key);
			}
		}
	}

	private static Map<String, String> printFile(final String recKey) {
		final Map<String, String> returnMap = new HashMap<>();
		try {
			List<PrintPool> printPoolList = new ArrayList<>();
			if (FULL_PRINT_POOL_MAPPING.containsKey(recKey)) {
				printPoolList.addAll(FULL_PRINT_POOL_MAPPING.get(recKey));
			}
//            System.out.println("printFile printPoolList size:" + printPoolList.size());
			if (printPoolList.isEmpty()) {
				returnMap.put(Epbdevice.MSG_ID, Epbdevice.RETURN_OK);
				returnMap.put(Epbdevice.MSG, EMPTY);
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
					printEncoding = pp.getConst1(); // print charset
//                    System.out.println("Encoding:" + printEncoding);
				}
				if (PRINTER_LINE.compareTo(pp.getLineNo()) == 0 || index == size - 1) {
					if (!printerPrintPoolList.isEmpty()) {
						printPort = printerPrintPoolList.get(0).getPrintPort();
//                        System.out.println("printPort:" + printPort);
						if (printPort != null
								&& (printPort.toUpperCase().startsWith(COM) || printPort.toUpperCase().startsWith(LPT)
										|| !Epbnetprinter.checkNetPort(printPort))) {
							// do nothing
						} else {
							CommonUtility.printLog("print to net printer:" + printPort);
							final String returnMsg = Epbnetprinter.printPosReceipt(printPort, printerPrintPoolList,
									printEncoding);
							if (!EMPTY.equals(returnMsg)) {
								// never return
//                                returnMap.put(Epbdevice.MSG_ID, "printerror");
//                                returnMap.put(Epbdevice.MSG, returnMsg + ", Print key is " + recKey);
//                                return returnMap;
								System.out.println(returnMsg + ", Print key is " + recKey);
							} else {
								CommonUtility.printLog("Done");
							}
						}
					}
					printerPrintPoolList.clear();
				}
			}

			returnMap.put(Epbdevice.MSG_ID, Epbdevice.RETURN_OK);
			returnMap.put(Epbdevice.MSG, EMPTY);
			return returnMap;
		} catch (Throwable ex) {
//            ex.printStackTrace();
			LOG.error("error exec printFile", ex);
			returnMap.put(Epbdevice.MSG_ID, "unhandle exception");
			returnMap.put(Epbdevice.MSG, ex.getMessage() + ", Print key is " + recKey);
			return returnMap;
		} finally {
			CommonUtility.printLog("print end");
		}
	}

//    public static Map<String, String> printFile(final Connection conn, final String recKey, final String userId) {
//        final Map<String, String> returnMap = new HashMap<>();
//        try {
//            CommonUtility.printLog("print start");
//            final Map<String, Object> printMap = getPrintPoolList(conn, recKey, userId);
////            List<PrintPool> printPoolList = getPrintPoolList(conn, recKey, userId);
//            if (!OK.equals(printMap.get(MSG_ID))) {
//                returnMap.put(Epbdevice.MSG_ID, (String) printMap.get(MSG_ID));
//                returnMap.put(Epbdevice.MSG, (String) printMap.get(MSG));
//            }
//            List<PrintPool> printPoolList = (List<PrintPool>) printMap.get(PRINT_LIST);
//            if (printPoolList == null || printPoolList.isEmpty()) {
//                returnMap.put(Epbdevice.MSG_ID, "nodatafound");
//                returnMap.put(Epbdevice.MSG, "No printer data generated, Print key is " + recKey);
//                return returnMap;
//            }
//            String printEncoding = EMPTY;
//            
//            List<PrintPool> printerPrintPoolList = new ArrayList<>();
////            boolean opened;
//            String printPort;
//            PrintPool pp;
//            int size = printPoolList.size();
//            for (int index = 0; index < size; index++) {
//                pp = printPoolList.get(index);
//                if (PRINTER_LINE.compareTo(pp.getLineNo()) != 0) {
//                    printerPrintPoolList.add(pp);
//                } else {
//                    printEncoding = pp.getVal();
//                }
//                if (PRINTER_LINE.compareTo(pp.getLineNo()) == 0 || index == size - 1) {
//                    if (!printerPrintPoolList.isEmpty()) {
//                        printPort = printerPrintPoolList.get(0).getPrintPort();
//                        if (printPort != null 
//                                && (printPort.toUpperCase().startsWith(COM) || printPort.toUpperCase().startsWith(LPT) || !Epbnetprinter.checkNetPort(printPort))) { 
////                            opened = Epbcomprinter.OpenEpbprinter(printPort);
////                            if (opened) {
////                                Epbcomprinter.printPosReceipt(printPoolList);
////                                Epbcomprinter.closePrinter();
////                            } else {
////                                returnMap.put(Epbdevice.MSG_ID, "error");
////                                returnMap.put(Epbdevice.MSG, "Failed to open printer port" + "->" + printPort);
////                                return returnMap;
////                            }     
//                            final String returnMsg = Epbcomprinter.printPosReceipt(printPort, printerPrintPoolList);
//                            if (!EMPTY.equals(returnMsg)) {
//                                // never return
////                                returnMap.put(Epbdevice.MSG_ID, "printerror");
////                                returnMap.put(Epbdevice.MSG, returnMsg + ", Print key is " + recKey);
////                                return returnMap;
//                                System.out.println(returnMsg + ", Print key is " + recKey);
//                            }
//                        } else {
////                            opened = Epbnetprinter.openEpbNetPrinter(printPort);
////                            if (opened) {
////                                Epbnetprinter.printPosReceipt(printPoolList);
////                                Epbnetprinter.closeNetPrinter();
////                            } else {
////                                returnMap.put(Epbdevice.MSG_ID, "error");
////                                returnMap.put(Epbdevice.MSG, "Failed to open net printer port" + "->" + printPort);
////                                return returnMap;
////                            }
//                            CommonUtility.printLog("print to net printer:" + printPort);
//                            final String returnMsg = Epbnetprinter2.printPosReceipt(printPort, printerPrintPoolList, printEncoding);
//                            CommonUtility.printLog("Done:" + printPort);
//                            if (!EMPTY.equals(returnMsg)) {
//                                // never return
////                                returnMap.put(Epbdevice.MSG_ID, "printerror");
////                                returnMap.put(Epbdevice.MSG, returnMsg + ", Print key is " + recKey);
////                                return returnMap;
//                                System.out.println(returnMsg + ", Print key is " + recKey);
//                            }
//                        }
//                    }
//                    printerPrintPoolList.clear();
//                }
//            }
//            
//            returnMap.put(Epbdevice.MSG_ID, Epbdevice.RETURN_OK);
//            returnMap.put(Epbdevice.MSG, EMPTY);
//            return returnMap;
//        } catch (Throwable thr) {
//            returnMap.put(Epbdevice.MSG_ID, "unhandle exception");
//            returnMap.put(Epbdevice.MSG, thr.getMessage() + ", Print key is " + recKey);
//            return returnMap;
//        } finally {
//            CommonUtility.printLog("print end");
//        }
//    }
//    
//    private static Map<String, Object> getPrintPoolList(final Connection conn, final String recKey, final String userId) {
//        final List<PrintPool> list = new ArrayList<>();
//        PreparedStatement pstmt = null;
//        ResultSet rs = null;
//        final Map<String, Object> returnMap = new HashMap<>();
//        try {
//            if (conn == null) {
//                returnMap.put(MSG_ID, "failed");
//                returnMap.put(MSG, "Connection is null, Print key is " + recKey);
//                return returnMap;
//            }
//
//            //调用函数
//            CallableStatement stmt = (CallableStatement ) conn.prepareCall("call EP_BISTRO.get_pos_print_file(?,?,?,?)");
//            stmt.registerOutParameter(1, java.sql.Types.VARCHAR);
//            stmt.registerOutParameter(2, java.sql.Types.VARCHAR);
//            stmt.setString(3, recKey);
//            stmt.setString(4, userId);
//            stmt.execute();
//            String strRtn = stmt.getString(1);
//            String strMsg = stmt.getString(2);
//            if (OK.equals(strRtn)) {
//                StringBuilder sb = new StringBuilder();
//                sb.append("SELECT * FROM POS_PRINTER_FILE WHERE REC_KEY_REF = '");
//                sb.append(recKey);
//                sb.append("' ORDER BY PRINT_SEQ, PRINT_PORT, LINE_NO, ORDER_NO ASC");
//                pstmt = conn.prepareStatement(sb.toString());
//                rs = pstmt.executeQuery();
//                ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
//                int columnCount = metaData.getColumnCount();
//                PrintPool printPool;
//                while (rs.next()) {
//                    printPool = new PrintPool();
//                    for (int i = 1; i <= columnCount; i++) {
//                        String columnName = metaData.getColumnLabel(i);
//                        Object value = rs.getObject(columnName);
//                        // PRINT_PORT, LINE_NO, ORDER_NO, PRINT_COMMAND, CONST1, CONST2, FORMAT, LENGTH, ALIGN, BREAK_FLG, FILL_BLANK_FLG, VAL
//                        if ("PRINT_PORT".equals(columnName.toUpperCase())) {
//                            printPool.setPrintPort((String) value);
//                        } else if ("LINE_NO".equals(columnName.toUpperCase())) {
//                            printPool.setLineNo(value instanceof BigDecimal ? (BigDecimal) value : new BigDecimal(value + EMPTY));
//                        } else if ("ORDER_NO".equals(columnName.toUpperCase())) {
//                            printPool.setOrderNo(value instanceof BigInteger ? (BigInteger) value 
//                                    : value == null ? null
//                                    : new BigDecimal(value + EMPTY).toBigInteger());
//                        } else if ("PRINT_COMMAND".equals(columnName.toUpperCase())) {
//                            printPool.setPrintCommand((String) value);
//                        } else if ("CONST1".equals(columnName.toUpperCase())) {
//                            printPool.setConst1((String) value);
//                        } else if ("CONST2".equals(columnName.toUpperCase())) {
//                            printPool.setConst2((String) value);
//                        } else if ("FORMAT".equals(columnName.toUpperCase())) {
//                            printPool.setFormat((String) value);
//                        } else if ("LENGTH".equals(columnName.toUpperCase())) {
//                            printPool.setLength(value instanceof BigInteger ? (BigInteger) value 
//                                    : value == null ? null
//                                    : new BigDecimal(value + EMPTY).toBigInteger());
//                        } else if ("ALIGN".equals(columnName.toUpperCase())) {
//                            printPool.setAlign((String) value);
//                        } else if ("BREAK_FLG".equals(columnName.toUpperCase())) {
//                            printPool.setBreakFlg((String) value);
//                        } else if ("FILL_BLANK_FLG".equals(columnName.toUpperCase())) {
//                            printPool.setFillBlankFlg((String) value);
//                        } else if ("VAL".equals(columnName.toUpperCase())) {
//                            printPool.setVal((String) value);
//                        }
//                    }
//                    list.add(printPool);
//                }
//                returnMap.put(MSG_ID, OK);
//                returnMap.put(MSG, EMPTY);
//                returnMap.put(PRINT_LIST, list);
//                return returnMap;
//            } else {
//                returnMap.put(MSG_ID, strRtn);
//                returnMap.put(MSG, strMsg);
//                return returnMap;
//            }
//        } catch (SQLException e) {
//            returnMap.put(MSG_ID, "Failed to execute get_pos_print_file");
//            returnMap.put(MSG, "Failed to execute get_pos_print_file:" + e.getMessage() + ", Print key is " + recKey);
//            return returnMap;
//        } finally {
//            try {
//                if (pstmt != null) {
//                    pstmt.close();
//                }
//                if (rs != null) {
//                    rs.close();
//                }
//            } catch (SQLException thr) {
//                // DO NOTHING
//            }
//        }
//    }

	private static Map<String, String> printFileThread(final String fileRecKey, final List<PrintPool> printPoolList) {
		// always return OK
		final Map<String, String> returnMap = new HashMap<String, String>();
		returnMap.put(MSG_ID, OK);
		returnMap.put(MSG, EMPTY);
		try {
			LOG.info("printFile:" + fileRecKey);
			if (printPoolList == null || printPoolList.isEmpty()) {
				returnMap.put(Epbdevice.MSG_ID, "nodatafound");
				returnMap.put(Epbdevice.MSG, "No printer data generated, Print key is " + fileRecKey);
				return returnMap;
			}
			final EpbPrinterJob printerJob = new EpbPrinterJob(fileRecKey);
			// 创建一个线程池
			ExecutorService pool = Executors.newFixedThreadPool(2);

			String printEncoding = EMPTY;

			List<PrintPool> printerPrintPoolList = new ArrayList<PrintPool>();
//            boolean opened;
			String printPort;
			PrintPool pp;
			int size = printPoolList.size();
			for (int index = 0; index < size; index++) {
				pp = printPoolList.get(index);
				if (PRINTER_LINE.compareTo(pp.getLineNo()) != 0) {
					printerPrintPoolList.add(pp);
				} else {
					printEncoding = pp.getConst1(); // print charset
//                    System.out.println("Encoding:" + printEncoding);
				}

				if (PRINTER_LINE.compareTo(pp.getLineNo()) == 0 || index == size - 1) {
					if (!printerPrintPoolList.isEmpty()) {
						printPort = printerPrintPoolList.get(0).getPrintPort();
//                        System.out.println("printPort:" + printPort);
						if (printPort != null
								&& (printPort.toUpperCase().startsWith(COM) || printPort.toUpperCase().startsWith(LPT)
										|| !Epbnetprinter.checkNetPort(printPort))) {
							// do nothing
						} else {
							CommonUtility.printLog("call net printer:" + printPort);
							EpbPrintThread printThread = new EpbPrintThread(printerJob, printerPrintPoolList, printPort,
									printEncoding);
							// 执行各个线程
							pool.execute(printThread);
						}
					}
					printerPrintPoolList.clear();
				}
			}

			// 关闭线程池
			pool.shutdown();
			return returnMap;
		} catch (Throwable thrl) {
			LOG.error("Failed to print", thrl);
			// TO DO
			return returnMap;
		}
	}

}
