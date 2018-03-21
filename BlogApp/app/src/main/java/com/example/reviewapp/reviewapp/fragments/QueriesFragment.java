package com.example.reviewapp.reviewapp.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reviewapp.reviewapp.ApplicationController;
import com.example.reviewapp.reviewapp.R;
import com.example.reviewapp.reviewapp.activities.ProfileActivity;
import com.example.reviewapp.reviewapp.adapter.QueryAdapter;
import com.example.reviewapp.reviewapp.models.LocationModel;
import com.example.reviewapp.reviewapp.models.QueryModel;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class QueriesFragment extends Fragment{
    //Initialize Firebase
    //Initialize UI
    private ListView query_list, my_query_list;
    private List<QueryModel> queries;
    private List<QueryModel> my_queries;
    private QueryAdapter query_adapter, my_query_adapter;
    private DatabaseReference mDatabaseQueries;
    private FloatingActionButton addQueryDialog;
    private Dialog placeQuery;
    private FirebaseAuth auth;
    private int PLACE_PICKER_REQUEST = 1;
    private final static int RESULT_OK = -1;
    private Button btn_get_place;
    private TextView location_text;
    private LocationModel locationModel;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_queries,container,false);
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
        mDatabaseQueries = FirebaseDatabase.getInstance().getReference("queries");
        queries = new ArrayList<>();
        my_queries = new ArrayList<>();
        query_list = (ListView) rootView.findViewById(R.id.query_list_view);
        my_query_list = (ListView) rootView.findViewById(R.id.my_query_list_view);
        addQueryDialog = (FloatingActionButton) rootView.findViewById(R.id.fab);

        //Custom Dialog for Placing new query.
        placeQuery = new Dialog(getContext());
        placeQuery.setTitle("Place Query");
        placeQuery.setContentView(R.layout.place_query_dialog);

        addQueryDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open the place query dialog
                displayPlaceQueryDialog();
            }
        });

        //
        return rootView;
    }

    //Display place query dialog
    public void displayPlaceQueryDialog()
    {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(placeQuery.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        placeQuery.getWindow().setAttributes(lp);

        //get the textbox value
        final EditText title = (EditText) placeQuery.findViewById(R.id.titleEditText);
        final EditText querytext = (EditText) placeQuery.findViewById(R.id.queryEditText);

        btn_get_place = placeQuery.findViewById(R.id.btn_get_place);
        location_text = placeQuery.findViewById(R.id.location_text);

        btn_get_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                }
                catch (Exception ex)
                {
                    Log.e("Exception",ex.getMessage());
                }
            }
        });

        final Button saveQuery = (Button) placeQuery.findViewById(R.id.saveQuery);
        saveQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //validate the textbox.
                if (TextUtils.isEmpty(title.getText().toString()))
                {
                    Toast.makeText(getContext(),"Title is required",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(querytext.getText().toString()))
                {
                    Toast.makeText(getContext(),"Description is required",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(location_text.getText().toString()))
                {
                    Toast.makeText(getContext(),"Location is required",Toast.LENGTH_SHORT).show();
                    return;
                }
                //Save the data into the Firebase Database.
                //First get the userid
                String userid = auth.getCurrentUser().getUid();
                String key = mDatabaseQueries.push().getKey();

                QueryModel query = new QueryModel(title.getText().toString(),querytext.getText().toString(),userid, locationModel);
                mDatabaseQueries.child(key).setValue(query);
                title.setText("");
                querytext.setText("");
            }
        });
        //
        placeQuery.show();
    }
    @Override
    public void onStart()
    {
        super.onStart();
        mDatabaseQueries.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                queries.clear();
                my_queries.clear();
                Log.d("SnapShot Value ",dataSnapshot.getChildren().toString());
                for (DataSnapshot querySnapshot: dataSnapshot.getChildren())
                {
                    QueryModel query = querySnapshot.getValue(QueryModel.class);
                    query.setKey(querySnapshot.getKey());
                    if(userId.equals(query.getUserid())){
                        my_queries.add(query);
                    } else {
                        boolean isContained = false;
                        for (int i = 0; i < ApplicationController.getInstance().getUserModel().getLocations().size(); i++) {
                            float[] distance = new float[2];
                            if (query.getLocation() == null) {
                                continue;
                            }
                            Location.distanceBetween(query.getLocation().getLat(),
                                    query.getLocation().getLon(),
                                    ApplicationController.getInstance().getUserModel().getLocations().get(i).getLat(),
                                    ApplicationController.getInstance().getUserModel().getLocations().get(i).getLon(),
                                    distance);
                            if (distance[0] < 5000) {
                                isContained = true;
                                break;
                            }
                        }
                        if (isContained)
                            queries.add(query);
                    }
                }
                query_list.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 118 * queries.size()));
                query_adapter = new QueryAdapter(getContext(),queries, 1);
                query_list.setAdapter(query_adapter);

                my_query_list.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 118 * my_queries.size()));
                my_query_adapter = new QueryAdapter(getContext(),my_queries, 0);
                my_query_list.setAdapter(my_query_adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(getContext(), data);
                String toastMsg = String.format("Address: %s", place.getAddress());
                locationModel = new LocationModel(toastMsg, place.getLatLng().longitude, place.getLatLng().latitude);
                location_text.setText(toastMsg);
                Toast.makeText(getContext(), toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }
}
