package com.devexpress.demos;

import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.pdf.*;

import java.io.*;

public class PdfMergeDocuments {

    static final String FIRST_INPUT_FILE_PATH = "SharedFiles/PDF/Sample_Main.pdf";
    static final String SECOND_INPUT_FILE_PATH = "SharedFiles/PDF/Sample_Alternative.pdf";
    static final String OUTPUT_FILE_NAME = "MergedDocuments.pdf";

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            InputStream first = PdfMergeDocuments.class.getResourceAsStream("/" + FIRST_INPUT_FILE_PATH);
            InputStream second = PdfMergeDocuments.class.getResourceAsStream("/" + SECOND_INPUT_FILE_PATH);
            if (first == null)
                throw new FileNotFoundException("Resource not found: " + FIRST_INPUT_FILE_PATH);
            if (second == null)
                throw new FileNotFoundException("Resource not found: " + SECOND_INPUT_FILE_PATH);

            ByteArrayOutputStream outputStream = mergeDocuments(first, second);
            first.close();
            second.close();

            try (FileOutputStream fos = new FileOutputStream(OUTPUT_FILE_NAME)) {
                outputStream.writeTo(fos);
            }
            System.out.println("Created " + OUTPUT_FILE_NAME);
        } catch (Exception e) {
            System.err.println(e);
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
