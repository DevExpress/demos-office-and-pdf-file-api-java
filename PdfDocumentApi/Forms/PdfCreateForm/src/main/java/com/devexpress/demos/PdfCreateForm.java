package com.devexpress.demos;

import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.pdf.*;
import com.devexpress.drawing.printing.DXPaperKind;
import com.devexpress.system.drawing.RectangleF;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PdfCreateForm {

    static final String OUTPUT_FILE_NAME = "CreatedForm.pdf";
    static final boolean FILL_FROM_FILE = true;
    static final String FORM_DATA_FILE_PATH = "SharedFiles/PDF/Sample_FormData.xml";

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            InputStream xmlStream = null;
            if (FILL_FROM_FILE) {
                xmlStream = PdfCreateForm.class.getResourceAsStream("/" + FORM_DATA_FILE_PATH);
                if (xmlStream == null)
                    throw new FileNotFoundException("Resource not found: " + FORM_DATA_FILE_PATH);
            }

            ByteArrayOutputStream outputStream = createFormDocument(FILL_FROM_FILE, xmlStream);
            if (xmlStream != null) xmlStream.close();

            try (FileOutputStream fos = new FileOutputStream(OUTPUT_FILE_NAME)) {
                outputStream.writeTo(fos);
            }
            System.out.println("Created " + OUTPUT_FILE_NAME);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    static ByteArrayOutputStream createFormDocument(boolean fillFromFile, InputStream xmlStream) throws IOException {
        try (PdfDocument doc = new PdfDocument()) {
            Page page = doc.getPages().add(DXPaperKind.A4);
            float pageW = page.getCropBox().getWidth();
            float pageH = page.getCropBox().getHeight();
            float left = 50f, right = 545f, width = right - left;

            page.addFragment(PathFragment.rectangle(0, pageH - 62, pageW, 62, SolidFill.getDarkBlue()));
            TextFragment headerText = new TextFragment();
            headerText.setText("PASSENGER INFORMATION FORM");
            headerText.setLocation(new com.devexpress.system.drawing.PointF(left, pageH - 26));
            headerText.setForegroundFill(SolidFill.getWhite());
            page.addFragment(headerText);

            page.addFragment(PathFragment.rectangle(left, 722, width, 18, SolidFill.getLightGray()));
            page.addTextFragment("Personal Information", left + 3, 726);
            page.addTextFragment("First Name:", left, 706);
            page.addTextFragment("Last Name:", left + 245, 706);
            addTextField(doc, page, "FirstName", new RectangleF(left, 683, 200, 18), false);
            addTextField(doc, page, "LastName", new RectangleF(left + 245, 683, 200, 18), false);

            page.addTextFragment("Date of Birth:", left, 661);
            page.addTextFragment("Gender:", left + 245, 661);
            addTextField(doc, page, "MM", new RectangleF(left, 638, 38, 18), false);
            page.addTextFragment("/", left + 41, 642);
            addTextField(doc, page, "DD", new RectangleF(left + 48, 638, 38, 18), false);
            page.addTextFragment("/", left + 89, 642);
            addTextField(doc, page, "YYYY", new RectangleF(left + 96, 638, 52, 18), false);

            RadioGroupField genderField = new RadioGroupField("Gender");
            genderField.setButtonStyle(FormFieldButtonStyle.CHECK);
            RadioGroupItemWidgetAnnotation maleWidget = new RadioGroupItemWidgetAnnotation(genderField, "Male",
                    new RectangleF(left + 245, 640, 14, 14));
            maleWidget.setButtonStyle(genderField.getButtonStyle());
            page.getAnnotations().add(maleWidget);
            page.addTextFragment("Male", left + 262, 642);
            RadioGroupItemWidgetAnnotation femaleWidget = new RadioGroupItemWidgetAnnotation(genderField, "Female",
                    new RectangleF(left + 300, 640, 14, 14));
            femaleWidget.setButtonStyle(genderField.getButtonStyle());
            page.getAnnotations().add(femaleWidget);
            page.addTextFragment("Female", left + 317, 642);
            doc.getFields().add(genderField);

            page.addTextFragment("Nationality:", left, 621);
            addComboBoxField(doc, page, "Nationality", new RectangleF(left, 598, 250, 18),
                    "American", "British", "Spanish", "Cypriot", "Hungarian", "Armenian");

            page.addFragment(PathFragment.rectangle(left, 568, width, 18, SolidFill.getLightGray()));
            page.addTextFragment("Travel Documents", left + 3, 572);
            page.addTextFragment("Passport No:", left, 553);
            page.addTextFragment("Visa No:", left + 245, 553);
            addTextField(doc, page, "PassportNo", new RectangleF(left, 530, 200, 18), false);
            addTextField(doc, page, "VisaNo", new RectangleF(left + 245, 530, 200, 18), false);
            page.addTextFragment("Flight No:", left, 513);
            addTextField(doc, page, "FlightNo", new RectangleF(left, 490, 250, 18), false);

            page.addFragment(PathFragment.rectangle(left, 450, width, 18, SolidFill.getLightGray()));
            page.addTextFragment("Address", left + 3, 454);
            page.addTextFragment("Address:", left, 430);
            addTextField(doc, page, "Address", new RectangleF(left, 380, width, 46), true);

            if (fillFromFile && xmlStream != null)
                doc.importFormData(xmlStream, ExportDataFormat.XML);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos;
        }
    }

    static void addTextField(PdfDocument doc, Page page, String name, RectangleF rect, boolean multiline) {
        TextBoxField field = new TextBoxField(name);
        field.setMultiline(multiline);
        TextBoxWidgetAnnotation widget = new TextBoxWidgetAnnotation(field, rect);
        widget.setBackgroundColor(PdfColor.getWhite());
        page.getAnnotations().add(widget);
        doc.getFields().add(field);
    }

    static void addComboBoxField(PdfDocument doc, Page page, String name, RectangleF rect, String... options) {
        ComboBoxField field = new ComboBoxField(name);
        List<ChoiceFieldItem> items = new ArrayList<>();
        for (String option : options)
            items.add(new ChoiceFieldItem(option));
        field.setItems(items);
        ComboBoxWidgetAnnotation widget = new ComboBoxWidgetAnnotation(field, rect);
        widget.setBackgroundColor(PdfColor.getWhite());
        page.getAnnotations().add(widget);
        doc.getFields().add(field);
    }
}
