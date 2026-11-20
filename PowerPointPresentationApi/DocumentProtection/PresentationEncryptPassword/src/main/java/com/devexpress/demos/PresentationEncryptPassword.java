package com.devexpress.demos;

import com.devexpress.docs.presentation.DocumentFormat;
import com.devexpress.docs.presentation.Presentation;
import com.devexpress.docs.office.EncryptionOptions;
import com.devexpress.docs.office.EncryptionType;

import java.io.*;

public class PresentationEncryptPassword {

    static final String INPUT_FILE_PATH = "SharedFiles/Sample.pptx";
    static final String OUTPUT_DIRECTORY_NAME = "Output";
    static final String OUTPUT_DIRECTORY_DEMO_NAME = PresentationEncryptPassword.class.getSimpleName();
    static final String OUTPUT_FILE_NAME = "Encrypted_Presentation.pptx";
    static final DocumentFormat OUTPUT_FORMAT = DocumentFormat.PPTX;
    static final String PASSWORD = "123";
    static final EncryptionType ENCRYPTION_TYPE = EncryptionType.STRONG;

    public static void main(String[] args) {
        try {
            InputStream outputStream;
            try (InputStream inputResource = PresentationEncryptPassword.class.getResourceAsStream("/" + INPUT_FILE_PATH)) {
                if (inputResource == null)
                    throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);
                outputStream = encryptDocumentWithPassword(inputResource, OUTPUT_FORMAT, PASSWORD, ENCRYPTION_TYPE);
            }

            File outputDirectory = new File(OUTPUT_DIRECTORY_NAME, OUTPUT_DIRECTORY_DEMO_NAME);
            outputDirectory.mkdirs();

            File outputFile = new File(outputDirectory, OUTPUT_FILE_NAME);

            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                outputStream.transferTo(fos);
            }
            System.out.println("Created " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    static InputStream encryptDocumentWithPassword(InputStream inputStream, DocumentFormat outputFormat, String password, EncryptionType encryptionType) throws IOException {
        try (Presentation presentation = new Presentation(inputStream)) {
            EncryptionOptions options = new EncryptionOptions(password, encryptionType);
            presentation.encrypt(options);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            presentation.save(baos, outputFormat);
            return new ByteArrayInputStream(baos.toByteArray());
        }
    }
}
