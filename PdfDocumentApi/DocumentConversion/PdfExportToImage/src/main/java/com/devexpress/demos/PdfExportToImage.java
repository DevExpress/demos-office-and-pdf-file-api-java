package com.devexpress.demos;

import com.devexpress.docs.pdf.PdfDocument;
import com.devexpress.docs.pdf.ImageExportOptions;
import com.devexpress.drawing.DXImage;
import com.devexpress.drawing.DXImageFormat;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PdfExportToImage {

    static final String INPUT_FILE_PATH = "SharedFiles/PDF/Sample_Main.pdf";
    static final String OUTPUT_DIRECTORY_NAME = "Output";
    static final String OUTPUT_DIRECTORY_DEMO_NAME = PdfExportToImage.class.getSimpleName();
    static final String OUTPUT_FILE_NAME = "page_image";
    static final DXImageFormat IMAGE_FORMAT = DXImageFormat.getPng();
    static final int RESOLUTION = 300;

    public static void main(String[] args) {
        try {
            List<ByteArrayOutputStream> imageStreams;
            try (InputStream inputResource = PdfExportToImage.class.getResourceAsStream("/" + INPUT_FILE_PATH)) {
                if (inputResource == null)
                    throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);
                imageStreams = exportToImages(inputResource, IMAGE_FORMAT, RESOLUTION, new int[0]);
            }

            if (imageStreams.isEmpty())
                throw new IllegalStateException("Failed to export document pages to images.");

            File outputDirectory = new File(OUTPUT_DIRECTORY_NAME, OUTPUT_DIRECTORY_DEMO_NAME);
            outputDirectory.mkdirs();

            if (imageStreams.size() == 1) {
                File outputFile = new File(outputDirectory, OUTPUT_FILE_NAME + "." + getImageExtension(IMAGE_FORMAT));
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    imageStreams.get(0).writeTo(fos);
                }
                System.out.println("Created " + outputFile.getAbsolutePath());
                return;
            }

            for (int i = 0; i < imageStreams.size(); i++) {
                File outputFile = new File(outputDirectory, "page_" + (i + 1) + "." + getImageExtension(IMAGE_FORMAT));
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    imageStreams.get(i).writeTo(fos);
                }
            }
            System.out.println("Created " + outputDirectory.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    static List<ByteArrayOutputStream> exportToImages(InputStream inputStream, DXImageFormat imageFormat,
            int resolution, int[] pageIndexes) throws IOException {
        List<ByteArrayOutputStream> outputStreams = new ArrayList<>();
        try (PdfDocument document = new PdfDocument(inputStream)) {
            ImageExportOptions options = new ImageExportOptions();
            options.setResolution(resolution);
            DXImage[] images = document.exportToImages(options, pageIndexes);
            for (DXImage image : images) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.save(baos, imageFormat);
                outputStreams.add(baos);
            }
        }
        return outputStreams;
    }

    static String getImageExtension(DXImageFormat imageFormat) {
        if (imageFormat.equals(DXImageFormat.getBmp())) return "bmp";
        if (imageFormat.equals(DXImageFormat.getJpeg())) return "jpeg";
        if (imageFormat.equals(DXImageFormat.getSvg())) return "svg";
        if (imageFormat.equals(DXImageFormat.getTiff())) return "tiff";
        return "png";
    }
}
