package com.devexpress.demos;

import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.OfficeImage;
import com.devexpress.docs.OfficeImageBase;
import com.devexpress.docs.presentation.*;
import com.devexpress.drawing.DXImage;
import com.devexpress.drawing.DXImageFormat;

import java.io.*;
import java.util.*;

public class PresentationExtractImages {

    static final String INPUT_FILE_PATH = "SharedFiles/Sample.pptx";
    static final String OUTPUT_DIRECTORY_NAME = "Presentation_Images";
    static final DXImageFormat IMAGE_FORMAT = DXImageFormat.getPng();

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            InputStream inputResource = PresentationExtractImages.class.getResourceAsStream("/" + INPUT_FILE_PATH);
            if (inputResource == null)
                throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);

            File outputDirectory = new File(OUTPUT_DIRECTORY_NAME);
            if (outputDirectory.exists())
                deleteDirectory(outputDirectory);
            outputDirectory.mkdirs();

            List<ByteArrayOutputStream> imageStreams = extractImages(inputResource, IMAGE_FORMAT, new int[0]);
            inputResource.close();

            if (imageStreams.isEmpty())
                throw new IllegalStateException("Failed to extract document images.");

            for (int index = 0; index < imageStreams.size(); index++) {
                File outputFile = new File(outputDirectory, "image_" + (index + 1) + "." + getOutputExtension(IMAGE_FORMAT));
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    imageStreams.get(index).writeTo(fos);
                }
            }
            System.out.println("Created " + outputDirectory.getAbsolutePath());
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    static List<ByteArrayOutputStream> extractImages(InputStream inputStream, DXImageFormat imageFormat, int[] slideRange) throws IOException {
        List<ByteArrayOutputStream> imageStreams = new ArrayList<>();
        try (Presentation presentation = new Presentation(inputStream)) {
            List<Slide> slides = presentation.getSlides();
            boolean allSlides = slideRange.length == 0;
            Set<Integer> slideSet = new HashSet<>();
            for (int idx : slideRange) slideSet.add(idx);
            for (int index = 0; index < slides.size(); index++) {
                if (allSlides || slideSet.contains(index)) {
                    for (ShapeBase shape : slides.get(index).getShapes())
                        extractShapeImages(shape, imageStreams, imageFormat);
                }
            }
        }
        return imageStreams;
    }

    static void extractShapeImages(ShapeBase shape, List<ByteArrayOutputStream> imageStreams, DXImageFormat imageFormat) throws IOException {
        if (shape instanceof PictureShape) {
            PictureShape pictureShape = (PictureShape) shape;
            OfficeImageBase officeImageBase = pictureShape.getImage();
            if (officeImageBase instanceof OfficeImage) {
                DXImage dxImage = ((OfficeImage) officeImageBase).getDXImage();
                if (dxImage != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    dxImage.save(baos, imageFormat);
                    imageStreams.add(baos);
                }
            }
        } else if (shape instanceof GroupShape) {
            GroupShape groupShape = (GroupShape) shape;
            for (ShapeBase innerShape : groupShape.getShapes())
                extractShapeImages(innerShape, imageStreams, imageFormat);
        }
    }

    static String getOutputExtension(DXImageFormat imageFormat) {
        if (imageFormat.equals(DXImageFormat.getPng())) return "png";
        if (imageFormat.equals(DXImageFormat.getJpeg())) return "jpeg";
        if (imageFormat.equals(DXImageFormat.getBmp())) return "bmp";
        throw new IllegalArgumentException("Unsupported image format.");
    }

    static void deleteDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null)
            for (File file : files)
                if (file.isDirectory()) deleteDirectory(file);
                else file.delete();
        dir.delete();
    }
}
