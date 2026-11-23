package com.devexpress.demos;

import com.devexpress.docs.pdf.*;
import com.devexpress.system.drawing.RectangleF;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PdfClearPages {

    static final String INPUT_FILE_PATH = "SharedFiles/PDF/Sample_Main.pdf";
    static final String OUTPUT_DIRECTORY_NAME = "Output";
    static final String OUTPUT_DIRECTORY_DEMO_NAME = PdfClearPages.class.getSimpleName();
    static final String OUTPUT_FILE_NAME = "ClearedPages.pdf";
    static final boolean CLEAR_ANNOTATIONS = true;
    static final boolean CLEAR_GRAPHICS = true;
    static final boolean CLEAR_IMAGES = true;
    static final boolean CLEAR_TEXT = true;
    static final int[] PAGE_RANGE = new int[0];

    public static void main(String[] args) {
        try {
            ByteArrayOutputStream outputStream;
            try (InputStream inputResource = PdfClearPages.class.getResourceAsStream("/" + INPUT_FILE_PATH)) {
                if (inputResource == null)
                    throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);
                outputStream = clearPages(inputResource, CLEAR_ANNOTATIONS, CLEAR_GRAPHICS,
                        CLEAR_IMAGES, CLEAR_TEXT, PAGE_RANGE);
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

    static ByteArrayOutputStream clearPages(InputStream documentStream, boolean clearAnnotations,
            boolean clearGraphics, boolean clearImages, boolean clearText, int[] pageRange) throws IOException {
        try (PdfDocument doc = new PdfDocument(documentStream)) {
            List<Integer> indices = new ArrayList<>();
            if (pageRange.length == 0) {
                for (int i = 0; i < doc.getPages().size(); i++)
                    indices.add(i);
            } else {
                for (int i : pageRange)
                    indices.add(i);
            }

            for (int index : indices) {
                if (index < 0 || index >= doc.getPages().size())
                    continue;
                RectangleF mediaBox = doc.getPages().get(index).getMediaBox();
                List<OrientedRectangle> regions = new ArrayList<>();
                regions.add(new OrientedRectangle(mediaBox));
                doc.clearContent(index, regions, clearText, clearImages, clearGraphics, clearAnnotations);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos;
        }
    }
}
