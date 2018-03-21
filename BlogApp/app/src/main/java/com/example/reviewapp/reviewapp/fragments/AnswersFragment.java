package com.example.reviewapp.reviewapp.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.example.reviewapp.reviewapp.R;
import com.example.reviewapp.reviewapp.adapter.AnswerAdapter;
import com.example.reviewapp.reviewapp.models.AnswerModel;
import com.example.reviewapp.reviewapp.models.QueryModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.*;

public class AnswersFragment extends Fragment{

    private FirebaseAuth auth;
    private ListView anserListView;
    private List<AnswerModel> answers;
    private AnswerAdapter answer_adapter;
    private DatabaseReference mDatabaseQueries;

    private List<QueryModel> queries;
    private List<String> query_title;
    private List<String> query_content;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.fragment_answers,container,false);
        answers = new ArrayList<>();
        queries = new ArrayList<>();
        query_title = new ArrayList<>();
        query_content = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        anserListView = (ListView) rootView.findViewById(R.id.answer_list_view);
        mDatabaseQueries = FirebaseDatabase.getInstance().getReference("queries");
        return rootView;
    }
    @Override
    public void onStart()
    {
        super.onStart();
        mDatabaseQueries.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                queries.clear();
                answers.clear();
                query_title.clear();
                query_content.clear();

                Log.d("SnapShot Value ",dataSnapshot.getChildren().toString());
                for (DataSnapshot querySnapshot: dataSnapshot.getChildren())
                {
                    QueryModel query = querySnapshot.getValue(QueryModel.class);
                    query.setKey(querySnapshot.getKey());
                    queries.add(query);
                }

                HashMap<String, AnswerModel> answerModelHashMap;
                String currentUserid = auth.getCurrentUser().getUid();
                for (QueryModel query:queries)
                {
                    answerModelHashMap = query.getAnswers();
                    Iterator iterator = answerModelHashMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry queryAnswers= (Map.Entry) iterator.next();

                        AnswerModel answer = (AnswerModel) queryAnswers.getValue();
                        //check if the answer userid is null or not otherwise it will give exception
                        //check if the answer has same userid or not.
                        //If the userid is different then we don't need to add it in the child.
                        if (answer.getUserid() !=null) {
                            if (answer.getUserid().equals(currentUserid)) {
                                answer.setQueryId(query.getKey());
                                answers.add(answer);
                                query_title.add(query.getTitle());
                                query_content.add(query.getQuery());
                            }
                        }

                    }
                }
                answer_adapter = new AnswerAdapter(getContext(),answers, query_title, query_content);
                anserListView.setAdapter(answer_adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
