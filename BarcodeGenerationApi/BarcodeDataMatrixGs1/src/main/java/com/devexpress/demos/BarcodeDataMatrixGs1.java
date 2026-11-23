package com.devexpress.demos;

import com.devexpress.docs.barcode.*;
import com.devexpress.drawing.DXImageFormat;
import com.devexpress.system.drawing.Color;

import java.io.*;

public class BarcodeDataMatrixGs1 {
    static final String DEFAULT_DATA = "(01)09521234543213(10)ABC123(17)280101";
    static final String OUTPUT_DIRECTORY_NAME = "Output";
    static final String OUTPUT_DIRECTORY_DEMO_NAME = BarcodeDataMatrixGs1.class.getSimpleName();
    static final String OUTPUT_FILE_NAME = "gs1-datamatrix.png";

    public static void main(String[] args) {
        try {
            DataMatrixGS1Options options = new DataMatrixGS1Options();
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
