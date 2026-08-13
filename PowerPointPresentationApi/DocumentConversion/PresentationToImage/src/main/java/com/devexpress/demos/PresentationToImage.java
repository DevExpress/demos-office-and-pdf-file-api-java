package com.devexpress.demos;

import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.presentation.ImageExportOptions;
import com.devexpress.docs.presentation.Presentation;
import com.devexpress.drawing.DXImage;
import com.devexpress.drawing.DXImageFormat;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PresentationToImage {

    static final String INPUT_FILE_PATH = "SharedFiles/Sample.pptx";
    static final String OUTPUT_DIRECTORY_NAME = "Output";
    static final DXImageFormat IMAGE_FORMAT = DXImageFormat.getPng();
    static final int RESOLUTION = 300;

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            InputStream inputResource = PresentationToImage.class.getResourceAsStream("/" + INPUT_FILE_PATH);
            if (inputResource == null)
                throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);

            File outputDirectory = new File(OUTPUT_DIRECTORY_NAME);
            outputDirectory.mkdirs();

            List<ByteArrayOutputStream> imageStreams = convertToImages(inputResource, IMAGE_FORMAT, RESOLUTION, new int[0]);
            inputResource.close();

            if (imageStreams.isEmpty())
                throw new IllegalStateException("Failed to convert presentation to images.");

            for (int i = 0; i < imageStreams.size(); i++) {
                File outputFile = new File(outputDirectory, "slide_" + (i + 1) + "." + getImageExtension(IMAGE_FORMAT));
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    imageStreams.get(i).writeTo(fos);
                }
                System.out.println("Created " + outputFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    static List<ByteArrayOutputStream> convertToImages(InputStream inputStream, DXImageFormat imageFormat,
            int resolution, int[] slideIndexes) throws IOException {
        List<ByteArrayOutputStream> outputStreams = new ArrayList<>();
        try (Presentation presentation = new Presentation(inputStream)) {
            ImageExportOptions options = new ImageExportOptions();
            options.setResolution(resolution);
            DXImage[] images = presentation.exportToImages(options, slideIndexes);
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
        return "png";
    }
}
