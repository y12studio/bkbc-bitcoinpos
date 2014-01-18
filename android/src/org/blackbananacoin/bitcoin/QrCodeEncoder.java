package org.blackbananacoin.bitcoin;

import java.util.EnumMap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QrCodeEncoder {

	public int[] getPixels(String content, int dimention)
			throws WriterException {

		QRCodeWriter writer = new QRCodeWriter();

		EnumMap<EncodeHintType, Object> hint = new EnumMap<EncodeHintType, Object>(
				EncodeHintType.class);
		hint.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE,
				dimention, dimention, hint);
		int width = bitMatrix.getWidth();
		int height = bitMatrix.getHeight();
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = bitMatrix.get(x, y) ? 0xFF000000
						: 0xFFFFFFFF;
				// pixels[offset + x] = bitMatrix.get(x, y) ? colorBack :
				// colorFront;
			}
		}

		return pixels;

	}
}
