package com.example.tonote;

import static com.example.tonote.MainActivity.Notes;
import static com.example.tonote.MainActivity.Notes_Firebase;
import static com.example.tonote.MainActivity.noteAdapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class NoteEditorActivity extends AppCompatActivity {

    final Date currentDate = Calendar.getInstance().getTime();
    final SimpleDateFormat TimestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    final SimpleDateFormat WrittenDateFormat = new SimpleDateFormat("MMMM dd HH:mm");
    final String currentDateString = WrittenDateFormat.format(currentDate);

    String noteId, content, title, dateString;
    Timestamp DateCreated;
    ImageView Delete_note_btn;
    Boolean isEditedMode = false, NoteisDeleted = false;
    EditText note_header, note_content;
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

        // Initialize the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the Up button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false); // Hide the title text
        }

        // Change the delete button color based on the current theme
        int color = isDarkMode() ? Color.WHITE : Color.BLACK;
        Delete_note_btn.setColorFilter(
                color,
                PorterDuff.Mode.SRC_IN
        );

        Delete_note_btn.setOnClickListener(view -> DeleteNote());
        RetrieveDataFromintent();
        note_content.setText(content);
        note_header.setText(title);
        if (position != -1) noteId = Notes.get(position).getDocId();
        if (noteId != null) {
            isEditedMode = true;
            Delete_note_btn.setVisibility(View.VISIBLE);
        }
        ConfigureDate();
    }

    private boolean isDarkMode() {
        int nightModeFlags = getResources().getConfiguration().uiMode &
                android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;
    }

    private void DeleteNote() {
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForNotes().document(noteId);
        Notes_Firebase.remove(position);
        NoteisDeleted = true;

        documentReference.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Notes.remove(position);
                Utility.ShowToast(NoteEditorActivity.this, "Note deleted successfully");
                finish();
            } else Utility.ShowToast(NoteEditorActivity.this, "Failed to delete the note ");
        });
        finish();
    }

    public void SaveData() {
        Note note = new Note(note_content.getText().toString(), note_header.getText().toString(), DateCreated);

        if (!NoteisDeleted) {
            if (!Note_exist(note) && !Note_empty(note)) {
                if (isEditedMode) {
                    Notes_Firebase.set(position, note);
                    Notes.set(position, new Note_Doc(note, Notes.get(position).getDocId()));
                } else {
                    SaveNoteInFireBase(note);
                }
            }
            if (Note_empty(note) && isEditedMode) DeleteNote();
        }
    }

    private void SaveNoteInFireBase(Note note) {
        DocumentReference documentReference;
        if (isEditedMode) {
            documentReference = Utility.getCollectionReferenceForNotes().document(noteId);
        } else {
            documentReference = Utility.getCollectionReferenceForNotes().document();
            noteId = documentReference.getId();
        }

        documentReference.set(note).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!isEditedMode) {
                    Notes.add(new Note_Doc(note, documentReference.getId()));
                    Notes_Firebase.add(note);
                }
                sortNotes();
                Utility.ShowToast(NoteEditorActivity.this, "Note added successfully");
                finish();
            } else Utility.ShowToast(NoteEditorActivity.this, "Failed to add the note ");
        });
    }

    private void sortNotes() {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        String sortOption = sharedPreferences.getString("SortSettings", "byDate");

        if ("byTitle".equals(sortOption)) {
            Collections.sort(Notes_Firebase, (note1, note2) -> note1.getHeader().toLowerCase().compareTo(note2.getHeader().toLowerCase()));
            Collections.sort(Notes, (note_doc1, note_doc2) -> note_doc1.getNote().getHeader().toLowerCase().compareTo(note_doc2.getNote().getHeader().toLowerCase()));
        } else {
            Collections.sort(Notes_Firebase, (note1, note2) -> note2.getTimestamp().compareTo(note1.getTimestamp()));
            Collections.sort(Notes, (note_doc1, note_doc2) -> note_doc2.getNote().getTimestamp().compareTo(note_doc1.getNote().getTimestamp()));
        }

        noteAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean Note_empty(Note note) {
        return note.getHeader().isEmpty() && note.getnote_text().isEmpty();
    }

    private boolean Note_exist(Note note) {
        for (Note NoteCompared : Notes_Firebase) {
            if (NoteCompared.equals(note)) return true;
        }
        return false;
    }

    private void ConfigureDate() {
        Date date;
        if (!isEditedMode) {
            date = currentDate;
            note_date.setText(currentDateString);
        } else {
            try {
                date = TimestampFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
                date = currentDate;
            }
            note_date.setText(WrittenDateFormat.format(date));
        }
        DateCreated = new Timestamp(date);
    }

    private void RetrieveDataFromintent() {
        Intent intent = getIntent();
        content = intent.getStringExtra("content");
        title = intent.getStringExtra("title");
        dateString = intent.getStringExtra("Date");
        position = intent.getIntExtra("Position", -1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SaveData();
        noteAdapter.notifyDataSetChanged();
    }
}
