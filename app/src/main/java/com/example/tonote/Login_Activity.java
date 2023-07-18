package com.example.tonote;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Login_Activity extends AppCompatActivity {
    EditText PasswordEditText,emailEditText;
    Button loginbtn ;
    ProgressBar progressBar;
    TextView Sign_up_txt ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        PasswordEditText=findViewById(R.id.editTextTextPassword);
        emailEditText=findViewById(R.id.editTextTextEmailAddress);
        loginbtn=findViewById(R.id.login_account_btn);
        Sign_up_txt=findViewById(R.id.Sign_up_txt);
        progressBar=findViewById(R.id.progress_bar);
        Sign_up_txt.setOnClickListener(v->startActivity(new Intent(Login_Activity.this,CreateAccountActivity.class)));

        loginbtn.setOnClickListener(v->LoginUser());
    }

    void  LoginUser() {
        String email =emailEditText.getText().toString();
        String password = PasswordEditText.getText().toString();
        boolean isValidated =ValidateData( email, password);
        if(!isValidated) return;
        LoginInFirebase(email,password);
    }

    private void LoginInFirebase(String email,String password) {
        changeInprogress(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            changeInprogress(false);
            if(task.isSuccessful()){
                //Login successfully
                if(Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified()){
                    Utility.ShowToast(Login_Activity.this,"Login successfully"); // for simplifying throwing toasts we implement ShowToast
                    startActivity(new Intent(Login_Activity.this,MainActivity.class));
                }
                else {
                    Utility.ShowToast(Login_Activity.this,"Email not verified,Please verify Your email");
                }
            }
            else {
                Utility.ShowToast(Login_Activity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage());
            }
        });
    }

    void changeInprogress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            loginbtn.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            loginbtn.setVisibility(View.VISIBLE);
        }
    }

    boolean ValidateData(String email,String password){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email invalid");
            return false ;}
        if(password.length()<6){
            PasswordEditText.setError("Password length is invalid");
            return false;
        }

        return true;
    }

}