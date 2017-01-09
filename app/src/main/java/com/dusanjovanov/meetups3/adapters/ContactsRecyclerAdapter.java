package com.dusanjovanov.meetups3.adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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

public class ContactsRecyclerAdapter extends RecyclerView.Adapter<ContactsRecyclerAdapter.ContactViewHolder>{

    private Context context;
    private ArrayList<Contact> contacts;
    private RowClickListener rowClickListener;

    public interface RowClickListener{
        void onClick(Contact contact);
    }

    public ContactsRecyclerAdapter(Context context, ArrayList<Contact> contacts,RowClickListener rowClickListener) {
        this.context = context;
        this.contacts = contacts;
        this.rowClickListener = rowClickListener;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        return new ContactViewHolder(inflater.inflate(R.layout.item_contact,parent,false));
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        holder.bindModel(contacts.get(position));
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView ivProfileImage;
        private CircleImageView civProfileImage;
        private TextView txtDisplayName;

        public ContactViewHolder(View itemView) {
            super(itemView);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.iv_profile_image);
            civProfileImage = (CircleImageView) itemView.findViewById(R.id.civ_profile_image);
            txtDisplayName = (TextView) itemView.findViewById(R.id.txt_display_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Contact contact = contacts.get(getAdapterPosition());
            rowClickListener.onClick(contact);
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

    public static class HorizontalDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable divider;

        public HorizontalDividerItemDecoration(Drawable divider) {
            this.divider = divider.mutate();
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount - 1; i++) {

                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params =
                        (RecyclerView.LayoutParams) child.getLayoutParams();
                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + divider.getIntrinsicHeight();
                divider.setBounds(left, top, right, bottom);
                divider.draw(c);

            }
        }
    }
}
