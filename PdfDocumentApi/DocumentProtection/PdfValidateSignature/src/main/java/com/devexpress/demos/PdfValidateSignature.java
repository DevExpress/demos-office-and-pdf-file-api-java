package com.devexpress.demos;

import com.devexpress.docs.pdf.*;

import java.io.*;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PdfValidateSignature {

    static final String INPUT_FILE_PATH = "SharedFiles/PDF/Sample_Signed.pdf";
    static final String OUTPUT_DIRECTORY_NAME = "Output";
    static final String OUTPUT_DIRECTORY_DEMO_NAME = PdfValidateSignature.class.getSimpleName();
    static final String OUTPUT_FILE_NAME = "Validation_Result.txt";
    static final boolean VALIDATE_CRL_OCSP = true;
    static final boolean VALIDATE_LTV = true;

    public static void main(String[] args) {
        try {
            byte[] documentBytes;
            try (InputStream inputResource = PdfValidateSignature.class.getResourceAsStream("/" + INPUT_FILE_PATH)) {
                if (inputResource == null)
                    throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);
                documentBytes = inputResource.readAllBytes();
            }

            ByteArrayOutputStream outputStream = validateSignatures(documentBytes, VALIDATE_CRL_OCSP, VALIDATE_LTV);

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

    static ByteArrayOutputStream validateSignatures(byte[] documentBytes, boolean validateCrlOcsp, boolean validateLtv) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream));

        List<SignatureInfo> signatureInfo = new ArrayList<>();
        boolean hasSignatures;
        try (PdfDocument doc = new PdfDocument(new ByteArrayInputStream(documentBytes))) {
            hasSignatures = doc.tryGetSignatures(signatureInfo);
        }

        if (hasSignatures) {
            writer.println("Found " + signatureInfo.size() + " signatures");
            writer.println();

            PdfSignatureValidator signatureValidator = new PdfSignatureValidator(new ByteArrayInputStream(documentBytes));

            for (int i = 0; i < signatureInfo.size(); i++) {
                validateSignature(signatureValidator, signatureInfo.get(i), i, validateCrlOcsp, writer);
            }

            if (validateLtv && !signatureInfo.isEmpty())
                validateLtvInformation(signatureValidator, writer);
        } else {
            writer.println("Signatures not found");
            writer.println();
        }
        writer.flush();
        return outputStream;
    }

    static void validateSignature(PdfSignatureValidator signatureValidator, SignatureInfo signature, int signatureIndex,
            boolean validateCrlOcsp, PrintWriter writer) {
        writer.println("Signature #" + signatureIndex);
        writer.println("===");
        writer.println("Field Name : " + signature.getField().getName());
        writer.println("Signer name : " + signature.getSignerName());

        Pkcs7Signature pkcs7 = signatureValidator.getPkcs7Signature(signature.getField().getName());
        writer.println("Valid : " + pkcs7.getIsSignatureVerified());

        X509Certificate certificate = pkcs7.getSignatureCertificate();
        writer.println("Certificate Issuer : " + certificate.getIssuerX500Principal().getName());
        writer.println("Certificate Valid : " + isCertificateValid(certificate));

        OffsetDateTime timestampDate = pkcs7.getTimeStampDate();
        writer.println("Has Timestamp : " + (timestampDate != null));
        if (timestampDate != null) {
            writer.println("Timestamp : " + timestampDate);
            writer.println("Timestamp Valid : " + pkcs7.getIsTimeStampVerified());
            writer.println("Document Timestamp : " + pkcs7.getIsDocumentTimeStamp());
        }

        if (validateCrlOcsp)
            validateCrlOcspInformation(pkcs7, writer);

        writer.println();
    }

    static boolean isCertificateValid(X509Certificate certificate) {
        try {
            certificate.checkValidity();
            return true;
        } catch (CertificateExpiredException | CertificateNotYetValidException e) {
            return false;
        }
    }

    static void validateCrlOcspInformation(Pkcs7Signature pkcs7, PrintWriter writer) {
        writer.println();
        writer.println("CRL/OCSP Information");
        writer.println("CRL Embedded : " + (pkcs7.getCrlList() != null));
        writer.println("OCSP Embedded : " + (pkcs7.getOcspList() != null));

        boolean tryFetchCrlOnline = false;
        boolean tryFetchOcspOnline = false;

        SignatureCertificateRevocationStatus crlOcspResult = pkcs7.checkCertificateRevocation(
                OffsetDateTime.now(ZoneOffset.UTC), tryFetchCrlOnline, tryFetchOcspOnline);

        writer.println("Certificate Revoked : " + crlOcspResult.getIsCrlRevoked());
        if (crlOcspResult.getIsCrlRevoked())
            writer.println("CRL Found Online : " + crlOcspResult.getIsCrlFoundOnline());
        writer.println("OCSP Response Status : " + crlOcspResult.getOcspRevocationStatus());
        if (crlOcspResult.getOcspRevocationStatus() != OcspRevocationStatus.NONE)
            writer.println("OCSP found online : " + crlOcspResult.getIsOcspFoundOnline());
    }

    static void validateLtvInformation(PdfSignatureValidator signatureValidator, PrintWriter writer) {
        writer.println("===");
        writer.println("LTV Information");
        writer.println("===");
        writer.println();

        boolean verifyEntireCertificateChain = true;
        boolean tryFetchCrlOnline = false;
        boolean tryFetchOcspOnline = false;

        Iterable<LtvVerificationStatus> ltvResult = signatureValidator.verifyLtv(
                verifyEntireCertificateChain, tryFetchCrlOnline, tryFetchOcspOnline);
        for (LtvVerificationStatus entry : ltvResult) {
            writer.println("Signature Name : " + entry.getSignatureFieldName());
            Map<X509Certificate, SignatureCertificateRevocationStatus> statuses = entry.getCertificateRevocationStatuses();
            writer.println("Certificates In Chain : " + statuses.size());

            for (Map.Entry<X509Certificate, SignatureCertificateRevocationStatus> revocation : statuses.entrySet()) {
                writer.println("Certificate " + revocation.getKey().getSubjectX500Principal().getName());
                writer.println("Certificate revoked : " + revocation.getValue().getIsCrlRevoked());
                if (revocation.getValue().getIsCrlRevoked())
                    writer.println("CRL found online : " + revocation.getValue().getIsCrlFoundOnline());
                writer.println("OCSP Response Status : " + revocation.getValue().getOcspRevocationStatus());
                if (revocation.getValue().getOcspRevocationStatus() != OcspRevocationStatus.NONE)
                    writer.println("OCSP found online : " + revocation.getValue().getIsOcspFoundOnline());
                writer.println();
            }
        }

        writer.println();
    }
}