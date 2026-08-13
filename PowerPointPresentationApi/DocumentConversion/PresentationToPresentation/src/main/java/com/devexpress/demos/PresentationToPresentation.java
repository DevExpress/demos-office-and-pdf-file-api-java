package com.devexpress.demos;

import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.presentation.DocumentFormat;
import com.devexpress.docs.presentation.Presentation;

import java.io.*;

public class PresentationToPresentation {

    static final String INPUT_FILE_PATH = "SharedFiles/Sample.pptx";
    static final String OUTPUT_FILE_NAME = "Converted_Presentation.pptx";
    static final DocumentFormat OUTPUT_FORMAT = DocumentFormat.PPTX;

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            InputStream inputResource = PresentationToPresentation.class.getResourceAsStream("/" + INPUT_FILE_PATH);
            if (inputResource == null)
                throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);

            ByteArrayOutputStream outputStream = convertToFormat(inputResource, OUTPUT_FORMAT);
            inputResource.close();

            try (FileOutputStream fos = new FileOutputStream(OUTPUT_FILE_NAME)) {
                outputStream.writeTo(fos);
            }
            System.out.println("Created " + OUTPUT_FILE_NAME);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    static ByteArrayOutputStream convertToFormat(InputStream inputStream, DocumentFormat targetFormat) throws IOException {
        try (Presentation presentation = new Presentation(inputStream)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            presentation.saveDocument(baos, targetFormat);
            return baos;
        }
    }
}
