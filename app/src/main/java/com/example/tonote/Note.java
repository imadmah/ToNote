package com.example.tonote;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Note implements Comparable<String> {

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
    public int compareTo(String s) {
        if(header.compareTo(s)>0) return 1;
        else if(header.compareTo(s)<0)  return -1;
        return 0;
    }
}

