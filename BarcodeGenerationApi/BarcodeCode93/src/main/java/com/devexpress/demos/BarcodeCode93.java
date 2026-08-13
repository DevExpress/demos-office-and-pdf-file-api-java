package com.devexpress.demos;

import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.barcode.*;
import com.devexpress.drawing.DXImageFormat;
import com.devexpress.system.drawing.Color;

import java.io.*;

public class BarcodeCode93 {
    static final String DEFAULT_DATA = "CODE93-123";
    static final String OUTPUT_FILE_NAME = "code93.png";

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            Code93Options options = new Code93Options();
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
