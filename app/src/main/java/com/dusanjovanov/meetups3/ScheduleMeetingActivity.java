package com.dusanjovanov.meetups3;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dusanjovanov.meetups3.models.ChatMessage;
import com.dusanjovanov.meetups3.models.Group;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.rest.ApiClient;
import com.dusanjovanov.meetups3.util.ConstantsUtil;
import com.dusanjovanov.meetups3.util.DateTimeUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleMeetingActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int ACTION_DONE = 0;

    private Group group;
    private User currentUser;

    private EditText edtLabel;
    private LinearLayout llDate;
    private TextView txtDate;
    private LinearLayout llTime;
    private TextView txtTime;

    private Calendar cal = Calendar.getInstance();
    private DateFormat dfDate;
    private DateFormat dfTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_meeting);

        handleIntent();

        Toolbar appBar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(appBar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("Zakazivanje sastanka");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        dfDate = android.text.format.DateFormat.getMediumDateFormat(this);
        dfTime = android.text.format.DateFormat.getTimeFormat(this);

        initializeViews();
    }

    private void handleIntent(){
        Intent intent = getIntent();
        String action = null;
        if(intent!=null){
            action = intent.getStringExtra(ConstantsUtil.EXTRA_ACTION);
            if(action!=null && action.equals(GroupActivity.TAG)){
                group = (Group) intent.getSerializableExtra(ConstantsUtil.EXTRA_GROUP);
                currentUser = (User) intent.getSerializableExtra(ConstantsUtil.EXTRA_CURRENT_USER);
            }
        }
    }

    private void initializeViews(){
        edtLabel = (EditText) findViewById(R.id.edt_label);
        llDate = (LinearLayout) findViewById(R.id.ll_date);
        txtDate = (TextView) findViewById(R.id.txt_date);
        llTime = (LinearLayout) findViewById(R.id.ll_time);
        txtTime = (TextView) findViewById(R.id.txt_time);

        llDate.setOnClickListener(this);
        llTime.setOnClickListener(this);

        setDateTime();
    }

    private void setDateTime(){
        Date date = new Date();

        txtDate.setText(dfDate.format(date));
        txtTime.setText(dfTime.format(date));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_date:
                DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(year,month,day);
                        Date date = cal.getTime();
                        txtDate.setText(dfDate.format(date));
                    }
                },cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                break;
            case R.id.ll_time:
                TimePickerDialog dialog1 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(0,0,0,hour,minute);
                        Date date = cal.getTime();
                        txtTime.setText(dfTime.format(date));
                    }
                },cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),true);
                dialog1.show();
                break;
        }
    }

    private void checkInput(){
        boolean error = false;
        String message = null;

        String label = edtLabel.getText().toString();

        if(label.length()<=0){
            error = true;
            message = "Morate da unesete naslov sastanka";
        }

        String dateTime = txtDate.getText()+" "+txtTime.getText();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy. HH.mm", Locale.getDefault());

        Calendar cal = null;

        try {
            Date date = dateFormat.parse(dateTime);
            cal = Calendar.getInstance();
            cal.setTime(date);

            if(System.currentTimeMillis()>cal.getTimeInMillis()){
                error = true;
                message = "Sastanak ne sme biti zakazan u prošlom vremenu";
            }

        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        if(error){
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
        else{
            scheduleMeeting(cal.getTimeInMillis(),label);
        }
    }

    private void scheduleMeeting(final long startTime, String label){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        String firebaseNode = dbRef.child("meetings").push().getKey();

        ChatMessage message = new ChatMessage("a",null,"a",0);

        dbRef.child("meetings").child(firebaseNode).setValue(message);

        Call<Void> call = ApiClient.getApi().scheduleMeeting(group.getId(),startTime/1000,firebaseNode,label);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    new AlertDialog.Builder(ScheduleMeetingActivity.this)
                            .setTitle("Zakazan sastanak")
                            .setMessage("Sastanak je zakazan za "+DateTimeUtil.getDateTime(startTime,ScheduleMeetingActivity.this))
                            .setIcon(R.drawable.ic_info_outline_blue_36dp)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    onBackPressed();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem actionDone = menu.add(Menu.NONE,ACTION_DONE,Menu.NONE,"Done");
        actionDone.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        actionDone.setIcon(R.drawable.ic_check_black_36dp);

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
                break;
        }

        return true;
    }
}
