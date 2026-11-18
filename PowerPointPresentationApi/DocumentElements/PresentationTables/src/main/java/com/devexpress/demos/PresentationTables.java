package com.devexpress.demos;

import com.devexpress.docs.presentation.*;
import com.devexpress.office.utils.Units;

import java.io.*;

public class PresentationTables {

    static final String OUTPUT_DIRECTORY_NAME = "Output";
    static final String OUTPUT_DIRECTORY_DEMO_NAME = PresentationTables.class.getSimpleName();
    static final String OUTPUT_FILE_NAME = "Table_Result";
    static final boolean IS_PDF_OUTPUT = false;
    static final int NUMBER_OF_ROWS = 5;
    static final int NUMBER_OF_COLUMNS = 4;
    static final float COLUMN_WIDTH = 1.5f;
    static final TableStyleType TABLE_STYLE = TableStyleType.DARK_STYLE_1_ACCENT_1;
    static final boolean HAS_HEADER = true;
    static final boolean HAS_BANDED_ROWS = true;
    static final boolean HAS_BANDED_COLUMNS = false;

    public static void main(String[] args) {
        try {
            ByteArrayOutputStream outputStream = createPresentationTable();

            File outputDirectory = new File(OUTPUT_DIRECTORY_NAME, OUTPUT_DIRECTORY_DEMO_NAME);
            outputDirectory.mkdirs();
            String outputFormat = IS_PDF_OUTPUT ? "pdf" : "pptx";
            File outputFile = new File(outputDirectory, OUTPUT_FILE_NAME + "." + outputFormat);

            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                outputStream.writeTo(fos);
            }
            System.out.println("Created " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    static ByteArrayOutputStream createPresentationTable() throws IOException {
        try (Presentation presentation = new Presentation()) {
            presentation.getSlides().clear();
            Slide slide = new Slide(SlideLayoutType.BLANK);
            presentation.getSlides().add(slide);

            float tableWidth = Units.inchesToDocumentsF(NUMBER_OF_COLUMNS * COLUMN_WIDTH);
            Table table = new Table(NUMBER_OF_ROWS, NUMBER_OF_COLUMNS, 100, 100, tableWidth);
            table.setStyle(new ThemedTableStyle(TABLE_STYLE));
            table.setHasHeaderRow(HAS_HEADER);
            table.setHasBandedRows(HAS_BANDED_ROWS);
            table.setHasBandedColumns(HAS_BANDED_COLUMNS);
            slide.getShapes().add(table);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if (IS_PDF_OUTPUT)
                presentation.exportToPdf(stream);
            else
                presentation.save(stream, DocumentFormat.PPTX);
            return stream;
        }
    }
}
