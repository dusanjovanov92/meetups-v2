package com.dusanjovanov.meetups3.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dusanjovanov.meetups3.R;
import com.dusanjovanov.meetups3.models.Contact;
import com.dusanjovanov.meetups3.util.InterfaceUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by duca on 5/1/2017.
 */

public class ContactsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private ArrayList<Contact> contacts;
    private LayoutInflater inflater;
    private OnRowClickListener listener;

    public interface OnRowClickListener extends InterfaceUtil.OnRowClickListener {
        void onClickDelete(Contact contact,int adapterPosition);
    }

    public ContactsRecyclerAdapter(Context context, ArrayList<Contact> contacts) {
        this.context = context;
        this.contacts = contacts;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOnRowClickListener(OnRowClickListener listener){
        this.listener = listener;
    }

    private boolean isOnRowClickListenerSet(){
        return this.listener != null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==R.id.row){
            return new RowHolder(inflater.inflate(R.layout.item_contact,parent,false));
        }
        else{
            return new InterfaceUtil.NoResultsHolder(inflater.inflate(R.layout.item_no_results,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof RowHolder){
            ((RowHolder)holder).bindModel(contacts.get(position));
        }
        else{
            ((InterfaceUtil.NoResultsHolder)holder).bind("Trenutno nemate nikog u kontaktima");
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            if(contacts.size()==0){
                return R.id.no_results;
            }
        }
        return R.id.row;
    }

    @Override
    public int getItemCount() {
        return contacts.size()==0 ? 1: contacts.size();
    }

    private class RowHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView ivProfileImage;
        private CircleImageView civProfileImage;
        private TextView txtDisplayName;
        private ImageView ivDelete;

        public RowHolder(View itemView) {
            super(itemView);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.iv_profile_image);
            civProfileImage = (CircleImageView) itemView.findViewById(R.id.civ_profile_image);
            txtDisplayName = (TextView) itemView.findViewById(R.id.txt_display_name);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
            ivDelete.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(isOnRowClickListenerSet()){
                Contact contact = contacts.get(getAdapterPosition());
                switch (view.getId()){
                    case R.id.iv_delete:
                        listener.onClickDelete(contact,getAdapterPosition());
                        break;
                    default:
                        listener.onRowClick(contact);
                        break;
                }
            }
        }

        void bindModel(Contact model){
            if(model.getUser().getPhotoUrl()==null){
                ivProfileImage.setImageDrawable(InterfaceUtil.getTextDrawable(model.getUser().getDisplayName()));
            }
            else{
                ivProfileImage.setVisibility(View.GONE);
                civProfileImage.setVisibility(View.VISIBLE);
                Picasso.with(context).load(model.getUser().getPhotoUrl()).into(civProfileImage);
            }

            txtDisplayName.setText(model.getUser().getDisplayName());
        }

    }

}
