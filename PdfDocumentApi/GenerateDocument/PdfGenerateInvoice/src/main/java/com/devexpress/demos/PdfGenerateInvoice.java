package com.devexpress.demos;

import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.pdf.*;
import com.devexpress.drawing.printing.DXPaperKind;
import com.devexpress.system.drawing.PointF;

import java.io.*;
import java.util.*;

public class PdfGenerateInvoice {

    static final String OUTPUT_FILE_NAME = "GeneratedInvoice.pdf";
    static final boolean INCLUDE_ZUGFERD = true;
    static final String XML_FILE_PATH = "SharedFiles/PDF/Northwind_Invoice.xml";
    static final int INVOICE_NUMBER = 10643;

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            InputStream xmlStream = null;
            if (INCLUDE_ZUGFERD) {
                xmlStream = PdfGenerateInvoice.class.getResourceAsStream("/" + XML_FILE_PATH);
                if (xmlStream == null)
                    throw new FileNotFoundException("Resource not found: " + XML_FILE_PATH);
            }

            ByteArrayOutputStream outputStream = generateInvoiceDocument(
                    Locale.forLanguageTag("en-US"), INVOICE_NUMBER, INCLUDE_ZUGFERD, xmlStream);
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

    static ByteArrayOutputStream generateInvoiceDocument(Locale documentLocale, int invoiceNumber,
            boolean includeZugferd, InputStream xmlStream) throws IOException {
        String title = "Invoice " + invoiceNumber;

        SolidFill darkFill = new SolidFill(PdfColor.fromRgb(0x2F, 0x3B, 0x4A));
        SolidFill mutedFill = new SolidFill(PdfColor.fromRgb(0x7A, 0x87, 0x94));
        Outline rowDivider = Outline.create(new SolidFill(PdfColor.fromRgb(0xE1, 0xE5, 0xEA)), 0.5);
        Outline heavyBorder = Outline.create(new SolidFill(PdfColor.fromRgb(0x9A, 0xA5, 0xB1)), 2.0);

        try (PdfDocument doc = new PdfDocument()) {
            ViewerPreferences viewerPrefs = new ViewerPreferences();
            viewerPrefs.setDisplayDocTitle(true);
            doc.setViewerPreferences(viewerPrefs);
            doc.setLanguageCulture(documentLocale);
            DocumentMetadata metadata = new DocumentMetadata();
            metadata.setXmp(new XmpMetadata());
            doc.setMetadata(metadata);
            MarkInfo markInfo = new MarkInfo();
            markInfo.setMarked(true);
            doc.setMarkInfo(markInfo);

            doc.getMetadata().getXmp().getXmpPdfUASchema().setPart(new XmpInteger(1));
            doc.getMetadata().getXmp().getXmpPdfAExtensions().register(new XmpPdfAExtensionDescriptor(
                    "http://www.aiim.org/pdfua/ns/id/",
                    "pdfuaid",
                    "PDF/UA identification schema",
                    Arrays.asList(new XmpPdfAExtensionProperty("part", "Indicates which part of ISO 14289 is followed.", "Integer"))
            ));
            doc.getMetadata().getXmp().getXmpPdfASchema().setPart(new XmpInteger(3));
            doc.getMetadata().getXmp().getXmpPdfASchema().setConformance(new XmpString("B"));
            doc.getMetadata().getXmp().getXmpDublinCoreSchema().getTitle().add(documentLocale.toLanguageTag(), title);

            StructureElement root = doc.getStructureTree().addChildElement(Pdf17StructureType.DOCUMENT);
            root.setLanguageCulture(documentLocale);
            root.setTitle(title);
            Page page = doc.getPages().add(DXPaperKind.A4);

            StructureElement headerSection = root.addChildElement(Pdf17StructureType.SECT);
            MarkedContentGroup artifactGroup = new MarkedContentGroup("Artifact");
            page.addFragment(artifactGroup);
            artifactGroup.getFragments().add(PathFragment.rectangle(0, page.getCropBox().getHeight() - 130,
                    page.getCropBox().getWidth(), 130, new SolidFill(PdfColor.fromRgb(0xDF, 0xE3, 0xE8))));

            float currentX = page.getCropBox().getLeft() + 60f;

            headerSection.addChildElement(Pdf17StructureType.H1).addFragment(page,
                    textFrag("Northwind Traders", currentX, page.getCropBox().getHeight() - 40, darkFill, 14));
            headerSection.addChildElement(Pdf17StructureType.P).addFragment(page,
                    textFrag("One Portals Way, Twin Points WA, 98156", currentX, page.getCropBox().getHeight() - 58, null, 0));
            headerSection.addChildElement(Pdf17StructureType.P).addFragment(page,
                    textFrag("1-206-555-1417", currentX, page.getCropBox().getHeight() - 74, null, 0));
            headerSection.addChildElement(Pdf17StructureType.P).addFragment(page,
                    textFrag("northwind@mail.com", currentX, page.getCropBox().getHeight() - 90, null, 0));
            headerSection.addChildElement(Pdf17StructureType.P).addFragment(page,
                    textFrag("www.northwind.com", currentX, page.getCropBox().getHeight() - 106, null, 0));
            headerSection.addChildElement(Pdf17StructureType.H1).addFragment(page,
                    textFrag("INVOICE", 393f, page.getCropBox().getHeight() - 68, darkFill, 28));

            float metaLabelX = page.getCropBox().getRight() - 200f;
            float metaValX = page.getCropBox().getRight() - 90f;
            StructureElement metaSection = headerSection.addChildElement(Pdf17StructureType.SECT);
            StructureMarkedContentReference ref1 = metaSection.addChildElement(Pdf17StructureType.P).addFragment(page,
                    textFrag("Invoice №:", metaLabelX, page.getCropBox().getHeight() - 90, mutedFill, 0));
            ref1.addFragment(textFrag(String.valueOf(invoiceNumber), metaValX, page.getCropBox().getHeight() - 90, null, 0));
            StructureMarkedContentReference ref2 = metaSection.addChildElement(Pdf17StructureType.P).addFragment(page,
                    textFrag("Invoice Date:", metaLabelX, page.getCropBox().getHeight() - 106, mutedFill, 0));
            ref2.addFragment(textFrag("04-21-26", metaValX, page.getCropBox().getHeight() - 106, null, 0));

            StructureElement billSection = root.addChildElement(Pdf17StructureType.SECT);
            billSection.addChildElement(Pdf17StructureType.H2).addFragment(page,
                    textFrag("Bill to:", page.getCropBox().getLeft() + 50, page.getCropBox().getHeight() - 155, null, 0));

            String[][] billRows = {
                    {"Company:", "Alfreds Futterkiste"},
                    {"Contact Name:", "Maria Anders"},
                    {"Address:", "Obere Str. 57, Berlin, Germany, 12209"},
                    {"Phone:", "030-0074321"},
                    {"Mail:", "alfredsfutterkiste@mail.com"}
            };
            float billValueX = page.getCropBox().getLeft() + 150f;
            float billRowY = page.getCropBox().getHeight() - 175f;
            for (String[] row : billRows) {
                StructureMarkedContentReference billRef = billSection.addChildElement(Pdf17StructureType.P).addFragment(page,
                        textFrag(row[0], page.getCropBox().getLeft() + 50, billRowY, mutedFill, 0));
                billRef.addFragment(textFrag(row[1], billValueX, billRowY, null, 0));
                billRowY -= 16f;
            }

            float tableTop = billRowY - 20f;
            float rowHeight = 20f;
            float tableWidth = page.getCropBox().getRight() - page.getCropBox().getLeft() - 100f;
            StructureElement table = root.addChildElement(Pdf17StructureType.TABLE);
            StructureElement tableHead = table.addChildElement(Pdf17StructureType.T_HEAD);
            artifactGroup.getFragments().add(PathFragment.rectangle(page.getCropBox().getLeft() + 50,
                    tableTop - rowHeight, tableWidth, rowHeight, SolidFill.getLightGray()));
            artifactGroup.getFragments().add(PathFragment.line(page.getCropBox().getLeft() + 50,
                    tableTop - rowHeight, page.getCropBox().getRight() - 50f, tableTop - rowHeight, heavyBorder));

            StructureElement headerRow = tableHead.addChildElement(Pdf17StructureType.TR);
            float[] columnPositions = {55f, 95f, 305f, 375f, 435f, 495f};
            String[] headers = {"#", "Product Name", "Unit Price", "Quantity", "Discount", "Total"};
            for (int i = 0; i < headers.length; i++) {
                StructureElement headerCell = headerRow.addChildElement(Pdf17StructureType.TH);
                TableAttribute tableAttr = new TableAttribute();
                tableAttr.setScope(TableScope.COLUMN);
                headerCell.getAttributes().add(tableAttr);
                headerCell.addFragment(page, textFrag(headers[i], columnPositions[i], tableTop - rowHeight + 5, darkFill, 0));
            }

            String[][] items = {
                    {"01", "Rössle Sauerkraut", "$45.60", "15", "$3.75", "$683.75"},
                    {"02", "Chartreuse verte", "$18.00", "21", "$5.25", "$377.75"},
                    {"03", "Spegesild", "$12.00", "2", "$0.50", "$23.75"}
            };

            StructureElement tableBody = table.addChildElement(Pdf17StructureType.T_BODY);
            float rowY = tableTop - 2 * rowHeight;
            for (String[] item : items) {
                StructureElement row = tableBody.addChildElement(Pdf17StructureType.TR);
                for (int i = 0; i < item.length; i++)
                    row.addChildElement(Pdf17StructureType.TD).addFragment(page,
                            textFrag(item[i], columnPositions[i], rowY + 5, null, 0));
                artifactGroup.getFragments().add(PathFragment.line(page.getCropBox().getLeft() + 50,
                        rowY, page.getCropBox().getRight() - 50f, rowY, rowDivider));
                rowY -= rowHeight;
            }

            float totalY = rowY + rowHeight - 25f;
            float totalLabelX = columnPositions[3];
            float totalValueX = columnPositions[5];
            StructureElement totalSection = root.addChildElement(Pdf17StructureType.SECT);
            artifactGroup.getFragments().add(PathFragment.line(totalLabelX - 5f, totalY - 22f,
                    page.getCropBox().getRight() - 50f, totalY - 22f, heavyBorder));
            totalSection.addChildElement(Pdf17StructureType.P).addFragment(page,
                    textFrag("Sub Total:", totalLabelX, totalY, null, 0))
                    .addFragment(textFrag("$1086.00", totalValueX, totalY, null, 0));
            totalSection.addChildElement(Pdf17StructureType.P).addFragment(page,
                    textFrag("Discount Total:", totalLabelX, totalY - 16f, null, 0))
                    .addFragment(textFrag("$9.50", totalValueX, totalY - 16f, null, 0));
            float grandTotalY = totalY - 38f;
            totalSection.addChildElement(Pdf17StructureType.P).addFragment(page,
                    textFrag("Grand Total:", totalLabelX, grandTotalY, darkFill, 0))
                    .addFragment(textFrag("$1076.50", totalValueX, grandTotalY, darkFill, 0));

            if (includeZugferd && xmlStream != null)
                doc.attachZugferdInvoice(xmlStream);

            SaveOptions saveOptions = new SaveOptions();
            saveOptions.setSyncMetadata(true);
            saveOptions.setUpdateCreatedAt(true);
            saveOptions.setUpdateModifiedAt(true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos, saveOptions);
            return baos;
        }
    }

    static TextFragment textFrag(String text, float x, float y, SolidFill fill, double fontSize) {
        TextFragment frag = new TextFragment();
        frag.setText(text);
        frag.setLocation(new PointF(x, y));
        if (fill != null)
            frag.setForegroundFill(fill);
        if (fontSize > 0)
            frag.setFontSize(fontSize);
        return frag;
    }
}
