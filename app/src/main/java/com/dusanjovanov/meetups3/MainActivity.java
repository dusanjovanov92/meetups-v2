package com.dusanjovanov.meetups3;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dusanjovanov.meetups3.models.ChatMessage;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.rest.ApiClient;
import com.dusanjovanov.meetups3.util.ConstantsUtil;
import com.dusanjovanov.meetups3.util.FirebaseUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "TagMainActivity";
    private GoogleApiClient googleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private User currentUser;
    private SharedPreferences preferences;
    private Button btnAdresa;
    private Button btnPokreni;
    private TextView txtAdresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = FirebaseUtil.getCurrentUser();

        btnAdresa = (Button) findViewById(R.id.btn_adresa);
        btnPokreni = (Button) findViewById(R.id.btn_pokreni);
        txtAdresa = (TextView) findViewById(R.id.txt_adresa);
        txtAdresa.setText(ApiClient.BASE_URL);
        btnAdresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Promeni adresu")
                        .setPositiveButton("Promeni", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Dialog dialog = (Dialog)dialogInterface;
                                EditText edtName = (EditText) dialog.findViewById(R.id.edt);
                                promeniAdresu(edtName.getText().toString());
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("Otkaži", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                final EditText input = new EditText(MainActivity.this);
                input.setId(R.id.edt);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                input.setText(ApiClient.BASE_URL);
                builder.setView(input);

                builder.create().show();
            }
        });

        btnPokreni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doTasks();
            }
        });

        if(mFirebaseUser == null){
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }
    }

    private void promeniAdresu(String adresa){
        ApiClient.BASE_URL = adresa;
        txtAdresa.setText(adresa);
    }

    private void doTasks(){
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = preferences.getString(ConstantsUtil.REGISTRATION_TOKEN,null);
        Call<User> call = ApiClient.getApi().updateToken(mFirebaseUser.getEmail(),token);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    currentUser = response.body();

                    AlarmManager alarmManager = (AlarmManager) MainActivity.this.getSystemService(ALARM_SERVICE);
                    Intent alarmIntent = new Intent(MainActivity.this,AlarmReceiver.class);
                    alarmIntent.putExtra(ConstantsUtil.EXTRA_CURRENT_USER,currentUser);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0,alarmIntent
                            ,PendingIntent.FLAG_UPDATE_CURRENT);

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis()+5000,
                            60000,
                            pendingIntent);

                    getNumberOfMessages();

                }
                else{
//                    mFirebaseAuth.signOut();
                    Toast.makeText(MainActivity.this, "Greska", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
//                mFirebaseAuth.signOut();
                Toast.makeText(MainActivity.this, "Greska", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getNumberOfMessages(){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("chat").child(String.valueOf(currentUser.getId())).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int firebaseMessageCount = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            for (DataSnapshot snapshot1 : snapshot.getChildren()){
                                ChatMessage message = snapshot1.getValue(ChatMessage.class);
                                if(!message.getDisplayName().equals(mFirebaseUser.getDisplayName())){
                                    firebaseMessageCount++;
                                }
                            }
                            firebaseMessageCount--;
                        }

                        int idUser = preferences.getInt("id_user",-1);

                        if(idUser!=currentUser.getId()){
                            preferences.edit()
                                    .putInt("message_count",firebaseMessageCount)
                                    .putInt("new_message_count",0)
                                    .putInt("id_user",currentUser.getId())
                                    .apply();
                        }

                        Intent intent = new Intent(MainActivity.this,MainScreenActivity.class);
                        intent.putExtra(ConstantsUtil.EXTRA_ACTION,TAG);
                        intent.putExtra(ConstantsUtil.EXTRA_CURRENT_USER,currentUser);

                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        FirebaseUtil.showGooglePlayErrorDialog(this);
    }
}
