package com.devexpress.demos;

import com.devexpress.docs.office.RestrictedHyperlinkRemovalMode;
import com.devexpress.docs.presentation.*;
import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.SecurityLoadingLimitExceededException;
import java.io.*;

public class PresentationSecureDocumentLoading {
    static final String INPUT_FILE_PATH = "SharedFiles/Sample.pptx";
    static final String OUTPUT_FILE_NAME = "Secure_Document.pptx";
    static final DocumentFormat OUTPUT_FORMAT = DocumentFormat.PPTX;

    static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    static final int MAX_SLIDE_COUNT = 10;
    static final int MAX_SHAPE_COUNT_PER_SLIDE = 20;
    static final long MAX_XML_ELEMENT_COUNT = 1000;
    static final int MAX_XML_ELEMENT_DEPTH = 20;

    static final boolean REMOVE_ACTIVEX_CONTENT = true;
    static final boolean REMOVE_CUSTOM_XML_PARTS = true;
    static final boolean REMOVE_EXTERNAL_IMAGES = true;
    static final boolean REMOVE_MACROS = true;
    static final boolean REMOVE_OLE_OBJECTS = true;
    static final RestrictedHyperlinkRemovalMode RESTRICTED_HYPERLINK_REMOVAL_MODE = RestrictedHyperlinkRemovalMode.URI_ONLY;

    record PresentationLoadResult<T>(T outputStream, String errorMessage) {}

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            InputStream inputResource = PresentationSecureDocumentLoading.class.getResourceAsStream("/" + INPUT_FILE_PATH);
            if (inputResource == null)
                throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);

            var loadResult = tryLoadPresentation(inputResource, createPresentationSecurityLoadingLimits(), createPresentationSecurityLoadingOptions(), OUTPUT_FORMAT);
            inputResource.close();

            InputStream outputStream = loadResult.outputStream();
            String loadErrorMessage = loadResult.errorMessage();

            if (loadErrorMessage != null && !loadErrorMessage.isEmpty()) {
                throw new IllegalStateException(loadErrorMessage);
            }

            try (FileOutputStream fos = new FileOutputStream(OUTPUT_FILE_NAME)) {
                outputStream.transferTo(fos);
            }
            System.out.println("Created " + OUTPUT_FILE_NAME);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }
    private static PresentationSecurityLoadingLimits createPresentationSecurityLoadingLimits() {
        var presentationSecurityLoadingLimits = new PresentationSecurityLoadingLimits();
        presentationSecurityLoadingLimits.setMaxFileSize(MAX_FILE_SIZE);
        presentationSecurityLoadingLimits.setMaxSlideCount(MAX_SLIDE_COUNT);
        presentationSecurityLoadingLimits.setMaxShapeCountPerSlide(MAX_SHAPE_COUNT_PER_SLIDE);
        presentationSecurityLoadingLimits.setMaxXmlElementCount(MAX_XML_ELEMENT_COUNT);
        presentationSecurityLoadingLimits.setMaxXmlElementDepth(MAX_XML_ELEMENT_DEPTH);
        return presentationSecurityLoadingLimits;
    }
    private static PresentationSecurityLoadingOptions createPresentationSecurityLoadingOptions() {
        var presentationSecurityLoadingOptions = new PresentationSecurityLoadingOptions();
        presentationSecurityLoadingOptions.setRemoveActiveXContent(REMOVE_ACTIVEX_CONTENT);
        presentationSecurityLoadingOptions.setRemoveCustomXMLParts(REMOVE_CUSTOM_XML_PARTS);
        presentationSecurityLoadingOptions.setRemoveExternalImages(REMOVE_EXTERNAL_IMAGES);
        presentationSecurityLoadingOptions.setRemoveMacros(REMOVE_MACROS);
        presentationSecurityLoadingOptions.setRemoveOleObjects(REMOVE_OLE_OBJECTS);
        presentationSecurityLoadingOptions.setRestrictedHyperlinkRemovalMode(RESTRICTED_HYPERLINK_REMOVAL_MODE);
        return presentationSecurityLoadingOptions;
    }
    static PresentationLoadResult<InputStream> tryLoadPresentation(InputStream inputStream, PresentationSecurityLoadingLimits securityLoadingLimits, PresentationSecurityLoadingOptions securityLoadingOptions, DocumentFormat outputFormat){
        var loadOptions = new LoadOptions();
        loadOptions.setSecurityLoadingLimits(securityLoadingLimits);
        loadOptions.setSecurityLoadingOptions(securityLoadingOptions);

        try (Presentation presentation = new Presentation(inputStream, loadOptions)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            presentation.saveDocument(baos, outputFormat);
            return new PresentationLoadResult<>(
                    new ByteArrayInputStream(baos.toByteArray()),
                    ""
            );
        }
        catch (SecurityLoadingLimitExceededException limitExceededException){
            var loadErrorMessage = limitExceededException.getMessage();
            return new PresentationLoadResult<>(
                    null,
                    loadErrorMessage
            );
        }
    }
}