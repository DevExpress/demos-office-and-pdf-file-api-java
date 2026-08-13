package com.devexpress.demos;

import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.pdf.*;
import com.devexpress.system.drawing.RectangleF;

import java.io.*;
import java.time.OffsetDateTime;

public class PdfAnnotations {

    static final String INPUT_FILE_PATH = "SharedFiles/PDF/Sample_Main.pdf";
    static final String OUTPUT_DIRECTORY_NAME = "Output";
    static final String SEARCH_TEXT = "DevExpress";
    static final String FREE_TEXT = "Sample Text";
    static final String ANNOTATION_COLOR = "#CC5B00";
    static final String STAMP_ICON_NAME = "Approved";
    static final String STICKY_NOTE_ICON_NAME = "Note";

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            InputStream inputResource = PdfAnnotations.class.getResourceAsStream("/" + INPUT_FILE_PATH);
            if (inputResource == null)
                throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);
            byte[] inputBytes = inputResource.readAllBytes();
            inputResource.close();

            int r = Integer.parseInt(ANNOTATION_COLOR.substring(1, 3), 16);
            int g = Integer.parseInt(ANNOTATION_COLOR.substring(3, 5), 16);
            int b = Integer.parseInt(ANNOTATION_COLOR.substring(5, 7), 16);

            File outputDirectory = new File(OUTPUT_DIRECTORY_NAME);
            outputDirectory.mkdirs();

            saveDocument(createLink(new ByteArrayInputStream(inputBytes), SEARCH_TEXT, r, g, b),
                    new File(outputDirectory, "annotation_link.pdf"));
            saveDocument(createFreeTextAnnotation(new ByteArrayInputStream(inputBytes), FREE_TEXT, r, g, b),
                    new File(outputDirectory, "annotation_freetext.pdf"));
            saveDocument(createRubberStamp(new ByteArrayInputStream(inputBytes), STAMP_ICON_NAME),
                    new File(outputDirectory, "annotation_stamp.pdf"));

            byte[] stickyNoteBytes;
            try (ByteArrayOutputStream baos = createStickyNote(new ByteArrayInputStream(inputBytes), STICKY_NOTE_ICON_NAME, r, g, b)) {
                stickyNoteBytes = baos.toByteArray();
            }
            saveDocument(createStickyNoteOutput(stickyNoteBytes), new File(outputDirectory, "annotation_stickynote.pdf"));
            saveDocument(flattenAllAnnotations(new ByteArrayInputStream(stickyNoteBytes)),
                    new File(outputDirectory, "annotation_flattened.pdf"));
            saveDocument(clearAllAnnotations(new ByteArrayInputStream(stickyNoteBytes)),
                    new File(outputDirectory, "annotation_cleared.pdf"));

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

    static ByteArrayOutputStream createStickyNoteOutput(byte[] bytes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(bytes);
        return baos;
    }

    static RectangleF unionBounds(Iterable<OrientedRectangle> rects) {
        RectangleF result = null;
        for (OrientedRectangle rect : rects) {
            RectangleF bb = rect.getBoundingBox();
            if (result == null) {
                result = bb;
            } else {
                float x = Math.min(result.getX(), bb.getX());
                float y = Math.min(result.getY(), bb.getY());
                float right = Math.max(result.getX() + result.getWidth(), bb.getX() + bb.getWidth());
                float bottom = Math.max(result.getY() + result.getHeight(), bb.getY() + bb.getHeight());
                result = new RectangleF(x, y, right - x, bottom - y);
            }
        }
        return result;
    }

    static ByteArrayOutputStream createLink(InputStream documentStream, String searchText, int r, int g, int b) throws IOException {
        try (PdfDocument doc = new PdfDocument(documentStream)) {
            for (TextSearchInfo result : doc.findText(searchText)) {
                for (TextMatchInfo match : result.getMatches()) {
                    RectangleF bounds = unionBounds(match.getRectangles());
                    if (bounds == null) continue;
                    LinkAnnotation link = new LinkAnnotation(bounds);
                    UriAction action = new UriAction();
                    action.setUri("https://www.devexpress.com");
                    link.setAction(action);
                    link.setColor(PdfColor.fromRgb(r, g, b));
                    AnnotationBorderStyle borderStyle = new AnnotationBorderStyle();
                    borderStyle.setWidth(2);
                    borderStyle.setStyle(BorderStyle.DASHED);
                    link.setBorderStyle(borderStyle);
                    doc.getPages().get(result.getPageIndex()).getAnnotations().add(link);
                }
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos;
        }
    }

    static ByteArrayOutputStream createFreeTextAnnotation(InputStream documentStream, String text, int r, int g, int b) throws IOException {
        try (PdfDocument doc = new PdfDocument(documentStream)) {
            Page page = doc.getPages().get(0);
            FreeTextAnnotation annotation = new FreeTextAnnotation(new RectangleF(450, 20, 130, 100));
            annotation.setContent(text);
            annotation.setTitle("DX Demo");
            annotation.setColor(PdfColor.fromRgb(r, g, b));
            annotation.setCreationDate(OffsetDateTime.now());
            page.getAnnotations().add(annotation);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos;
        }
    }

    static ByteArrayOutputStream createRubberStamp(InputStream documentStream, String iconName) throws IOException {
        try (PdfDocument doc = new PdfDocument(documentStream)) {
            Page page = doc.getPages().get(0);
            RubberStampAnnotation stamp = new RubberStampAnnotation(
                    new RectangleF(350, 80, 230, 55),
                    RubberStampAnnotationIconName.fromName(iconName));
            stamp.setTitle("DX Demo");
            stamp.setCreationDate(OffsetDateTime.now());
            page.getAnnotations().add(stamp);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos;
        }
    }

    static ByteArrayOutputStream createStickyNote(InputStream documentStream, String iconName, int r, int g, int b) throws IOException {
        try (PdfDocument doc = new PdfDocument(documentStream)) {
            Page page = doc.getPages().get(0);
            TextAnnotation annotation = new TextAnnotation(new RectangleF(450, 150, 20, 20));
            annotation.setIconName(TextAnnotationIconName.valueOf(iconName.toUpperCase()));
            annotation.setColor(PdfColor.fromRgb(r, g, b));
            annotation.setContent("This is a Sticky Note");
            annotation.setTitle("DX Demo");
            annotation.setCreationDate(OffsetDateTime.now());
            page.getAnnotations().add(annotation);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos;
        }
    }

    static ByteArrayOutputStream flattenAllAnnotations(InputStream documentStream) throws IOException {
        try (PdfDocument doc = new PdfDocument(documentStream)) {
            doc.flattenAnnotations();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos;
        }
    }

    static ByteArrayOutputStream clearAllAnnotations(InputStream documentStream) throws IOException {
        try (PdfDocument doc = new PdfDocument(documentStream)) {
            for (Page page : doc.getPages())
                page.getAnnotations().clear();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos;
        }
    }
}
