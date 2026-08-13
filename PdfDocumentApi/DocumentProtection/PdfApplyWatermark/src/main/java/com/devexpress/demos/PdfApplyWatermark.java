package com.devexpress.demos;

import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.pdf.*;
import com.devexpress.system.drawing.PointF;
import com.devexpress.system.drawing.RectangleF;

import java.io.*;

public class PdfApplyWatermark {

    static final String INPUT_FILE_PATH = "SharedFiles/PDF/Sample_Main.pdf";
    static final String OUTPUT_FILE_NAME = "PdfWithWatermark.pdf";
    static final String WATERMARK_TEXT = "WATERMARK";
    static final boolean ROTATE = false;

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            InputStream inputResource = PdfApplyWatermark.class.getResourceAsStream("/" + INPUT_FILE_PATH);
            if (inputResource == null)
                throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);

            ByteArrayOutputStream outputStream = applyWatermark(inputResource, WATERMARK_TEXT, ROTATE);
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

    static ByteArrayOutputStream applyWatermark(InputStream documentStream, String text, boolean rotate) throws IOException {
        try (PdfDocument doc = new PdfDocument(documentStream)) {
            FormTemplate form = new FormTemplate();
            form.setBounds(new RectangleF(0, 0, 842, 595));

            TextFragment textFragment = new TextFragment();
            textFragment.setText(text);
            textFragment.setLocation(rotate ? new PointF(300, 220) : new PointF(280, 300));
            textFragment.setScaleX(4);
            textFragment.setScaleY(4);
            textFragment.setForegroundFill(new SolidFill(PdfColor.getLightSkyBlue(), 0.5));
            textFragment.setRotationAngle(rotate ? 45 : 0);
            form.addFragment(textFragment);

            for (Page page : doc.getPages()) {
                WatermarkAnnotation watermark = new WatermarkAnnotation(page.getMediaBox());
                AnnotationAppearances appearances = new AnnotationAppearances();
                AnnotationAppearance normalAppearance = new AnnotationAppearance();
                normalAppearance.setDefaultForm(form);
                appearances.setNormal(normalAppearance);
                watermark.setAppearance(appearances);
                page.getAnnotations().add(watermark);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos;
        }
    }
}
