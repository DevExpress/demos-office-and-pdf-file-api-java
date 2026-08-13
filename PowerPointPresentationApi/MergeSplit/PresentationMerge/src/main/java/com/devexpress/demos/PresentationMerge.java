package com.devexpress.demos;

import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.presentation.DocumentFormat;
import com.devexpress.docs.presentation.Presentation;
import com.devexpress.docs.presentation.Slide;
import com.devexpress.docs.presentation.ResizeMode;

import java.io.*;
import java.util.List;

public class PresentationMerge {

    static final String[] INPUT_FILE_PATHS = {
        "SharedFiles/NorthwindPresentation.pptx",
        "SharedFiles/ProjectStatusOverview.pptx"
    };
    static final String OUTPUT_FILE_NAME = "Merged_Presentation.pdf";
    static final boolean IS_PDF_OUTPUT = true;
    static final DocumentFormat OUTPUT_FORMAT = DocumentFormat.PPTX;

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            List<InputStream> inputStreams = new java.util.ArrayList<>();
            try {
                for (String path : INPUT_FILE_PATHS) {
                    InputStream resource = PresentationMerge.class.getResourceAsStream("/" + path);
                    if (resource == null)
                        throw new FileNotFoundException("Resource not found: " + path);
                    inputStreams.add(resource);
                }

                ByteArrayOutputStream outputStream = mergePresentations(inputStreams, OUTPUT_FORMAT, IS_PDF_OUTPUT);

                try (FileOutputStream fos = new FileOutputStream(OUTPUT_FILE_NAME)) {
                    outputStream.writeTo(fos);
                }
                System.out.println("Created " + OUTPUT_FILE_NAME);
            } finally {
                for (InputStream s : inputStreams)
                    s.close();
            }
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    static ByteArrayOutputStream mergePresentations(List<InputStream> inputStreams, DocumentFormat targetFormat, boolean pdfOutput) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (!inputStreams.isEmpty()) {
            try (Presentation presentation = new Presentation(inputStreams.get(0))) {
                for (int index = 1; index < inputStreams.size(); index++) {
                    try (Presentation tempPresentation = new Presentation(inputStreams.get(index))) {
                        tempPresentation.resizeSlides(presentation.getSlideSize(), ResizeMode.ENSURE_FIT);
                        for (Slide slide : tempPresentation.getSlides())
                            presentation.getSlides().add(slide);
                    }
                }
                if (pdfOutput)
                    presentation.exportToPdf(baos);
                else
                    presentation.saveDocument(baos, targetFormat);
            }
        }
        return baos;
    }
}
