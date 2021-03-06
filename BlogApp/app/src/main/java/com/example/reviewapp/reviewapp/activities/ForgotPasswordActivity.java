package com.example.reviewapp.reviewapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reviewapp.reviewapp.R;
import com.example.reviewapp.reviewapp.utils.Validation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextView btnSignIn;
    private Button btnResetPassword;
    private EditText emailEditText;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        btnSignIn = (TextView) findViewById(R.id.btn_signin);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        emailEditText = (EditText) findViewById(R.id.txt_reset_textbox);

        // [START initialize_auth]
        auth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(emailEditText.getText()))
                {
                    Toast.makeText(ForgotPasswordActivity.this,"Email is required",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Validation.isValidEmailAddress(emailEditText.getText().toString()))
                {
                    Toast.makeText(ForgotPasswordActivity.this,"Enter valid email address",Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
                progressDialog.setMessage("Email Sending...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                auth.sendPasswordResetEmail("user@example.com")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgotPasswordActivity.this,"Email sent",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPasswordActivity.this,LoginActivity.class));
                finish();
            }
        });
    }
}
