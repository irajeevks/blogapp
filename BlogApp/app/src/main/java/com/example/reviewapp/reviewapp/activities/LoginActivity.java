package com.example.reviewapp.reviewapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reviewapp.reviewapp.R;
import com.example.reviewapp.reviewapp.utils.Validation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

public class LoginActivity extends AppCompatActivity {

    private Button btnLgoin;
    private EditText txtemail, txtpassword;
    private TextView btnSignUp, btnForgotPass;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;
    private com.google.android.gms.common.SignInButton btnGoogleLogin;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.reviewapp.reviewapp.R.layout.activity_login);
        setStatusBarTranslucent(true);

        btnLgoin = findViewById(R.id.btn_login);
        txtemail = findViewById(R.id.txt_login_email);
        txtpassword = findViewById(R.id.txt_login_password);
        btnSignUp = findViewById(R.id.btn_signup);
        btnForgotPass = findViewById(R.id.btn_forgot_password);
        btnGoogleLogin = findViewById(R.id.google_sign_in);

        btnLgoin.setOnClickListener(onClickListener);
        btnSignUp.setOnClickListener(onClickListener);
        btnForgotPass.setOnClickListener(onClickListener);
        btnGoogleLogin.setOnClickListener(onClickListener);

        setGooglePlusButtonText(btnGoogleLogin, "Sign in using Google account");
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        auth = FirebaseAuth.getInstance();
        // [END initialize_auth]


    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btn_forgot_password) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                overridePendingTransition(R.anim.animation_enter_right, R.anim.animation_leave_left);
                finish();
            } else if (view.getId() == R.id.btn_signup) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                overridePendingTransition(R.anim.animation_enter_right, R.anim.animation_leave_left);
                finish();
            } else if (view.getId() == R.id.btn_login) {
                loginUser(txtemail.getText().toString(), txtpassword.getText().toString());
            } else if (view.getId() == R.id.google_sign_in) {
                googleSignIn();
            }
        }
    };

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            moveToProfileActivity();
        }
    }

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    // [END onactivityresult]
    // [END on_start_check_user]


    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            //redirect the user to the profile page
                            moveToProfileActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.activity_login), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void moveToProfileActivity() {
        //cehck if the table does not exist then create table
        //if table exist redirect to MainSwipeActivity
        SQLiteDatabase mydatabase = openOrCreateDatabase("logins", MODE_PRIVATE, null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS login(Email VARCHAR,Password VARCHAR);");

        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        overridePendingTransition(R.anim.animation_enter_right, R.anim.animation_leave_left);
        Log.d("", auth.getCurrentUser().toString());
        finish();
    }
    // [END auth_with_google]

    // [START signin]
    private void googleSignIn() {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("LogIn...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void loginUser(String email, final String password) {
        //validate email and password.
        if (Validation.isValidEmailAddress(email)) {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("LogIn...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            } else {
                                //redirect the user to the main activity
                                moveToProfileActivity();
                                progressDialog.dismiss();
                            }
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
        }

    }

    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}