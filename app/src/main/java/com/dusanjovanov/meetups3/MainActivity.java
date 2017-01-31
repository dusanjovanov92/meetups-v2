package com.dusanjovanov.meetups3;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.rest.ApiClient;
import com.dusanjovanov.meetups3.util.ConstantsUtil;
import com.dusanjovanov.meetups3.util.FirebaseUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "TagMainActivity";
    private GoogleApiClient googleApiClient;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        currentUser = FirebaseUtil.getCurrentUser();

        if(currentUser==null){
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }
        else{
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String token = preferences.getString(ConstantsUtil.REGISTRATION_TOKEN,null);
            Call<User> call = ApiClient.getApi().updateToken(currentUser.getEmail(),token);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if(response.isSuccessful()){
                        User currentUser = response.body();

                        AlarmManager alarmManager = (AlarmManager) MainActivity.this.getSystemService(ALARM_SERVICE);
                        Intent alarmIntent = new Intent(MainActivity.this,AlarmReceiver.class);
                        alarmIntent.putExtra(ConstantsUtil.EXTRA_CURRENT_USER,currentUser);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0,alarmIntent
                                ,PendingIntent.FLAG_UPDATE_CURRENT);

                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                                System.currentTimeMillis()+5000,
                                60000,
                                pendingIntent);

                        Intent intent = new Intent(MainActivity.this,MainScreenActivity.class);
                        intent.putExtra(ConstantsUtil.EXTRA_ACTION,TAG);
                        intent.putExtra(ConstantsUtil.EXTRA_CURRENT_USER,response.body());

                        startActivity(intent);
                        finish();

                    }
                    else{
                        Toast.makeText(MainActivity.this, "Greska", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Greska", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        FirebaseUtil.showGooglePlayErrorDialog(this);
    }
}
