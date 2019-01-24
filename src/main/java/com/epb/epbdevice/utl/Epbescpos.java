/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epb.epbdevice.utl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.imageio.ImageIO;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

/**
 *
 * @author liang
 */
public class Epbescpos {
    
//    private static final Log LOG = LogFactory.getLog(Epbescpos.class);
    // Feed control sequences,0x0a=10,打印并换行,打印行缓冲器里的内容并向前走纸一行。当行缓冲器为 空时只向前走纸一行。  在页模式下：输出行缓冲器里的内容，光标定位到下一行
    private static final byte[] CTL_LF          = {0x0a};          // Print and line feed
    // Line Spacing,0x1b=27,0x33=51
    private static final byte[] LINE_SPACE_24 = {0x1b,0x33,24}; // Set the line spacing at 24.   27,51,n:设置行间距为n点行,n = 0-255,默认值行间距是30点
    private static final byte[] LINE_SPACE_30 = {0x1b,0x33,30}; // Set the line spacing at 30.
    //Image,0x1B=27,0x2A=42
    private static final byte[] SELECT_BIT_IMAGE_MODE = {0x1B, 0x2A, 33};
//    // Printer hardware
//    private static final byte[] HW_INIT         = {0x1b,0x40};          // Clear data in buffer and reset modes
//    // Cash Drawer
//    private static final byte[] CD_KICK_2       = {0x1b,0x70,0x00};      // Sends a pulse to pin 2 []
//    private static final byte[] CD_KICK_5       = {0x1b,0x70,0x01};      // Sends a pulse to pin 5 []
//    // Paper
//    private static final byte[]  PAPER_FULL_CUT = {0x1d,0x56,0x00}; // Full cut paper
//    private static final byte[]  PAPER_PART_CUT = {0x1d,0x56,0x01}; // Partial cut paper
//    // Text format
//    private static final byte[] TXT_NORMAL      = {0x1b,0x21,0x00}; // Normal text
//    private static final byte[] TXT_2HEIGHT     = {0x1b,0x21,0x10}; // Double height text
//    private static final byte[] TXT_2WIDTH      = {0x1b,0x21,0x20}; // Double width text
//    private static final byte[] TXT_4SQUARE     = {0x1b,0x21,0x30}; // Quad area text
//    private static final byte[] TXT_UNDERL_OFF  = {0x1b,0x2d,0x00}; // Underline font OFF
//    private static final byte[] TXT_UNDERL_ON   = {0x1b,0x2d,0x01}; // Underline font 1-dot ON
//    private static final byte[] TXT_UNDERL2_ON  = {0x1b,0x2d,0x02}; // Underline font 2-dot ON
//    private static final byte[] TXT_BOLD_OFF    = {0x1b,0x45,0x00}; // Bold font OFF
//    private static final byte[] TXT_BOLD_ON     = {0x1b,0x45,0x01}; // Bold font ON
//    private static final byte[] TXT_FONT_A      = {0x1b,0x4d,0x00}; // Font type A
//    private static final byte[] TXT_FONT_B      = {0x1b,0x4d,0x01};// Font type B
//    private static final byte[] TXT_ALIGN_LT    = {0x1b,0x61,0x00}; // Left justification
//    private static final byte[] TXT_ALIGN_CT    = {0x1b,0x61,0x01}; // Centering
//    private static final byte[] TXT_ALIGN_RT    = {0x1b,0x61,0x02}; // Right justification
//    // Char code table
//    private static final byte[] CHARCODE_PC437  = {0x1b,0x74,0x00}; // USA){ Standard Europe
//    private static final byte[] CHARCODE_JIS    = {0x1b,0x74,0x01}; // Japanese Katakana
//    private static final byte[] CHARCODE_PC850  = {0x1b,0x74,0x02}; // Multilingual
//    private static final byte[] CHARCODE_PC860  = {0x1b,0x74,0x03}; // Portuguese
//    private static final byte[] CHARCODE_PC863  = {0x1b,0x74,0x04}; // Canadian-French
//    private static final byte[] CHARCODE_PC865  = {0x1b,0x74,0x05}; // Nordic
//    private static final byte[] CHARCODE_WEU    = {0x1b,0x74,0x06}; // Simplified Kanji, Hirakana
//    private static final byte[] CHARCODE_GREEK  = {0x1b,0x74,0x07}; // Simplified Kanji
//    private static final byte[] CHARCODE_HEBREW = {0x1b,0x74,0x08}; // Simplified Kanji
//    private static final byte[] CHARCODE_PC1252 = {0x1b,0x74,0x10}; // Western European Windows Code Set
//    private static final byte[] CHARCODE_PC866  = {0x1b,0x74,0x12}; // Cirillic //2
//    private static final byte[] CHARCODE_PC852  = {0x1b,0x74,0x13}; // Latin 2
//    private static final byte[] CHARCODE_PC858  = {0x1b,0x74,0x14}; // Euro
//    private static final byte[] CHARCODE_THAI42 = {0x1b,0x74,0x15}; // Thai character code 42
//    private static final byte[] CHARCODE_THAI11 = {0x1b,0x74,0x16}; // Thai character code 11
//    private static final byte[] CHARCODE_THAI13 = {0x1b,0x74,0x17}; // Thai character code 13
//    private static final byte[] CHARCODE_THAI14 = {0x1b,0x74,0x18}; // Thai character code 14
//    private static final byte[] CHARCODE_THAI16 = {0x1b,0x74,0x19}; // Thai character code 16
//    private static final byte[] CHARCODE_THAI17 = {0x1b,0x74,0x1a}; // Thai character code 17
//    private static final byte[] CHARCODE_THAI18 = {0x1b,0x74,0x1b}; // Thai character code 18
//
//    // Barcode format
//    private static final byte[] BARCODE_TXT_OFF = {0x1d,0x48,0x00}; // HRI printBarcode chars OFF
//    private static final byte[] BARCODE_TXT_ABV = {0x1d,0x48,0x01}; // HRI printBarcode chars above
//    private static final byte[] BARCODE_TXT_BLW = {0x1d,0x48,0x02}; // HRI printBarcode chars below
//    private static final byte[] BARCODE_TXT_BTH = {0x1d,0x48,0x03}; // HRI printBarcode chars both above and below
//    private static final byte[] BARCODE_FONT_A  = {0x1d,0x66,0x00}; // Font type A for HRI printBarcode chars
//    private static final byte[] BARCODE_FONT_B  = {0x1d,0x66,0x01}; // Font type B for HRI printBarcode chars
//    private static final byte[] BARCODE_HEIGHT  = {0x1d,0x68,0x64}; // Barcode Height [1-255]
//    private static final byte[] BARCODE_WIDTH   = {0x1d,0x77,0x03}; // Barcode Width  [2-6]
//    private static final byte[] BARCODE_UPC_A   = {0x1d,0x6b,0x00}; // Barcode type UPC-A
//    private static final byte[] BARCODE_UPC_E   = {0x1d,0x6b,0x01}; // Barcode type UPC-E
//    private static final byte[] BARCODE_EAN13   = {0x1d,0x6b,0x02}; // Barcode type EAN13
//    private static final byte[] BARCODE_EAN8    = {0x1d,0x6b,0x03}; // Barcode type EAN8
//    private static final byte[] BARCODE_CODE39  = {0x1d,0x6b,0x04}; // Barcode type CODE39
//    private static final byte[] BARCODE_ITF     = {0x1d,0x6b,0x05}; // Barcode type ITF
//    private static final byte[] BARCODE_NW7     = {0x1d,0x6b,0x06}; // Barcode type NW7
//    // Printing Density
//    private static final byte[] PD_N50          = {0x1d,0x7c,0x00}; // Printing Density -50%
//    private static final byte[] PD_N37          = {0x1d,0x7c,0x01}; // Printing Density -37.5%
//    private static final byte[] PD_N25          = {0x1d,0x7c,0x02}; // Printing Density -25%
//    private static final byte[] PD_N12          = {0x1d,0x7c,0x03}; // Printing Density -12.5%
//    private static final byte[] PD_0            = {0x1d,0x7c,0x04}; // Printing Density  0%
//    private static final byte[] PD_P50          = {0x1d,0x7c,0x08}; // Printing Density +50%
//    private static final byte[] PD_P37          = {0x1d,0x7c,0x07}; // Printing Density +37.5%
//    private static final byte[] PD_P25          = {0x1d,0x7c,0x06}; // Printing Density +25%
//    private static final byte[] PD_P12          = {0x1d,0x7c,0x05}; // Printing Density +12.5%
    private static final String SPACE = " ";
    private static final String TEPDIR_PROPERTY = "java.io.tmpdir"; //NOI18N
    
