package com.example.reviewapp.reviewapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.example.reviewapp.reviewapp.R;
import com.example.reviewapp.reviewapp.utils.Validation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnSignUp;
    private EditText txtemail,txtpassword;
    private TextView btnLgoin,btnForgotPass;

    private RelativeLayout activity_signup;

    private FirebaseAuth auth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        txtemail = (EditText)findViewById(R.id.txt_login_email);
        txtpassword = (EditText)findViewById(R.id.txt_login_password);
        btnLgoin = (TextView)findViewById(R.id.btn_login);
        btnForgotPass = (TextView)findViewById(R.id.btn_forgot_password);

        activity_signup = (RelativeLayout)findViewById(R.id.activity_signup);

        btnLgoin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        btnForgotPass.setOnClickListener(this);

        //Init Firebase Auth
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_login)
        {
            startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
            finish();
        }
        else if (view.getId() == R.id.btn_forgot_password)
        {
            startActivity(new Intent(SignUpActivity.this,ForgotPasswordActivity.class));
            finish();
        }
        else if (view.getId() == R.id.btn_signup)
        {
            userSignUp(txtemail.getText().toString(),txtpassword.getText().toString());
        }
    }

    private void userSignUp(String email, String password) {
        //validate email and password.
        if (Validation.isValidEmailAddress(email))
        {
            progressDialog = new ProgressDialog(SignUpActivity.this);
            progressDialog.setMessage("Registering...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (!task.isSuccessful())
                            {
                                Snackbar snackbar = Snackbar.make(activity_signup,"Error : "+task.getException(),Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }
                            else
                            {
                                Toast.makeText(SignUpActivity.this,"Registration Successful",Toast.LENGTH_SHORT).show();
                                //redirect the user to the Login activity
                                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                                finish();
                            }
                        }
                    });
        }
        else
        {
            Snackbar snackbar = Snackbar.make(activity_signup,"Invalid email address",Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }
}
