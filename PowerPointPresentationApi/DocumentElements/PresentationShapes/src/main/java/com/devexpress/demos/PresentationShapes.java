package com.devexpress.demos;

import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.OfficeColor;
import com.devexpress.docs.office.SolidFill;
import com.devexpress.docs.office.TextArea;
import com.devexpress.docs.presentation.*;
import com.devexpress.drawing.DXImage;
import com.devexpress.office.utils.Units;
import com.devexpress.system.drawing.Color;

import java.io.*;

public class PresentationShapes {

    static final String IMAGE_FILE_PATH = "SharedFiles/Photo1.jpeg";
    static final String OUTPUT_FILE_NAME = "Presentation_Shapes.pptx";
    static final DocumentFormat OUTPUT_FORMAT = DocumentFormat.PPTX;
    static final ShapeActionType SHAPE_ACTION = ShapeActionType.FIGURE;
    static final GeometryPresetType SHAPE_GEOMETRY = GeometryPresetType.RECTANGLE;
    static final String SHAPE_TEXT = "Sample Text";
    static final float OUTLINE_WIDTH = 1.0f;
    static final String OUTLINE_COLOR = "#9E67FF";
    static final String FILL_COLOR = "#FFFFFF";
    static final int NUMBER_OF_SHAPES = 3;

    enum ShapeActionType { PICTURE, TEXTBOX, FIGURE, GROUP }

    static ShapeType getShapeType(GeometryPresetType geometryPresetType) {
        if (geometryPresetType.equals(GeometryPresetType.RECTANGLE)) return ShapeType.getRectangle();
        if (geometryPresetType.equals(GeometryPresetType.OVAL)) return ShapeType.getOval();
        if (geometryPresetType.equals(GeometryPresetType.STAR_4)) return ShapeType.getStar4();
        if (geometryPresetType.equals(GeometryPresetType.HEART)) return ShapeType.getHeart();
        if (geometryPresetType.equals(GeometryPresetType.RIGHT_ARROW)) return ShapeType.getRightArrow();
        return ShapeType.getRectangle();
    }

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            Color fillColor = parseColor(FILL_COLOR);
            Color outlineColor = parseColor(OUTLINE_COLOR);
            float defaultX = 1f, defaultY = 1f, defaultWidth = 1.5f, defaultHeight = 1.5f;

            ByteArrayOutputStream outputStream;
            switch (SHAPE_ACTION) {
                case TEXTBOX:
                    outputStream = addTextbox(SHAPE_TEXT, defaultX, defaultY, defaultWidth, defaultHeight, false, fillColor, outlineColor, OUTLINE_WIDTH, OUTPUT_FORMAT);
                    break;
                case FIGURE:
                    outputStream = addFigure(getShapeType(SHAPE_GEOMETRY), defaultX, defaultY, defaultWidth, defaultHeight, false, fillColor, outlineColor, OUTLINE_WIDTH, OUTPUT_FORMAT);
                    break;
                case GROUP:
                    outputStream = addShapeGroup(getShapeType(SHAPE_GEOMETRY), NUMBER_OF_SHAPES, defaultX, defaultY, defaultWidth, defaultHeight, false, fillColor, outlineColor, OUTLINE_WIDTH, OUTPUT_FORMAT);
                    break;
                default:
                    InputStream imageResource = PresentationShapes.class.getResourceAsStream("/" + IMAGE_FILE_PATH);
                    if (imageResource == null) throw new FileNotFoundException("Resource not found: " + IMAGE_FILE_PATH);
                    outputStream = addPicture(imageResource, false, defaultX, defaultY, defaultWidth, defaultHeight, outlineColor, OUTLINE_WIDTH, OUTPUT_FORMAT);
                    imageResource.close();
                    break;
            }

