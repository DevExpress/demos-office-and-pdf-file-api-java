package com.devexpress.demos;

import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.presentation.DocumentCustomProperty;
import com.devexpress.docs.presentation.DocumentProperties;
import com.devexpress.docs.presentation.Presentation;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class PresentationMetadata {

    static final String INPUT_FILE_PATH = "SharedFiles/Sample.pptx";
    static final String OUTPUT_FILE_NAME = "Document_Metadata.txt";
    static final boolean READ_DOCUMENT_PROPERTIES = true;
    static final boolean READ_CUSTOM_PROPERTIES = true;

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            InputStream inputResource = PresentationMetadata.class.getResourceAsStream("/" + INPUT_FILE_PATH);
            if (inputResource == null)
                throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);

            String metadataText = readDocumentPropertiesText(inputResource, READ_DOCUMENT_PROPERTIES, READ_CUSTOM_PROPERTIES);
            inputResource.close();

            try (FileOutputStream fos = new FileOutputStream(OUTPUT_FILE_NAME)) {
                fos.write(metadataText.getBytes(StandardCharsets.UTF_8));
            }
            System.out.println("Created " + OUTPUT_FILE_NAME);
        } catch (Exception e) {
            System.err.println(e);
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
                output.append("Created Date: ").append(props.getCreated()).append(System.lineSeparator());
                output.append("Last Printed: ").append(props.getPrinted()).append(System.lineSeparator());
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

    static String nvl(String s) {
        return s != null ? s : "";
    }
}
