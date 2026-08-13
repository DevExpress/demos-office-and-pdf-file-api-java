package com.devexpress.demos;

import com.devexpress.docs.office.MetadataRemovalScope;
import com.devexpress.docs.presentation.PresentationSanitizeOptions;
import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.presentation.DocumentFormat;
import com.devexpress.docs.presentation.Presentation;
import com.devexpress.docs.presentation.PresentationInspectOptions;
import com.devexpress.docs.office.HiddenContentSanitizeMode;

import java.io.*;
import java.util.stream.Collectors;

public class PresentationInspectAndSanitize {
    static final String INPUT_FILE_PATH = "SharedFiles/Sample.pptx";
    static final DocumentFormat OUTPUT_FORMAT = DocumentFormat.PPTX;
    static final InspectOperationType OPERATION = InspectOperationType.INSPECT;
    static final String INSPECT_OUTPUT_FILE_NAME = "Inspection_Log.txt";
    static final String SANITIZE_OUTPUT_FILE_NAME = "Sanitized_Presentation.pptx";

    static final boolean REMOVE_HIDDEN_SLIDES = true;
    static final boolean REMOVE_HIDDEN_SHAPES = true;
    static final boolean REMOVE_ACTIVEX_CONTENT = true;
    static final boolean REMOVE_COMMENTS = true;
    static final boolean REMOVE_CUSTOM_XML_PARTS = true;
    static final boolean REMOVE_MACROS = true;
    static final boolean REMOVE_METADATA = true;
    static final boolean REMOVE_NOTES = true;
    static final boolean REMOVE_OLE_OBJECTS = true;
    static final boolean REMOVE_OFF_SLIDE_CONTENT = true;

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            InputStream inputResource = PresentationInspectAndSanitize.class.getResourceAsStream("/" + INPUT_FILE_PATH);
            if (inputResource == null)
                throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);

            InputStream outputStream = (OPERATION == InspectOperationType.INSPECT)
                    ? createInspectionLog(inputResource)
                    : sanitizePresentation(inputResource, OUTPUT_FORMAT, createPresentationSanitizeOptions());
            inputResource.close();

            var outputFileName = OPERATION == InspectOperationType.INSPECT ? INSPECT_OUTPUT_FILE_NAME : SANITIZE_OUTPUT_FILE_NAME;
            try (FileOutputStream fos = new FileOutputStream(outputFileName)) {
                outputStream.transferTo(fos);
            }
            System.out.println("Created " + outputFileName);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }
    static PresentationSanitizeOptions createPresentationSanitizeOptions(){
        var sanitizeOptions = new PresentationSanitizeOptions();

        sanitizeOptions.setHiddenSlides(REMOVE_HIDDEN_SLIDES ? HiddenContentSanitizeMode.REMOVE : HiddenContentSanitizeMode.IGNORE);
        sanitizeOptions.setHiddenShapes(REMOVE_HIDDEN_SHAPES ? HiddenContentSanitizeMode.REMOVE : HiddenContentSanitizeMode.IGNORE);

        sanitizeOptions.setRemoveActiveXContent(REMOVE_ACTIVEX_CONTENT);
        sanitizeOptions.setRemoveComments(REMOVE_COMMENTS);
        sanitizeOptions.setRemoveCustomXmlParts(REMOVE_CUSTOM_XML_PARTS);
        sanitizeOptions.setRemoveMacros(REMOVE_MACROS);
        sanitizeOptions.setMetadata(REMOVE_METADATA ? MetadataRemovalScope.ALL : MetadataRemovalScope.NONE);
        sanitizeOptions.setRemoveNotes(REMOVE_NOTES);
        sanitizeOptions.setRemoveOleObjects(REMOVE_OLE_OBJECTS);
        sanitizeOptions.setRemoveOffSlideContent(REMOVE_OFF_SLIDE_CONTENT);
        return sanitizeOptions;
    }
    static InputStream createInspectionLog(InputStream inputStream) throws Exception {
        String inspectionResult = inspectPresentation(inputStream, PresentationInspectOptions.ALL);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(baos, java.nio.charset.StandardCharsets.UTF_8), true)) {
            writer.write(inspectionResult);
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }
    static String inspectPresentation(InputStream inputStream, PresentationInspectOptions inspectOptions)  throws IOException {
        try (Presentation presentation = new Presentation(inputStream)) {
            StringBuilder output = new StringBuilder();
            var inspectResult = presentation.inspect(inspectOptions);
            if(inspectResult.getContentTypes().isEmpty()){
                output.append("No issues found.")
                        .append(System.lineSeparator());
            } else {
                output.append("Presentation contains the following hidden/sensitive content:")
                        .append(System.lineSeparator());
                output.append(
                        inspectResult.getContentTypes().stream()
                                .map(Object::toString)
                                .collect(Collectors.joining(System.lineSeparator())));
            }
            return output.toString();
        }
    }
    static InputStream sanitizePresentation(InputStream inputStream, DocumentFormat outputFormat, PresentationSanitizeOptions sanitizeOptions) throws IOException {
        try (Presentation presentation = new Presentation(inputStream)) {
            if(sanitizeOptions == null){
                var inspectResult = presentation.inspect();
                sanitizeOptions = inspectResult.createSanitizeOptions();
            }

            presentation.sanitize(sanitizeOptions);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            presentation.saveDocument(baos, outputFormat);
            return new ByteArrayInputStream(baos.toByteArray());
        }
    }
    enum InspectOperationType {
        INSPECT,
        SANITIZE
    }
}