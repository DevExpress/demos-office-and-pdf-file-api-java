package com.devexpress.demos;

import com.devexpress.jpeg.JpegBackend;
import com.devexpress.jpeg.JpegCodecSettings;
import com.devexpress.docs.presentation.*;

import java.io.*;

public class PresentationNotes {

    static final String INPUT_FILE_PATH = "SharedFiles/Sample.pptx";
    static final String OUTPUT_FILE_NAME = "Presentation_Notes.pptx";
    static final String NOTE_TEXT = "Sample note text";
    static final int SLIDE_INDEX = 1;
    static final NotesActionType ACTION_TYPE = NotesActionType.ADD_NOTE_TO_SLIDE;
    static final DocumentFormat OUTPUT_FORMAT = DocumentFormat.PPTX;

    enum NotesActionType { ADD_NOTE_TO_SLIDE, CLEAR_ALL_NOTES }

    static {
        JpegCodecSettings.setBackend(JpegBackend.MANAGED);
    }

    public static void main(String[] args) {
        try {
            InputStream inputResource = PresentationNotes.class.getResourceAsStream("/" + INPUT_FILE_PATH);
            if (inputResource == null)
                throw new FileNotFoundException("Resource not found: " + INPUT_FILE_PATH);

            ByteArrayOutputStream outputStream = applyNotes(inputResource, ACTION_TYPE, NOTE_TEXT, SLIDE_INDEX - 1, OUTPUT_FORMAT);
            inputResource.close();

            try (FileOutputStream fos = new FileOutputStream(OUTPUT_FILE_NAME)) {
                outputStream.writeTo(fos);
            }
            System.out.println("Created " + OUTPUT_FILE_NAME);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    static ByteArrayOutputStream applyNotes(InputStream input, NotesActionType action, String noteText, int slideIndex, DocumentFormat format) throws IOException {
        try (Presentation presentation = new Presentation(input)) {
            switch (action) {
                case CLEAR_ALL_NOTES:
                    clearAllNotes(presentation);
                    break;
                case ADD_NOTE_TO_SLIDE:
                    addUpdateNote(presentation, slideIndex, noteText);
                    break;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            presentation.saveDocument(baos, format);
            return baos;
        }
    }

    static void addUpdateNote(Presentation presentation, int slideIndex, String noteText) {
        if (slideIndex >= 0 && slideIndex < presentation.getSlides().size()) {
            Slide slide = presentation.getSlides().get(slideIndex);
            if (presentation.getNotesMaster() == null)
                presentation.setNotesMaster(new NotesMaster("notesMaster"));
            NotesSlide notes;
            if (slide.getNotes() == null) {
                notes = new NotesSlide();
                slide.setNotes(notes);
            } else {
                notes = slide.getNotes();
            }
            notes.getTextArea().setText(noteText);
        }
    }

    static void clearAllNotes(Presentation presentation) {
        for (Slide slide : presentation.getSlides())
            slide.setNotes(null);
    }
}
