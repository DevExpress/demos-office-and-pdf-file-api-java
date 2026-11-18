package com.devexpress.demos;

import com.devexpress.docs.pdf.*;

import java.io.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class PdfFindHighlight {

    static final String INPUT_FILE_PATH = "SharedFiles/PDF/Sample_Main.pdf";
    static final String OUTPUT_DIRECTORY_NAME = "Output";
    static final String OUTPUT_DIRECTORY_DEMO_NAME = PdfFindHighlight.class.getSimpleName();
    static final String OUTPUT_FILE_NAME = "HighlightedDocument.pdf";
    static final String SEARCH_TEXT = "DevExpress";
    static final String ANNOTATION_COLOR = "#FF0000";
    static final TextMarkupAnnotationType ANNOTATION_TYPE = TextMarkupAnnotationType.HIGHLIGHT;

    public static void main(String[] args) {
        try {
            int r = Integer.parseInt(ANNOTATION_COLOR.substring(1, 3), 16);
            int g = Integer.parseInt(ANNOTATION_COLOR.substring(3, 5), 16);
            int b = Integer.parseInt(ANNOTATION_COLOR.substring(5, 7), 16);

            ByteArrayOutputStream outputStream;
            try (InputStream inputResource = PdfFindHighlight.class.getResourceAsStream("/" + INPUT_FILE_PATH)) {
                if (inputResource == null)
                    throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);
                outputStream = findHighlightText(inputResource, SEARCH_TEXT, r, g, b, ANNOTATION_TYPE);
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

    static ByteArrayOutputStream findHighlightText(InputStream documentStream, String searchText,
            int r, int g, int b, TextMarkupAnnotationType annotationType) throws IOException {
        try (PdfDocument doc = new PdfDocument(documentStream)) {
            for (TextSearchInfo result : doc.findText(searchText)) {
                for (TextMatchInfo match : result.getMatches()) {
                    TextMarkupAnnotation annotation = new TextMarkupAnnotation(match.getRectangles());
                    annotation.setColor(PdfColor.fromRgb(r, g, b));
                    annotation.setCreationDate(OffsetDateTime.now(ZoneOffset.UTC));
                    annotation.setTitle("DX Demo");
                    annotation.setType(annotationType);
                    doc.getPages().get(result.getPageIndex()).getAnnotations().add(annotation);
                }
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos;
        }
    }
}