    public static void printImage(FileOutputStream bus, int linePosition, BufferedImage image) {
        try {
            Image img = new Image();
            int[][] pixels = img.getPixelsSlow(image);
            for (int y = 0; y < pixels.length; y += 24) {
                for (int i = 0; i < linePosition; i++) {
                    bus.write(SPACE.getBytes());
                }
                bus.write(LINE_SPACE_30);    
                bus.write(SELECT_BIT_IMAGE_MODE);
                bus.write(new byte[]{(byte) (0x00ff & pixels[y].length), (byte) ((0xff00 & pixels[y].length) >> 8)});
                for (int x = 0; x < pixels[y].length; x++) {
                    bus.write(img.recollectSlice(y, x, pixels));
                }
                bus.write(CTL_LF);
                bus.flush();
            }
            bus.flush();
//            bus.write(CTL_LF);
        } catch (Throwable e) {
//            LOG.error("failed to printImage", e);
            System.out.println(e.getMessage());
        }
    }
    
    public static void printImage(PrintWriter bus, int linePosition, BufferedImage image) {
        try {
            Image img = new Image();
            int[][] pixels = img.getPixelsSlow(image);
            for (int y = 0; y < pixels.length; y += 24) {
                for (int i = 0; i < linePosition; i++) {
                    bus.println(SPACE.getBytes());
                }
                bus.println(LINE_SPACE_30);    
                bus.println(SELECT_BIT_IMAGE_MODE);
                bus.println(new byte[]{(byte) (0x00ff & pixels[y].length), (byte) ((0xff00 & pixels[y].length) >> 8)});
                for (int x = 0; x < pixels[y].length; x++) {
                    bus.println(img.recollectSlice(y, x, pixels));
                }
                bus.println(CTL_LF);
                bus.flush();
            }
            bus.flush();
//            bus.write(CTL_LF);
        } catch (Throwable e) {
//            LOG.error("failed to printImage", e);
            System.out.println(e.getMessage());
        }
    }
    
