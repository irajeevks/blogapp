package com.example.reviewapp.reviewapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

import com.example.reviewapp.reviewapp.R;
import com.example.reviewapp.reviewapp.utils.Validation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnSignUp;
    private EditText emailEditText,passwordEditText, repassEditText;
    private TextView btnLogin;

    private FirebaseAuth auth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setStatusBarTranslucent(true);

        btnSignUp = findViewById(R.id.btn_signup);
        emailEditText = findViewById(R.id.txt_login_email);
        passwordEditText = findViewById(R.id.txt_login_password);
        repassEditText = findViewById(R.id.txt_login_repassword);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);

        //Init Firebase Auth
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_login)
        {
            startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
            overridePendingTransition(R.anim.animation_enter_left, R.anim.animation_leave_right);
            finish();
        }
        else if (view.getId() == R.id.btn_signup)
        {
            userSignUp(emailEditText.getText().toString(),passwordEditText.getText().toString());
        }
    }

    private void userSignUp(String email, String password) {
        //validate email and password.
//        if(!Validation.isValidUserName(userNameEditText.getText().toString())){
//            Toast.makeText(getApplicationContext(), "UserName Invaild", Toast.LENGTH_SHORT).show();
//            return;
//        }
        if(!Validation.isValidEmailAddress(email)) {
            Toast.makeText(getApplicationContext(),"Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if(passwordEditText.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Password Can not be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!passwordEditText.getText().toString().equals(repassEditText.getText().toString())){
            Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setMessage("Registering...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"Error : "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(SignUpActivity.this,"Registration Successful",Toast.LENGTH_SHORT).show();
                            //redirect the user to the Login activity
                            startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                            overridePendingTransition(R.anim.animation_enter_right, R.anim.animation_leave_left);
                            finish();
                        }
                    }
                });
    }

    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
