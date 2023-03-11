package com.example.tonote;



import static com.example.tonote.Utility.AdjustHeaderContent;

import static com.example.tonote.Utility.getCollectionReferenceForNotes;

import android.content.Context;
import android.content.Intent;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import java.io.File;
import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    Context context;
    //List of Notes That are saved locally //
    //                                     //
     ArrayList<Note> list = new ArrayList<>();

    public NoteAdapter(Context context,ArrayList<Note> list ) {
        this.context=context;
        this.list=list;
    }

        @Override
        public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        if(list!=null){
            Note note =list.get(position);
            holder.Date_text.setText(note.getDate());
            holder.content.setText(note.getnote_text());
            holder.header.setText(note.getHeader());
            AdjustHeaderContent(holder.header,holder.content);
            holder.itemView.setOnClickListener((v)->{
                Intent intent =new Intent(context,NoteEditorActivity.class);
                intent.putExtra("title",note.getHeader());
                intent.putExtra("content",note.getnote_text());
                intent.putExtra("Date",note.getDate());
                intent.putExtra("Position",position);
                context.startActivity(intent);

            });
    }}

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.my_note_item,parent,false);
        return new NoteViewHolder(view);
    }




    @Override
    public int getItemCount() {
        return list.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder{

        TextView content;
        TextView Date_text;
        TextView header;
        public NoteViewHolder(@NonNull View itemView){
            super(itemView);
            content = itemView.findViewById(R.id.note_Text);
            Date_text= itemView.findViewById(R.id.date_text);
            header = itemView.findViewById(R.id.header);
        }


    }



}
