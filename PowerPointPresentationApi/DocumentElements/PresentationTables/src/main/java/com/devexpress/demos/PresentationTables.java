package com.devexpress.demos;

import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.presentation.*;
import com.devexpress.office.utils.Units;

import java.io.*;

public class PresentationTables {

    static final String OUTPUT_FILE_NAME = "Table_Result.pptx";
    static final int NUMBER_OF_ROWS = 5;
    static final int NUMBER_OF_COLUMNS = 4;
    static final float COLUMN_WIDTH = 1.5f;
    static final TableStyleType TABLE_STYLE = TableStyleType.DARK_STYLE_1_ACCENT_1;
    static final boolean HAS_HEADER = true;
    static final boolean HAS_BANDED_ROWS = true;
    static final boolean HAS_BANDED_COLUMNS = false;
    static final boolean IS_PDF_OUTPUT = false;
    static final DocumentFormat OUTPUT_FORMAT = DocumentFormat.PPTX;

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            ByteArrayOutputStream outputStream = createPresentationTable(
                NUMBER_OF_ROWS, NUMBER_OF_COLUMNS, COLUMN_WIDTH, TABLE_STYLE,
                HAS_HEADER, HAS_BANDED_ROWS, HAS_BANDED_COLUMNS, IS_PDF_OUTPUT, OUTPUT_FORMAT);

            try (FileOutputStream fos = new FileOutputStream(OUTPUT_FILE_NAME)) {
                outputStream.writeTo(fos);
            }
            System.out.println("Created " + OUTPUT_FILE_NAME);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    static ByteArrayOutputStream createPresentationTable(int rows, int columns, float columnWidthInInches,
            TableStyleType styleType, boolean hasHeader, boolean hasBandedRows, boolean hasBandedColumns,
            boolean isPdfOutput, DocumentFormat format) throws IOException {
        try (Presentation presentation = new Presentation()) {
            presentation.getSlides().clear();
            Slide slide = new Slide(SlideLayoutType.BLANK);
            presentation.getSlides().add(slide);

            float tableWidth = Units.inchesToDocumentsF(columns * columnWidthInInches);
            Table table = new Table(rows, columns, 100, 100, tableWidth);
            table.setStyle(new ThemedTableStyle(styleType));
            table.setHasHeaderRow(hasHeader);
            table.setHasBandedRows(hasBandedRows);
            table.setHasBandedColumns(hasBandedColumns);
            slide.getShapes().add(table);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (isPdfOutput)
                presentation.exportToPdf(baos);
            else
                presentation.saveDocument(baos, format);
            return baos;
        }
    }
}
