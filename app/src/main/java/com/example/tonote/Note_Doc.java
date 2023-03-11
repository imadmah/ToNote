package com.example.tonote;

public class Note_Doc {
    private Note note ;
    private String DocId;

    public Note_Doc(Note note, String docId) {
        this.note = note;
        DocId = docId;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public String getDocId() {
        return DocId;
    }

    public void setDocId(String docId) {
        DocId = docId;
    }
}
