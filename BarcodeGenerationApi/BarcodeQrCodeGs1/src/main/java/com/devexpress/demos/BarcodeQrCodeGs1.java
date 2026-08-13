package com.devexpress.demos;

import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.barcode.*;
import com.devexpress.drawing.DXImageFormat;
import com.devexpress.system.drawing.Color;

import java.io.*;

public class BarcodeQrCodeGs1 {
    static final String DEFAULT_DATA = "(01)09521234543213(8200)https://www.devexpress.com";
    static final String OUTPUT_FILE_NAME = "gs1-qr.png";

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            QRCodeGS1Options options = new QRCodeGS1Options();
            options.setBackColor(Color.getWhite());
            options.setForeColor(Color.getBlack());
            options.setShowText(false);

            File outputFile = new File(OUTPUT_FILE_NAME);
            try (FileOutputStream stream = new FileOutputStream(outputFile);
                 BarcodeGenerator generator = new BarcodeGenerator(options)) {
                generator.export(DEFAULT_DATA, stream, DXImageFormat.getPng());
            }
            System.out.println("Created " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}
