package com.example.tonote;



import static com.example.tonote.MainActivity.Notes_Firebase;
import static com.example.tonote.MainActivity.noteAdapter;
import static com.example.tonote.Utility.AdjustHeaderContent;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    Context context;
    ArrayList<Note> list ;
    private final Filter noteFilter =new NoteFilter();


    public NoteAdapter(Context context,ArrayList<Note> list ) {
        this.list=list;
        this.context=context;
    }

        @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position)
        {
            if(list!=null)
            {
                Note note =list.get(position);
                holder.content.setText(note.getnote_text());
                holder.header.setText(note.getHeader());
                AdjustHeaderContent(holder.header,holder.content);
                String formattedDate ;
                Date date;
                if(note.getTimestamp()==null)  date= Calendar.getInstance().getTime();
                else  date = new Date(note.getTimestamp().toDate().getTime());
                SimpleDateFormat dateFormat = new SimpleDateFormat(" MMMM dd ");
                holder.Date_text.setText(dateFormat.format(date));
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                formattedDate = dateFormat.format(date);

                holder.itemView.setOnClickListener((v)->
                    {
                        Intent intent =new Intent(context,NoteEditorActivity.class);
                        intent.putExtra("title",note.getHeader());
                        intent.putExtra("content",note.getnote_text());
                        intent.putExtra("Date",formattedDate);
                        intent.putExtra("Position",position);
                        context.startActivity(intent);
                    });
            }
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.my_note_item,parent,false);
        return new NoteViewHolder(view);
    }


    public ArrayList<Note> getList() {
        return list;
    }

    public void setList(ArrayList<Note> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Filter getFilter() {
        return noteFilter;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder{

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

    private class NoteFilter extends Filter {
        ArrayList<Note> filteredNotes ;
        boolean Donefiltering =false;
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
             filteredNotes = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                // show all notes if search query is empty
                Donefiltering =true;

            } else {
                // filter notes based on search query
                String query = constraint.toString().toLowerCase().trim();
                for (Note note : MainActivity.Notes_Firebase) {
                    if (note.getHeader().toLowerCase().contains(query) ||
                            note.getnote_text().toLowerCase().contains(query)) {
                        filteredNotes.add(note);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredNotes;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // update adapter with filtered notes
            ((Activity)context).runOnUiThread(() -> {
                // update adapter with filtered notes on the UI thread
                filteredNotes = (ArrayList<Note>) results.values;
                if(filteredNotes==null) filteredNotes= new ArrayList<>();
                if(Donefiltering) {
                    noteAdapter.setList(MainActivity.Notes_Firebase);
                    Donefiltering=false;
                }
                    else noteAdapter.setList(filteredNotes);
            });

        }
    }

}

