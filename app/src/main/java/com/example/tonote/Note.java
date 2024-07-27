package com.example.tonote;

import com.google.firebase.Timestamp;

import java.util.Objects;

public class Note implements Comparable<Note> {

        private String note_text ;
        private String header ;
        private Timestamp timestamp ;

    public void setTimestamp(Timestamp Timestamp) {
        this.timestamp = Timestamp;
    }

    public Note(){}


    public Note(String note_text, String header, Timestamp Timestamp)
    {
            this.note_text = note_text;
            this.header = header;
            this.timestamp= Timestamp;
    }
    public Note(String note_text, String header )
    {
        this.note_text = note_text;
        this.header = header;

    }


    public String getnote_text() {
        return note_text;
    }

    public void setnote_text(String note_text) {
        this.note_text = note_text;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(note_text, note.note_text) && Objects.equals(header, note.header) && Objects.equals(timestamp, note.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(note_text, header, timestamp);
    }


    @Override
    public int compareTo(Note note) {
        return 0;
    }
}

