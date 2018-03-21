package com.example.reviewapp.reviewapp.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.*;

import com.example.reviewapp.reviewapp.ApplicationController;
import com.example.reviewapp.reviewapp.activities.LoginActivity;
import com.example.reviewapp.reviewapp.R;
import com.example.reviewapp.reviewapp.models.LocationModel;
import com.example.reviewapp.reviewapp.models.QueryModel;
import com.example.reviewapp.reviewapp.models.UserModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class ProfilesFragment extends Fragment {

    private String[] gender = {"Male", "Female"};
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseQueries;
    private List<QueryModel> queryModelList;
    private FirebaseAuth auth;
    private EditText dobEditText;
    private Spinner genderSpinner;
    private EditText phoneNumberEditText;
    private Button saveProfileButton;
    private Button signOutButton;
    private Button selectLocation;
    private int PLACE_PICKER_REQUEST = 1;
    private final static int RESULT_OK = -1;
    private GoogleSignInClient mGoogleSignInClient;
    private ArrayList<LocationModel> locations;
    private ArrayList<String> locationStrings;
    private ListView locationListView;
    private ArrayAdapter locationsAdapter;
    private RatingBar reputation;
    private String userid;



    //reputation count number of votes by the user.
    // if 5 votes then the star color will increase .5 and fill with yellow color
    // 50 users votes means the star will fill.

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        dobEditText = (EditText) rootView.findViewById(R.id.BirthdayFinal);
        genderSpinner = (Spinner) rootView.findViewById(R.id.genderspinnerFinal);
        phoneNumberEditText = (EditText) rootView.findViewById(R.id.txt_profile_phone_number);
        saveProfileButton = (Button) rootView.findViewById(R.id.btn_save_profile_final);
        signOutButton = (Button) rootView.findViewById(R.id.btn_sign_out_final);
        selectLocation = (Button) rootView.findViewById(R.id.btn_get_place);
        locationListView = (ListView) rootView.findViewById(R.id.location_list);
        locations = new ArrayList<>();
        locationStrings = new ArrayList<>();
        queryModelList = new ArrayList<>();
        reputation = (RatingBar) rootView.findViewById(R.id.ratingBarFinal);
        locationsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, locationStrings);
        auth = FirebaseAuth.getInstance();
        userid = auth.getCurrentUser().getUid();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users").child(userid);
        mDatabaseQueries = FirebaseDatabase.getInstance().getReference("queries");
        final Calendar ageCalendar = Calendar.getInstance();

        locationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                locations.remove(position);
                locationsAdapter.notifyDataSetChanged();
                locationListView.setAdapter(locationsAdapter);
            }
        });

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        //Save The User Profile
        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String selectedGender = genderSpinner.getSelectedItem().toString();
                String selectedAge = dobEditText.getText().toString();
                String phoneNumber = phoneNumberEditText.getText().toString();


                if (locations.size() == 0 || locations.size() > 3) {
                    Toast.makeText(getContext(), "Maximum 3 Locations Minimum 1 Location", Toast.LENGTH_LONG).show();
                    return;
                }
                if (auth.getCurrentUser() != null) {
                    Toast.makeText(getContext(), "Gender: " + selectedGender + " Age " + selectedAge + " Phone Number " + phoneNumber, Toast.LENGTH_LONG).show();
                    UserModel user = new UserModel();
                    user.setBirthDate(selectedAge);
                    user.setPhoneNumber(phoneNumber);
                    user.setGender(selectedGender);
                    user.setLocations(locations);
                    mDatabaseUsers.setValue(user);
                    ApplicationController.getInstance().setUserModel(user);
                    //goto the main swipe Activity
                    Toast.makeText(getContext(), "Profile Successfully Updated", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Place Picker Button
        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (Exception ex) {
                    Log.e("Exception", ex.getMessage());
                }

            }
        });
        //End of place picker button

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                genderSpinner.setSelection(position);
                String selState = (String) genderSpinner.getSelectedItem();
                Toast.makeText(getContext(), "Your Gender:" + selState, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Set Age Calender
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                Log.d("Message", "This Line is for avoiding duplicate code");
                ageCalendar.set(Calendar.YEAR, year);
                ageCalendar.set(Calendar.MONTH, monthOfYear);
                ageCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(dobEditText, ageCalendar);
            }

        };

        dobEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), date, ageCalendar
                        .get(Calendar.YEAR), ageCalendar.get(Calendar.MONTH),
                        ageCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        //end of Age Calender
        //Sign out
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();

            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                updateUI(userModel);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabaseQueries.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                queryModelList.clear();
                for (DataSnapshot querySnapshot: dataSnapshot.getChildren())
                {
                    QueryModel query = querySnapshot.getValue(QueryModel.class);
                    query.setKey(querySnapshot.getKey());
                    queryModelList.add(query);
                }
                int sum = 0;
                //update the star by checking the upvotes.
                for (QueryModel query:queryModelList)
                {
                    if (query.getUserid().equals(userid))
                    {
                        sum += query.getVotes().size();
                    }
                }
                float noRating = 0;
                if (sum >= 5 && sum <10)
                {
                    noRating = .5f;
                }
                else if (sum >=10 && sum <15)
                {
                    noRating = 1.0f;
                }
                else if (sum >= 15 && sum <20)
                {
                    noRating =1.5f;
                }
                else if (sum >=20 && sum <25)
                {
                    noRating = 2.0f;
                }
                else if (sum >= 25 && sum <30)
                {
                    noRating =2.5f;
                }
                else if (sum >= 35 && sum <40)
                {
                    noRating =3.0f;
                }
                else if (sum >=40 && sum <45)
                {
                    noRating = 3.5f;
                }
                else if (sum >= 45 && sum <50)
                {
                    noRating =4.0f;
                }
                else if (sum >=50 && sum <55)
                {
                    noRating = 4.5f;
                }
                else if (sum >= 55)
                {
                    noRating =5.0f;
                }
                reputation.setRating(noRating);
                LayerDrawable stars = (LayerDrawable) reputation.getProgressDrawable();
                stars.getDrawable(2).setColorFilter(Color.parseColor("#ffff00"), PorterDuff.Mode.SRC_ATOP);
                System.out.println(sum);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateUI(UserModel userModel) {
        //Update the UI Accordingly.

        if (userModel !=null) {
            if (userModel.birthDate.equals("")) {
                dobEditText.setText("Date Of Birth");
            } else {
                dobEditText.setText(userModel.birthDate);
            }

            phoneNumberEditText.setText(userModel.getPhoneNumber());
            locations.clear();
            locationStrings.clear();
            locations.addAll(userModel.getLocations());
            for(int i = 0; i < locations.size(); i++){
                locationStrings.add(userModel.getLocations().get(i).getLocation());
            }
            locationsAdapter.notifyDataSetChanged();
            locationListView.setAdapter(locationsAdapter);

            System.out.println(gender + " is " + userModel.gender);
            //Gender Spinner
            ArrayAdapter<String> adapter_state = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, gender);
            adapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            genderSpinner.setAdapter(adapter_state);
            genderSpinner.setSelection(adapter_state.getPosition(userModel.gender));
        }

    }

    private void signOut() {

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(),
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            startActivity(new Intent(getContext(), LoginActivity.class));
                            getActivity().finish();
                        }
                    }
                });
        auth.signOut();
        startActivity(new Intent(getContext(), LoginActivity.class));
        getActivity().finish();

    }

    //This method will update the Birthdate
    private void updateLabel(EditText editText, Calendar calendar) {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editText.setText(sdf.format(calendar.getTime()));
    }

    //This Activity will fire when user will select Location
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //User can only select maximum 3 locations
        if (locations.size() < 3) {
            if (requestCode == PLACE_PICKER_REQUEST) {
                if (resultCode == RESULT_OK) {
                    locationStrings.clear();
                    Place place = PlacePicker.getPlace(getContext(), data);
                    String toastMsg = String.format("Address: %s", place.getAddress());
                    LocationModel locationModel = new LocationModel(toastMsg, place.getLatLng().longitude, place.getLatLng().latitude);
                    locations.add(locationModel);
                    locationStrings.add(toastMsg);
                    locationsAdapter.notifyDataSetChanged();
                    locationListView.setAdapter(locationsAdapter);
                    Toast.makeText(getContext(), toastMsg, Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(getContext(), "You can select maximum 3 locations", Toast.LENGTH_LONG).show();
        }

    }

}
