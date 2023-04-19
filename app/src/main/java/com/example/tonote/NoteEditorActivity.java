package com.example.tonote;


import static com.example.tonote.MainActivity.Notes;
import static com.example.tonote.MainActivity.Notes_Firebase;
import static com.example.tonote.MainActivity.noteAdapter;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NoteEditorActivity extends AppCompatActivity {

    final Date currentDate = Calendar.getInstance().getTime();
    final SimpleDateFormat TimestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    final SimpleDateFormat WrittenDateFormat =new SimpleDateFormat("MMMM dd HH:mm");
    final String currentDateString = WrittenDateFormat.format(currentDate);


    String noteId,content,title,dateString;
    Timestamp DateCreated;
    ImageView Delete_note_btn ;
    Boolean isEditedMode=false,NoteisDeleted=false;
    EditText note_header,note_content;
    TextView note_date;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
        note_content = findViewById(R.id.note_edit_text);
        note_header = findViewById(R.id.note_edit_header);
        note_date = findViewById(R.id.note_date);
        Delete_note_btn = findViewById(R.id.Delete_note_txt);
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Delete_note_btn.setOnClickListener(view -> DeleteNote());
        RetrieveDataFromintent();
        note_content.setText(content);
        note_header.setText(title);
        if (position != -1) noteId = Notes.get(position).getDocId();
        // Show the Deletebtn and make editmode bool true //
        if (noteId != null)
        {
          isEditedMode = true;
          Delete_note_btn.setVisibility(View.VISIBLE);
        }
        ConfigureDate();

        
    }

    private void DeleteNote() {
        DocumentReference documentReference;
            //Delete the Note
            documentReference =Utility.getCollectionReferenceForNotes().document(noteId);
            Notes_Firebase.remove(position);
            NoteisDeleted=true;
            
        documentReference.delete().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Notes.remove(position);
                Utility.ShowToast(NoteEditorActivity.this,"Note deleted successfully");
                finish();
            }
            else Utility.ShowToast(NoteEditorActivity.this,"Failed to deleted the note ");
        });
        finish();
    }



    //  Validate saving the data                //
    public void SaveData()
    {

          Note note = new Note(note_content.getText().toString(), note_header.getText().toString(), DateCreated);

          if(!NoteisDeleted)
          {
            if(!Note_exist(note) & !Note_empty(note))
            {
                if(isEditedMode)
                {
                    // In Case of editing the note  //
                    Notes_Firebase.set(position, note);
                    Notes.set(position, new Note_Doc(note, Notes.get(position).getDocId()));
                }
                else Notes_Firebase.add(note);
                // For Note_doc it is added in saveNoteinFireBase methode because      //
                // we need DocId and we only get it after creating a new note in firebase //
                SaveNoteInFireBase(note);

            }
            //In Case of Emptying the Note //
            if(Note_empty(note) & isEditedMode  ) DeleteNote();
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
            Notes.add(new Note_Doc(note,documentReference.getId()));
        }

        documentReference.set(note).addOnCompleteListener(task ->
        {
            if(task.isSuccessful()){

                Utility.ShowToast(NoteEditorActivity.this,"Note added successfully");
                finish();
            }
            else Utility.ShowToast(NoteEditorActivity.this,"Failed to add the note ");
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
        {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteAdapter.notifyDataSetChanged();
        SaveData();


    }
    private boolean Note_empty(Note note ){
        return note.getHeader().equals("") && note.getnote_text().equals("");
    }

    private boolean Note_exist(Note note){
        for (Note NoteCompared :Notes_Firebase ) {
            if(NoteCompared.equals(note)) return true;
        }
        return false ;

    }

    private void ConfigureDate(){
        Date date = null;
        // Here if the note is new we get current date else //
        // we parse the date given from the NoteAdapter //
        if (!isEditedMode) {
            date = currentDate;
            note_date.setText(currentDateString);

        } else {
            try {
                date = TimestampFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            note_date.setText(WrittenDateFormat.format(date));
        }

        DateCreated = new Timestamp(date);
    }

    private void RetrieveDataFromintent(){
        // Fetch data that is passed from NoteAdapter  //
        Intent intent = getIntent();
        content = intent.getStringExtra("content");
        title = intent.getStringExtra("title");
        dateString = intent.getStringExtra("Date");
        position = intent.getIntExtra("Position", -1);
        //
    }
}