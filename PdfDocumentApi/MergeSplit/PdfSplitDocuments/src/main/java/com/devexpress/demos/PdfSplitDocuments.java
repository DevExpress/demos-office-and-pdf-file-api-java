package com.devexpress.demos;

import com.devexpress.docs.pdf.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PdfSplitDocuments {

    static final String INPUT_FILE_PATH = "SharedFiles/PDF/Sample_Main.pdf";
    static final String OUTPUT_DIRECTORY_NAME = "Output";
    static final String OUTPUT_DIRECTORY_DEMO_NAME = PdfSplitDocuments.class.getSimpleName();
    static final int[] PAGE_RANGE = new int[0];

    public static void main(String[] args) {
        try {
            File outputDirectory = new File(OUTPUT_DIRECTORY_NAME, OUTPUT_DIRECTORY_DEMO_NAME);
            outputDirectory.mkdirs();

            List<ByteArrayOutputStream> documents;
            try (InputStream inputResource = PdfSplitDocuments.class.getResourceAsStream("/" + INPUT_FILE_PATH)) {
                if (inputResource == null)
                    throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);
                documents = splitDocuments(inputResource, PAGE_RANGE);
            }

            for (int i = 0; i < documents.size(); i++) {
                File outputFile = new File(outputDirectory, "document_" + (i + 1) + ".pdf");
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    documents.get(i).writeTo(fos);
                }
            }
            System.out.println("Created " + outputDirectory.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    static List<ByteArrayOutputStream> splitDocuments(InputStream documentStream, int[] pageRange) throws IOException {
        try (PdfDocument source = new PdfDocument(documentStream)) {
            List<Integer> indices = new ArrayList<>();
            if (pageRange.length == 0) {
                for (int i = 0; i < source.getPages().size(); i++)
                    indices.add(i);
            } else {
                for (int i : pageRange)
                    indices.add(i);
            }

            List<ByteArrayOutputStream> result = new ArrayList<>();
            for (int index : indices) {
                if (index < 0 || index >= source.getPages().size())
                    continue;
                try (PdfDocument pageDocument = new PdfDocument()) {
                    pageDocument.getPages().add(source.getPages().get(index).deepClone());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    pageDocument.save(baos);
                    result.add(baos);
                }
            }
            return result;
        }
    }
}
