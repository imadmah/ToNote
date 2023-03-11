package com.example.tonote;

import static android.content.ContentValues.TAG;


import static com.example.tonote.Utility.getCollectionReferenceForNotes;
import static com.example.tonote.Utility.retrieveDataFromFirestore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  static   NoteAdapter noteAdapter;
    //LIST OF NOTES THAT RETRIEVED FROM FIREBASE //
    //                                           //
    static ArrayList<Note_Doc>  Notes = new ArrayList<>();
    static ArrayList<Note>  Notes_local = new ArrayList<>();
    static ArrayList<Note>  Notes_Firebase = new ArrayList<>();

    RecyclerView recyclerView;
    private String SortOrder="";


    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {

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
            case R.id.logout_button:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,Login_Activity.class));
                finish();
                return true;
            case R.id.bytitle: SortOrder="header";
                return true;
            case R.id.bydate: SortOrder="Date";
                return true;

        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycle_view);
        setupRecycleView();
      //  SearchView search_bar = findViewById(R.id.search_bar);
        //if(LoadData())







    }
    // Return true if data is retrieved , else if not data is stored locally return false //
     /* private boolean LoadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.note", Context.MODE_PRIVATE);
        Gson gson= new Gson();
        String json = sharedPreferences.getString("notes",null);
        Type type = new TypeToken<ArrayList<Note>>() {}.getType();
        if(json!=null)  Notes_local = gson.fromJson(json,type);
         return !Notes_local.isEmpty();
      }*/


    private void setupRecycleView(){
        Notes= retrieveDataFromFirestore();
        noteAdapter=new NoteAdapter(this,Notes_Firebase);
        recyclerView.setHasFixedSize(false);

        recyclerView.setAdapter(noteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

     }


    public void add_note(View view) {
        Intent intent = new Intent(getApplicationContext(), NoteEditorActivity.class);
        startActivity(intent);

    }
    @Override
    protected void onStart() {
        super.onStart();
    }

}