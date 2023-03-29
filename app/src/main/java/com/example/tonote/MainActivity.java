package com.example.tonote;

import static android.content.ContentValues.TAG;


import static com.example.tonote.Utility.getCollectionReferenceForNotes;


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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  static   NoteAdapter noteAdapter;
    //LIST OF NOTES THAT RETRIEVED FROM FIREBASE //
    //                                           //
    static ArrayList<Note_Doc>  Notes = new ArrayList<>();
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
        SearchView searchView = findViewById(R.id.search_bar);
        recyclerView = findViewById(R.id.recycle_view);

        searchView.setIconifiedByDefault(true); // show search icon in expanded state
        searchView.setQueryHint("Search notes"); // set hint text
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // use filter to update adapter with filtered notes
                noteAdapter.getFilter().filter(newText);
                return false;
            }
        });

        RetrieveDataFromFirestore();
        setupRecycleView();


    }


    private void setupRecycleView()
    {

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter=new NoteAdapter(this,Notes_Firebase);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                recyclerView.setAdapter(noteAdapter);
            }
        });
        noteAdapter.notifyDataSetChanged();


     }


    public void add_note(View view)
    {
        Intent intent = new Intent(getApplicationContext(), NoteEditorActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void RetrieveDataFromFirestore()
    {

        CollectionReference collection = getCollectionReferenceForNotes();


        collection.get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                for (DocumentSnapshot document : documents)
                {
                    Notes_Firebase.add(document.toObject(Note.class));
                    Log.println(Log.ERROR,document.getId(),"22222");
                    Notes.add(new Note_Doc(document.toObject(Note.class) ,document.getId()));
                    noteAdapter.notifyDataSetChanged();
                }

            }
            else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }



}