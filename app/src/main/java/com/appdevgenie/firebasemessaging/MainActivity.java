package com.appdevgenie.firebasemessaging;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.appdevgenie.firebasemessaging.Models.FirebaseCloudMessage;
import com.appdevgenie.firebasemessaging.Models.MessageData;
import com.appdevgenie.firebasemessaging.Utils.FirebaseCloudMessengerAPI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/";
    private static final String SERVER_KEY = "";
    private static final String TAG = "MainActivity";

    private EditText etTitle;
    private EditText etMessage;
    private Button bSend;
    private Set<String> tokens;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {

        tokens = new HashSet<>();

        etTitle = findViewById(R.id.etTitle);
        etMessage = findViewById(R.id.etMessage);

        bSend = findViewById(R.id.bSendMessage);
        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(etTitle.getText().toString(), etMessage.getText().toString());
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                Log.e("newToken", instanceIdResult.getToken());
            }
        });
    }

    private void sendMessage(String title, String message) {

        Log.d(TAG, "sendMessage: sending message");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //create the interface
        FirebaseCloudMessengerAPI firebaseCloudMessengerAPI = retrofit.create(FirebaseCloudMessengerAPI.class);

        //attach the headers
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "key=" + SERVER_KEY);

        //for(String token : tokens){

            //Log.d(TAG, "sendMessageToDepartment: sending to token: " + token);
            MessageData data = new MessageData();
            data.setMessage(message);
            data.setTitle(title);
            //data.setData_type(getString(R.string.data_type_admin_broadcast));
            FirebaseCloudMessage firebaseCloudMessage = new FirebaseCloudMessage();
            firebaseCloudMessage.setMessageData(data);
            //firebaseCloudMessage.setTo("e8aHnCAgji4:APA91bGN3IKVeMjphspBoCBANDUNt6zEuPl0UUSlQve4yUY945caAqL0KXU8U3fZdLEXUvfBvAN9aSNzCG1SbRlKj6NKnnw2b1aRugX1-C1bT7Iuth3UWwxR76gmq0-Tn-gPtFFIG1wP");

            Call<ResponseBody> call = firebaseCloudMessengerAPI.send(headers, firebaseCloudMessage);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d(TAG, "onResponse: Server Response: "  + response.toString());
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "onFailure: Unable to send the message." + t.getMessage() );
                }
            });
        //}
    }
}
