package com.example.tonote;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;

public class NoteAdapter extends ArrayAdapter implements Filterable {
    public NoteAdapter(Activity context, ArrayList<Note> Notes){

        super(context, 0, Notes);
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.my_note_item ,parent, false);
         }

        // Get the {@link AndroidFlavor} object located at this position in the list
        Note currentNote = (Note) getItem(position);


        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView header = listItemView.findViewById(R.id.header);
        header.setText(currentNote.getHeader());

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView First_line = listItemView.findViewById(R.id.note_Text);
        First_line.setText(currentNote.getnote_text());

        TextView Date_text= listItemView.findViewById(R.id.date_text);
        Date_text.setText(currentNote.getDate());

        if(header.getText().toString().isEmpty()) {
            header.setVisibility(View.GONE);
            First_line.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            First_line.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        }
        else{
            header.setVisibility(View.VISIBLE);
            First_line.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            First_line.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
        if(First_line.getText().toString().isEmpty()) {
            First_line.setVisibility(View.GONE);
        }
        else{
            First_line.setVisibility(View.VISIBLE);

        }

        return listItemView;
    }


}
