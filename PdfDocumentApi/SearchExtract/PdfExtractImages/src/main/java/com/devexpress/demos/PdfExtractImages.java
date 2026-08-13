package com.devexpress.demos;

import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.pdf.*;
import com.devexpress.drawing.DXImage;
import com.devexpress.drawing.DXImageFormat;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PdfExtractImages {

    static final String INPUT_FILE_PATH = "SharedFiles/PDF/Sample_Alternative.pdf";
    static final String OUTPUT_DIRECTORY_NAME = "Output";
    static final String IMAGE_FORMAT_NAME = "png";
    static final int[] PAGE_RANGE = new int[0];

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            InputStream inputResource = PdfExtractImages.class.getResourceAsStream("/" + INPUT_FILE_PATH);
            if (inputResource == null)
                throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);

            File outputDirectory = new File(OUTPUT_DIRECTORY_NAME);
            outputDirectory.mkdirs();

            DXImageFormat imageFormat = getImageFormat(IMAGE_FORMAT_NAME);
            List<ByteArrayOutputStream> images = extractImages(inputResource, imageFormat, PAGE_RANGE);
            inputResource.close();

            for (int i = 0; i < images.size(); i++) {
                File outputFile = new File(outputDirectory, "image_" + (i + 1) + "." + IMAGE_FORMAT_NAME);
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    images.get(i).writeTo(fos);
                }
            }
            System.out.println("Created " + outputDirectory.getAbsolutePath());
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    static DXImageFormat getImageFormat(String format) {
        switch (format.toLowerCase()) {
            case "png": return DXImageFormat.getPng();
            case "jpeg": return DXImageFormat.getJpeg();
            case "bmp": return DXImageFormat.getBmp();
            case "tiff": return DXImageFormat.getTiff();
            default: return DXImageFormat.getPng();
        }
    }

    static List<ByteArrayOutputStream> extractImages(InputStream documentStream, DXImageFormat imageFormat,
            int[] pageRange) throws IOException {
        try (PdfDocument doc = new PdfDocument(documentStream)) {
            List<Integer> indices = new ArrayList<>();
            if (pageRange.length == 0) {
                for (int i = 0; i < doc.getPages().size(); i++)
                    indices.add(i);
            } else {
                for (int i : pageRange)
                    indices.add(i);
            }

            List<ByteArrayOutputStream> result = new ArrayList<>();
            for (int index : indices) {
                if (index < 0 || index >= doc.getPages().size())
                    continue;
                for (PageFragment fragment : doc.getPages().get(index).getFragments()) {
                    if (fragment instanceof ImageFragment imageFragment) {
                        DXImage image = imageFragment.getImage();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        image.save(baos, imageFormat);
                        result.add(baos);
                    }
                }
            }
            return result;
        }
    }
}
