package com.dusanjovanov.meetups3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dusanjovanov.meetups3.fragments.ContactsFragment;
import com.dusanjovanov.meetups3.models.ChatMessage;
import com.dusanjovanov.meetups3.models.Contact;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.util.InterfaceUtil;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private DatabaseReference chatRef;
    private RecyclerView rvMessages;
    private FirebaseRecyclerAdapter<ChatMessage,MessageViewHolder> adapter;
    private Contact contact;
    private User currentUser;
    private ProgressBar progressBar;
    private LinearLayoutManager layoutManager;
    private EditText edtMessage;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        handleIntent();

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        setupRecyclerView();
        setupMessageViews();

    }

    private void setupMessageViews(){
        edtMessage = (EditText) findViewById(R.id.edt_message);
        btnSend = (Button) findViewById(R.id.btn_send);

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
                sendMessage();
            }
        });
    }

    private void handleIntent(){
        Intent intent = getIntent();
        String action = null;
        if(intent!=null){
            action = intent.getStringExtra("action");
        }
        if(action!=null){
            if(action.equals(ContactsFragment.TAG)){
                currentUser = (User) intent.getSerializableExtra("user");
                contact = (Contact) intent.getSerializableExtra("contact");
            }
        }
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder{

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

    private void setupRecyclerView(){
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMessages = (RecyclerView) findViewById(R.id.rv_messages);
        chatRef = FirebaseDatabase.getInstance().getReference().child("chat").child(contact.getFirebaseNode());
        adapter = new FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>(
                ChatMessage.class,
                R.layout.item_chat_message,
                MessageViewHolder.class,
                chatRef) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, ChatMessage model, int position) {
                progressBar.setVisibility(View.INVISIBLE);

                if(model.getPhotoUrl()==null){
                    viewHolder.ivProfileImage.setImageDrawable(InterfaceUtil.getTextDrawable(model.getDisplayName()));
                }
                else{
                    viewHolder.ivProfileImage.setVisibility(View.GONE);
                    viewHolder.civProfileImage.setVisibility(View.VISIBLE);
                    Picasso.with(ChatActivity.this).load(model.getPhotoUrl()).into(viewHolder.civProfileImage);
                }

                viewHolder.txtDisplayName.setText(model.getDisplayName());
                viewHolder.txtMessage.setText(model.getMessage());
                viewHolder.txtTime.setText(String.valueOf(model.getTime()));
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

    private void sendMessage(){
        String messageText = edtMessage.getText().toString().trim();
        ChatMessage message = new ChatMessage(currentUser.getDisplayName(),currentUser.getPhotoUrl(),messageText,System.currentTimeMillis());

        chatRef.push().setValue(message);
        edtMessage.setText("");
    }

}
