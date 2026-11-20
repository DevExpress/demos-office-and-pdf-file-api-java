package com.devexpress.demos;

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
    static final String OUTPUT_DIRECTORY_NAME = "Output";
    static final String OUTPUT_DIRECTORY_DEMO_NAME = PresentationMerge.class.getSimpleName();
    static final String OUTPUT_FILE_NAME = "Merged_Presentation.pdf";
    static final boolean IS_PDF_OUTPUT = true;
    static final DocumentFormat OUTPUT_FORMAT = DocumentFormat.PPTX;

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

                File outputDirectory = new File(OUTPUT_DIRECTORY_NAME, OUTPUT_DIRECTORY_DEMO_NAME);
                outputDirectory.mkdirs();

                File outputFile = new File(outputDirectory, OUTPUT_FILE_NAME);

                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    outputStream.writeTo(fos);
                }
                System.out.println("Created " + outputFile.getAbsolutePath());
            } finally {
                for (InputStream s : inputStreams)
                    s.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                    presentation.save(baos, targetFormat);
            }
        }
        return baos;
    }
}
