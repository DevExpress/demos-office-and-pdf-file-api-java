package com.devexpress.demos;

import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.pdf.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class PdfXMPMetadata {

    static final String INPUT_FILE_PATH = "SharedFiles/PDF/Sample_Main.pdf";
    static final String METADATA_FILE_PATH = "SharedFiles/PDF/Sample_Metadata.xmp";
    static final String OUTPUT_DIRECTORY_NAME = "Output";

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            InputStream inputResource = PdfXMPMetadata.class.getResourceAsStream("/" + INPUT_FILE_PATH);
            if (inputResource == null)
                throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);
            byte[] inputBytes = inputResource.readAllBytes();
            inputResource.close();

            File outputDirectory = new File(OUTPUT_DIRECTORY_NAME);
            outputDirectory.mkdirs();

            String metadataText = readMetadata(new ByteArrayInputStream(inputBytes));
            try (FileOutputStream fos = new FileOutputStream(new File(outputDirectory, "Metadata.txt"))) {
                fos.write(metadataText.getBytes(StandardCharsets.UTF_8));
            }

            saveDocument(removeXmpMetadata(new ByteArrayInputStream(inputBytes)),
                    new File(outputDirectory, "MetadataRemoved.pdf"));

            InputStream metadataResource = PdfXMPMetadata.class.getResourceAsStream("/" + METADATA_FILE_PATH);
            if (metadataResource == null)
                throw new FileNotFoundException("Resource not found: " + METADATA_FILE_PATH);
            saveDocument(addXmpMetadata(new ByteArrayInputStream(inputBytes), metadataResource),
                    new File(outputDirectory, "MetadataAdded.pdf"));
            metadataResource.close();

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

    static String readMetadata(InputStream documentStream) throws IOException {
        try (PdfDocument doc = new PdfDocument(documentStream)) {
            XmpMetadata xmp = doc.getMetadata().getXmp();
            return xmp != null ? xmp.toXmlString() : "";
        }
    }

    static ByteArrayOutputStream removeXmpMetadata(InputStream documentStream) throws IOException {
        try (PdfDocument doc = new PdfDocument(documentStream)) {
            doc.getMetadata().setXmp(null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos;
        }
    }

    static ByteArrayOutputStream addXmpMetadata(InputStream documentStream, InputStream metadataStream) throws IOException {
        try (PdfDocument doc = new PdfDocument(documentStream)) {
            doc.getMetadata().setXmp(XmpMetadata.fromStream(metadataStream));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos;
        }
    }
}