    public static void printImage(FileOutputStream bus, int linePosition, final String imagePath) {
        try {
            File file = new File(imagePath);
            if (file.exists()) {
                BufferedImage read = ImageIO.read(file);
                printImage(bus, linePosition, read);
            } else {
//                LOG.debug("image does not exists" + "-->" + imagePath);
                System.out.println("image does not exists" + "-->" + imagePath);
            }
        } catch (Throwable ex) {
//            LOG.error("failed to printImage", ex);
            System.out.println("failed to printImage" + "-->" + ex.getMessage());
        }
    }
    
    public static void printImage(PrintWriter bus, int linePosition, final String imagePath) {
        try {
            File file = new File(imagePath);
            if (file.exists()) {
                BufferedImage read = ImageIO.read(file);
                printImage(bus, linePosition, read);
            } else {
//                LOG.debug("image does not exists" + "-->" + imagePath);
                System.out.println("image does not exists" + "-->" + imagePath);
            }
        } catch (Throwable ex) {
//            LOG.error("failed to printImage", ex);
            System.out.println("failed to printImage" + "-->" + ex.getMessage());
        }
    }
    
    
    public static void printQRCode(FileOutputStream bus, int linePosition, String qrCode, int size) throws QRCodeException {
        QRCodeGenerator q = new QRCodeGenerator();
        printImage(bus, linePosition, q.generate(qrCode, size));
    }
    
    public static void printQRCode(PrintWriter bus, int linePosition, String qrCode, int size) throws QRCodeException {
        QRCodeGenerator q = new QRCodeGenerator();
        printImage(bus, linePosition, q.generate(qrCode, size));
    }

    public static void printQRCode(FileOutputStream bus, int linePosition, String qrCode) throws QRCodeException {
        try {
            printQRCode(bus, linePosition, qrCode, 150);
        } catch (Throwable ex) {
//            LOG.error("failed to printQRCode", ex);
            System.out.println("failed to printQRCode" + "-->" + ex.getMessage());
        }
    }
    
    public static void printImage(OutputStream bus, int linePosition, BufferedImage image) {
        try {
            Image img = new Image();
            int[][] pixels = img.getPixelsSlow(image);
            for (int y = 0; y < pixels.length; y += 24) {
                for (int i = 0; i < linePosition; i++) {
                    bus.write(SPACE.getBytes());
                }
                bus.write(LINE_SPACE_30);     
                bus.write(SELECT_BIT_IMAGE_MODE);
                bus.write(new byte[]{(byte) (0x00ff & pixels[y].length), (byte) ((0xff00 & pixels[y].length) >> 8)});
                for (int x = 0; x < pixels[y].length; x++) {
                    bus.write(img.recollectSlice(y, x, pixels));
                }
                bus.write(CTL_LF);
                bus.flush();
            }
            bus.flush();
//            bus.write(CTL_LF);
        } catch (Throwable e) {
//            LOG.error("failed to printImage", e);
            System.out.println("failed to printImage" + "-->" + e.getMessage());
        }
    }
    
