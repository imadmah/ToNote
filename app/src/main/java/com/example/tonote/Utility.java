package com.example.tonote;

import android.content.Context;
import android.widget.Toast;

public class Utility {
    static void ShowToast(Context context, String toast){
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }
}
