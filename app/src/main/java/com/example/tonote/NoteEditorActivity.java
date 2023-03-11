package com.example.tonote;


import static com.example.tonote.MainActivity.Notes;
import static com.example.tonote.MainActivity.Notes_Firebase;
import static com.example.tonote.MainActivity.Notes_local;
import static com.example.tonote.MainActivity.noteAdapter;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
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
    ImageView Delete_note_btn ;
    ArrayList<Note> Note_Not_Saved_INFireBase = new ArrayList<>();
    //
    Date currentTime = Calendar.getInstance().getTime();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM hh:mm");

    String currentDate = simpleDateFormat.format(currentTime);
    //
    Boolean isEditedMode=false,NoteisDeleted=false;
    EditText note_header;
    TextView note_date;
    private Note note;
    int position;
    EditText note_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
         note_content = findViewById(R.id.note_edit_text);
         note_header =findViewById(R.id.note_edit_header);
         note_date = findViewById(R.id.note_date);
        Delete_note_btn =findViewById(R.id.Delete_note_txt);
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Delete_note_btn.setOnClickListener(view -> DeleteNoteFromFirebase());


        // Fetch data that is passed from NoteAdapter  //
        //                                              //
        Intent intent = getIntent();

        try {
            content = intent.getStringExtra("content");
            title = intent.getStringExtra("title");
            DateCreated=intent.getStringExtra("Date");
            note_content.setText(content);
            note_header.setText(title);
            position = intent.getIntExtra("Position",-1);
            if(position!=-1) noteId = Notes.get(position).getDocId();

            // Show the deletebtn and make editmode bool true //
            if( noteId!=null ){
                isEditedMode=true;
                Delete_note_btn.setVisibility(View.VISIBLE);
            }

            // Set time only when the note is a new one //
            if(!isEditedMode) note_date.setText(currentDate);
            else note_date.setText(DateCreated);
        }
        catch (Exception e){
            Log.println(Log.ERROR,"Exception",e.fillInStackTrace().toString());

        }




    }
    // Creating Object of SharedPreferences to store data in the phone//
   /* public void SaveDataLocally(Note note){
        if(note!=null){
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.note", Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json =gson.toJson(Notes_local);
            sharedPreferences.edit().putString("notes",json).apply();
        }}*/

    //TODO :
    //add the offline delete  //
    // //

    private void DeleteNoteFromFirebase() {
        DocumentReference documentReference;
        if(isEditedMode){
            //Delete the Note
            documentReference =Utility.getCollectionReferenceForNotes().document(noteId);
            Notes_Firebase.remove(position);
            NoteisDeleted=true;
            Utility.ShowToast(NoteEditorActivity.this,"Note deleted Locally");
        }
        else return;

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



    //                                                                //

      public void SaveData(){
          note = new Note(note_content.getText().toString(), note_header.getText().toString(), note_date.getText().toString());
        if(!NoteisDeleted  ) {
            if(!Note_exist(note) & !Note_empty(note)){

                if(isEditedMode) {  // In Case of editing the note  //
                    Notes_Firebase.add(position, note);
                    Notes.add(position, new Note_Doc(note, Notes.get(position).getDocId()));
                }
               else         Notes_Firebase.add(note);

                Log.println(Log.ERROR,note.getnote_text(),note.getHeader());
                // SaveDataLocally(note);
                SaveNoteInFireBase(note);

        }
            //In Case of Emptying the Note //
            if(Note_empty(note) ) DeleteNoteFromFirebase();
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


}