package com.example.tonote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<Note> notes= new ArrayList<>();
    static NoteAdapter noteAdapter;
    NoteAdapter filteredadapter;
    static   ArrayList<Note>   filterednotes;
    static boolean filtered_list ;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.add_note:
                Intent intent = new Intent(getApplicationContext(), NoteEditorActivity.class);
                startActivity(intent);
                return true;
            case R.id.import_button:
            case R.id.export_button:
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);

        SearchView search_bar =findViewById(R.id.search_bar);

        LoadData();

           filterednotes = new ArrayList<>();
         filteredadapter= new NoteAdapter(this,filterednotes);
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

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:
                        //scroll was stopped, let's show search bar again

                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        //user is scrolling, let's hide search bar

                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > 0) {
                    //user scrolled down, first element is hidden
                   // search_bar.setVisibility(View.GONE);
                }
                if(firstVisibleItem==0) search_bar.setVisibility(View.VISIBLE);

            }
        });

        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filteredadapter.clear();

                if(!s.isEmpty()){
                    Filter_List(s);
                    filtered_list=true;
                    listView.setAdapter(filteredadapter);
                }
                else {
                    listView.setAdapter(noteAdapter);
                }
                return false;
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
    public void Filter_List(String Sequence){

        for(Note note:notes){
            try {
                    if(note.getHeader().toLowerCase().contains(Sequence.toLowerCase()) || note.getnote_text().toLowerCase().contains(Sequence))
                        if(!filterednotes.contains(note)) filterednotes.add(note);
                    filteredadapter.notifyDataSetChanged();

                }
                catch (Exception e ){}
            }





    }
}