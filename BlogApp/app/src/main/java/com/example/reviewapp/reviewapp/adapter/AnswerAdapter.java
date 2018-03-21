package com.example.reviewapp.reviewapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.reviewapp.reviewapp.activities.AnswerDetailActivity;
import com.example.reviewapp.reviewapp.R;
import com.example.reviewapp.reviewapp.models.AnswerModel;

import java.util.List;

public class AnswerAdapter extends BaseAdapter {
    Context context;
    List<AnswerModel> answers;
    List<String> query_title;
    List<String> query_content;

    public AnswerAdapter(Context context,List<AnswerModel> answers, List<String> query_title, List<String> query_content)
    {
        this.context = context;
        this.answers = answers;
        this.query_title = query_title;
        this.query_content = query_content;
    }
    @Override
    public int getCount() {
        return answers.size();
    }

    @Override
    public Object getItem(int position) {
        return answers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View answerView, ViewGroup parent) {
        if (answerView == null) {
            answerView = LayoutInflater.from(context).inflate(R.layout.answers_model, parent,false);
        }
        TextView answer_title = (TextView) answerView.findViewById(R.id.answer_title);
        final AnswerModel answerModel = (AnswerModel) this.getItem(position);
        answer_title.setText(answerModel.getTitle());

        //on item click
        answerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AnswerDetailActivity.class);
                intent.putExtra("TITLE", answerModel.getTitle());
                intent.putExtra("QUERYID",answerModel.getQueryId());
                intent.putExtra("USERID",answerModel.getUserid());
                intent.putExtra("QUERY_TITLE", query_title.get(position));
                intent.putExtra("QUERY_CONTENT", query_content.get(position));
                context.startActivity(intent);
            }
        });
        return answerView;
    }
}
