package com.example.reviewapp.reviewapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.example.reviewapp.reviewapp.activities.QueryDetailActivity;
import com.example.reviewapp.reviewapp.R;
import com.example.reviewapp.reviewapp.models.AnswerModel;
import com.example.reviewapp.reviewapp.models.QueryModel;
import com.example.reviewapp.reviewapp.models.VoteModel;

import java.util.*;

public class QueryAdapter extends BaseAdapter {
    Context context;
    List<QueryModel> queries;
    int type;
    public QueryAdapter(Context context,List<QueryModel> queries, int type)
    {
        this.context = context;
        this.queries = queries;
        this.type = type;
    }
    @Override
    public int getCount() {
        return queries.size();
    }

    @Override
    public Object getItem(int position) {
        return queries.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View queryView, ViewGroup parent) {
        if (queryView == null)
        {
            queryView = LayoutInflater.from(context).inflate(R.layout.queries_model,parent,false);
        }
        TextView query_title = (TextView) queryView.findViewById(R.id.query_title);
        final QueryModel queryModel =(QueryModel) this.getItem(position);
        query_title.setText(queryModel.getTitle());
        Log.d("Votes",queryModel.getVotes().toString());

        //on item click
        queryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,queryModel.getTitle(),Toast.LENGTH_SHORT).show();

                ArrayList<AnswerModel> answerList = new ArrayList<>();
                ArrayList<VoteModel> voteList = new ArrayList<>();

                Iterator iterator = queryModel.getAnswers().entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry answers= (Map.Entry) iterator.next();
                    System.out.println("Key: "+answers.getKey() + " & Value: " + answers.getValue());

                    AnswerModel answer = (AnswerModel) answers.getValue();
                    answerList.add(answer);
                }

                Iterator votesIterator = queryModel.getVotes().entrySet().iterator();
                while (votesIterator.hasNext())
                {
                    Map.Entry votes = (Map.Entry) votesIterator.next();

                    VoteModel vote = (VoteModel) votes.getValue();
                    voteList.add(vote);
                    System.out.println("Vote User ID"+vote.userid);
                    System.out.println(vote.vote);
                }
                Intent intent = new Intent(context, QueryDetailActivity.class);
                intent.putExtra("TITLE", queryModel.getTitle());
                intent.putExtra("QUERY",queryModel.getQuery());
                intent.putExtra("USERID",queryModel.getUserid());
                intent.putExtra("KEY",queryModel.getKey());
                intent.putExtra("ANSWERS",answerList);
                intent.putExtra("VOTES",voteList);
                intent.putExtra("TYPE", type);
                context.startActivity(intent);
            }
        });
        return queryView;
    }
}
