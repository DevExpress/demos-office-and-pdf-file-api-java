package com.devexpress.demos;

import com.devexpress.docs.pdf.ComplianceIssue;
import com.devexpress.docs.pdf.ConversionResult;
import com.devexpress.docs.pdf.PdfACompatibility;
import com.devexpress.docs.pdf.PdfDocument;

import java.io.*;

public class PdfConvertToConformance {

    static final String INPUT_FILE_PATH = "SharedFiles/PDF/Sample_Alternative.pdf";
    static final String OUTPUT_DIRECTORY_NAME = "Output";
    static final String OUTPUT_DIRECTORY_DEMO_NAME = PdfConvertToConformance.class.getSimpleName();
    static final String OUTPUT_FILE_NAME = "Document.pdf";
    static final PdfACompatibility COMPATIBILITY = PdfACompatibility.PDF_A2B;

    public static void main(String[] args) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            ConversionResult result;
            try (InputStream inputResource = PdfConvertToConformance.class.getResourceAsStream("/" + INPUT_FILE_PATH)) {
                if (inputResource == null)
                    throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);
                result = convertToPdfA(inputResource, COMPATIBILITY, outputStream);
            }

            File outputDirectory = new File(OUTPUT_DIRECTORY_NAME, OUTPUT_DIRECTORY_DEMO_NAME);
            outputDirectory.mkdirs();
            File outputFile = new File(outputDirectory, OUTPUT_FILE_NAME);
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                outputStream.writeTo(fos);
            }

            System.out.println("Conversion Status : " + result.getStatus());
            if (!result.getIssues().isEmpty()) {
                System.out.println("Found Issues :");
                for (ComplianceIssue issue : result.getIssues())
                    System.out.println(issue.getSeverity() + " : " + issue.getMessage());
            }

            System.out.println("Created " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    static ConversionResult convertToPdfA(InputStream inputStream, PdfACompatibility compatibility,
            OutputStream outputStream) throws IOException {
        try (PdfDocument document = new PdfDocument(inputStream)) {
            ConversionResult result = document.convertToPdfA(compatibility);
            document.save(outputStream);
            return result;
        }
    }
}
