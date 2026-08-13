package com.devexpress.demos;

import com.devexpress.docs.pdf.*;

import java.io.*;

public class PdfBookmarks {

    static final String INPUT_FILE_PATH = "SharedFiles/PDF/Sample_Main.pdf";
    static final String OUTPUT_DIRECTORY_NAME = "Output";

    public static void main(String[] args) {
        try {
            InputStream inputResource = PdfBookmarks.class.getResourceAsStream("/" + INPUT_FILE_PATH);
            if (inputResource == null)
                throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);
            byte[] inputBytes = inputResource.readAllBytes();
            inputResource.close();

            File outputDirectory = new File(OUTPUT_DIRECTORY_NAME);
            outputDirectory.mkdirs();

            byte[] addedBookmarksBytes;
            try (ByteArrayOutputStream baos = addBookmarks(new ByteArrayInputStream(inputBytes))) {
                addedBookmarksBytes = baos.toByteArray();
            }
            saveDocument(createOutputStream(addedBookmarksBytes), new File(outputDirectory, "bookmarks_added.pdf"));
            saveDocument(deleteBookmarks(new ByteArrayInputStream(addedBookmarksBytes)),
                    new File(outputDirectory, "bookmarks_removed.pdf"));

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

    static ByteArrayOutputStream createOutputStream(byte[] bytes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(bytes);
        return baos;
    }

    static ByteArrayOutputStream addBookmarks(InputStream documentStream) throws IOException {
        try (PdfDocument doc = new PdfDocument(documentStream)) {
            for (int i = 0; i < doc.getPages().size(); i++) {
                Page page = doc.getPages().get(i);
                doc.getBookmarks().add("Page " + (i + 1), page, new FitBBoxDestination());
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos;
        }
    }

    static ByteArrayOutputStream deleteBookmarks(InputStream documentStream) throws IOException {
        try (PdfDocument doc = new PdfDocument(documentStream)) {
            doc.getBookmarks().clear();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos;
        }
    }
}
