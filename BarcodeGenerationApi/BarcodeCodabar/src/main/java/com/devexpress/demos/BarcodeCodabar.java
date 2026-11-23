package com.devexpress.demos;

import com.devexpress.docs.barcode.*;
import com.devexpress.drawing.DXImageFormat;
import com.devexpress.system.drawing.Color;

import java.io.*;

public class BarcodeCodabar {
    static final String DEFAULT_DATA = "0123456789";
    static final String OUTPUT_DIRECTORY_NAME = "Output";
    static final String OUTPUT_DIRECTORY_DEMO_NAME = BarcodeCodabar.class.getSimpleName();
    static final String OUTPUT_FILE_NAME = "codabar.png";

    public static void main(String[] args) {
        try {
            CodabarOptions options = new CodabarOptions();
            options.setBackColor(Color.getWhite());
            options.setForeColor(Color.getBlack());
            options.setShowText(false);

            File outputDirectory = new File(OUTPUT_DIRECTORY_NAME, OUTPUT_DIRECTORY_DEMO_NAME);
            outputDirectory.mkdirs();

            File outputFile = new File(outputDirectory, OUTPUT_FILE_NAME);
            try (FileOutputStream stream = new FileOutputStream(outputFile);
                 BarcodeGenerator generator = new BarcodeGenerator(options)) {
                generator.export(DEFAULT_DATA, stream, DXImageFormat.getPng());
            }
            System.out.println("Created " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
