package com.example.tonote;

import static android.content.ContentValues.TAG;
import static com.example.tonote.Utility.getCollectionReferenceForNotes;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import 	com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseReference database;
   static NoteAdapter noteAdapter;
    RecyclerView recyclerView;
    ArrayList<Note> list ;
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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycle_view);
        SearchView search_bar = findViewById(R.id.search_bar);
        list = new ArrayList<>();
        setupRecycleView();
        /*database = FirebaseDatabase.getInstance().getReference("my_notes");

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Note note =dataSnapshot.getValue(Note.class);
                    list.add(note);
                }
                noteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/



           /*  listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Going from MainActivity to NotesEditorActivity
                Intent intent = new Intent(getApplicationContext(), NoteEditorActivity.class);
                intent.putExtra("noteId", i);
                startActivity(intent);

            }
        });*/



          /*
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
*/

          /*listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            //  a long press on a note a delete page appeare //
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
*/
    }
  /*  private void LoadData(){
      SharedPreferences sharedPreferences = getSharedPreferences("com.example.note", Context.MODE_PRIVATE);
      Gson gson= new Gson();
      String json = sharedPreferences.getString("note",null);
      Type type = new TypeToken<ArrayList<Note>>() {}.getType();
      if(json!=null)  notes = gson.fromJson(json,type);
      if (notes == null) notes.add(new Note("EXMPLE","EXMPLE"));

    }*/
    public void add_note(View view) {
        Intent intent = new Intent(getApplicationContext(), NoteEditorActivity.class);
        startActivity(intent);

    }
   /* public void Filter_List(String Sequence){

        for(Note note:notes){
            try {
                    if(note.getHeader().toLowerCase().contains(Sequence.toLowerCase()) || note.getnote_text().toLowerCase().contains(Sequence))
                        if(!filterednotes.contains(note)) filterednotes.add(note);
                    filteredadapter.notifyDataSetChanged();

                }
                catch (Exception e ){}
            }





    }*/

    void setupRecycleView(){
        CollectionReference collection = getCollectionReferenceForNotes();


        collection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    for (DocumentSnapshot document : documents) {
                        list.add( document.toObject(Note.class));
                        System.out.println(document.getData());
                    }
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        noteAdapter=new NoteAdapter(this,list);
        recyclerView.setAdapter(noteAdapter);
    }



    @Override
    protected void onResume() {
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }
}