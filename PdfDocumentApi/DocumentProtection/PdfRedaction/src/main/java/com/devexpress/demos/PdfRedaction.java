package com.devexpress.demos;

import com.devexpress.docs.pdf.*;
import com.devexpress.system.drawing.RectangleF;

import java.io.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class PdfRedaction {

    static final String INPUT_FILE_PATH = "SharedFiles/PDF/Sample_Main.pdf";
    static final String OUTPUT_DIRECTORY_NAME = "Output";
    static final String OUTPUT_DIRECTORY_DEMO_NAME = PdfRedaction.class.getSimpleName();
    static final String OUTPUT_FILE_NAME = "RedactedDocument.pdf";
    static final String SEARCH_TEXT = "PDF Viewer";
    static final String REDACTION_TEXT = "REDACTED";
    static final boolean APPLY_REDACTION = true;

    public static void main(String[] args) {
        try {
            ByteArrayOutputStream outputStream;
            try (InputStream inputResource = PdfRedaction.class.getResourceAsStream("/" + INPUT_FILE_PATH)) {
                if (inputResource == null)
                    throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);
                outputStream = addRedaction(inputResource, SEARCH_TEXT, REDACTION_TEXT, APPLY_REDACTION);
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

    static ByteArrayOutputStream addRedaction(InputStream documentStream, String searchText, String redactionText,
            boolean applyRedaction) throws IOException {
        try (PdfDocument doc = new PdfDocument(documentStream)) {
            Iterable<TextSearchInfo> searchResults = doc.findText(searchText, new TextSearchOptions());

            for (TextSearchInfo result : searchResults) {
                List<RedactionAnnotation> annotations = new ArrayList<>();
                for (TextMatchInfo match : result.getMatches()) {
                    List<OrientedRectangle> rects = new ArrayList<>();
                    for (OrientedRectangle r : match.getRectangles())
                        rects.add(r);
                    OrientedRectangle[] rectsArray = rects.toArray(new OrientedRectangle[0]);
                    RedactionAnnotation annotation = new RedactionAnnotation(RectangleF.EMPTY);
                    annotation.setGeometry(new RedactionGeometry(rectsArray));
                    annotation.setFillColor(PdfColor.getBlack());
                    annotation.setColor(PdfColor.getRed());
                    if (redactionText != null && !redactionText.isEmpty())
                        annotation.setOverlayText(redactionText);
                    annotation.setCreationDate(OffsetDateTime.now(ZoneOffset.UTC));
                    annotation.setTextJustification(TextJustification.LEFT_JUSTIFIED);
                    annotation.setRepeatText(true);
                    TextAppearance textAppearance = new TextAppearance();
                    textAppearance.setFontSize(0);
                    textAppearance.setFill(SolidFill.getWhite());
                    annotation.setTextAppearance(textAppearance);
                    annotations.add(annotation);
                }
                if (applyRedaction)
                    doc.applyRedaction(result.getPageIndex(), annotations.toArray(new RedactionAnnotation[0]));
                else
                    for (RedactionAnnotation annotation : annotations)
                        doc.getPages().get(result.getPageIndex()).getAnnotations().add(annotation);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos;
        }
    }
}
