package com.devexpress.demos;

import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.presentation.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PresentationExtractText {

    static final String INPUT_FILE_PATH = "SharedFiles/Sample.pptx";
    static final String OUTPUT_FILE_NAME = "Presentation_Text.txt";
    static final boolean EXTRACT_FROM_SHAPES = true;
    static final boolean EXTRACT_FROM_TABLES = true;
    static final boolean EXTRACT_NOTES = false;

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            InputStream inputResource = PresentationExtractText.class.getResourceAsStream("/" + INPUT_FILE_PATH);
            if (inputResource == null)
                throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);

            String presentationText = extractText(inputResource, new int[0], EXTRACT_FROM_SHAPES, EXTRACT_FROM_TABLES, EXTRACT_NOTES);
            inputResource.close();

            try (FileOutputStream fos = new FileOutputStream(OUTPUT_FILE_NAME)) {
                fos.write(presentationText.getBytes(StandardCharsets.UTF_8));
            }
            System.out.println("Created " + OUTPUT_FILE_NAME);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    static String extractText(InputStream inputStream, int[] slideRange, boolean extractFromShapes, boolean extractFromTables, boolean extractNotes) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (Presentation presentation = new Presentation(inputStream)) {
            List<Slide> slides = presentation.getSlides();
            boolean allSlides = slideRange.length == 0;
            Set<Integer> slideSet = new HashSet<>();
            for (int idx : slideRange) slideSet.add(idx);
            for (int index = 0; index < slides.size(); index++) {
                if (allSlides || slideSet.contains(index)) {
                    Slide slide = slides.get(index);
                    builder.append("## Slide ").append(index + 1).append(System.lineSeparator());
                    extractShapeText(builder, slide.getShapes(), extractFromShapes, extractFromTables);
                    if (extractNotes && slide.getNotes() != null) {
                        String notesText = slide.getNotes().getTextArea().getText();
                        if (notesText != null && !notesText.isBlank()) {
                            builder.append("### Notes").append(System.lineSeparator());
                            builder.append(notesText).append(System.lineSeparator());
                        }
                    }
                    builder.append(System.lineSeparator());
                }
            }
        }
        return builder.toString();
    }

    static void extractShapeText(StringBuilder builder, IShapeCollection shapes, boolean extractFromShapes, boolean extractFromTables) {
        List<ShapeBase> sortedShapes = new ArrayList<>(shapes);
        sortedShapes.sort(Comparator.comparingDouble(s -> (double) s.getY() * 1e9 + s.getX()));
        for (ShapeBase shape : sortedShapes) {
            if (shape instanceof Table) {
                if (extractFromTables)
                    builder.append(extractTextFromTable((Table) shape));
            } else if (shape instanceof Shape) {
                if (extractFromShapes) {
                    Shape textShape = (Shape) shape;
                    if (textShape.getTextArea() != null) {
                        String text = textShape.getTextArea().getText();
                        if (text != null && !text.isEmpty())
                            builder.append(text).append(System.lineSeparator());
                    }
                }
            } else if (shape instanceof GroupShape) {
                extractShapeText(builder, ((GroupShape) shape).getShapes(), extractFromShapes, extractFromTables);
            }
        }
    }

    static String extractTextFromTable(Table table) {
        StringBuilder builder = new StringBuilder();
        for (int row = 0; row < table.getRows().size(); row++) {
            for (int column = 0; column < table.getColumns().size(); column++) {
                TableCell cell = table.getThis(row, column);
                String cellText = cell.getTextArea() != null ? cell.getTextArea().getText() : "";
                if (cellText == null) cellText = "";
                builder.append(cellText);
                if (column != table.getColumns().size() - 1)
                    builder.append('\t');
            }
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }
}
