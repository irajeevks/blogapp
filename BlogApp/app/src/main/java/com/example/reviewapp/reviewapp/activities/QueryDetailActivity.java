package com.example.reviewapp.reviewapp.activities;

import android.app.Dialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.reviewapp.reviewapp.R;
import com.example.reviewapp.reviewapp.models.AnswerModel;
import com.example.reviewapp.reviewapp.models.NotificationModel;
import com.example.reviewapp.reviewapp.models.VoteModel;
import com.example.reviewapp.reviewapp.notifications.VoteNotifications;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class QueryDetailActivity extends AppCompatActivity {

    Button updateQuery;
    EditText titleEditText;
    EditText queryEditText;
    TextView titleTextView;
    TextView queryTextView;
    FloatingActionButton queryAnswer;
    private DatabaseReference mDatabaseQueries;
    ArrayList<AnswerModel> answers;
    android.support.v7.widget.Toolbar toolbar;
    private Dialog answerQueryDialog;
    private Button voteButton;
    private ArrayList<VoteModel> votes;
    private String queryId;
    private String postUserid;
    private int type;
    FirebaseAuth auth;
    private static RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(com.example.reviewapp.reviewapp.R.id.query_detail_toolbar);
        type = getIntent().getIntExtra("TYPE", 0);

        if (type == 1){
            setContentView(R.layout.activity_query_detail);
            titleTextView = (TextView) findViewById(R.id.titleText);
            queryTextView = (TextView) findViewById(R.id.queryText);
            queryAnswer = (FloatingActionButton) findViewById(R.id.queryAnswer);
        } else {
            setContentView(R.layout.activity_my_query_detail);
            titleEditText = (EditText) findViewById(R.id.titleEditText);
            queryEditText = (EditText) findViewById(R.id.queryEditText);
            updateQuery = (Button) findViewById(R.id.updateQuery);
        }

        setSupportActionBar(toolbar);

        //Custom Dialog for Placing new query.
        answerQueryDialog = new Dialog(QueryDetailActivity.this);
        answerQueryDialog.setTitle("Answer Query");
        answerQueryDialog.setContentView(R.layout.answer_query_dialog);
        voteButton = (Button) findViewById(R.id.btn_vote);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        final String title = getIntent().getStringExtra("TITLE");
        final String query = getIntent().getStringExtra("QUERY");
        postUserid = getIntent().getStringExtra("USERID");
        queryId = getIntent().getStringExtra("KEY");

        answers = (ArrayList<AnswerModel>) getIntent().getSerializableExtra("ANSWERS");
        votes = (ArrayList<VoteModel>) getIntent().getSerializableExtra("VOTES");


        for (AnswerModel answer:answers)
        {
            Log.d("Answer Title",answer.getTitle());
        }



        mDatabaseQueries = FirebaseDatabase.getInstance().getReference();
        //Initialize the Firebase App

        //This variable
        auth = FirebaseAuth.getInstance();
        //Check that this query is posted by the user or not.
        String uid = auth.getCurrentUser().getUid();
        for (VoteModel vote:votes)
        {
            if (uid.equals(vote.getUserid()))
            {
                //set the button text to remove vote.
                voteButton.setText("Remove Vote");
                break;

            }
        }

        if(type == 0) {
            titleEditText.setText(title);
            queryEditText.setText(query);
            updateQuery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(titleEditText.getText()))
                    {
                        Toast.makeText(QueryDetailActivity.this,"Title is required",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(queryEditText.getText().toString()))
                    {
                        Toast.makeText(QueryDetailActivity.this,"Description is required",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //Check if the textbox value is changed or not.
                    //If the value is changed then update it.
                    //Otherwise we don't need to send request in the database.
                    if (!title.equals(titleEditText.getText().toString()))
                    {
                        Toast.makeText(QueryDetailActivity.this,"Title Updated",Toast.LENGTH_SHORT).show();
                        mDatabaseQueries.child("queries").child(queryId).child("title").setValue(titleEditText.getText().toString());
                    }
                    if (!query.equals(queryEditText.getText().toString()))
                    {
                        Toast.makeText(QueryDetailActivity.this,"Description Updated",Toast.LENGTH_SHORT).show();
                        mDatabaseQueries.child("queries").child(queryId).child("query").setValue(queryEditText.getText().toString());
                    }
                    titleEditText.setText("");
                    queryEditText.setText("");

                }
            });
        } else {
            titleTextView.setText("Q: " + title);
            queryTextView.setText(query);

            //Fab Icon click open the add or edit dialog
            queryAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayQueryAnswerDialog();
                }
            });
        }



        //voting button click
        //send push notification to the owner of the post.
        voteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("vote button clicked",queryId);
                //first check if the user is voted previously or not.
                String userid = auth.getCurrentUser().getUid();
                boolean found = false;
                String message = "";
                System.out.println("Array Items"+votes.size());
                for (VoteModel vote: votes)
                {
                    if (vote.userid.equals(userid))
                    {
                        found = true;
                        votes.remove(vote);
                        break;
                    }
                }
                if (found)
                {
                    //remove the child node.
                    mDatabaseQueries.child("queries").child(queryId).child("votes").child(userid).removeValue();
                    voteButton.setText("Upvote");
                    message = auth.getCurrentUser().getEmail()+" Deleted vote";
                }
                else
                {
                    // add the vote
                    //  queries/{queryId}/votes/{userid}
                    mDatabaseQueries.child("queries").child(queryId).child("votes").child(userid).child("userid").setValue(userid);
                    mDatabaseQueries.child("queries").child(queryId).child("votes").child(userid).child("vote").setValue(1);
                    VoteModel voteModel = new VoteModel();
                    voteModel.setUserid(userid);
                    voteModel.setVote(1);
                    votes.add(voteModel);
                    voteButton.setText("Remove Vote");
                    message = auth.getCurrentUser().getEmail()+" Added New Vote";
//                    requestQueue.add(VoteNotifications.sendNotification());

                }
                final String finalMessage = message;
                mDatabaseQueries.child("notifications").child(postUserid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        NotificationModel notificationModel = dataSnapshot.getValue(NotificationModel.class);
                        System.out.println(notificationModel.getAuth_token());
                        requestQueue.add(VoteNotifications.sendNotification(finalMessage,notificationModel.getAuth_token()));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
    public void displayQueryAnswerDialog()
    {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(answerQueryDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        answerQueryDialog.getWindow().setAttributes(lp);

        //get the key of the query
        // get the userid
        // update url will be something like /queries/{queryId}/answers/{userId}
        final EditText answerTitle = (EditText) answerQueryDialog.findViewById(R.id.queryAnswerText);
        Button saveAnswer = (Button) answerQueryDialog.findViewById(R.id.saveAnswer);
        saveAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(answerTitle.getText()))
                {
                    Toast.makeText(QueryDetailActivity.this,"Answer description is required",Toast.LENGTH_SHORT).show();
                    return;
                }
                String userid = auth.getCurrentUser().getUid();
                mDatabaseQueries.child("queries").child(queryId).child("answers").child(userid).child("title").setValue(answerTitle.getText().toString());
                mDatabaseQueries.child("queries").child(queryId).child("answers").child(userid).child("userid").setValue(userid);
                Toast.makeText(QueryDetailActivity.this,"Answer Successfully placed",Toast.LENGTH_SHORT).show();
                answerTitle.setText("");
            }
        });

        answerQueryDialog.show();
    }
}
