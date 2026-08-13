package com.devexpress.demos;

import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.pdf.*;

import java.io.*;

public class PdfEncryptPassword {

    static final String INPUT_FILE_PATH = "SharedFiles/PDF/Sample_Main.pdf";
    static final String OUTPUT_FILE_NAME = "EncryptedDocument.pdf";
    static final EncryptionAlgorithm ALGORITHM = EncryptionAlgorithm.AES_256;
    static final DocumentDataExtractionPermissions DATA_EXTRACTION_PERMISSIONS = DocumentDataExtractionPermissions.ALLOWED;
    static final DocumentInteractivityPermissions INTERACTIVITY_PERMISSIONS = DocumentInteractivityPermissions.ALLOWED;
    static final DocumentModificationPermissions MODIFICATION_PERMISSIONS = DocumentModificationPermissions.ALLOWED;
    static final DocumentPrintPermissions PRINT_PERMISSIONS = DocumentPrintPermissions.ALLOWED;
    static final String OWNER_PASSWORD = "dxdemo-owner";
    static final String USER_PASSWORD = "dxdemo-user";

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            InputStream inputResource = PdfEncryptPassword.class.getResourceAsStream("/" + INPUT_FILE_PATH);
            if (inputResource == null)
                throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);

            ByteArrayOutputStream outputStream = encrypt(inputResource, ALGORITHM, DATA_EXTRACTION_PERMISSIONS,
                    INTERACTIVITY_PERMISSIONS, MODIFICATION_PERMISSIONS, PRINT_PERMISSIONS,
                    OWNER_PASSWORD, USER_PASSWORD);
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

    static ByteArrayOutputStream encrypt(InputStream documentStream, EncryptionAlgorithm algorithm,
            DocumentDataExtractionPermissions dataExtractionPermissions,
            DocumentInteractivityPermissions interactivityPermissions,
            DocumentModificationPermissions modificationPermissions,
            DocumentPrintPermissions printPermissions,
            String ownerPassword, String userPassword) throws IOException {
        try (PdfDocument doc = new PdfDocument(documentStream)) {
            EncryptionOptions options = new EncryptionOptions(ownerPassword, userPassword);
            options.setAlgorithm(algorithm);
            options.setDataExtractionPermissions(dataExtractionPermissions);
            options.setInteractivityPermissions(interactivityPermissions);
            options.setModificationPermissions(modificationPermissions);
            options.setPrintPermissions(printPermissions);
            doc.encrypt(options);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos;
        }
    }
}
