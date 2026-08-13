package com.devexpress.demos;

import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.pdf.*;

import java.io.*;
import java.time.OffsetDateTime;

public class PdfAttachments {

    static final String INPUT_FILE_PATH = "SharedFiles/PDF/Sample_Alternative.pdf";
    static final String ATTACHMENT_FILE_PATH = "SharedFiles/DevExpress.png";
    static final String OUTPUT_FILE_NAME = "PdfWithAttachment.pdf";
    static final String ATTACHMENT_DESCRIPTION = "DevExpress Logo Image";
    static final String ATTACHMENT_MIME_TYPE = "image/png";

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            InputStream docResource = PdfAttachments.class.getResourceAsStream("/" + INPUT_FILE_PATH);
            if (docResource == null)
                throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);

            InputStream attachResource = PdfAttachments.class.getResourceAsStream("/" + ATTACHMENT_FILE_PATH);
            if (attachResource == null)
                throw new FileNotFoundException("Resource not found: " + ATTACHMENT_FILE_PATH);

            byte[] attachmentBytes = attachResource.readAllBytes();
            attachResource.close();

            String fileName = ATTACHMENT_FILE_PATH.substring(ATTACHMENT_FILE_PATH.lastIndexOf('/') + 1);
            ByteArrayOutputStream outputStream = attachFile(docResource, fileName, ATTACHMENT_DESCRIPTION,
                    ATTACHMENT_MIME_TYPE, attachmentBytes);
            docResource.close();

            try (FileOutputStream fos = new FileOutputStream(OUTPUT_FILE_NAME)) {
                outputStream.writeTo(fos);
            }
            System.out.println("Created " + OUTPUT_FILE_NAME);
        } catch (Exception e) {
            System.err.println(e);
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
            attachment.setCreationDate(OffsetDateTime.now());
            attachment.setModificationDate(OffsetDateTime.now());
            attachment.setRelationship(AssociatedFileRelationship.SUPPLEMENT);
            doc.getAttachments().add(attachment);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos;
        }
    }
}
