package com.devexpress.demos;

import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.presentation.DocumentFormat;
import com.devexpress.docs.presentation.Presentation;
import com.devexpress.docs.presentation.Slide;

import java.io.*;
import java.util.*;

public class PresentationSplit {

    static final String INPUT_FILE_PATH = "SharedFiles/Sample.pptx";
    static final String OUTPUT_DIRECTORY_NAME = "Split_Presentation";
    static final String SLIDE_RANGE = "1-2";
    static final boolean IS_SINGLE_FILE_OUTPUT = false;
    static final boolean IS_PDF_OUTPUT = false;
    static final DocumentFormat OUTPUT_FORMAT = DocumentFormat.PPTX;

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            InputStream inputResource = PresentationSplit.class.getResourceAsStream("/" + INPUT_FILE_PATH);
            if (inputResource == null)
                throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);

            File outputDirectory = new File(OUTPUT_DIRECTORY_NAME);
            if (outputDirectory.exists())
                deleteDirectory(outputDirectory);
            outputDirectory.mkdirs();

            int[] slideIndexes = parseSlideRange(SLIDE_RANGE);
            List<ByteArrayOutputStream> slideStreams = extractSlides(inputResource, slideIndexes, IS_SINGLE_FILE_OUTPUT, IS_PDF_OUTPUT, OUTPUT_FORMAT);
            inputResource.close();

            if (slideStreams.isEmpty())
                throw new IllegalStateException("Failed to split presentation.");

            for (int index = 0; index < slideStreams.size(); index++) {
                String extension = IS_PDF_OUTPUT ? "pdf" : "pptx";
                File outputFile = new File(outputDirectory, "slide_" + (index + 1) + "." + extension);
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    slideStreams.get(index).writeTo(fos);
                }
            }
            System.out.println("Created " + outputDirectory.getAbsolutePath());
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    static int[] parseSlideRange(String slideRange) {
        if (slideRange == null || slideRange.isBlank()) return new int[0];
        SortedSet<Integer> result = new TreeSet<>();
        String[] tokens = slideRange.split("[,;\\s]+");
        for (String raw : tokens) {
            String part = raw.trim();
            if (part.isEmpty()) continue;
            int dashIndex = part.indexOf('-');
            if (dashIndex > 0) {
                try {
                    int start = Integer.parseInt(part.substring(0, dashIndex).trim());
                    int end = Integer.parseInt(part.substring(dashIndex + 1).trim());
                    if (start <= 0 || end <= 0) continue;
                    if (end < start) { int tmp = start; start = end; end = tmp; }
                    for (int v = start; v <= end; v++) result.add(v - 1);
                } catch (NumberFormatException ignored) {}
                continue;
            }
            try {
                int single = Integer.parseInt(part);
                if (single > 0) result.add(single - 1);
            } catch (NumberFormatException ignored) {}
        }
        return result.stream().mapToInt(Integer::intValue).toArray();
    }

    static List<ByteArrayOutputStream> extractSlides(InputStream inputStream, int[] slideIndexes,
            boolean isSingleFileOutput, boolean isPdfOutput, DocumentFormat documentFormat) throws IOException {
        try (Presentation presentation = new Presentation(inputStream)) {
            return isSingleFileOutput
                ? extractSlidesSingleFile(slideIndexes, isPdfOutput, documentFormat, presentation)
                : extractSlidesDifferentFiles(slideIndexes, isPdfOutput, documentFormat, presentation);
        }
    }

    static List<ByteArrayOutputStream> extractSlidesDifferentFiles(int[] slideIndexes, boolean isPdfOutput,
            DocumentFormat documentFormat, Presentation presentation) throws IOException {
        List<ByteArrayOutputStream> outputStreams = new ArrayList<>();
        List<Slide> slides = presentation.getSlides();
        int[] effectiveIndexes = slideIndexes.length == 0
            ? java.util.stream.IntStream.range(0, slides.size()).toArray()
            : slideIndexes;
        for (int slideIndex : effectiveIndexes) {
            if (slideIndex < 0 || slideIndex >= slides.size()) continue;
            try (Presentation single = new Presentation()) {
                single.getSlides().clear();
                single.setSlideSize(presentation.getSlideSize());
                single.getSlides().add(slides.get(slideIndex));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                if (isPdfOutput) single.exportToPdf(baos);
                else single.saveDocument(baos, documentFormat);
                outputStreams.add(baos);
            }
        }
        return outputStreams;
    }

    static List<ByteArrayOutputStream> extractSlidesSingleFile(int[] slideIndexes, boolean isPdfOutput,
            DocumentFormat documentFormat, Presentation presentation) throws IOException {
        List<Slide> slides = presentation.getSlides();
        Set<Integer> keepSet = new HashSet<>();
        if (slideIndexes.length == 0) {
            for (int i = 0; i < slides.size(); i++) keepSet.add(i);
        } else {
            for (int idx : slideIndexes) keepSet.add(idx);
        }
        for (int index = slides.size() - 1; index >= 0; index--) {
            if (!keepSet.contains(index)) presentation.getSlides().remove(index);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (isPdfOutput) presentation.exportToPdf(baos);
        else presentation.saveDocument(baos, documentFormat);
        return Collections.singletonList(baos);
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