    public static void printQRCode(OutputStream bus, int linePosition, String qrCode, int size) throws QRCodeException {
        QRCodeGenerator q = new QRCodeGenerator();
        printImage(bus, linePosition, q.generate(qrCode, size));
    }

    public static void printQRCode(OutputStream bus, int linePosition, String qrCode) throws QRCodeException {
        try {
            printQRCode(bus, linePosition, qrCode, 150);
        } catch (Throwable ex) {
//            LOG.error("failed to printQRCode", ex);
            System.out.println("failed to printQRCode" + "-->" + ex.getMessage());
        }
    }
    
    public static void printImage(OutputStream bus, int linePosition, final String imagePath) {
        try {
            File file = new File(imagePath);
            if (file.exists()) {
                BufferedImage read = ImageIO.read(file);
                printImage(bus, linePosition, read);
            } else {
//                LOG.debug("image does not exists" + "-->" + imagePath);
                System.out.println("image does not exists" + "-->" + imagePath);
            }
        } catch (Throwable ex) {
//            LOG.error("failed to printImage", ex);
            System.out.println("failed to printImage" + "-->" + ex);
        }
    }
    
    
    
    // best for 115200 baudrate
    public static void printImageBaudrate115200(FileOutputStream bus, int linePosition, BufferedImage image) {
        try {
            Thread.sleep(100); // sleep 100 ms
            Image img = new Image();
            int[][] pixels = img.getPixelsSlow(image);
            for (int y = 0; y < pixels.length; y += 24) {
                for (int i = 0; i < linePosition; i++) {
                    bus.write(SPACE.getBytes());
                }
                bus.write(LINE_SPACE_24);    
                bus.write(SELECT_BIT_IMAGE_MODE);
//                System.out.println("----");
                bus.write(new byte[]{(byte) (0x00ff & pixels[y].length), (byte) ((0xff00 & pixels[y].length) >> 8)});
                for (int x = 0; x < pixels[y].length; x++) {
                    bus.write(img.recollectSlice(y, x, pixels));
                }
                bus.write(CTL_LF);
//                bus.write(("\r\n").getBytes());
                bus.flush();
            }
            bus.flush();
//            bus.write(CTL_LF);
        } catch (Throwable e) {
//            LOG.error("failed to printImageBaudrate115200", e);
            System.out.println("failed to printImageBaudrate115200" + "-->" + e);
        }
    }
    
    public static void printImageBaudrate115200(PrintWriter bus, int linePosition, BufferedImage image) {
        try {
            Thread.sleep(100); // sleep 100 ms
            Image img = new Image();
            int[][] pixels = img.getPixelsSlow(image);
            for (int y = 0; y < pixels.length; y += 24) {
                for (int i = 0; i < linePosition; i++) {
                    bus.println(SPACE.getBytes());
                }
                bus.println(LINE_SPACE_24);    
                bus.println(SELECT_BIT_IMAGE_MODE);
//                System.out.println("----");
                bus.println(new byte[]{(byte) (0x00ff & pixels[y].length), (byte) ((0xff00 & pixels[y].length) >> 8)});
                for (int x = 0; x < pixels[y].length; x++) {
                    bus.println(img.recollectSlice(y, x, pixels));
                }
                bus.println(CTL_LF);
//                bus.write(("\r\n").getBytes());
                bus.flush();
            }
            bus.flush();
//            bus.write(CTL_LF);
        } catch (Throwable e) {
//            LOG.error("failed to printImageBaudrate115200", e);
            System.out.println("failed to printImageBaudrate115200" + "-->" + e);
        }
    }
    
    public static void printQRCodeBaudrate115200(FileOutputStream bus, int linePosition, String qrCode, int size) throws QRCodeException {
        QRCodeGenerator q = new QRCodeGenerator();
        BufferedImage bufferedImage = q.generate(qrCode, size);
//        String qrFilePath = generateQR(bufferedImage, size);
        printImageBaudrate115200(bus, linePosition, bufferedImage);
//        if (qrFilePath != null) {
//            printImageBaudrate115200(bus, linePosition, qrFilePath);
//        }        
    }
    
