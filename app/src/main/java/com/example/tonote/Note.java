package com.example.tonote;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Note {

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
        public Note(String note_text, String header) {
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
}

