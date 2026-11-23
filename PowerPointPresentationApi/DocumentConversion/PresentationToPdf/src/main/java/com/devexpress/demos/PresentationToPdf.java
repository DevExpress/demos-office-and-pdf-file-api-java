package com.devexpress.demos;

import com.devexpress.docs.presentation.Presentation;

import java.io.*;

public class PresentationToPdf {

    static final String INPUT_FILE_PATH = "SharedFiles/Sample.pptx";
    static final String OUTPUT_DIRECTORY_NAME = "Output";
    static final String OUTPUT_DIRECTORY_DEMO_NAME = PresentationToPdf.class.getSimpleName();
    static final String OUTPUT_FILE_NAME = "Converted_Presentation.pdf";

    public static void main(String[] args) {
        try {
            ByteArrayOutputStream outputStream;
            try (InputStream inputResource = PresentationToPdf.class.getResourceAsStream("/" + INPUT_FILE_PATH)) {
                if (inputResource == null)
                    throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);
                outputStream = convertToPdf(inputResource);
            }

            File outputDirectory = new File(OUTPUT_DIRECTORY_NAME, OUTPUT_DIRECTORY_DEMO_NAME);
            outputDirectory.mkdirs();

            File outputFile = new File(outputDirectory, OUTPUT_FILE_NAME);

            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                outputStream.writeTo(fos);
            }
            System.out.println("Created " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    static ByteArrayOutputStream convertToPdf(InputStream inputStream) throws IOException {
        try (Presentation presentation = new Presentation(inputStream)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            presentation.exportToPdf(baos);
            return baos;
        }
    }
}
