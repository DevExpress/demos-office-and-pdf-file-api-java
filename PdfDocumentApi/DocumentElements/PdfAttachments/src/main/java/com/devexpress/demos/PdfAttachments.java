package com.devexpress.demos;

import com.devexpress.docs.pdf.*;

import java.io.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class PdfAttachments {

    static final String INPUT_FILE_PATH = "SharedFiles/PDF/Sample_Alternative.pdf";
    static final String ATTACHMENT_FILE_PATH = "SharedFiles/DevExpress.png";
    static final String OUTPUT_DIRECTORY_NAME = "Output";
    static final String OUTPUT_DIRECTORY_DEMO_NAME = PdfAttachments.class.getSimpleName();
    static final String OUTPUT_FILE_NAME = "PdfWithAttachment.pdf";
    static final String ATTACHMENT_DESCRIPTION = "DevExpress Logo Image";
    static final String ATTACHMENT_MIME_TYPE = "image/png";

    public static void main(String[] args) {
        try {
            byte[] attachmentBytes;
            try (InputStream attachResource = PdfAttachments.class.getResourceAsStream("/" + ATTACHMENT_FILE_PATH)) {
                if (attachResource == null)
                    throw new FileNotFoundException("Resource not found: " + ATTACHMENT_FILE_PATH);
                attachmentBytes = attachResource.readAllBytes();
            }

            String fileName = ATTACHMENT_FILE_PATH.substring(ATTACHMENT_FILE_PATH.lastIndexOf('/') + 1);
            ByteArrayOutputStream outputStream;
            try (InputStream docResource = PdfAttachments.class.getResourceAsStream("/" + INPUT_FILE_PATH)) {
                if (docResource == null)
                    throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);
                outputStream = attachFile(docResource, fileName, ATTACHMENT_DESCRIPTION,
                        ATTACHMENT_MIME_TYPE, attachmentBytes);
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

    static ByteArrayOutputStream attachFile(InputStream documentStream, String fileName, String fileDescription,
            String fileMimeType, byte[] fileData) throws IOException {
        try (PdfDocument doc = new PdfDocument(documentStream)) {
            Attachment attachment = new Attachment();
            attachment.setFileName(fileName);
            attachment.setData(fileData);
            attachment.setMimeType(fileMimeType);
            attachment.setDescription(fileDescription);
            attachment.setCreationDate(OffsetDateTime.now(ZoneOffset.UTC));
            attachment.setModificationDate(OffsetDateTime.now(ZoneOffset.UTC));
            attachment.setRelationship(AssociatedFileRelationship.SUPPLEMENT);
            doc.getAttachments().add(attachment);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos;
        }
    }
}
