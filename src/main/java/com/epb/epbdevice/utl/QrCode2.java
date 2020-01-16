/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epb.epbdevice.utl;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

/**
 *
 * @author sim_liang
 */
public class QrCode2 {
    
//    private static final Log LOG = LogFactory.getLog(QrCode2.class);
    
    public static synchronized void printQrCode(final PrintWriter socketWriter, final String qrdata) {
//        String qrdata = "https://redcross.give.asia/campaign/como-lifestyle-for-australian-bushfire-emergency-response#/";
        int store_len = qrdata.length() + 6;
        byte store_pL = (byte) (store_len % 256);
        byte store_pH = (byte) (store_len / 256);

        // QR Code: Select the model
        //              Hex     1D      28      6B      04      00      31      41      n1(x32)     n2(x00) - size of model
        // set n1 [49 x31, model 1] [50 x32, model 2] [51 x33, micro qr code]
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=140
        byte[] modelQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, (byte) 0x04, (byte) 0x00, (byte) 0x31, (byte) 0x41, (byte) 0x32, (byte) 0x00};
        // QR Code: Set the size of module
        // Hex      1D      28      6B      03      00      31      43      n
        // n depends on the printer
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=141
        // 0x43:67   0x45:69    0x30:48    80:0x50    81:0x51
        byte[] sizeQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, (byte) 0x03, (byte) 0x00, (byte) 0x31, (byte) 0x43, (byte) 0x05};
        //          Hex     1D      28      6B      03      00      31      45      n
        // Set n for error correction [48 x30 -> 7%] [49 x31-> 15%] [50 x32 -> 25%] [51 x33 -> 30%]
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=142
        byte[] errorQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, (byte) 0x03, (byte) 0x00, (byte) 0x31, (byte) 0x45, (byte) 0x31};
        // QR Code: Store the data in the symbol storage area
        // Hex      1D      28      6B      pL      pH      31      50      30      d1...dk
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=143
        //                        1D          28          6B         pL          pH  cn(49->x31) fn(80->x50) m(48->x30) d1…dk
        byte[] storeQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, store_pL, store_pH, (byte) 0x31, (byte) 0x50, (byte) 0x30};
        // QR Code: Print the symbol data in the symbol storage area
        // Hex      1D      28      6B      03      00      31      51      m
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=144
        byte[] printQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, (byte) 0x03, (byte) 0x00, (byte) 0x31, (byte) 0x51, (byte) 0x30};
        
        // flush() runs the print job and clears out the print buffer
        socketWriter.flush();

        // align to center
        byte[] centerCmd = {(byte) 0x1B, (byte) 0x61, (byte) 0x01}; // align to center
        byte[] selectCmd = {(byte) 0x1B, (byte) 0x3D, (byte) 0x01}; //select.
        socketWriter.print(new String(centerCmd));
//        socketWriter.flush();
        socketWriter.println(new String(selectCmd));
//        socketWriter.flush();
        
        
        // write() simply appends the data to the buffer
        socketWriter.print(new String(modelQR));
//        socketWriter.flush();

        socketWriter.print(new String(sizeQR));
//        socketWriter.flush();
        socketWriter.print(new String(errorQR));
//        socketWriter.flush();
        socketWriter.println(new String(storeQR));
//        socketWriter.flush();
        socketWriter.println(qrdata);
