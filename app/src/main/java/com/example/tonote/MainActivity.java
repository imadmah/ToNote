package com.example.tonote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.SearchView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<Note> notes= new ArrayList<>();
     NoteAdapter noteAdapter;
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

        SearchView search_bar = findViewById(R.id.search_bar);
        setupRecycleView();


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
        Query query ;
        if(!SortOrder.isEmpty())  query =Utility.getCollectionReferenceForNotes().orderBy(SortOrder );
        else  query =Utility.getCollectionReferenceForNotes();
        FirestoreRecyclerOptions<Note> options =new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query,Note.class).build();
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter=new NoteAdapter(options,this);
        recyclerView.setAdapter(noteAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }
}