    public static void printQRCodeBaudrate115200(PrintWriter bus, int linePosition, String qrCode, int size) throws QRCodeException {
        QRCodeGenerator q = new QRCodeGenerator();
        BufferedImage bufferedImage = q.generate(qrCode, size);
//        String qrFilePath = generateQR(bufferedImage, size);
        printImageBaudrate115200(bus, linePosition, bufferedImage);
//        if (qrFilePath != null) {
//            printImageBaudrate115200(bus, linePosition, qrFilePath);
//        }        
    }

    public static void printQRCodeBaudrate115200(FileOutputStream bus, int linePosition, String qrCode) throws QRCodeException {
        try {
            printQRCodeBaudrate115200(bus, linePosition, qrCode, 150);
        } catch (Throwable ex) {
//            LOG.error("failed to printQRCodeBaudrate115200", ex);
            System.out.println("failed to printQRCodeBaudrate115200" + "-->" + ex);
        }
    }
    
    public static void printQRCodeBaudrate115200(PrintWriter bus, int linePosition, String qrCode) throws QRCodeException {
        try {
            printQRCodeBaudrate115200(bus, linePosition, qrCode, 150);
        } catch (Throwable ex) {
//            LOG.error("failed to printQRCodeBaudrate115200", ex);
            System.out.println("failed to printQRCodeBaudrate115200" + "-->" + ex);
        }
    }
    
    public static void printImageBaudrate115200(FileOutputStream bus, int linePosition, final String imagePath) {
        try {
            File file = new File(imagePath);
            if (file.exists()) {
                BufferedImage read = ImageIO.read(file);
                printImageBaudrate115200(bus, linePosition, read);
            } else {
//                LOG.debug("image does not exists" + "-->" + imagePath);
                System.out.println("image does not exists" + "-->" + imagePath);
            }            
        } catch (Throwable ex) {
//            LOG.error("failed to printImageBaudrate115200", ex);
            System.out.println("failed to printImageBaudrate115200" + "-->" + ex);
        }
    }
    
    public static void printImageBaudrate115200(PrintWriter bus, int linePosition, final String imagePath) {
        try {
            File file = new File(imagePath);
            if (file.exists()) {
                BufferedImage read = ImageIO.read(file);
                printImageBaudrate115200(bus, linePosition, read);
            } else {
//                LOG.debug("image does not exists" + "-->" + imagePath);
                System.out.println("image does not exists" + "-->" + imagePath);
            }            
        } catch (Throwable ex) {
//            LOG.error("failed to printImageBaudrate115200", ex);
            System.out.println("failed to printImageBaudrate115200" + "-->" + ex.getMessage());
        }
    }
    
    public static String generateBarcode128(final String barcode) {
        return generateBarcode128(barcode, 0);
    }
    
    public static String generateBarcode128(final String barcode, final int fontSize) {
        try {
            final File reportFile = new File(System.getProperty(TEPDIR_PROPERTY), "report");
            final String reportPath = reportFile.getPath();
//            final String reportPath = EpbSharedObjects.getApplicationLaunchPath().getPath()
//                    + System.getProperty("file.separator")  + "report";
            final String barcode128FilePath =
                    reportPath + System.getProperty("file.separator") + "barcode"+ barcode + (System.currentTimeMillis()) + ".bmp";
            File reportFoleder = new File(reportPath);
            if (!reportFoleder.exists()) {
                reportFoleder.mkdir();
            }
            File barcode128File = new File(barcode128FilePath);
            if (barcode128File.exists()) {
                barcode128File.delete();
            }
            if (barcode128File.exists()) {
//                LOG.debug("barcode128File exists");
                System.out.println("barcode128File exists");
                return null;
            }
            BarcodeGenerator.generate128File(barcode, barcode128FilePath, fontSize);
            if (barcode128File.exists()) {
                return barcode128FilePath;
            }
            barcode128File = null;
//            LOG.debug("failed to generateBarcode128");
            System.out.println("failed to generateBarcode128");
            return null;
        } catch (Throwable ex) {
//            LOG.error("failed to generateBarcode128", ex);
            System.out.println("failed to generateBarcode128" + "-->" + ex.getMessage());
            return null;
        }
    }
    
