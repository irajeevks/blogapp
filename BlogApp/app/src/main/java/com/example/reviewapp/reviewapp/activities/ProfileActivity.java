package com.example.reviewapp.reviewapp.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.example.reviewapp.reviewapp.ApplicationController;
import com.example.reviewapp.reviewapp.R;
import com.example.reviewapp.reviewapp.models.LocationModel;
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
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private String userName;
    private ListView location_list;
    private ArrayList<LocationModel> loc_list;
    private ArrayList<String> locationStrings;
    private EditText phoneNumber;
    Spinner genderspinner;
    Button save_profile;
    int PLACE_PICKER_REQUEST = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth auth;
    private DatabaseReference mDatabaseUsers;
    private ArrayAdapter locationAdapter;
    Button btn_get_place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btn_get_place = findViewById(R.id.btn_get_place);
        location_list = findViewById(R.id.location_list);
        loc_list = new ArrayList();
        locationStrings = new ArrayList<>();
        phoneNumber = findViewById(R.id.txt_profile_phone_number);
        save_profile = findViewById(R.id.btn_save_profile);
        genderspinner = findViewById(R.id.spinner_gender);

        auth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");
        locationAdapter = new ArrayAdapter<>(this,
                R.layout.listview_item, locationStrings);

        final Calendar myCalendar = Calendar.getInstance();
        final EditText edittext= findViewById(R.id.et_date_selector);
        //final Button signOut = findViewById(R.id.btn_sign_out);

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        location_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                loc_list.remove(position);
                locationAdapter.notifyDataSetChanged();
                location_list.setAdapter(locationAdapter);
            }
        });

        //Save The User Profile
        save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validate all the properties.
                String selectedGender = genderspinner.getSelectedItem().toString();
                String selectedAge = edittext.getText().toString();
                String phone = phoneNumber.getText().toString();
                double ratings = 0;
                if (loc_list.size()==0 || loc_list.size()>3) {
                    Toast.makeText(ProfileActivity.this, "Maximum 3 Locations Minimum 1 Location", Toast.LENGTH_LONG).show();
                    return;
                }
                if (auth.getCurrentUser() !=null) {
                    String userid = auth.getCurrentUser().getUid();
                    Toast.makeText(ProfileActivity.this, "Gender: "+selectedGender+" Age "+selectedAge+" Phone Number "+phone, Toast.LENGTH_LONG).show();
                    UserModel user = new UserModel(userName, phone,selectedGender,selectedAge,ratings,loc_list);
                    mDatabaseUsers.child(userid).setValue(user);
                    ApplicationController.getInstance().setUserModel(user);
                    //goto the main swipe Activity
                    startActivity(new Intent(ProfileActivity.this, MainSwipeActivity.class));
                    finish();
                }
            }
        });

        //Gender Spinner
//        ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item, gender);
//        adapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        genderspinner.setAdapter(adapter_state);
        genderspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                genderspinner.setSelection(position);
                String selState = (String) genderspinner.getSelectedItem();
                Toast.makeText(ProfileActivity.this, "Your Gender:" + selState, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //End of Gender Spinner

        //Place Picker Button
        btn_get_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(ProfileActivity.this), PLACE_PICKER_REQUEST);
                }
                catch (Exception ex)
                {
                    Log.e("Exception",ex.getMessage());
                }

            }
        });
        //End of place picker button

        //Set Age Calender
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(edittext,myCalendar);
            }

        };

        edittext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ProfileActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        //end of Age Calender


        //Sign out
//        signOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                signOut();
//
//            }
//        });
    }

    private void signOut() {
        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            startActivity(new Intent(ProfileActivity.this,LoginActivity.class));
                            finish();
                        }
                    }
                });
        auth.signOut();
        startActivity(new Intent(ProfileActivity.this,LoginActivity.class));
        finish();
    }
    //This method will update the Birthdate
    private void updateLabel(EditText editText,Calendar calendar) {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editText.setText(sdf.format(calendar.getTime()));
    }
    //This Activity will fire when user will select Location
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //User can only select maximum 3 locations
        if (loc_list.size()<3) {
            if (requestCode == PLACE_PICKER_REQUEST) {
                if (resultCode == RESULT_OK) {
                    locationStrings.clear();
                    Place place = PlacePicker.getPlace(this, data);
                    String toastMsg = String.format("Address: %s", place.getAddress());
                    LocationModel locationModel = new LocationModel(toastMsg, place.getLatLng().longitude, place.getLatLng().latitude);
                    loc_list.add(locationModel);
                    locationStrings.add(toastMsg);
                    locationAdapter.notifyDataSetChanged();
                    location_list.setAdapter(locationAdapter);
                    Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                }
            }
        }
        else {
            Toast.makeText(this, "You can select maximum 3 locations", Toast.LENGTH_LONG).show();
        }

    }
}
