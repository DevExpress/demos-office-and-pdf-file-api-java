package com.devexpress.demos;

import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.pdf.*;

import java.io.*;

public class PdfRemoveFlattenForm {

    static final String INPUT_FILE_PATH = "SharedFiles/PDF/Sample_Form.pdf";
    static final String OUTPUT_DIRECTORY_NAME = "Output";

    static final String[] SAMPLE_FIELDS = {
            "LastName",
            "FirstName",
            "Nationality",
            "Address",
            "PassportNo",
            "VisaNo",
            "Gender",
            "MM",
            "DD",
            "YYYY",
            "FlightNo"
    };

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            InputStream inputResource = PdfRemoveFlattenForm.class.getResourceAsStream("/" + INPUT_FILE_PATH);
            if (inputResource == null)
                throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);
            byte[] inputBytes = inputResource.readAllBytes();
            inputResource.close();

            File outputDirectory = new File(OUTPUT_DIRECTORY_NAME);
            outputDirectory.mkdirs();

            saveDocument(flattenForm(new ByteArrayInputStream(inputBytes), true, SAMPLE_FIELDS),
                    new File(outputDirectory, "flatten_all.pdf"));
            saveDocument(flattenForm(new ByteArrayInputStream(inputBytes), false, new String[] { "LastName", "FirstName" }),
                    new File(outputDirectory, "flatten_selected.pdf"));
            saveDocument(removeForm(new ByteArrayInputStream(inputBytes), true, SAMPLE_FIELDS),
                    new File(outputDirectory, "remove_all.pdf"));
            saveDocument(removeForm(new ByteArrayInputStream(inputBytes), false, new String[] { "LastName", "FirstName" }),
                    new File(outputDirectory, "remove_selected.pdf"));

            System.out.println("Created " + outputDirectory.getAbsolutePath());
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    static void saveDocument(ByteArrayOutputStream baos, File outputFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            baos.writeTo(fos);
        }
    }

    static ByteArrayOutputStream flattenForm(InputStream documentStream, boolean flattenAll, String[] fieldsToFlatten) throws IOException {
        try (PdfDocument doc = new PdfDocument(documentStream)) {
            if (flattenAll)
                doc.flattenFormFields();
            else
                doc.flattenFormFields(fieldsToFlatten);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos;
        }
    }

    static ByteArrayOutputStream removeForm(InputStream documentStream, boolean removeAll, String[] fieldsToRemove) throws IOException {
        try (PdfDocument doc = new PdfDocument(documentStream)) {
            if (removeAll) {
                doc.getFields().clear();
            } else {
                for (String fieldName : fieldsToRemove) {
                    FormField field = doc.getFields().findByName(fieldName);
                    if (field != null)
                        doc.getFields().remove(field);
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos;
        }
    }
}
