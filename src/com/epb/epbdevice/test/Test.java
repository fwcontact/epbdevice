package com.epb.epbdevice.test;

import com.epb.epbdevice.Epbdevice;
import com.epb.epbdevice.beans.PrintPool;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;

public class Test {
//    
//    public static List<PrintPool> getTestPrintPool() {
//        final List<PrintPool> list = new ArrayList<PrintPool>();
//        try {
//            String driver = "oracle.jdbc.driver.OracleDriver"; 
//            String url = "jdbc:oracle:thin:@192.168.1.11:1521:orcl";
//            String user = "EPBSH";
//            String pwd = "EPBSH";
//            Class.forName(driver);
//            System.out.println("driver is ok");
//
//            Connection conn = DriverManager.getConnection(url, user, pwd);
//            System.out.println("conection is ok");
//
////            Statement statement = conn.createStatement();
//            //conn.setAutoCommit(false);
//
//            //输入参数      
//            String orgId = "04";
//            String locId = "03";
//            String posNo = "POS001";
//            String transType = "A";  // pos document trans type, for excemple A is SALES, E is return
//            String recKey = "123";   // OPENTABLE.rec_key
//            String userId = "Admin"; // Waiter
//
//            //调用函数
//            CallableStatement stmt = (CallableStatement ) conn.prepareCall("call get_pos_print_array(?,?,?,?,?,?,?,?,?)");
//            stmt.registerOutParameter(1, java.sql.Types.VARCHAR);
//            stmt.registerOutParameter(2, java.sql.Types.VARCHAR);
//            stmt.setString(3, orgId);
//            stmt.setString(4, locId);
//            stmt.setString(5, posNo);
//            stmt.setString(6, transType);
//            stmt.setString(7, recKey);
//            stmt.setString(8, userId);
////            stmt.registerOutParameter(9, java.sql.Types.);
//            stmt.registerOutParameter(9, OracleTypes.CURSOR);
//            stmt.execute();
//            //取的结果集的方式一：
//            ResultSet rs = ((OracleCallableStatement) stmt).getCursor(9);
//            //取的结果集的方式二：
//            //  rs = (ResultSet) stmt.getObject(1);
////            String ric;
////            String price;
////            String updated;
//
//            PrintPool printPool;
//            while (rs.next()) {
//                printPool = new PrintPool();
//                System.out.println(new BigDecimal(rs.getString(1)) + "," + new BigDecimal(rs.getString(2)) + "," + rs.getString(3) + "," + rs.getString(4));
//                printPool.setLineNo(new BigDecimal(rs.getString(1)));
//                printPool.setOrderNo(new BigDecimal(rs.getString(2)).toBigInteger());
//                printPool.setPrintCommand(rs.getString(3));
//                printPool.setConst1(rs.getString(4));
//                printPool.setConst2(rs.getString(5));
//                printPool.setFormat(rs.getString(6));
//                printPool.setLength(rs.getString(7) == null || rs.getString(7).length() == 0 ? null : new BigDecimal(rs.getString(7)).toBigInteger());
//                printPool.setAlign(rs.getString(8));
//                printPool.setBreakFlg(rs.getString(9));
//                printPool.setFillBlankFlg(rs.getString(10));
//                printPool.setVal(rs.getString(11));
////                line_no         NUMBER(8,2),
////  order_no        NUMBER(8),
////  print_command   VARCHAR2(256),
////  const1          VARCHAR2(128),
////  const2          VARCHAR2(128),
////  format          VARCHAR2(128),
////  lenth           NUMBER(8),
////  align           CHAR(1),
////  break_flg       CHAR(1),
////  fill_blank_flg  CHAR(1),  
////  val             VARCHAR2(128)
//                list.add(printPool);
////                ric = rs.getString(1);
////                price = rs.getString(2);
////                updated = rs.getString(3);
////                System.out.println("ric:" + ric + ";-- price:" + price + "; --" + updated + "; ");
//            }
//            return list;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return list;
//        }
//    }
//    
//    private static void testPrintBardcode() {
//        try {
//            Socket socket = socket = new Socket();
//// 设置发送地址  
//            SocketAddress addr = new InetSocketAddress("192.168.1.68", 9100);
//// 超时1秒，并连接服务器  
//            socket.connect(addr, 1000);
//
//            OutputStream socketOut = socket.getOutputStream();
//            socket.isClosed();
//            OutputStreamWriter writer = new OutputStreamWriter(socketOut, "GBK");
//            try {
//
//                //初始化打印机  
//                writer.write(0x1B);
//                writer.write(0x40);
//                writer.flush();
//
//                //打印文本  
//                writer.write("文本内容");
//                writer.write("\n");
//                writer.write("\n");
//                writer.flush();
//
//                //切纸  
//                writer.write(0x1D);
//                writer.write(86);
//                writer.write(65);
//                writer.write(0);
//                writer.flush();
//                
////                writer.write(0x1B);
////                writer.write(97);
////                writer.write(1);//字体居中  
////                
////                writer.write(0x1D);
////                writer.write(33);
//////横向放大一倍 要将纵向放大与横向放大求和  
////                writer.write(17);
////                
////                //设置字体大小默认  
////                writer.write(0x1D);
////                writer.write(33);
////                writer.write(0);
////                
////                writer.write(0x1B);
////                writer.write(97);
////                //设置条码居中  
////                writer.write(1);
////                
////                writer.write(0x1D);
////                writer.write('w');
////                writer.write(2);//默认是2  2-6 之间  
////                writer.flush();
////                
////                //设置条形码的高度  
////                writer.write(0x1D);
////                writer.write('h');
////                writer.write(120);//默认是60  
////                writer.flush();
////                
////                // 条码注释打印在条码下方
////                writer.write(0x1D);
////                writer.write(72);
////                writer.write(2);
////                
////                // 打印条码
////                writer.write(0x1D);
////                writer.write('k');
//////选择code128  
////                writer.write(73);
//////设置字符个数  
////                writer.write(14);
//////使用CODEB来打印  
////                writer.write(123);
////                writer.write(66);
//////条形码内容  
////                writer.write("51266669");
////                writer.flush();
//
//            } finally {
//                writer.close();
//                socketOut.close();
//                socket.close();
//            }
//        } catch (Throwable ex) {
//        }
//    }
//
//    public static void main(String args[]) {
//        if (1 == 1) {
//            testPrintBardcode();
//            return;
//        }
//        final List<PrintPool> list = getTestPrintPool();
//        if (list == null || list.isEmpty()) {
//            return;
//        }
//        // initial COM port when open POS
//        Epbdevice.initBat("D:\\EPBrowser\\EPBSH\\Shell\\init.bat");
//
////        String printPort = "COM2";
//        String printPort = "D:\\test.txt";
////        String printPort = "192.168.0.201";
////        if (printPort != null && (printPort.toUpperCase().startsWith("COM") || printPort.toUpperCase().startsWith("LPT"))) { // printPort = "COM2"
//            // printer
//            Epbprinter.OpenEpbprinter(printPort);
////            Epbprinter.printText("          Test ORDER          ");
////            Epbprinter.printText("Casher    :Admin");
////            Epbprinter.printText("Print Time:2018/10/15");
////            Epbprinter.printText("No      Stock ID      Qty      Amount");
////            Epbprinter.printText("1       A001          1        20.00");
////            Epbprinter.printText("2       A002          1        50.00");
////            Epbprinter.printText("                     Pay Money:70.00");
////            Epbprinter.cutPaper();
//            Epbprinter.printPosReceipt(list);
//            Epbprinter.closePrinter();
//
////            // open drawer
////            Epbprinter.openDrawer(printPort, null);
////        } else if (printPort != null && Epbnetprinter.checkNetPort(printPort)) { // printPort = "192.168.0.201"
////            // net printer
////            // net printer not support open drawer
////            if (Epbnetprinter.openEpbNetPrinter(printPort)) {
////                Epbnetprinter.printText("          Test ORDER          ");
////                Epbnetprinter.printText("Casher    :Admin");
////                Epbnetprinter.printText("Print Time:2018/10/15");
////                Epbnetprinter.printText("No      Stock ID      Qty      Amount");
////                Epbnetprinter.printText("1       A001          1        20.00");
////                Epbnetprinter.printText("2       A002          1        50.00");
////                Epbnetprinter.printText("                     Pay Money:70.00");
////                Epbnetprinter.cutPaper();
////                Epbnetprinter.closeNetPrinter();
////            }
////        }
//    }
}
