package com.example.tonote;

import static android.content.ContentValues.TAG;


import static com.example.tonote.MainActivity.Notes_Firebase;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Utility {


    static void ShowToast(Context context, String toast){
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }

    @NonNull
    static CollectionReference getCollectionReferenceForNotes(){
        FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        return FirebaseFirestore.getInstance().collection("notes")
                .document(CurrentUser.getUid()).collection("my_notes");
    }

    static void AdjustHeaderContent (@NonNull TextView header, TextView content){
        if(header.getText().toString().isEmpty()) {
            header.setVisibility(View.GONE);
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            content.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            content.setTextColor(Color.parseColor("#000000"));

        }
        else{
            header.setVisibility(View.VISIBLE);
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            content.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
        if(content.getText().toString().isEmpty()) {
            content.setVisibility(View.GONE);
        }
        else{
            content.setVisibility(View.VISIBLE);

        }
    }

    @NonNull
    public static Timestamp convertStringToTimestamp(String dateString) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(dateString);
        Timestamp timestamp = new Timestamp(date);
        return timestamp;
    }


}
