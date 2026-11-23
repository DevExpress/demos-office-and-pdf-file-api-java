package com.devexpress.demos;

import com.devexpress.docs.office.DocumentCustomProperty;
import com.devexpress.docs.presentation.DocumentProperties;
import com.devexpress.docs.presentation.Presentation;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class PresentationMetadata {

    static final String INPUT_FILE_PATH = "SharedFiles/Sample.pptx";
    static final String OUTPUT_DIRECTORY_NAME = "Output";
    static final String OUTPUT_DIRECTORY_DEMO_NAME = PresentationMetadata.class.getSimpleName();
    static final String OUTPUT_FILE_NAME = "Document_Metadata.txt";
    static final boolean READ_DOCUMENT_PROPERTIES = true;
    static final boolean READ_CUSTOM_PROPERTIES = true;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Instant UNSET_DATE = Instant.parse("0001-01-01T00:00:00Z");
    private static final String UNSET_DATE_TEXT = "0001-01-01 00:00:00";

    public static void main(String[] args) {
        try {
            String metadataText;
            try (InputStream inputResource = PresentationMetadata.class.getResourceAsStream("/" + INPUT_FILE_PATH)) {
                if (inputResource == null)
                    throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);
                metadataText = readDocumentPropertiesText(inputResource, READ_DOCUMENT_PROPERTIES, READ_CUSTOM_PROPERTIES);
            }

            File outputDirectory = new File(OUTPUT_DIRECTORY_NAME, OUTPUT_DIRECTORY_DEMO_NAME);
            outputDirectory.mkdirs();

            File outputFile = new File(outputDirectory, OUTPUT_FILE_NAME);

            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                fos.write(metadataText.getBytes(StandardCharsets.UTF_8));
            }
            System.out.println("Created " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    static String readDocumentPropertiesText(InputStream inputStream, boolean readDocumentProperties, boolean readCustomProperties) throws IOException {
        StringBuilder output = new StringBuilder();
        try (Presentation presentation = new Presentation(inputStream)) {
            DocumentProperties props = presentation.getDocumentProperties();
            if (readDocumentProperties) {
                output.append("CORE DOCUMENT PROPERTIES:").append(System.lineSeparator());
                output.append("-".repeat(30)).append(System.lineSeparator());
                output.append("Title: ").append(nvl(props.getTitle())).append(System.lineSeparator());
                output.append("Author: ").append(nvl(props.getAuthor())).append(System.lineSeparator());
                output.append("Subject: ").append(nvl(props.getSubject())).append(System.lineSeparator());
                output.append("Keywords: ").append(nvl(props.getKeywords())).append(System.lineSeparator());
                output.append("Category: ").append(nvl(props.getCategory())).append(System.lineSeparator());
                output.append("Company: ").append(nvl(props.getCompany())).append(System.lineSeparator());
                output.append("Manager: ").append(nvl(props.getManager())).append(System.lineSeparator());
                output.append("Created Date: ").append(formatDate(props.getCreated())).append(System.lineSeparator());
                output.append("Last Printed: ").append(formatDate(props.getPrinted())).append(System.lineSeparator());
                output.append(System.lineSeparator());
            }
            if (readCustomProperties) {
                output.append("CUSTOM PROPERTIES:").append(System.lineSeparator());
                output.append("-".repeat(30)).append(System.lineSeparator());
                boolean hasCustomProperties = false;
                for (java.util.Map.Entry<String, DocumentCustomProperty> entry : props.getCustomProperties().entrySet()) {
                    hasCustomProperties = true;
                    DocumentCustomProperty value = entry.getValue();
                    output.append(entry.getKey()).append(": ").append(value.getValue())
                        .append(" (").append(value.getType()).append(")").append(System.lineSeparator());
                }
                if (!hasCustomProperties)
                    output.append("No custom properties found.").append(System.lineSeparator());
                output.append(System.lineSeparator());
            }
        }
        return output.toString();
    }

    private static String formatDate(Instant value) {
        if (value == null || value.equals(UNSET_DATE)) {
            return UNSET_DATE_TEXT;
        }
        return DATE_FORMAT.format(value.atZone(ZoneId.systemDefault()));
    }

    static String nvl(String s) {
        return s != null ? s : "";
    }
}
