package com.example.tonote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    static ArrayList<Note> notes= new ArrayList<>();
    static NoteAdapter noteAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.add_note) {
            // Going from MainActivity to NotesEditorActivity
            Intent intent = new Intent(getApplicationContext(), NoteEditorActivity.class);
            startActivity(intent);
            return true;
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = findViewById(R.id.listView);
        LoadData();

        noteAdapter = new NoteAdapter(this,notes);
        listView.setAdapter(noteAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Going from MainActivity to NotesEditorActivity
                Intent intent = new Intent(getApplicationContext(), NoteEditorActivity.class);
                intent.putExtra("noteId", i);
                startActivity(intent);

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
     public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

        final int itemToDelete = i;
             // To delete the data from the App
       new AlertDialog.Builder(MainActivity.this)
           .setIcon(android.R.drawable.ic_dialog_alert)
             .setTitle("Are you sure?")
             .setMessage("Do you want to delete this note?")
          .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                 @Override
          public void onClick(DialogInterface dialogInterface, int i) {
               notes.remove(itemToDelete);
               noteAdapter.notifyDataSetChanged();
               SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.note", Context.MODE_PRIVATE);
               Gson gson = new Gson();
               String json =gson.toJson(MainActivity.notes);
               sharedPreferences.edit().putString("note",json).apply();
                                }
                            }).setNegativeButton("No", null).show();

                    return true;
                }
            });
    }
    private void LoadData(){
      SharedPreferences sharedPreferences = getSharedPreferences("com.example.note", Context.MODE_PRIVATE);
      Gson gson= new Gson();
      String json = sharedPreferences.getString("note",null);
      Type type = new TypeToken<ArrayList<Note>>() {}.getType();
      if(json!=null)  notes = gson.fromJson(json,type);
      if (notes == null) notes.add(new Note("EXMPLE","EXMPLE"));

    }

    public void add_note(View view) {
        Intent intent = new Intent(getApplicationContext(), NoteEditorActivity.class);
        startActivity(intent);

    }
}