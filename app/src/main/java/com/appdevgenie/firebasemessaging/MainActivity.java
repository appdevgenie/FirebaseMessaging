package com.appdevgenie.firebasemessaging;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.appdevgenie.firebasemessaging.Adapters.UserAdapter;
import com.appdevgenie.firebasemessaging.Models.FirebaseCloudMessage;
import com.appdevgenie.firebasemessaging.Models.Data;
import com.appdevgenie.firebasemessaging.Models.User;
import com.appdevgenie.firebasemessaging.Utils.FirebaseCloudMessengerAPI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.appdevgenie.firebasemessaging.Utils.Constants.DB_SERVER;
import static com.appdevgenie.firebasemessaging.Utils.Constants.DB_USERS;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/";
    //private static final String SERVER_KEY = "";
    private static final String TAG = "MainActivity";

    //private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;

    private EditText etTitle;
    private EditText etMessage;
    private Button bSend;
    private Set<String> tokens;
    private String serverKey;
    private RecyclerView recyclerView;
    private ArrayList<User> userList;
    private UserAdapter userAdapter;

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
        userList = new ArrayList<>();

        recyclerView = findViewById(R.id.rvUsers);
        userAdapter = new UserAdapter(this, userList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(userAdapter);

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

        getServerKey();
        getUserList();
        getTokens();
    }


    private void getUserList() throws NullPointerException{
        Log.d(TAG, "getUserList: getting a list of all users");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(DB_USERS);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    Log.d(TAG, "onDataChange: found a user: " + user.getName());
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getTokens() {
        Log.d(TAG, "getUserTokens: getting a list of all user tokens");

        tokens.clear();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(DB_USERS);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String token = snapshot.getValue(User.class).getToken();
                    Log.d(TAG, "onDataChange: got a token for user named: "
                            + snapshot.getValue(User.class).getName());
                    tokens.add(token);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
        headers.put("Authorization", "key=" + serverKey);

        for(String token : tokens){

            Log.d(TAG, "sendMessage: sending to token: " + token);
            Data data = new Data();
            data.setMessage(message);
            data.setTitle(title);
            //data.setData_type(getString(R.string.data_type_admin_broadcast));
            FirebaseCloudMessage firebaseCloudMessage = new FirebaseCloudMessage();
            firebaseCloudMessage.setData(data);
            firebaseCloudMessage.setTo(token);

            Call<ResponseBody> call = firebaseCloudMessengerAPI.send(headers, firebaseCloudMessage);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    Log.d(TAG, "onResponse: Server Response: "  + response.toString());
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: Unable to send the message." + t.getMessage() );
                }
            });
        }
    }

    private void getServerKey(){
        Log.d(TAG, "getServerKey: retrieving server key.");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(DB_SERVER)
                .orderByValue();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: got the server key.");
                DataSnapshot singleSnapshot = dataSnapshot.getChildren().iterator().next();
                serverKey = singleSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
