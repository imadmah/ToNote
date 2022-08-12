package com.example.tonote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

public class NoteEditorActivity extends AppCompatActivity {
    int noteId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
        ArrayList<Note>   temps_notes;
        EditText note_content = findViewById(R.id.note_edit_text);
        EditText note_header =findViewById(R.id.note_edit_header);
        TextView note_date = findViewById(R.id.note_date);
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM hh:mm");
        String currentDate = simpleDateFormat.format(currentTime);

        // Fetch data that is passed from MainActivity  //
        Intent intent = getIntent();

        // Accessing the data using key and value   //
        noteId = intent.getIntExtra("noteId", -1);
        if (noteId != -1) {
            if (!MainActivity.filtered_list) {
                temps_notes=MainActivity.notes;

            }
            else temps_notes=MainActivity.filterednotes;
            note_date.setText(temps_notes.get(noteId).getDate());
            note_content.setText(temps_notes.get(noteId).getnote_text());
            note_header.setText(temps_notes.get(noteId).getHeader());

        }
        else {

            MainActivity.notes.add(new Note());
            noteId = MainActivity.notes.size() - 1;
            note_date.setText(currentDate);


        }



        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);


        note_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                MainActivity.notes.get(noteId).setDate(currentDate);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                MainActivity.notes.get(noteId).setnote_text(charSequence.toString());
                MainActivity.noteAdapter.notifyDataSetChanged();
                SaveData(MainActivity.notes.get(noteId));
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });


        //HEADER OF THE NOTE ACTION  //
        //                          //
        note_header.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    MainActivity.notes.get(noteId).setHeader(charSequence.toString());
                MainActivity.noteAdapter.notifyDataSetChanged();

                SaveData(MainActivity.notes.get(noteId));

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });




    }

    //                                                                //
    // Creating Object of SharedPreferences to store data in the phone//
      public void SaveData(Note note){
        if(note!=null){

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.note", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json =gson.toJson(MainActivity.notes);
        sharedPreferences.edit().putString("note",json).apply();

    }}
      private boolean Note_isempty(){
          if(MainActivity.notes.get(noteId).getnote_text()==null & MainActivity.notes.get(noteId).getHeader()==null)
              return true;
          else return false ;

      }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}