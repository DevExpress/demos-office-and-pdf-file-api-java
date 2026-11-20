package com.devexpress.demos;

import com.devexpress.docs.pdf.*;
import com.devexpress.drawing.DXImage;
import com.devexpress.office.digitalsignatures.HashAlgorithmType;
import com.devexpress.office.tsp.TsaClient;
import com.devexpress.system.Uri;
import com.devexpress.system.drawing.RectangleF;

import java.io.*;
import java.net.http.HttpClient;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class PdfDigitalSignature {

    static final String INPUT_FILE_PATH = "SharedFiles/PDF/Sample_Alternative.pdf";
    static final String CERTIFICATE_FILE_PATH = "SharedFiles/PDF/Sample_Certificate.pfx";
    static final String SIGNATURE_IMAGE_FILE_PATH = "SharedFiles/DevExpress.png";
    static final String OUTPUT_DIRECTORY_NAME = "Output";
    static final String OUTPUT_DIRECTORY_DEMO_NAME = PdfDigitalSignature.class.getSimpleName();
    static final String OUTPUT_FILE_NAME = "Document.pdf";
    static final String CERTIFICATE_PASSWORD = "dxdemo";
    static final String TSA_URL = "https://www.freetsa.org/tsr";
    static final HashAlgorithm CERTIFICATE_HASH_ALGORITHM = HashAlgorithm.SHA_256;
    static final HashAlgorithmType TSA_HASH_ALGORITHM = HashAlgorithmType.SHA_256;
    static final boolean SHOW_DATE = true;
    static final boolean SHOW_LOCATION = true;
    static final boolean SHOW_NAME = true;
    static final boolean SHOW_REASON = true;
    static final boolean USE_CUSTOM_APPEARANCE = false;

    public static void main(String[] args) {
        try {
            ByteArrayOutputStream outputStream;
            try (InputStream documentResource = PdfDigitalSignature.class.getResourceAsStream("/" + INPUT_FILE_PATH);
                 InputStream certificateResource = PdfDigitalSignature.class.getResourceAsStream("/" + CERTIFICATE_FILE_PATH);
                 InputStream signatureImageResource = PdfDigitalSignature.class.getResourceAsStream("/" + SIGNATURE_IMAGE_FILE_PATH)) {
                if (documentResource == null)
                    throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);
                if (certificateResource == null)
                    throw new FileNotFoundException("Resource not found: " + CERTIFICATE_FILE_PATH);
                if (signatureImageResource == null)
                    throw new FileNotFoundException("Resource not found: " + SIGNATURE_IMAGE_FILE_PATH);

                outputStream = sign(
                        documentResource,
                        certificateResource,
                        signatureImageResource,
                        CERTIFICATE_HASH_ALGORITHM,
                        TSA_HASH_ALGORITHM,
                        USE_CUSTOM_APPEARANCE
                                ? new AppearanceSettings(SHOW_DATE, SHOW_LOCATION, SHOW_NAME, SHOW_REASON)
                                : null);
            }

            File outputDirectory = new File(OUTPUT_DIRECTORY_NAME, OUTPUT_DIRECTORY_DEMO_NAME);
            outputDirectory.mkdirs();

            File outputFile = new File(outputDirectory, OUTPUT_FILE_NAME);

            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                outputStream.writeTo(fos);
            }
            System.out.println("Created " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    static ByteArrayOutputStream sign(
            InputStream documentStream,
            InputStream certificateStream,
            InputStream signatureImageStream,
            HashAlgorithm certificateHashAlgorithm,
            HashAlgorithmType tsaHashAlgorithm,
            AppearanceSettings appearanceSettings) throws IOException {
        try (PdfDocument doc = new PdfDocument(documentStream)) {
            SignatureField signatureField = createSignatureField(doc);
            SignatureWidgetAnnotation signatureWidget = createSignatureWidget(
                    doc.getPages().getLast(), signatureField);

            byte[] certificateBytes = certificateStream.readAllBytes();

            SignatureOptions signatureOptions = new SignatureOptions();
            signatureOptions.setCertificate(certificateBytes, CERTIFICATE_PASSWORD);
            signatureOptions.setCertificationLevel(CertificationLevel.NO_CERTIFICATION);
            signatureOptions.setContactInfo("support@devexpress.com");
            signatureOptions.setHashAlgorithm(certificateHashAlgorithm);
            signatureOptions.setLocation("US");
            signatureOptions.setSignerName("John Doe");
            signatureOptions.setReason("Demo");
            signatureOptions.setSigningTime(OffsetDateTime.now(ZoneOffset.UTC));

            try (DXImage dxImage = DXImage.fromStream(signatureImageStream);
                 HttpClient httpClient = HttpClient.newHttpClient()) {
                if (appearanceSettings == null)
                    buildSignatureAppearance(signatureWidget, dxImage);
                else
                    buildSignatureAppearance(signatureWidget, dxImage, signatureOptions, appearanceSettings);

                TsaClient tsaClient = new TsaClient(new Uri(TSA_URL), tsaHashAlgorithm, httpClient);

                Pkcs7DocumentSigner documentSigner = new Pkcs7DocumentSigner(signatureOptions, tsaClient);
                doc.addSignature(signatureField, documentSigner);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                doc.save(outputStream);
                return outputStream;
            }
        }
    }

    static SignatureField createSignatureField(PdfDocument document) {
        SignatureField signatureField = new SignatureField("SignatureField");
        document.getFields().add(signatureField);
        return signatureField;
    }

    static SignatureWidgetAnnotation createSignatureWidget(Page page, SignatureField signatureField) {
        float pageWidth = (float) page.getWidth();
        float pageHeight = (float) page.getHeight();
        RectangleF signatureFieldBounds = new RectangleF(
                pageWidth / 3,
                pageHeight / 10,
                pageWidth / 3 * 1.5f,
                pageHeight / 10);
        SignatureWidgetAnnotation signatureFieldAnnotation = new SignatureWidgetAnnotation(signatureField, signatureFieldBounds);
        page.getAnnotations().add(signatureFieldAnnotation);
        return signatureFieldAnnotation;
    }

    static void buildSignatureAppearance(SignatureWidgetAnnotation widget, DXImage image) {
        float boxWidth = widget.getBounds().getWidth();
        float boxHeight = widget.getBounds().getHeight();
        float scale = Math.min((boxWidth - 4) / image.getWidth(), (boxHeight - 4) / image.getHeight());
        float displayWidth = image.getWidth() * scale;
        float displayHeight = image.getHeight() * scale;

        ImageFragment imageFragment = widget.addImageFragment(image, 2, (boxHeight - displayHeight) / 2);
        imageFragment.setScaleX(displayWidth);
        imageFragment.setScaleY(displayHeight);
    }

    static void buildSignatureAppearance(SignatureWidgetAnnotation widget, DXImage image, SignatureOptions signatureOptions,
            AppearanceSettings appearanceSettings) {
        float boxWidth = widget.getBounds().getWidth();
        float boxHeight = widget.getBounds().getHeight();
        float imageAreaWidth = boxWidth / 3;
        float scale = Math.min((imageAreaWidth - 4) / image.getWidth(), (boxHeight - 4) / image.getHeight());
        float displayWidth = image.getWidth() * scale;
        float displayHeight = image.getHeight() * scale;

        ImageFragment imageFragment = widget.addImageFragment(image, 2, (boxHeight - displayHeight) / 2);
        imageFragment.setScaleX(displayWidth);
        imageFragment.setScaleY(displayHeight);

        float textX = imageAreaWidth + 2;
        float lineHeight = 12;
        float textY = Math.max(lineHeight, boxHeight - lineHeight * 2);
        if (appearanceSettings.showName) {
            widget.addTextFragment("Digitally signed by " + signatureOptions.getSignerName(), textX, textY);
            textY -= lineHeight;
        }
        if (appearanceSettings.showReason) {
            widget.addTextFragment("Reason: " + signatureOptions.getReason(), textX, textY);
            textY -= lineHeight;
        }
        if (appearanceSettings.showLocation) {
            widget.addTextFragment("Location: " + signatureOptions.getLocation(), textX, textY);
            textY -= lineHeight;
        }
        if (appearanceSettings.showDate) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss xxx");
            widget.addTextFragment("Date: " + signatureOptions.getSigningTime().format(formatter), textX, textY);
        }
    }

    static class AppearanceSettings {
        final boolean showDate;
        final boolean showLocation;
        final boolean showName;
        final boolean showReason;

        AppearanceSettings(boolean showDate, boolean showLocation, boolean showName, boolean showReason) {
            this.showDate = showDate;
            this.showLocation = showLocation;
            this.showName = showName;
            this.showReason = showReason;
        }
    }
}