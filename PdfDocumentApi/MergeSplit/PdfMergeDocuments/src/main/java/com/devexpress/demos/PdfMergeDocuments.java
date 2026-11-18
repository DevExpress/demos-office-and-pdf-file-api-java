package com.devexpress.demos;

import com.devexpress.docs.pdf.*;

import java.io.*;

public class PdfMergeDocuments {

    static final String FIRST_INPUT_FILE_PATH = "SharedFiles/PDF/Sample_Main.pdf";
    static final String SECOND_INPUT_FILE_PATH = "SharedFiles/PDF/Sample_Alternative.pdf";
    static final String OUTPUT_DIRECTORY_NAME = "Output";
    static final String OUTPUT_DIRECTORY_DEMO_NAME = PdfMergeDocuments.class.getSimpleName();
    static final String OUTPUT_FILE_NAME = "MergedDocuments.pdf";

    public static void main(String[] args) {
        try {
            ByteArrayOutputStream outputStream;
            try (InputStream first = PdfMergeDocuments.class.getResourceAsStream("/" + FIRST_INPUT_FILE_PATH);
                 InputStream second = PdfMergeDocuments.class.getResourceAsStream("/" + SECOND_INPUT_FILE_PATH)) {
                if (first == null)
                    throw new FileNotFoundException("Resource not found: " + FIRST_INPUT_FILE_PATH);
                if (second == null)
                    throw new FileNotFoundException("Resource not found: " + SECOND_INPUT_FILE_PATH);
                outputStream = mergeDocuments(first, second);
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

    static ByteArrayOutputStream mergeDocuments(InputStream... documentStreams) throws IOException {
        try (PdfDocument doc = new PdfDocument()) {
            for (InputStream stream : documentStreams)
                doc.appendDocument(stream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos;
        }
    }
}
