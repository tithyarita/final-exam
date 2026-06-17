package com.example.demo.util;

import com.example.demo.model.BarcodeType;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.oned.EAN13Writer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class BarcodeGenerator {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 100;

    public static byte[] generateBarcodeImage(String text, BarcodeType barcodeType) throws WriterException, IOException {
        BarcodeFormat format = switch (barcodeType) {
            case CODE_128 -> BarcodeFormat.CODE_128;
            case EAN_13 -> BarcodeFormat.EAN_13;
        };

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix;
        if (barcodeType == BarcodeType.CODE_128) {
            Code128Writer writer = new Code128Writer();
            bitMatrix = writer.encode(text, BarcodeFormat.CODE_128, WIDTH, HEIGHT, hints);
        } else {
            EAN13Writer writer = new EAN13Writer();
            bitMatrix = writer.encode(text, BarcodeFormat.EAN_13, WIDTH, HEIGHT, hints);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        return outputStream.toByteArray();
    }

    public static String generateBarcodeBase64(String text, BarcodeType barcodeType) throws WriterException, IOException {
        byte[] imageBytes = generateBarcodeImage(text, barcodeType);
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}