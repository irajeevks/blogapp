package com.example.reviewapp.reviewapp.notifications;

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VoteNotifications {

    public static JsonObjectRequest sendNotification(String message,String auth_token)
    {
        JSONObject jsonParams = new JSONObject();

        JSONObject object = new JSONObject();
        try {
            object.put("title","New Message");
            object.put("message",message);
            jsonParams.put("to",auth_token);
            jsonParams.put("notification", object);

            System.out.println(jsonParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest myRequest = new JsonObjectRequest(
                Request.Method.POST,
                "https://fcm.googleapis.com/fcm/send",
                jsonParams,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Response Success"+response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Response Error"+error);
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", "key=AAAAfYKWujg:APA91bFLJApuJiVRNsxkSBs44e3HHSN4vwsno2PGa_QtnxkXqUF6I13cx5A6tGqfL0nVCtGAlFuiijGl-VK94k0-pAUx9lKNAY4_hfibYZ-AbeWS_i-2fu5OwqJnOagWlsKHueOaTEEu");
                return headers;
            }
        };
        return myRequest;
    }
}