            try (FileOutputStream fos = new FileOutputStream(OUTPUT_FILE_NAME)) {
                outputStream.writeTo(fos);
            }
            System.out.println("Created " + OUTPUT_FILE_NAME);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }


    static ByteArrayOutputStream addPicture(InputStream imageStream, boolean lockAspectRatio, float x, float y, float width, float height, Color outlineColor, float outlineWidth, DocumentFormat outputFormat) throws IOException {
        DXImage image = DXImage.fromStream(imageStream);
        return addImage(image, lockAspectRatio, x, y, width, height, false, 0, 0, 0, 0, outlineColor, outlineWidth, outputFormat);
    }

    static ByteArrayOutputStream addImage(DXImage dxImage, boolean lockAspectRatio, float x, float y, float width, float height, boolean shouldCrop, float cropLeft, float cropRight, float cropTop, float cropBottom, Color outlineColor, float outlineWidth, DocumentFormat outputFormat) throws IOException {
        try (Presentation presentation = new Presentation()) {
            presentation.getSlides().clear();
            Slide slide = new Slide(SlideLayoutType.BLANK);
            presentation.getSlides().add(slide);

            PictureShape pictureShape = new PictureShape(dxImage);
            pictureShape.setX(Units.inchesToDocumentsF(x));
            pictureShape.setY(Units.inchesToDocumentsF(y));
            pictureShape.setWidth(Units.inchesToDocumentsF(width));
            pictureShape.setHeight(Units.inchesToDocumentsF(height));
            LineStyle outline = new LineStyle();
            outline.setFill(new SolidFill(new OfficeColor(outlineColor)));
            outline.setWidth(Units.pointsToDocumentsF(outlineWidth));
            pictureShape.setOutline(outline);
            if (shouldCrop) {
                pictureShape.setCropLeft(cropLeft);
                pictureShape.setCropTop(cropTop);
                pictureShape.setCropRight(cropRight);
                pictureShape.setCropBottom(cropBottom);
            }
            pictureShape.getLockSettings().setDisableAspectRatioChange(lockAspectRatio);
            slide.getShapes().add(pictureShape);
            return createOutputStream(presentation, outputFormat);
        }
    }

    static ByteArrayOutputStream addTextbox(String text, float x, float y, float width, float height, boolean lockAspectRatio, Color fillColor, Color outlineColor, float outlineWidth, DocumentFormat outputFormat) throws IOException {
        try (Presentation presentation = new Presentation()) {
            presentation.getSlides().clear();
            Slide slide = new Slide(SlideLayoutType.BLANK);
            presentation.getSlides().add(slide);
            Shape textboxShape = new Shape(ShapeType.getRectangle());
            textboxShape.setTextArea(new TextArea(text));
            textboxShape.getTextArea().getLevel1ParagraphProperties().getTextProperties().setFill(
                new SolidFill(new OfficeColor(Color.fromArgb(255, 0, 0, 0))));
            formatShape(textboxShape, x, y, width, height, lockAspectRatio, outlineWidth, outlineColor, fillColor);
            slide.getShapes().add(textboxShape);
            return createOutputStream(presentation, outputFormat);
        }
    }

    static ByteArrayOutputStream addFigure(ShapeType shapeType, float x, float y, float width, float height, boolean lockAspectRatio, Color fillColor, Color outlineColor, float outlineWidth, DocumentFormat outputFormat) throws IOException {
        try (Presentation presentation = new Presentation()) {
            presentation.getSlides().clear();
            Slide slide = new Slide(SlideLayoutType.BLANK);
            presentation.getSlides().add(slide);
            Shape shape = new Shape(shapeType);
            formatShape(shape, x, y, width, height, lockAspectRatio, outlineWidth, outlineColor, fillColor);
            slide.getShapes().add(shape);
            return createOutputStream(presentation, outputFormat);
        }
    }

    static ByteArrayOutputStream addShapeGroup(ShapeType shapeType, int numberOfShapes, float x, float y, float width, float height, boolean lockAspectRatio, Color fillColor, Color outlineColor, float outlineWidth, DocumentFormat outputFormat) throws IOException {
        try (Presentation presentation = new Presentation()) {
            presentation.getSlides().clear();
            Slide slide = new Slide(SlideLayoutType.BLANK);
            presentation.getSlides().add(slide);
            GroupShape groupShape = new GroupShape();
            for (int i = 0; i < numberOfShapes; i++) {
                Shape shape = new Shape(shapeType);
                formatShape(shape, x, y, width, height, lockAspectRatio, outlineWidth, outlineColor, fillColor);
                groupShape.getShapes().add(shape);
                x += 0.5f;
                y += 0.5f;
            }
            slide.getShapes().add(groupShape);
            return createOutputStream(presentation, outputFormat);
        }
    }

    static void formatShape(Shape shape, float x, float y, float width, float height, boolean lockAspectRatio, float outlineWidth, Color outlineColor, Color fillColor) {
        shape.setX(Units.inchesToDocumentsF(x));
        shape.setY(Units.inchesToDocumentsF(y));
        shape.setWidth(Units.inchesToDocumentsF(width));
        shape.setHeight(Units.inchesToDocumentsF(height));
        shape.setFill(new SolidFill(new OfficeColor(fillColor)));
        LineStyle outline = new LineStyle();
        outline.setFill(new SolidFill(new OfficeColor(outlineColor)));
        outline.setWidth(Units.pointsToDocumentsF(outlineWidth));
        shape.setOutline(outline);
        shape.getLockSettings().setDisableAspectRatioChange(lockAspectRatio);
    }

    static ByteArrayOutputStream createOutputStream(Presentation presentation, DocumentFormat outputFormat) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        presentation.saveDocument(baos, outputFormat);
        return baos;
    }

    static Color parseColor(String hex) {
        int r = Integer.parseInt(hex.substring(1, 3), 16);
        int g = Integer.parseInt(hex.substring(3, 5), 16);
        int b = Integer.parseInt(hex.substring(5, 7), 16);
        return Color.fromArgb(255, r, g, b);
    }
}
