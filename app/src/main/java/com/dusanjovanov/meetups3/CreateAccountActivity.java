package com.dusanjovanov.meetups3;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.rest.ApiClient;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.gson.Gson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccountActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int ACTION_DONE = 0;

    private EditText edtDisplayName;
    private EditText edtEmail;
    private EditText edtPassword;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;

    private boolean error = false;
    private String message = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        setupToolbar();
        initializeViews();
    }

    private void setupToolbar(){
        Toolbar appBar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(appBar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("Napravi nalog");
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeViews(){
        edtDisplayName = (EditText) findViewById(R.id.edt_display_name);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPassword = (EditText) findViewById(R.id.edt_password);
    }

    private void checkInput() {
        final String displayName = edtDisplayName.getText().toString();
        final String email = edtEmail.getText().toString();
        final String password = edtPassword.getText().toString();

        if(displayName.length()<1 || email.length()<1 || password.length()<1){
            error = true;
            message = "Morate popuniti sva polja";
        }
        else if(displayName.length()<6){
            error = true;
            message = "Polje korisničko ime mora imati barem 6 karaktera";
        }
        else if(password.length()<6){
            error = true;
            message = "Lozinka mora da ima barem 6 karaktera";
        }
        else{
            String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
            Pattern p = java.util.regex.Pattern.compile(ePattern);
            Matcher m = p.matcher(email);

            if(!m.matches()){
                error = true;
                message = "Email adresa koju ste uneli nije ispravna";
            }

        }

        if(error){
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return;
        }

        Call<String> call = ApiClient.getApi().checkEmailExists(email);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    Boolean emailExists = new Gson().fromJson(response.body(),Boolean.class);

                    if(emailExists){
                        Toast.makeText(CreateAccountActivity.this, "Korisnik sa tim email-om vec postoji", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        createAccount(displayName,email,password);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void createAccount(final String displayName, final String email, String password){
        mFirebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mFirebaseUser = mFirebaseAuth.getCurrentUser();
                            UserProfileChangeRequest updateRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayName)
                                    .build();

                            mFirebaseUser.updateProfile(updateRequest)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CreateAccountActivity.this);
                                                String token = preferences.getString("token",null);
                                                Call<User> call = ApiClient.getApi().createUser(displayName,email,null,token);
                                                call.enqueue(new Callback<User>() {
                                                    @Override
                                                    public void onResponse(Call<User> call, Response<User> response) {
                                                        if(response.isSuccessful()){
                                                            mFirebaseAuth.signOut();
                                                            new AlertDialog.Builder(CreateAccountActivity.this)
                                                                    .setTitle("Uspeh")
                                                                    .setIcon(R.drawable.ic_info_outline_blue_36dp)
                                                                    .setMessage("Uspesno ste napravili nalog")
                                                                    .setPositiveButton("nastavi", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                                            dialogInterface.dismiss();
                                                                            onBackPressed();
                                                                        }
                                                                    })
                                                                    .show();
                                                        }
                                                        else{
                                                            deleteFirebaseUser();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<User> call, Throwable t) {
                                                        deleteFirebaseUser();
                                                    }
                                                });
                                            }
                                            else{
                                                deleteFirebaseUser();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void deleteFirebaseUser(){
        mFirebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CreateAccountActivity.this, "Doslo je do greske", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem actionDone = menu.add(Menu.NONE,ACTION_DONE,Menu.NONE,"Napravi nalog");
        actionDone.setIcon(R.drawable.ic_check_black_36dp);
        actionDone.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case ACTION_DONE:
                checkInput();
        }
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
