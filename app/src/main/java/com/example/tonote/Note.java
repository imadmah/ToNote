package com.example.tonote;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

public class Note  {

        private String note_text ;
        private String header ;
        private String Date ;

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        this.Date = date;
    }

    public Note(){}
        public Note(String note_text, String header,String Date) {
            this.note_text = note_text;
            this.header = header;
            this.Date= Date;
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



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(note_text, note.note_text) && Objects.equals(header, note.header) && Objects.equals(Date, note.Date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(note_text, header, Date);
    }
}

