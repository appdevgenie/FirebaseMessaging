package com.appdevgenie.firebasemessaging;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.appdevgenie.firebasemessaging.Models.FirebaseCloudMessage;
import com.appdevgenie.firebasemessaging.Models.MessageData;
import com.appdevgenie.firebasemessaging.Utils.FirebaseCloudMessengerAPI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    //private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;

    private EditText etTitle;
    private EditText etMessage;
    private Button bSend;
    private Set<String> tokens;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        //setupAuthStateListener();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        switch (itemID) {
            case R.id.menu_sign_out:
                firebaseAuth.signOut();
                finish();
                return true;

            /*case R.id.menu_exit:
                finish();
                return true;*/

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*private void setupAuthStateListener() {

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if(firebaseUser == null){
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }*/

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

        for(String token : tokens){

            //Log.d(TAG, "sendMessageToDepartment: sending to token: " + token);
            MessageData data = new MessageData();
            data.setMessage(message);
            data.setTitle(title);
            //data.setData_type(getString(R.string.data_type_admin_broadcast));
            FirebaseCloudMessage firebaseCloudMessage = new FirebaseCloudMessage();
            firebaseCloudMessage.setMessageData(data);
            //firebaseCloudMessage.setTo();

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
        }
    }

    /*@Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
        //isActivityRunning = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        }
        //isActivityRunning = false;
    }*/
}
