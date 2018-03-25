package com.example.reviewapp.reviewapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.reviewapp.reviewapp.R;
import com.example.reviewapp.reviewapp.utils.Validation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLgoin;
    private EditText txtemail, txtpassword;
    private TextView btnSignUp, btnForgotPass;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private RelativeLayout activity_login;
    private com.google.android.gms.common.SignInButton btngoogleLogin;
    ProgressDialog progressDialog;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.reviewapp.reviewapp.R.layout.activity_login);

        btnLgoin = (Button) findViewById(R.id.btn_login);
        txtemail = (EditText) findViewById(R.id.txt_login_email);
        txtpassword = (EditText) findViewById(R.id.txt_login_password);
        btnSignUp = (TextView) findViewById(R.id.btn_signup);
        btnForgotPass = (TextView) findViewById(R.id.btn_forgot_password);
        btngoogleLogin = (com.google.android.gms.common.SignInButton) findViewById(R.id.google_sign_in);

        activity_login = (RelativeLayout) findViewById(R.id.activity_login);

        btnLgoin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        btnForgotPass.setOnClickListener(this);
        btngoogleLogin.setOnClickListener(this);



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

        //if getCurrentUser does not returns null
        if(auth.getCurrentUser() != null){
            //TODO : Get Data From DB
            //that means user is already logged in
            //open profile activity
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            //so close this activity
            finish();
        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_forgot_password) {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            finish();
        } else if (view.getId() == R.id.btn_signup) {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            finish();
        } else if (view.getId() == R.id.btn_login) {
            loginUser(txtemail.getText().toString(), txtpassword.getText().toString());
        }
        else if (view.getId() == R.id.google_sign_in)
        {
            signIn();
        }
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser !=null)
        {
            //TODO: Start the ProfileActivity
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
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        Log.d("",auth.getCurrentUser().toString());
        finish();
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
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
                                Snackbar snackbar = Snackbar.make(activity_login, "Incorrect username or password", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                                progressDialog.dismiss();
                            } else {
                                //redirect the user to the main activity
                                moveToProfileActivity();
                                progressDialog.dismiss();
                            }
                        }
                    });
        } else {
            Snackbar snackbar = Snackbar.make(activity_login, "Invalid email address", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }

    }


}
