package com.dusanjovanov.meetups3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.dusanjovanov.meetups3.models.Response;
import com.dusanjovanov.meetups3.util.FirebaseUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener{

    private static final int RC_GOOGLE_SIGN_IN = 9001;
    public static final String TAG = "tagSignInActivity";
    private GoogleApiClient googleApiClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    //views
    private EditText edtEmail,edtPassword;
    private Button btnSignIn,btnCreateAccount;
    private SignInButton btnGoogleSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        firebaseAuth = FirebaseAuth.getInstance();

        initializeViews();

        setButtonListeners();
    }

    private void initializeViews(){
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        btnGoogleSign = (SignInButton) findViewById(R.id.btn_google_sign_in);
        btnSignIn = (Button) findViewById(R.id.btn_sign_in);
        btnCreateAccount = (Button) findViewById(R.id.btn_create_account);
    }

    private void setButtonListeners(){
        btnGoogleSign.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        btnCreateAccount.setOnClickListener(this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        FirebaseUtil.showGooglePlayErrorDialog(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_google_sign_in:
                showGoogleAccountChooser();
                break;
            case R.id.btn_sign_in:
                firebaseSignIn();
                break;
        }
    }

    private boolean checkEmailAndPassword(){
        String email = edtEmail.getText().toString();
        String password =edtPassword.getText().toString();

        if(email.length()<1 || password.length()<1){
            Toast.makeText(this, R.string.sign_in_activity_5, Toast.LENGTH_SHORT).show();
            return false;
        }

        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = java.util.regex.Pattern.compile(ePattern);
        Matcher m = p.matcher(email);
        if(!m.matches()){
            Toast.makeText(this, R.string.sign_in_activity_6, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void firebaseSignIn() {
        if(checkEmailAndPassword()){
            String email = edtEmail.getText().toString();
            String password=  edtPassword.getText().toString();

            firebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(SignInActivity.this,MainScreenActivity.class));
                                finish();
                            }
                            else{
                                Toast.makeText(SignInActivity.this, "Failed to sign in. Try again later.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void showGoogleAccountChooser() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseSignInWithGoogle(account);
            } else {

                Log.e(TAG, "Google Sign In failed.");
            }
        }
    }

    private void firebaseSignInWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGooogle:" + account.getId());
        final AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            createUser();

                        }
                    }
                });
    }

    private void createUser() {
        Map<String,String> params = new HashMap<>();
        params.put("display_name",currentUser.getDisplayName());
        params.put("email",currentUser.getEmail());
        params.put("photo_url",String.valueOf(currentUser.getPhotoUrl()));
        params.put("token","123");

        StandardRequest<Response> request = new StandardRequest<>(
                Request.Method.POST,
                "/users",
                params,
                Response.class,
                new StandardRequest.ResponseListener<Response>() {
                    @Override
                    public void onResponse(Response response) {
                        if(response.isError()){
                            Log.e(TAG,"Server error");
                            deleteUserFirebase();
                        }
                        else{
                            startActivity(new Intent(SignInActivity.this,MainScreenActivity.class));
                            finish();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG,error.toString());
                        deleteUserFirebase();
                    }
                }
        );
        VolleyHandler.getInstance(this).addToRequestQueue(request);
    }

    private void deleteUserFirebase(){
        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(SignInActivity.this, "Sign in failed. Try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
