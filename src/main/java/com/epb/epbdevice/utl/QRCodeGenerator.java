package com.epb.epbdevice.utl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

@SuppressWarnings({ "unused" })
public class QRCodeGenerator {

	public BufferedImage generate(String textValue) throws QRCodeException {
		return generate(textValue, 150);
	}

	public BufferedImage generate(String textValue, int size) throws QRCodeException {
		Map<EncodeHintType, Object> hintMap = setEncodingbehavior();
		BitMatrix bm = null;
		try {
			bm = getByteMatrix(textValue, size, hintMap);
			return getImage(bm);
		} catch (WriterException e) {
			throw new QRCodeException("QRCode generation error", e);
		} finally {
			hintMap.clear();
			if (bm != null) {
				bm.clear();
			}
		}
	}

	private Map<EncodeHintType, Object> setEncodingbehavior() {
//        Map<EncodeHintType, Object> hintMap = new EnumMap<>(EncodeHintType.class);
		Map<EncodeHintType, Object> hintMap = new HashMap<EncodeHintType, Object>();
		hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		hintMap.put(EncodeHintType.MARGIN, 1);
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		return hintMap;
	}

	private BitMatrix getByteMatrix(String textValue, int size, Map<EncodeHintType, Object> hintMap)
			throws WriterException {
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		return qrCodeWriter.encode(textValue, BarcodeFormat.QR_CODE, size, size, hintMap);
	}

	private BufferedImage getImage(BitMatrix bm) {
		BufferedImage image = new BufferedImage(bm.getWidth(), bm.getWidth(), BufferedImage.TYPE_INT_RGB);
		image.createGraphics();
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, bm.getWidth(), bm.getWidth());
		graphics.setColor(Color.BLACK);

		for (int i = 0; i < bm.getWidth(); i++) {
			for (int j = 0; j < bm.getWidth(); j++) {
				if (bm.get(i, j)) {
					graphics.fillRect(i, j, 1, 1);
				}
			}
		}
		return image;
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		try {
			BufferedImage bi = new QRCodeGenerator().generate("Hello word");
			java.awt.Image big = bi.getScaledInstance(140, 140, 10);
//            Image big = bi.getScaledInstance(256, 256, 10);
			BufferedImage inputbig = new BufferedImage(140, 140, BufferedImage.TYPE_INT_BGR);
			inputbig.getGraphics().drawImage(bi, 0, 0, 140, 140, null); // 画图

			File file2 = new File("C:/imageSort/targetPIC"); // 此目录保存缩小后的关键图
			if (file2.exists()) {
				System.out.println("多级目录已经存在不需要创建！！");
			} else {
				// 如果要创建的多级目录不存在才需要创建。
				file2.mkdirs();
			}
			ImageIO.write(inputbig, "jpg", new File("C:/imageSort/targetPIC/" + "xx1.jpg")); // 将其保存在C:/imageSort/targetPIC
		} catch (Throwable ex) {
			System.out.println("test:" + ex);
		}
	}
}