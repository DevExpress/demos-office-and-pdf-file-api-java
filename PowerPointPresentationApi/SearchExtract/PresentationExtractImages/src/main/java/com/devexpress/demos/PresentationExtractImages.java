package com.devexpress.demos;

import com.devexpress.docs.OfficeImage;
import com.devexpress.docs.OfficeImageBase;
import com.devexpress.docs.presentation.*;
import com.devexpress.drawing.DXImage;
import com.devexpress.drawing.DXImageFormat;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PresentationExtractImages {

    static final String INPUT_FILE_PATH = "SharedFiles/Sample.pptx";
    static final String OUTPUT_DIRECTORY_NAME = "Output";
    static final String OUTPUT_DIRECTORY_DEMO_NAME = PresentationExtractImages.class.getSimpleName();
    static final DXImageFormat IMAGE_FORMAT = DXImageFormat.getPng();
    private static final RangeExportMode IMAGE_EXTRACT_MODE = RangeExportMode.ALL;
    private static final String SLIDE_RANGE = "";

    public static void main(String[] args) {
        try {
            File outputDirectory = new File(OUTPUT_DIRECTORY_NAME, OUTPUT_DIRECTORY_DEMO_NAME);
            recreateDirectory(outputDirectory);

            List<ByteArrayOutputStream> imageStreams;
            try (InputStream inputResource = PresentationExtractImages.class.getResourceAsStream("/" + INPUT_FILE_PATH)) {
                if (inputResource == null)
                    throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);
                int[] slideRange = getIndexesByRange(IMAGE_EXTRACT_MODE, SLIDE_RANGE);
                imageStreams = extractImages(inputResource, IMAGE_FORMAT, slideRange);
            }

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
            e.printStackTrace();
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

    private static void recreateDirectory(File directory) throws IOException {
        if (directory.exists()) {
            List<Path> paths;
            try (Stream<Path> walkedPaths = Files.walk(directory.toPath())) {
                paths = walkedPaths.sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            }
            for (Path path : paths) {
                Files.delete(path);
            }
        }
        Files.createDirectories(directory.toPath());
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
