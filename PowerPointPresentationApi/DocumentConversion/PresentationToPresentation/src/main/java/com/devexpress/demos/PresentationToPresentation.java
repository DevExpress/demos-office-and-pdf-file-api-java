package com.devexpress.demos;

import com.devexpress.docs.presentation.DocumentFormat;
import com.devexpress.docs.presentation.Presentation;

import java.io.*;

public class PresentationToPresentation {

    static final String INPUT_FILE_PATH = "SharedFiles/Sample.pptx";
    static final String OUTPUT_DIRECTORY_NAME = "Output";
    static final String OUTPUT_DIRECTORY_DEMO_NAME = PresentationToPresentation.class.getSimpleName();
    static final String OUTPUT_FILE_NAME = "Converted_Presentation.pptx";
    static final DocumentFormat OUTPUT_FORMAT = DocumentFormat.PPTX;

    public static void main(String[] args) {
        try {
            ByteArrayOutputStream outputStream;
            try (InputStream inputResource = PresentationToPresentation.class.getResourceAsStream("/" + INPUT_FILE_PATH)) {
                if (inputResource == null)
                    throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);
                outputStream = convertToFormat(inputResource, OUTPUT_FORMAT);
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

    static ByteArrayOutputStream convertToFormat(InputStream inputStream, DocumentFormat targetFormat) throws IOException {
        try (Presentation presentation = new Presentation(inputStream)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            presentation.save(baos, targetFormat);
            return baos;
        }
    }
}
