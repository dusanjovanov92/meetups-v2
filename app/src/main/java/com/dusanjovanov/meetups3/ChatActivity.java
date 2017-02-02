package com.dusanjovanov.meetups3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dusanjovanov.meetups3.fragments.ContactsFragment;
import com.dusanjovanov.meetups3.models.ChatMessage;
import com.dusanjovanov.meetups3.models.Contact;
import com.dusanjovanov.meetups3.models.Group;
import com.dusanjovanov.meetups3.models.Meeting;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.util.ConstantsUtil;
import com.dusanjovanov.meetups3.util.DateTimeUtil;
import com.dusanjovanov.meetups3.util.InterfaceUtil;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private DatabaseReference messageRef;
    private DatabaseReference messageNode;
    private RecyclerView rvMessages;
    private FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder> adapter;
    private Contact contact;
    private User currentUser;
    private Meeting meeting;
    private Group group;
    private ProgressBar progressBar;
    private LinearLayoutManager layoutManager;
    private EditText edtMessage;
    private ImageButton btnSend;
    private String intentAction = "action";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        handleIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            String title = null;
            if (intentAction.equals(ContactsFragment.TAG)) {
                title = contact.getUser().getDisplayName();
            }
            else if (intentAction.equals(MeetingActivity.TAG)) {
                title = meeting.getLabel();
            }
            actionBar.setTitle(title);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        setupRecyclerView();
        setupMessageViews();

    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            intentAction = intent.getStringExtra("action");

            if (intentAction.equals(ContactsFragment.TAG)) {
                currentUser = (User) intent.getSerializableExtra("user");
                contact = (Contact) intent.getSerializableExtra("contact");
                messageRef = FirebaseDatabase.getInstance().getReference().child("chat");
                messageNode = messageRef.child(String.valueOf(currentUser.getId())).child(contact.getFirebaseNode());
            }
            else if (intentAction.equals(MeetingActivity.TAG)) {
                meeting = (Meeting) intent.getSerializableExtra(ConstantsUtil.EXTRA_MEETING);
                currentUser = (User) intent.getSerializableExtra(ConstantsUtil.EXTRA_CURRENT_USER);
                group = (Group) intent.getSerializableExtra(ConstantsUtil.EXTRA_GROUP);
                messageRef = FirebaseDatabase.getInstance().getReference().child("meetings");
                messageNode = messageRef.child(meeting.getFirebaseNode());
            }
        }
    }

    private void setupMessageViews() {
        edtMessage = (EditText) findViewById(R.id.edt_message);
        btnSend = (ImageButton) findViewById(R.id.btn_send);
        btnSend.setEnabled(false);

        edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                if (charSequence.toString().trim().length() > 0) {
                    btnSend.setEnabled(true);
                } else {
                    btnSend.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnSend.isEnabled()){
                    sendMessage();
                }
            }
        });
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage;
        CircleImageView civProfileImage;
        TextView txtDisplayName;
        TextView txtMessage;
        TextView txtTime;

        public MessageViewHolder(View itemView) {
            super(itemView);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.iv_profile_image);
            civProfileImage = (CircleImageView) itemView.findViewById(R.id.civ_profile_image);
            txtDisplayName = (TextView) itemView.findViewById(R.id.txt_display_name);
            txtMessage = (TextView) itemView.findViewById(R.id.txt_message);
            txtTime = (TextView) itemView.findViewById(R.id.txt_time);
        }
    }

    private void setupRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMessages = (RecyclerView) findViewById(R.id.rv_messages);
        adapter = new FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>(
                ChatMessage.class,
                R.layout.item_chat_message,
                MessageViewHolder.class,
                messageNode) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, ChatMessage model, int position) {
                progressBar.setVisibility(View.INVISIBLE);
                if (position == 0) {
                    viewHolder.itemView.setVisibility(View.GONE);
                }
                if (model.getPhotoUrl() == null) {
                    viewHolder.ivProfileImage.setImageDrawable(InterfaceUtil.getTextDrawable(model.getDisplayName()));
                } else {
                    viewHolder.ivProfileImage.setVisibility(View.GONE);
                    viewHolder.civProfileImage.setVisibility(View.VISIBLE);
                    Picasso.with(ChatActivity.this).load(model.getPhotoUrl()).into(viewHolder.civProfileImage);
                }

                viewHolder.txtDisplayName.setText(model.getDisplayName());
                viewHolder.txtMessage.setText(model.getMessage());
                viewHolder.txtTime.setText(DateTimeUtil.getChatDateTime(model.getTime(), ChatActivity.this));
            }
        };
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = adapter.getItemCount();
                int lastVisiblePosition =
                        layoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    rvMessages.scrollToPosition(positionStart);
                }
            }
        });
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(adapter);
    }

    private void sendMessage() {
        String messageText = edtMessage.getText().toString().trim();
        ChatMessage message = new ChatMessage(currentUser.getDisplayName(), currentUser.getPhotoUrl(), messageText, System.currentTimeMillis());
        if (intentAction.equals(ContactsFragment.TAG)) {
            messageRef.child(String.valueOf(currentUser.getId())).child(contact.getFirebaseNode()).push().setValue(message);
            messageRef.child(String.valueOf(contact.getUser().getId())).child(contact.getFirebaseNode()).push().setValue(message);
        }
        else if (intentAction.equals(MeetingActivity.TAG)) {
            messageRef.child(meeting.getFirebaseNode()).push().setValue(message);
        }
        edtMessage.setText("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return true;
    }
}
