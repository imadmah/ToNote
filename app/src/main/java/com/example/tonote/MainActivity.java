package com.example.tonote;

import static android.content.ContentValues.TAG;
import static com.example.tonote.Utility.getCollectionReferenceForNotes;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
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
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    static NoteAdapter noteAdapter;
    static ArrayList<Note_Doc> Notes = new ArrayList<>();
    SharedPreferences sharedPreferences;
    Query query = getCollectionReferenceForNotes();
    static ArrayList<Note> Notes_Firebase = new ArrayList<>();
    private final CollectionReference collection = getCollectionReferenceForNotes();
    RecyclerView recyclerView;
    ProgressBar progressBar;

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.add_note:
                Intent intent = new Intent(getApplicationContext(), NoteEditorActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout_button:
                showLogoutDialog();
                return true;
            case R.id.bytitle:
                sortNotes("byTitle");
                return true;
            case R.id.bydate:
                sortNotes("byDate");
                return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.search_bar);
        recyclerView = findViewById(R.id.recycle_view);
        progressBar = findViewById(R.id.progress_bar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            toolbar.post(new Runnable() {
                @Override
                public void run() {
                    if (toolbar.getOverflowIcon() != null) {
                        int color = isDarkMode() ? R.color.white : R.color.black;
                        toolbar.getOverflowIcon().setColorFilter(
                                ContextCompat.getColor(MainActivity.this, color),
                                PorterDuff.Mode.SRC_ATOP
                        );
                        setToolbarTextColor(toolbar, color);
                    }
                }
            });
        }

        sharedPreferences = getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        searchView.setQueryHint("Search notes");
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                noteAdapter.getFilter().filter(newText);
                return false;
            }
        });
        setupRecycleView();
        RetrieveDataFromFirestore();
    }

    private boolean isDarkMode() {
        int nightModeFlags = getResources().getConfiguration().uiMode &
                android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;
    }

    private void setToolbarTextColor(Toolbar toolbar, int colorRes) {
        int color = ContextCompat.getColor(this, colorRes);
        toolbar.setTitleTextColor(color);

        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(color);
            }
        }
    }

    private void setupRecycleView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(this, Notes_Firebase);
        recyclerView.setAdapter(noteAdapter);
        noteAdapter.notifyDataSetChanged();
    }

    public void add_note(View view) {
        Intent intent = new Intent(getApplicationContext(), NoteEditorActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void RetrieveDataFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);
        query.get().addOnCompleteListener((Task<QuerySnapshot> task) -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                Notes_Firebase.clear();
                Notes.clear();
                for (DocumentSnapshot document : documents) {
                    Notes_Firebase.add(document.toObject(Note.class));
                    Notes.add(new Note_Doc(Objects.requireNonNull(document.toObject(Note.class)), document.getId()));
                }
                if (!Notes.isEmpty()) {
                    applySavedSortOption();
                }
                noteAdapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    private void applySavedSortOption() {
        String retrievedValue = sharedPreferences.getString("SortSettings", "byDate"); // Default sort by date
        if ("byTitle".equals(retrievedValue)) {
            sortByTitle(Notes_Firebase);
            sortByTitle_doc(Notes);
        } else {
            sortByDate(Notes_Firebase);
            sortByDate_doc(Notes);
        }
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            Network network = cm.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities nc = cm.getNetworkCapabilities(network);
                return nc == null || (!nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) && !nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
            }
        }
        return true;
    }

    public void sortByTitle(ArrayList<Note> Notes) {
        Collections.sort(Notes, (note1, note2) -> note1.getHeader().toLowerCase().compareTo(note2.getHeader().toLowerCase()));
    }

    public void sortByDate(ArrayList<Note> Notes) {
        Collections.sort(Notes, (note2, note1) -> {
            if (note1.getTimestamp() == null && note2.getTimestamp() == null) {
                return 0;
            } else if (note1.getTimestamp() == null) {
                return -1;
            } else if (note2.getTimestamp() == null) {
                return 1;
            } else {
                return note1.getTimestamp().compareTo(note2.getTimestamp());
            }
        });
    }

    public void sortByTitle_doc(ArrayList<Note_Doc> Notes) {
        Collections.sort(Notes, (note_doc, t1) -> note_doc.getNote().getHeader().toLowerCase().compareTo(t1.getNote().getHeader().toLowerCase()));
    }

    public void sortByDate_doc(ArrayList<Note_Doc> Notes) {
        Collections.sort(Notes, (t1, note_doc) -> {
            if (note_doc.getNote().getTimestamp() == null && t1.getNote().getTimestamp() == null) {
                return 0;
            } else if (note_doc.getNote().getTimestamp() == null) {
                return -1;
            } else if (t1.getNote().getTimestamp() == null) {
                return 1;
            } else {
                return note_doc.getNote().getTimestamp().compareTo(t1.getNote().getTimestamp());
            }
        });
    }

    public void setSort(String sort_option) {
        sharedPreferences = getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("SortSettings", sort_option);
        editor.apply();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(R.drawable.logout_icon)
                .setTitle("Are you sure?")
                .setMessage("Do you want to Sign out")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    FirebaseAuth.getInstance().signOut();
                    Notes_Firebase.clear();
                    Notes.clear();
                    startActivity(new Intent(MainActivity.this, Login_Activity.class));
                    finish();
                }).setNegativeButton("No", null).show();
    }

    public void sortFromFirestore(String option, Query.Direction direction) {
        query = collection.orderBy(option, direction);
        Notes_Firebase.clear();
        Notes.clear();
        RetrieveDataFromFirestore();
    }

    private void sortNotes(String sortOption) {
        if (isInternetAvailable(getApplicationContext())) {
            if ("byTitle".equals(sortOption)) {
                sortByTitle(Notes_Firebase);
                sortByTitle_doc(Notes);
            } else {
                sortByDate(Notes_Firebase);
                sortByDate_doc(Notes);
            }
        } else {
            if ("byTitle".equals(sortOption)) {
                sortFromFirestore("header", Query.Direction.ASCENDING);
            } else {
                sortFromFirestore("timestamp", Query.Direction.DESCENDING);
            }
        }
        setSort(sortOption);
        noteAdapter.notifyDataSetChanged();
    }
}