    public static void resizeImage(String srcImgPath, String distImgPath,
            int width, int height) throws IOException {

        File srcFile = new File(srcImgPath);
        java.awt.Image srcImg = ImageIO.read(srcFile);
        BufferedImage buffImg = null;
        buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        buffImg.getGraphics().drawImage(
                srcImg.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0,
                0, 
                null);

        ImageIO.write(buffImg, "BMP", new File(distImgPath));
        srcFile = null;
    }
    
    private static void change() {
        try {
            BufferedImage src = ImageIO.read(new File("D:\\SJ_LOGO_xx.bmp")); // 读入源图像
            int width = src.getWidth(); // 源图宽
            int height = src.getHeight(); // 源图高
 
            java.awt.Image image = src.getScaledInstance((int) (width), (int) (height),
                    java.awt.Image.SCALE_DEFAULT);
 
            BufferedImage tag = new BufferedImage((int) (width),
                    (int) (height), BufferedImage.TYPE_BYTE_BINARY);
            Graphics2D g = tag.createGraphics();
 
            g.drawImage(image, 0, 0, null);
 
            g.dispose();
 
            OutputStream out = new FileOutputStream("D:\\SJ_LOGO_xx2.jpg");
            tag.setRGB(5, 5, 123);
            ImageIO.write(tag, "JPG", out);
            out.close();

        } catch (Throwable ex) {
            System.out.println(ex);
        }
    }
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
//            change();
////            resizeImage("D:\\pic\\SJ_LOGO2.bmp", "D:\\pic\\SJ_LOGO3.bmp", 450, 130);
//            if (1 == 1) {
//                return;
//            }
//            FileOutputStream ioPrint;
//            try {
//                ioPrint = new FileOutputStream("COM3");
//            } catch (Throwable ex) {
//                ioPrint = null;
//            }
//            if (ioPrint == null) {
//                System.out.println("Init print port failed!");
//                return;
//            }
//            // TODO code application logic here
//            File file = new File("D:\\test.jpg");
//            BufferedImage read = ImageIO.read(file);
//            printImage(ioPrint, 10, read);

            
            FileOutputStream ioPrint;
            try {
                ioPrint = new FileOutputStream("COM4");
            } catch (Throwable ex) {
                System.out.println("failed to open port" + ex.getMessage());
                ioPrint = null;
            }
            if (ioPrint == null) {
                System.out.println("Init print port failed!");
                return;
            }
            System.out.println("----x1----");
//            ioPrint.write("------".getBytes());
//            ioPrint.write("------1".getBytes());
//            ioPrint.write("------2".getBytes());
//            ioPrint.write("------3".getBytes());
//            ioPrint.write("------4".getBytes());
//            ioPrint.write("------5".getBytes());
//            ioPrint.write("------6".getBytes());
//            ioPrint.write("------7".getBytes());
//            ioPrint.write("------8".getBytes());
//            ioPrint.write("------9".getBytes());
//            ioPrint.write("------10".getBytes());
            String msg = "2200801321711210075";
//            String path = "D:\\barcode.bmp";
//            generateFile(msg, path);
//            printImageBaudrate115200(ioPrint, 0, path);
//            ioPrint.write("------------".getBytes());
            ioPrint.write(CTL_LF);
            String path128 = "D:\\barcode128.bmp";
            BarcodeGenerator.generate128File(msg, path128, 0);
            printImageBaudrate115200(ioPrint, 2, path128);
//            ioPrint.write(0x1B);
//            ioPrint.write(97);
//            //设置条码居中
//            ioPrint.write(1);
//
//            ioPrint.write(0x1D);
//            ioPrint.write('w');
//            ioPrint.write(2);//默认是2  2-6 之间
//            ioPrint.flush();
//
//            //设置条形码的高度
//            ioPrint.write(0x1D);
//            ioPrint.write('h');
//            ioPrint.write(120);//默认是60
//            ioPrint.flush();
//
//            ioPrint.write(0x1D);
//            ioPrint.write(72);
//            ioPrint.write(2);
//
//            ioPrint.write(0x1D);
//            ioPrint.write('k');
////选择code128
//            ioPrint.write(73);
////设置字符个数
//            ioPrint.write(14);
////使用CODEB来打印
//            ioPrint.write(123);
//            ioPrint.write(66);
////条形码内容
//            ioPrint.write("2110033".getBytes());
//            ioPrint.flush();
//            System.out.println("----end----");
        } catch (Throwable ex) {
            System.out.println("test:" + ex);
        }
    }
}
