package com.devexpress.demos;

import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.presentation.DocumentFormat;
import com.devexpress.docs.presentation.Presentation;
import com.devexpress.docs.office.EncryptionOptions;
import com.devexpress.docs.office.EncryptionType;

import java.io.*;

public class PresentationEncryptPassword {

    static final String INPUT_FILE_PATH = "SharedFiles/Sample.pptx";
    static final String OUTPUT_FILE_NAME = "Encrypted_Presentation.pptx";
    static final DocumentFormat OUTPUT_FORMAT = DocumentFormat.PPTX;
    static final String PASSWORD = "123";
    static final EncryptionType ENCRYPTION_TYPE = EncryptionType.STRONG;

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            InputStream inputResource = PresentationEncryptPassword.class.getResourceAsStream("/" + INPUT_FILE_PATH);
            if (inputResource == null)
                throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);

            InputStream outputStream = encryptDocumentWithPassword(inputResource, OUTPUT_FORMAT, PASSWORD, ENCRYPTION_TYPE);
            inputResource.close();

            try (FileOutputStream fos = new FileOutputStream(OUTPUT_FILE_NAME)) {
                outputStream.transferTo(fos);
            }
            System.out.println("Created " + OUTPUT_FILE_NAME);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    static InputStream encryptDocumentWithPassword(InputStream inputStream, DocumentFormat outputFormat, String password, EncryptionType encryptionType) throws IOException {
        try (Presentation presentation = new Presentation(inputStream)) {
            EncryptionOptions options = new EncryptionOptions(password, encryptionType);
            presentation.encrypt(options);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            presentation.saveDocument(baos, outputFormat);
            return new ByteArrayInputStream(baos.toByteArray());
        }
    }
}
