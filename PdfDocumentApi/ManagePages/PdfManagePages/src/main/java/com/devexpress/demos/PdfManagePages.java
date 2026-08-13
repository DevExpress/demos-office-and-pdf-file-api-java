package com.devexpress.demos;

import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.pdf.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PdfManagePages {

    static final String INPUT_FILE_PATH = "SharedFiles/PDF/Sample_Main.pdf";
    static final String OUTPUT_DIRECTORY_NAME = "Output";
    static final float NEW_PAGE_WIDTH = 595.28f;
    static final float NEW_PAGE_HEIGHT = 841.89f;
    static final int[] PAGE_RANGE = {0};

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            InputStream inputResource = PdfManagePages.class.getResourceAsStream("/" + INPUT_FILE_PATH);
            if (inputResource == null)
                throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);
            byte[] inputBytes = inputResource.readAllBytes();
            inputResource.close();

            File outputDirectory = new File(OUTPUT_DIRECTORY_NAME);
            outputDirectory.mkdirs();

            saveDocument(addPage(new ByteArrayInputStream(inputBytes), NEW_PAGE_WIDTH, NEW_PAGE_HEIGHT),
                    new File(outputDirectory, "page_added.pdf"));
            saveDocument(deletePages(new ByteArrayInputStream(inputBytes), PAGE_RANGE),
                    new File(outputDirectory, "page_deleted.pdf"));
            saveDocument(duplicatePages(new ByteArrayInputStream(inputBytes), PAGE_RANGE),
                    new File(outputDirectory, "page_duplicated.pdf"));

            System.out.println("Created " + outputDirectory.getAbsolutePath());
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    static void saveDocument(ByteArrayOutputStream baos, File outputFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            baos.writeTo(fos);
        }
    }

    static ByteArrayOutputStream addPage(InputStream documentStream, float width, float height) throws IOException {
        try (PdfDocument doc = new PdfDocument(documentStream)) {
            doc.getPages().add(width, height);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos;
        }
    }

    static ByteArrayOutputStream deletePages(InputStream documentStream, int[] pageRange) throws IOException {
        try (PdfDocument doc = new PdfDocument(documentStream)) {
            for (int i = pageRange.length - 1; i >= 0; i--) {
                int index = pageRange[i];
                if (index < 0 || index >= doc.getPages().size())
                    continue;
                doc.getPages().remove(index);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos;
        }
    }

    static ByteArrayOutputStream duplicatePages(InputStream documentStream, int[] pageRange) throws IOException {
        try (PdfDocument doc = new PdfDocument(documentStream)) {
            for (int index : pageRange) {
                if (index < 0 || index >= doc.getPages().size())
                    continue;
                doc.getPages().add(doc.getPages().get(index).deepClone());
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos;
        }
    }
}
