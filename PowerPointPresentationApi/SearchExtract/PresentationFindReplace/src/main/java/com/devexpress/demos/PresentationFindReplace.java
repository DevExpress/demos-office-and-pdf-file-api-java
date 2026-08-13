package com.devexpress.demos;

import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.OfficeColor;
import com.devexpress.docs.office.TextProperties;
import com.devexpress.docs.presentation.DocumentFormat;
import com.devexpress.docs.presentation.Presentation;
import com.devexpress.system.drawing.Color;

import java.io.*;
import java.util.List;

public class PresentationFindReplace {

    static final String INPUT_FILE_PATH = "SharedFiles/Sample.pptx";
    static final String OUTPUT_FILE_NAME = "Modified_Presentation.pptx";
    static final String SEARCH_TEXT = "North Wind";
    static final String REPLACE_TEXT = "DoeCompany";
    static final String HIGHLIGHT_COLOR = "#FFFF00";
    static final FindReplaceActionType OPERATION = FindReplaceActionType.REPLACE;
    static final DocumentFormat OUTPUT_FORMAT = DocumentFormat.PPTX;

    enum FindReplaceActionType { REPLACE, REMOVE, HIGHLIGHT }

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            InputStream inputResource = PresentationFindReplace.class.getResourceAsStream("/" + INPUT_FILE_PATH);
            if (inputResource == null)
                throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);

            int r = Integer.parseInt(HIGHLIGHT_COLOR.substring(1, 3), 16);
            int g = Integer.parseInt(HIGHLIGHT_COLOR.substring(3, 5), 16);
            int b = Integer.parseInt(HIGHLIGHT_COLOR.substring(5, 7), 16);
            Color highlightColor = Color.fromArgb(255, r, g, b);

            ByteArrayOutputStream outputStream = findReplacePresentationText(
                inputResource, OPERATION, SEARCH_TEXT, REPLACE_TEXT, highlightColor, OUTPUT_FORMAT);
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

    static ByteArrayOutputStream findReplacePresentationText(InputStream inputStream, FindReplaceActionType actionType,
            String findText, String replaceText, Color highlightColor, DocumentFormat outputFormat) throws IOException {
        try (Presentation presentation = new Presentation(inputStream)) {
            List<com.devexpress.docs.office.TextSearchInfo> searchResult = presentation.findText(findText);
            switch (actionType) {
                case REPLACE:
                    presentation.replaceText(searchResult, replaceText);
                    break;
                case REMOVE:
                    presentation.removeText(searchResult);
                    break;
                case HIGHLIGHT:
                    TextProperties properties = new TextProperties();
                    properties.setHighlightColor(new OfficeColor(highlightColor));
                    presentation.modifyTextProperties(searchResult, properties);
                    break;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            presentation.saveDocument(baos, outputFormat);
            return baos;
        }
    }
}
