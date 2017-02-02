package com.dusanjovanov.meetups3;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.dusanjovanov.meetups3.rest.ApiClient;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAccountActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int ACTION_DONE = 0;

    private EditText edtDisplayName;
    private EditText edtEmail;
    private EditText edtPassword;

    private FirebaseAuth mFirebaseAuth;
    private GoogleApiClient mGoogleApiClient;

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
        String displayName = edtDisplayName.getText().toString();
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

        boolean error = false;
        String message = null;

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
            else{
                String json = ApiClient.getApi().checkEmailExists(email);
                Boolean emailExists = new Gson().fromJson(json,Boolean.class);

                if(emailExists){
                    error = true;
                    message = "Korisnik sa tom email adresom vec postoji";
                }
            }
        }

        if(error){
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
        else{
            createAccount(displayName,email,password);
        }

    }

    private void createAccount(String displayName, String email, String password){
        Toast.makeText(this, "Napravi nalog", Toast.LENGTH_SHORT).show();
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
