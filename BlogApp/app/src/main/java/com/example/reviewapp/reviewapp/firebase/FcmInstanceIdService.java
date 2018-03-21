package com.example.reviewapp.reviewapp.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FcmInstanceIdService extends FirebaseInstanceIdService {

    private FirebaseAuth auth;
    private DatabaseReference notificationReference;
    @Override
    public void onTokenRefresh()
    {
        String recent_token = FirebaseInstanceId.getInstance().getToken();

        //Store this token into the firebase database inside notification node.
        auth = FirebaseAuth.getInstance();
        notificationReference = FirebaseDatabase.getInstance().getReference("notifications");
        if (auth.getCurrentUser() != null)
        {
            String userid = auth.getCurrentUser().getUid();
            //send it to the firebase database with the current userid
            notificationReference.child(userid).child("auth_token").setValue(recent_token);
        }


    }
}
