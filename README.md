# DevExpress Office & PDF File API for Java: Demos

[DevExpress Office & PDF File API for Java](https://www.devexpress.com/products/java/office-pdf-file-api/) allows you to create, modify, and export PowerPoint presentations, PDF files, and barcodes.

This repository contains sample console applications. Each project demonstrates a specific API feature and includes a standalone Java application that you can use as a starting point for your own projects.

> [!Note]
> DevExpress Office & PDF File API for Java is currently available as a Community Technology Preview (CTP). 

## Included Demos

### PDF Document API for Java
 
| Demo | Description | Project |
|------|-------------|--------|
| PDF Generate Invoice | Creates an invoice PDF document from data and layout elements. | [PdfGenerateInvoice.java](PdfDocumentApi/GenerateDocument/PdfGenerateInvoice/src/main/java/com/devexpress/demos/PdfGenerateInvoice.java) |
| PDF Create Form | Creates an interactive PDF form with fillable fields. | [PdfCreateForm.java](PdfDocumentApi/Forms/PdfCreateForm/src/main/java/com/devexpress/demos/PdfCreateForm.java) |
| PDF Annotations | Adds annotations to a PDF document. | [PdfAnnotations.java](PdfDocumentApi/DocumentElements/PdfAnnotations/src/main/java/com/devexpress/demos/PdfAnnotations.java) |
| PDF Attachments | Adds file attachments to a PDF document. | [PdfAttachments.java](PdfDocumentApi/DocumentElements/PdfAttachments/src/main/java/com/devexpress/demos/PdfAttachments.java) |
| PDF Apply Watermark | Applies a watermark to PDF pages. | [PdfApplyWatermark.java](PdfDocumentApi/DocumentProtection/PdfApplyWatermark/src/main/java/com/devexpress/demos/PdfApplyWatermark.java) |
| PDF Encrypt Password | Protects a PDF document with password encryption. | [PdfEncryptPassword.java](PdfDocumentApi/DocumentProtection/PdfEncryptPassword/src/main/java/com/devexpress/demos/PdfEncryptPassword.java) |
| PDF Redaction | Redacts sensitive content in a PDF document. | [PdfRedaction.java](PdfDocumentApi/DocumentProtection/PdfRedaction/src/main/java/com/devexpress/demos/PdfRedaction.java) |
| PDF Merge Documents | Merges multiple PDF documents into one file. | [PdfMergeDocuments.java](PdfDocumentApi/MergeSplit/PdfMergeDocuments/src/main/java/com/devexpress/demos/PdfMergeDocuments.java) |
| PDF Split Documents | Splits a PDF document into separate files. | [PdfSplitDocuments.java](PdfDocumentApi/MergeSplit/PdfSplitDocuments/src/main/java/com/devexpress/demos/PdfSplitDocuments.java) |
| PDF Manage Pages | Reorders, inserts, or removes pages in a PDF document. | [PdfManagePages.java](PdfDocumentApi/ManagePages/PdfManagePages/src/main/java/com/devexpress/demos/PdfManagePages.java) | 
| PDF Clear Pages | Clears page content from a PDF document. | [PdfClearPages.java](PdfDocumentApi/ManagePages/PdfClearPages/src/main/java/com/devexpress/demos/PdfClearPages.java) | 
| PDF Extract Images | Extracts images from a PDF document. | [PdfExtractImages.java](PdfDocumentApi/SearchExtract/PdfExtractImages/src/main/java/com/devexpress/demos/PdfExtractImages.java) | 
| PDF Find Highlight | Finds text in a PDF document and highlights matches. | [PdfFindHighlight.java](PdfDocumentApi/SearchExtract/PdfFindHighlight/src/main/java/com/devexpress/demos/PdfFindHighlight.java) |
| PDF XMP Metadata | Reads and updates XMP metadata in a PDF document. | [PdfXMPMetadata.java](PdfDocumentApi/SearchExtract/PdfXMPMetadata/src/main/java/com/devexpress/demos/PdfXMPMetadata.java) |

### PowerPoint Presentation API for Java

| Demo | Description | Project |
|------|-------------|--------|
| Presentation To Image | Exports presentation slides to image files. | [PresentationToImage.java](PowerPointPresentationApi/DocumentConversion/PresentationToImage/src/main/java/com/devexpress/demos/PresentationToImage.java) | 
| Presentation To PDF | Exports a PowerPoint presentation to PDF. | [PresentationToPdf.java](PowerPointPresentationApi/DocumentConversion/PresentationToPdf/src/main/java/com/devexpress/demos/PresentationToPdf.java) | 
| Presentation To Presentation | Converts a presentation to another presentation format. | [PresentationToPresentation.java](PowerPointPresentationApi/DocumentConversion/PresentationToPresentation/src/main/java/com/devexpress/demos/PresentationToPresentation.java) | 
| Presentation Encrypt Password | Protects a presentation with password encryption. | [PresentationEncryptPassword.java](PowerPointPresentationApi/DocumentProtection/PresentationEncryptPassword/src/main/java/com/devexpress/demos/PresentationEncryptPassword.java) |
| Presentation Notes | Adds or edits speaker notes in a presentation. | [PresentationNotes.java](PowerPointPresentationApi/DocumentElements/PresentationNotes/src/main/java/com/devexpress/demos/PresentationNotes.java) | 
| Presentation Shapes | Creates and customizes shapes on presentation slides. | [PresentationShapes.java](PowerPointPresentationApi/DocumentElements/PresentationShapes/src/main/java/com/devexpress/demos/PresentationShapes.java) |
| Presentation Tables | Creates and formats tables in presentation slides. | [PresentationTables.java](PowerPointPresentationApi/DocumentElements/PresentationTables/src/main/java/com/devexpress/demos/PresentationTables.java) | 
| Presentation Merge | Merges multiple PowerPoint presentations into one file. | [PresentationMerge.java](PowerPointPresentationApi/MergeSplit/PresentationMerge/src/main/java/com/devexpress/demos/PresentationMerge.java) |
| Presentation Split | Splits a PowerPoint presentation into separate files. | [PresentationSplit.java](PowerPointPresentationApi/MergeSplit/PresentationSplit/src/main/java/com/devexpress/demos/PresentationSplit.java) |
| Presentation Extract Images | Extracts images from presentation slides. | [PresentationExtractImages.java](PowerPointPresentationApi/SearchExtract/PresentationExtractImages/src/main/java/com/devexpress/demos/PresentationExtractImages.java) |
| Presentation Extract Text | Extracts text from a PowerPoint presentation. | [PresentationExtractText.java](PowerPointPresentationApi/SearchExtract/PresentationExtractText/src/main/java/com/devexpress/demos/PresentationExtractText.java) |
| Presentation Find Replace | Finds and replaces text in a PowerPoint presentation. | [PresentationFindReplace.java](PowerPointPresentationApi/SearchExtract/PresentationFindReplace/src/main/java/com/devexpress/demos/PresentationFindReplace.java) |
| Presentation Metadata | Reads and updates presentation metadata. | [PresentationMetadata.java](PowerPointPresentationApi/SearchExtract/PresentationMetadata/src/main/java/com/devexpress/demos/PresentationMetadata.java) |

### Barcode Generation API for Java

| Demo | Description | Project |
|------|-------------|--------|
| Barcode Aztec | Generates an Aztec barcode image. | [BarcodeAztec.java](BarcodeGenerationApi/BarcodeAztec/src/main/java/com/devexpress/demos/BarcodeAztec.java) |
| Barcode Codabar | Generates a Codabar barcode image. | [BarcodeCodabar.java](BarcodeGenerationApi/BarcodeCodabar/src/main/java/com/devexpress/demos/BarcodeCodabar.java) |
| Barcode Code 11 | Generates a Code 11 barcode image. | [BarcodeCode11.java](BarcodeGenerationApi/BarcodeCode11/src/main/java/com/devexpress/demos/BarcodeCode11.java) |
| Barcode Code 128 | Generates a Code 128 barcode image. | [BarcodeCode128.java](BarcodeGenerationApi/BarcodeCode128/src/main/java/com/devexpress/demos/BarcodeCode128.java) |
| Barcode Code 39 | Generates a Code 39 barcode image. | [BarcodeCode39.java](BarcodeGenerationApi/BarcodeCode39/src/main/java/com/devexpress/demos/BarcodeCode39.java) |
| Barcode Code 39 Extended | Generates an extended Code 39 barcode image. | [BarcodeCode39Extended.java](BarcodeGenerationApi/BarcodeCode39Extended/src/main/java/com/devexpress/demos/BarcodeCode39Extended.java) |
| Barcode Code 93 | Generates a Code 93 barcode image. | [BarcodeCode93.java](BarcodeGenerationApi/BarcodeCode93/src/main/java/com/devexpress/demos/BarcodeCode93.java) |
| Barcode Code 93 Extended | Generates an extended Code 93 barcode image. | [BarcodeCode93Extended.java](BarcodeGenerationApi/BarcodeCode93Extended/src/main/java/com/devexpress/demos/BarcodeCode93Extended.java) |
| Barcode Data Matrix | Generates a Data Matrix barcode image. | [BarcodeDataMatrix.java](BarcodeGenerationApi/BarcodeDataMatrix/src/main/java/com/devexpress/demos/BarcodeDataMatrix.java) |
| Barcode Data Matrix GS1 | Generates a GS1 Data Matrix barcode image. | [BarcodeDataMatrixGs1.java](BarcodeGenerationApi/BarcodeDataMatrixGs1/src/main/java/com/devexpress/demos/BarcodeDataMatrixGs1.java) |
| Barcode Deutsche Post Identcode | Generates a Deutsche Post Identcode barcode image. | [BarcodeDeutschePostIdentcode.java](BarcodeGenerationApi/BarcodeDeutschePostIdentcode/src/main/java/com/devexpress/demos/BarcodeDeutschePostIdentcode.java) |
| Barcode Deutsche Post Leitcode | Generates a Deutsche Post Leitcode barcode image. | [BarcodeDeutschePostLeitcode.java](BarcodeGenerationApi/BarcodeDeutschePostLeitcode/src/main/java/com/devexpress/demos/BarcodeDeutschePostLeitcode.java) |
| Barcode EAN-13 | Generates an EAN-13 barcode image. | [BarcodeEan13.java](BarcodeGenerationApi/BarcodeEan13/src/main/java/com/devexpress/demos/BarcodeEan13.java) |
| Barcode EAN-8 | Generates an EAN-8 barcode image. | [BarcodeEan8.java](BarcodeGenerationApi/BarcodeEan8/src/main/java/com/devexpress/demos/BarcodeEan8.java) |
| Barcode GS1-128 | Generates a GS1-128 barcode image. | [BarcodeGs1128.java](BarcodeGenerationApi/BarcodeGs1128/src/main/java/com/devexpress/demos/BarcodeGs1128.java) |
| Barcode GS1 DataBar | Generates a GS1 DataBar barcode image. | [BarcodeGs1DataBar.java](BarcodeGenerationApi/BarcodeGs1DataBar/src/main/java/com/devexpress/demos/BarcodeGs1DataBar.java) |
| Barcode Industrial 2 of 5 | Generates an Industrial 2 of 5 barcode image. | [BarcodeIndustrial2of5.java](BarcodeGenerationApi/BarcodeIndustrial2of5/src/main/java/com/devexpress/demos/BarcodeIndustrial2of5.java) |
| Barcode Intelligent Mail | Generates an Intelligent Mail barcode image. | [BarcodeIntelligentMail.java](BarcodeGenerationApi/BarcodeIntelligentMail/src/main/java/com/devexpress/demos/BarcodeIntelligentMail.java) |
| Barcode Intelligent Mail Package | Generates an Intelligent Mail Package barcode image. | [BarcodeIntelligentMailPackage.java](BarcodeGenerationApi/BarcodeIntelligentMailPackage/src/main/java/com/devexpress/demos/BarcodeIntelligentMailPackage.java) |
| Barcode Interleaved 2 of 5 | Generates an Interleaved 2 of 5 barcode image. | [BarcodeInterleaved2of5.java](BarcodeGenerationApi/BarcodeInterleaved2of5/src/main/java/com/devexpress/demos/BarcodeInterleaved2of5.java) |
| Barcode ITF-14 | Generates an ITF-14 barcode image. | [BarcodeItf14.java](BarcodeGenerationApi/BarcodeItf14/src/main/java/com/devexpress/demos/BarcodeItf14.java) |
| Barcode Matrix 2 of 5 | Generates a Matrix 2 of 5 barcode image. | [BarcodeMatrix2of5.java](BarcodeGenerationApi/BarcodeMatrix2of5/src/main/java/com/devexpress/demos/BarcodeMatrix2of5.java) |
| Barcode Micro QR Code | Generates a Micro QR Code barcode image. | [BarcodeMicroQrCode.java](BarcodeGenerationApi/BarcodeMicroQrCode/src/main/java/com/devexpress/demos/BarcodeMicroQrCode.java) |
| Barcode MSI Plessey | Generates an MSI Plessey barcode image. | [BarcodeMsiPlessey.java](BarcodeGenerationApi/BarcodeMsiPlessey/src/main/java/com/devexpress/demos/BarcodeMsiPlessey.java) |
| Barcode PDF417 | Generates a PDF417 barcode image. | [BarcodePdf417.java](BarcodeGenerationApi/BarcodePdf417/src/main/java/com/devexpress/demos/BarcodePdf417.java) |
| Barcode Pharmacode | Generates a Pharmacode barcode image. | [BarcodePharmacode.java](BarcodeGenerationApi/BarcodePharmacode/src/main/java/com/devexpress/demos/BarcodePharmacode.java) |
| Barcode PostNet | Generates a PostNet barcode image. | [BarcodePostNet.java](BarcodeGenerationApi/BarcodePostNet/src/main/java/com/devexpress/demos/BarcodePostNet.java) |
| Barcode QR Code | Generates a QR Code barcode image. | [BarcodeQrCode.java](BarcodeGenerationApi/BarcodeQrCode/src/main/java/com/devexpress/demos/BarcodeQrCode.java) |
| Barcode QR Code GS1 | Generates a GS1 QR Code barcode image. | [BarcodeQrCodeGs1.java](BarcodeGenerationApi/BarcodeQrCodeGs1/src/main/java/com/devexpress/demos/BarcodeQrCodeGs1.java) |
| Barcode SSCC-18 | Generates an SSCC-18 barcode image. | [BarcodeSscc18.java](BarcodeGenerationApi/BarcodeSscc18/src/main/java/com/devexpress/demos/BarcodeSscc18.java) |
| Barcode UPC-A | Generates a UPC-A barcode image. | [BarcodeUpca.java](BarcodeGenerationApi/BarcodeUpca/src/main/java/com/devexpress/demos/BarcodeUpca.java) |
| Barcode UPC-E0 | Generates a UPC-E0 barcode image. | [BarcodeUpce0.java](BarcodeGenerationApi/BarcodeUpce0/src/main/java/com/devexpress/demos/BarcodeUpce0.java) |
| Barcode UPC-E1 | Generates a UPC-E1 barcode image. | [BarcodeUpce1.java](BarcodeGenerationApi/BarcodeUpce1/src/main/java/com/devexpress/demos/BarcodeUpce1.java) |
| Barcode UPC Supplemental 2 | Generates a UPC supplemental 2-digit barcode image. | [BarcodeUpcSupplemental2.java](BarcodeGenerationApi/BarcodeUpcSupplemental2/src/main/java/com/devexpress/demos/BarcodeUpcSupplemental2.java) |
| Barcode UPC Supplemental 5 | Generates a UPC supplemental 5-digit barcode image. | [BarcodeUpcSupplemental5.java](BarcodeGenerationApi/BarcodeUpcSupplemental5/src/main/java/com/devexpress/demos/BarcodeUpcSupplemental5.java) |


## Prerequisites

- Java Development Kit (JDK) 21 or later.
- Gradle 8.14 or later.
- IntelliJ IDEA, Eclipse, or Visual Studio Code with Java extensions.

## Documentation

- [Office & PDF File API for Java Documentation](https://docs.devexpress.com/OfficeFileApiJava/405691/office-file-api) 
- [PDF Document API for Java](https://docs.devexpress.com/OfficeFileApiJava/405992/pdf-document-api/pdf-document-api)
- [PowerPoint Presentation API for Java](https://docs.devexpress.com/OfficeFileApiJava/405860/presentation-api/presentation-api-overview)
- [Barcode Generation API for Java](https://docs.devexpress.com/OfficeFileApiJava/405819/barcode-generation-api/barcode-generation-api)