//        socketWriter.flush();
        socketWriter.println(new String(printQR));
        socketWriter.flush();
    }
    
    public static synchronized void printQrCode(final FileOutputStream bus, final String qrdata) {
        try {
//            String qrdata = "https://redcross.give.asia/campaign/como-lifestyle-for-australian-bushfire-emergency-response#/";
            int store_len = qrdata.length() + 6;
            byte store_pL = (byte) (store_len % 256);
            byte store_pH = (byte) (store_len / 256);

            // QR Code: Select the model
            //              Hex     1D      28      6B      04      00      31      41      n1(x32)     n2(x00) - size of model
            // set n1 [49 x31, model 1] [50 x32, model 2] [51 x33, micro qr code]
            // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=140
            byte[] modelQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, (byte) 0x04, (byte) 0x00, (byte) 0x31, (byte) 0x41, (byte) 0x32, (byte) 0x00};
            // QR Code: Set the size of module
            // Hex      1D      28      6B      03      00      31      43      n
            // n depends on the printer
            // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=141
            // 0x43:67   0x45:69    0x30:48    80:0x50    81:0x51
            byte[] sizeQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, (byte) 0x03, (byte) 0x00, (byte) 0x31, (byte) 0x43, (byte) 0x05};
            //          Hex     1D      28      6B      03      00      31      45      n
            // Set n for error correction [48 x30 -> 7%] [49 x31-> 15%] [50 x32 -> 25%] [51 x33 -> 30%]
            // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=142
            byte[] errorQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, (byte) 0x03, (byte) 0x00, (byte) 0x31, (byte) 0x45, (byte) 0x31};
            // QR Code: Store the data in the symbol storage area
            // Hex      1D      28      6B      pL      pH      31      50      30      d1...dk
            // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=143
            //                        1D          28          6B         pL          pH  cn(49->x31) fn(80->x50) m(48->x30) d1…dk
            byte[] storeQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, store_pL, store_pH, (byte) 0x31, (byte) 0x50, (byte) 0x30};
            // QR Code: Print the symbol data in the symbol storage area
            // Hex      1D      28      6B      03      00      31      51      m
            // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=144
            byte[] printQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, (byte) 0x03, (byte) 0x00, (byte) 0x31, (byte) 0x51, (byte) 0x30};

            // flush() runs the print job and clears out the print buffer
            bus.flush();

            // align to center
            byte[] centerCmd = {(byte) 0x1B, (byte) 0x61, (byte) 0x01}; // align to center
            byte[] selectCmd = {(byte) 0x1B, (byte) 0x3D, (byte) 0x01}; //select.
            bus.write(centerCmd);
            bus.write(selectCmd);


            // write() simply appends the data to the buffer
            bus.write(modelQR);
            
            bus.write(sizeQR);
            bus.write(errorQR);
            bus.write(storeQR);
            bus.write(qrdata.getBytes());
            bus.write(printQR);
            bus.flush();
        } catch (Throwable thrl) {
//            LOG.error("printQrCode Failed", thrl);
            System.out.println("com.epb.epbdevice.utl.QrCode2.printQrCode():" + thrl.getMessage());
        }
    }
    
    public static synchronized void printQrCode(final OutputStream bus, final String qrdata) {
        try {
//            String qrdata = "https://redcross.give.asia/campaign/como-lifestyle-for-australian-bushfire-emergency-response#/";
            int store_len = qrdata.length() + 6;
            byte store_pL = (byte) (store_len % 256);
            byte store_pH = (byte) (store_len / 256);

            // QR Code: Select the model
            //              Hex     1D      28      6B      04      00      31      41      n1(x32)     n2(x00) - size of model
            // set n1 [49 x31, model 1] [50 x32, model 2] [51 x33, micro qr code]
            // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=140
            byte[] modelQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, (byte) 0x04, (byte) 0x00, (byte) 0x31, (byte) 0x41, (byte) 0x32, (byte) 0x00};
            // QR Code: Set the size of module
            // Hex      1D      28      6B      03      00      31      43      n
            // n depends on the printer
            // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=141
            // 0x43:67   0x45:69    0x30:48    80:0x50    81:0x51
            byte[] sizeQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, (byte) 0x03, (byte) 0x00, (byte) 0x31, (byte) 0x43, (byte) 0x05};
            //          Hex     1D      28      6B      03      00      31      45      n
            // Set n for error correction [48 x30 -> 7%] [49 x31-> 15%] [50 x32 -> 25%] [51 x33 -> 30%]
            // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=142
            byte[] errorQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, (byte) 0x03, (byte) 0x00, (byte) 0x31, (byte) 0x45, (byte) 0x31};
            // QR Code: Store the data in the symbol storage area
            // Hex      1D      28      6B      pL      pH      31      50      30      d1...dk
            // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=143
            //                        1D          28          6B         pL          pH  cn(49->x31) fn(80->x50) m(48->x30) d1…dk
            byte[] storeQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, store_pL, store_pH, (byte) 0x31, (byte) 0x50, (byte) 0x30};
            // QR Code: Print the symbol data in the symbol storage area
            // Hex      1D      28      6B      03      00      31      51      m
            // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=144
            byte[] printQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, (byte) 0x03, (byte) 0x00, (byte) 0x31, (byte) 0x51, (byte) 0x30};

            // flush() runs the print job and clears out the print buffer
            bus.flush();

            // align to center
            byte[] centerCmd = {(byte) 0x1B, (byte) 0x61, (byte) 0x01}; // align to center
            byte[] selectCmd = {(byte) 0x1B, (byte) 0x3D, (byte) 0x01}; //select.
            bus.write(centerCmd);
            bus.write(selectCmd);


            // write() simply appends the data to the buffer
            bus.write(modelQR);
            
            bus.write(sizeQR);
            bus.write(errorQR);
            bus.write(storeQR);
            bus.write(qrdata.getBytes());
            bus.write(printQR);
            bus.flush();
        } catch (Throwable thrl) {
//            LOG.error("printQrCode Failed", thrl);
            System.out.println("com.epb.epbdevice.utl.QrCode2.printQrCode():" + thrl.getMessage());
        }
    }
    
    public static synchronized void printQrCode(final BufferedOutputStream bos, final String qrdata) {
        try {
//        String qrdata = "https://redcross.give.asia/campaign/como-lifestyle-for-australian-bushfire-emergency-response#/";
            int store_len = qrdata.length() + 6;
            byte store_pL = (byte) (store_len % 256);
            byte store_pH = (byte) (store_len / 256);

            // QR Code: Select the model
            //              Hex     1D      28      6B      04      00      31      41      n1(x32)     n2(x00) - size of model
            // set n1 [49 x31, model 1] [50 x32, model 2] [51 x33, micro qr code]
            // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=140
            byte[] modelQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, (byte) 0x04, (byte) 0x00, (byte) 0x31, (byte) 0x41, (byte) 0x32, (byte) 0x00};
            // QR Code: Set the size of module
            // Hex      1D      28      6B      03      00      31      43      n
            // n depends on the printer
            // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=141
            // 0x43:67   0x45:69    0x30:48    80:0x50    81:0x51
            byte[] sizeQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, (byte) 0x03, (byte) 0x00, (byte) 0x31, (byte) 0x43, (byte) 0x05};
            //          Hex     1D      28      6B      03      00      31      45      n
            // Set n for error correction [48 x30 -> 7%] [49 x31-> 15%] [50 x32 -> 25%] [51 x33 -> 30%]
            // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=142
            byte[] errorQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, (byte) 0x03, (byte) 0x00, (byte) 0x31, (byte) 0x45, (byte) 0x31};
            // QR Code: Store the data in the symbol storage area
            // Hex      1D      28      6B      pL      pH      31      50      30      d1...dk
            // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=143
            //                        1D          28          6B         pL          pH  cn(49->x31) fn(80->x50) m(48->x30) d1…dk
            byte[] storeQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, store_pL, store_pH, (byte) 0x31, (byte) 0x50, (byte) 0x30};
            // QR Code: Print the symbol data in the symbol storage area
            // Hex      1D      28      6B      03      00      31      51      m
            // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=144
            byte[] printQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, (byte) 0x03, (byte) 0x00, (byte) 0x31, (byte) 0x51, (byte) 0x30};

            // flush() runs the print job and clears out the print buffer
            bos.flush();

            // align to center
            byte[] centerCmd = {(byte) 0x1B, (byte) 0x61, (byte) 0x01}; // align to center
            byte[] selectCmd = {(byte) 0x1B, (byte) 0x3D, (byte) 0x01}; //select.
            bos.write(centerCmd);
//            socketWriter.write(("\r\n").getBytes());
            bos.write(selectCmd);
            bos.write(("\r\n").getBytes());


            // write() simply appends the data to the buffer
            bos.write(modelQR);
//            socketWriter.write(("\r\n").getBytes());

            bos.write(sizeQR);
//            socketWriter.write(("\r\n").getBytes());
            bos.write(errorQR);
//            socketWriter.write(("\r\n").getBytes());
            bos.write(storeQR);
            bos.write(("\r\n").getBytes());
            bos.write(qrdata.getBytes());
            bos.write(("\r\n").getBytes());
            bos.write(printQR);
            bos.write(("\r\n").getBytes());
            bos.flush();
        } catch (Throwable thrl) {
            System.out.println(thrl);
        }
    }
}
