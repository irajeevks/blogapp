package com.example.reviewapp.reviewapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reviewapp.reviewapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AnswerDetailActivity extends AppCompatActivity {

    Button updateAnswer;
    EditText answerEditText;
    TextView query_titleTextView;
    TextView query_contentTextView;
    private DatabaseReference mDatabaseAnswers;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_detail);

        //Get the data from the ListView
        final String title = getIntent().getStringExtra("TITLE");
        final String queryId = getIntent().getStringExtra("QUERYID");
        final String userid = getIntent().getStringExtra("USERID");
        final String query_title = getIntent().getStringExtra("QUERY_TITLE");
        final String query_content = getIntent().getStringExtra("QUERY_CONTENT");

        updateAnswer = (Button) findViewById(R.id.updateAnswer);
        answerEditText = (EditText) findViewById(R.id.answerEditText);
        query_titleTextView = (TextView) findViewById(R.id.query_title);
        query_contentTextView = (TextView) findViewById(R.id.query_content);

        query_titleTextView.setText("Q: " + query_title);
        query_contentTextView.setText(query_content);

        answerEditText.setText(title);
        auth = FirebaseAuth.getInstance();
        mDatabaseAnswers = FirebaseDatabase.getInstance().getReference();

        updateAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if the textbox text is changed or not.
                //If change then value needs to be update.
                //else toast a message change the answer to update.
                if (TextUtils.isEmpty(answerEditText.getText().toString()))
                {
                    Toast.makeText(AnswerDetailActivity.this,"Field is required",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (title.equals(answerEditText.getText().toString()))
                {
                    Toast.makeText(AnswerDetailActivity.this,"Change the answer to update",Toast.LENGTH_SHORT).show();
                    return;
                }
                mDatabaseAnswers.child("queries").child(queryId).child("answers").child(userid).child("title").setValue(answerEditText.getText().toString());
                Toast.makeText(AnswerDetailActivity.this,"Answer Successfully Changed",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
