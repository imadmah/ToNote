package com.example.tonote;

import static android.content.ContentValues.TAG;


import static com.example.tonote.Utility.getCollectionReferenceForNotes;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  static   NoteAdapter noteAdapter;
    //LIST OF NOTES THAT RETRIEVED FROM FIREBASE //
    //                                           //
    static ArrayList<Note_Doc>  Notes = new ArrayList<>();
    static ArrayList<Note>  Notes_Firebase = new ArrayList<>();
    private final CollectionReference collection = getCollectionReferenceForNotes();
    RecyclerView recyclerView;
    ProgressBar progressBar;




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
                //TODO ADD case of no internet Can't singout //
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(R.drawable.logout_icon)
                        .setTitle("Are you sure?")
                        .setMessage("       Do you want to Sign out ")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth.getInstance().signOut();
                                Notes_Firebase.clear();
                                Notes.clear();
                                startActivity(new Intent(MainActivity.this,Login_Activity.class));
                                finish();
                            }
                        }).setNegativeButton("No", null).show();

                return true;


            case R.id.bytitle:
                if(!isInternetAvailable(getApplicationContext())) {
                    Collections.sort(Notes_Firebase, new Comparator<Note>() {
                        @Override
                        public int compare(Note note1, Note note2) {
                            return note1.getHeader().toLowerCase().compareTo(note2.getHeader().toLowerCase()); //compareTo differentiate between lowercase and uppercase//
                        }
                    });
                    Collections.sort(Notes, new Comparator<Note_Doc>() {
                        @Override
                        public int compare(Note_Doc note_doc, Note_Doc t1) {
                            return note_doc.getNote().getHeader().toLowerCase().compareTo(t1.getNote().getHeader().toLowerCase());
                        }
                    });}
                else
                {
                  collection.orderBy("header", Query.Direction.ASCENDING);
                  Notes_Firebase.clear();
                  Notes.clear();
                  RetrieveDataFromFirestore();
                }
                noteAdapter.notifyDataSetChanged();
                return true;



            case R.id.bydate:
                if(!isInternetAvailable(getApplicationContext())) {
                    Collections.sort(Notes_Firebase, new Comparator<Note>() {
                        @Override
                        public int compare(Note note1, Note note2) {
                            return note1.getTimestamp().compareTo(note2.getTimestamp());
                        }

                    });
                    Collections.sort(Notes, new Comparator<Note_Doc>() {
                        @Override
                        public int compare(Note_Doc note_doc, Note_Doc t1) {
                            return note_doc.getNote().getTimestamp().compareTo(t1.getNote().getTimestamp());
                        }
                    });}
                else
                {
                    collection.orderBy("timestamp",Query.Direction.ASCENDING);
                    Notes_Firebase.clear();
                    Notes.clear();
                    RetrieveDataFromFirestore();
                }



                //TODO : Fix when The sort of Note_doc//
                noteAdapter.notifyDataSetChanged();

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
         progressBar = findViewById(R.id.progress_bar);

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
        recyclerView.setAdapter(noteAdapter);

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
    {   progressBar.setVisibility(View.VISIBLE);

        collection.get().addOnCompleteListener((Task<QuerySnapshot> task) -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                for (DocumentSnapshot document : documents)
                {
                    Notes_Firebase.add(document.toObject(Note.class));
                    Notes.add(new Note_Doc(document.toObject(Note.class) ,document.getId()));
                    noteAdapter.notifyDataSetChanged();
                }

            }
            else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }
    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            Network network = cm.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities nc = cm.getNetworkCapabilities(network);
                return nc != null && (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
            }
        }
        return false;
    }





}