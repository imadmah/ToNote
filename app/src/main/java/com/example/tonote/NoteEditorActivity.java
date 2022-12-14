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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.model.Document;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

public class NoteEditorActivity extends AppCompatActivity {
    String noteId,content,title,DateCreated;
    Button Delete_note_btn ;
    //
    Date currentTime = Calendar.getInstance().getTime();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM hh:mm");
    String currentDate = simpleDateFormat.format(currentTime);
    //
    Boolean isEditedMode=false,NoteisDeleted=false;
    EditText note_header;
    TextView note_date;
    EditText note_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

         note_content = findViewById(R.id.note_edit_text);
         note_header =findViewById(R.id.note_edit_header);
         note_date = findViewById(R.id.note_date);



        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);


        Delete_note_btn =findViewById(R.id.Delete_note_txt);

       /* note_content.addTextChangedListener(new TextWatcher() {
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
*/

        Delete_note_btn.setOnClickListener(v->{DeleteNoteFromFirebase();});




        // Fetch data that is passed from NoteAdapter  //
        //                                              //
        Intent intent = getIntent();
         noteId = intent.getStringExtra("docId");
        content = intent.getStringExtra("content");
        title = intent.getStringExtra("title");
        DateCreated=intent.getStringExtra("Date");
        note_content.setText(content);
        note_header.setText(title);
        if(noteId!=null && !noteId.isEmpty() ) isEditedMode=true;
        if(!isEditedMode) note_date.setText(currentDate);
         else note_date.setText(DateCreated);


    }

    //TODO :
    //add the offline delete  //
    // //

    private void DeleteNoteFromFirebase() {
        DocumentReference documentReference;
        if(isEditedMode){
            //Update the Note
            documentReference =Utility.getCollectionReferenceForNotes().document(noteId);
        }
        else return;

        documentReference.delete().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Utility.ShowToast(NoteEditorActivity.this,"Note deleted successfully");
                NoteisDeleted=true;
                finish();
            }
            else Utility.ShowToast(NoteEditorActivity.this,"Failed to deleted the note ");
        });
    }



    //                                                                //
    // Creating Object of SharedPreferences to store data in the phone//
      public void SaveData(){
        if(!NoteisDeleted) {
            Note note = new Note(note_content.getText().toString(), note_header.getText().toString(), note_date.getText().toString());
            if (!note.getHeader().equals("") || !note.getnote_text().equals("")) {
                SaveNoteInFireBase(note);
            }
        }
     }

    private void SaveNoteInFireBase(Note note) {
        DocumentReference documentReference;
        if(isEditedMode){
            //Update the Note
            documentReference =Utility.getCollectionReferenceForNotes().document(noteId);
        }else{
            //Create new Note
            documentReference =Utility.getCollectionReferenceForNotes().document();
        }

        documentReference.set(note).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Utility.ShowToast(NoteEditorActivity.this,"Note added successfully");
                finish();
            }
            else Utility.ShowToast(NoteEditorActivity.this,"Failed to add the note ");
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SaveData();
    }
}