package com.appdevgenie.firebasemessaging.Utils;

import com.appdevgenie.firebasemessaging.Models.FirebaseCloudMessage;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface FirebaseCloudMessengerAPI {

    @POST("send")
    Call<ResponseBody> send(
            @HeaderMap Map<String, String> headers,
            @Body FirebaseCloudMessage firebaseCloudMessage
    );
}
