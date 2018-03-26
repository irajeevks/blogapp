package com.example.reviewapp.reviewapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.reviewapp.reviewapp.ApplicationController;
import com.example.reviewapp.reviewapp.R;
import com.example.reviewapp.reviewapp.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    private FirebaseAuth auth;
    ProgressDialog progressDialog;
    private DatabaseReference mDatabaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setStatusBarTranslucent(true);

        // [START initialize_auth]
        auth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                //if getCurrentUser does not returns null
                if(auth.getCurrentUser() != null) {
                    mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");

                    SQLiteDatabase mydatabase = openOrCreateDatabase("logins", MODE_PRIVATE, null);
                    if (tableExists(mydatabase, "login")) {
                        String userid = auth.getCurrentUser().getUid();
                        progressDialog = new ProgressDialog(SplashActivity.this);
                        progressDialog.setMessage("Loading Data...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        mDatabaseUsers.child(userid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                progressDialog.dismiss();
                                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                                if (userModel != null) {
                                    ApplicationController.getInstance().setUserModel(userModel);
                                    startActivity(new Intent(SplashActivity.this, MainSwipeActivity.class));
                                    overridePendingTransition(R.anim.animation_enter_right, R.anim.animation_leave_left);
                                    finish();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                progressDialog.dismiss();
                            }
                        });
                    } else {
                        /* Create an Intent that will start the Login-Activity. */
                        Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(mainIntent);
                        overridePendingTransition(R.anim.animation_fade_in, R.anim.animation_fade_out);
                        finish();
                    }
                } else {
                    /* Create an Intent that will start the Login-Activity. */
                    Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(mainIntent);
                    overridePendingTransition(R.anim.animation_fade_in, R.anim.animation_fade_out);
                    finish();
                }

            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    boolean tableExists(SQLiteDatabase db, String tableName)
    {
        if (tableName == null || db == null || !db.isOpen())
        {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
        if (!cursor.moveToFirst())
        {
            cursor.close();
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
