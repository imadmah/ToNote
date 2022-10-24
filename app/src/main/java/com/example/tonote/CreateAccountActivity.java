package com.example.tonote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {
    EditText PasswordEditText,ConfirmPasswordEditText,emailEditText;
    Button create_account ;
    ProgressBar progressBar;
    TextView loginBtnTextview ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        PasswordEditText=findViewById(R.id.editTextTextPassword);
        ConfirmPasswordEditText=findViewById(R.id.confirmeditTextTextPassword);
        emailEditText=findViewById(R.id.editTextTextEmailAddress);
        create_account=findViewById(R.id.create_account_btn);
        progressBar=findViewById(R.id.progress_bar);
        loginBtnTextview=findViewById(R.id.Login_text);
        create_account.setOnClickListener(v->createAccount());
        loginBtnTextview.setOnClickListener(v-> startActivity(new Intent(CreateAccountActivity.this, Login_Activity.class)) );




    }

    void createAccount(){
        String email =emailEditText.getText().toString();
        String confirmpassword = ConfirmPasswordEditText.getText().toString();
                String password = PasswordEditText.getText().toString();
                boolean isValidated =ValidateData( email, password, confirmpassword);
                if(!isValidated) return;
                createAccountInFirebase(email,password);
    }

    private void createAccountInFirebase(String email, String password) {

        changeInprogress(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) { //if the account is created successefully
                changeInprogress(false);
                if(task.isSuccessful()){
                    Toast.makeText(CreateAccountActivity.this, "Successfully create account,Check email to verify", Toast.LENGTH_SHORT).show();
                    firebaseAuth.getCurrentUser().sendEmailVerification();
                    firebaseAuth.signOut();
                    startActivity(new Intent(CreateAccountActivity.this,Login_Activity.class));
                    finish();// here we will open the login page for user to login .

                }
                else{
                    Toast.makeText(CreateAccountActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    void changeInprogress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            create_account.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            create_account.setVisibility(View.VISIBLE);
        }
    }

    boolean ValidateData(String email,String password,String confirmpassword){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email invalid");
            return false ;}
        if(password.length()<6){
            PasswordEditText.setError("Password length is invalid");
            return false;
        }
        if(!password.equals(confirmpassword)){
            ConfirmPasswordEditText.setError("Password not matched");
            return false ;
        }
        return true;
    }



}