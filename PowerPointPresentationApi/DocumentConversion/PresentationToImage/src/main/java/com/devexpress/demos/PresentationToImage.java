package com.devexpress.demos;

import com.devexpress.docs.presentation.ImageExportOptions;
import com.devexpress.docs.presentation.Presentation;
import com.devexpress.drawing.DXImage;
import com.devexpress.drawing.DXImageFormat;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class PresentationToImage {

    static final String INPUT_FILE_PATH = "SharedFiles/Sample.pptx";
    static final String OUTPUT_DIRECTORY_NAME = "Output";
    static final String OUTPUT_DIRECTORY_DEMO_NAME = PresentationToImage.class.getSimpleName();
    static final DXImageFormat IMAGE_FORMAT = DXImageFormat.getPng();
    static final int RESOLUTION = 300;
    private static final RangeExportMode EXPORT_MODE = RangeExportMode.ALL;
    private static final String SLIDE_RANGE = "";

    public static void main(String[] args) {
        try {
            List<ByteArrayOutputStream> imageStreams;
            try (InputStream inputResource = PresentationToImage.class.getResourceAsStream("/" + INPUT_FILE_PATH)) {
                if (inputResource == null)
                    throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);
                int[] slideIndexes = getIndexesByRange(EXPORT_MODE, SLIDE_RANGE);
                imageStreams = convertToImages(inputResource, IMAGE_FORMAT, RESOLUTION, slideIndexes);
            }

            if (imageStreams.isEmpty())
                throw new IllegalStateException("Failed to convert presentation to images.");

            File outputDirectory = new File(OUTPUT_DIRECTORY_NAME, OUTPUT_DIRECTORY_DEMO_NAME);
            outputDirectory.mkdirs();

            for (int i = 0; i < imageStreams.size(); i++) {
                File outputFile = new File(outputDirectory, "slide_" + (i + 1) + "." + getImageExtension(IMAGE_FORMAT));
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

    private static int[] getIndexesByRange(RangeExportMode exportMode, String range) {
        switch (exportMode) {
            case FIRST:
                return new int[] { 0 };
            case CUSTOM_RANGE:
                return parseRange(range);
            default:
                return new int[0];
        }
    }

    private static int[] parseRange(String range) {
        if (range == null || range.isBlank()) {
            return new int[0];
        }

        SortedSet<Integer> result = new TreeSet<>();
        String[] tokens = range.split("[,; \\t\\r\\n]+");
        for (String raw : tokens) {
            String part = raw.trim();
            if (part.isEmpty()) {
                continue;
            }

            int dashIndex = part.indexOf('-');
            if (dashIndex > 0) {
                String startText = part.substring(0, dashIndex).trim();
                String endText = part.substring(dashIndex + 1).trim();
                Integer maybeStart = tryParseInt(startText);
                Integer maybeEnd = tryParseInt(endText);
                if (maybeStart != null && maybeEnd != null) {
                    int start = maybeStart;
                    int end = maybeEnd;
                    if (start <= 0 || end <= 0) {
                        continue;
                    }
                    if (end < start) {
                        int swap = start;
                        start = end;
                        end = swap;
                    }
                    for (int value = start; value <= end; value++) {
                        result.add(value - 1);
                    }
                }
                continue;
            }

            Integer maybeSingle = tryParseInt(part);
            if (maybeSingle != null && maybeSingle > 0) {
                result.add(maybeSingle - 1);
            }
        }

        return result.stream().mapToInt(Integer::intValue).toArray();
    }

    private static Integer tryParseInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private enum RangeExportMode {
        FIRST,
        ALL,
        CUSTOM_RANGE
    }
}
