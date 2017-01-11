package com.dusanjovanov.meetups3.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dusanjovanov.meetups3.R;
import com.dusanjovanov.meetups3.models.User;

import java.util.ArrayList;

/**
 * Created by duca on 11/1/2017.
 */

public class MembersRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private ArrayList<User> members;
    private LayoutInflater layoutInflater;

    public MembersRecyclerAdapter(Context context, ArrayList<User> members) {
        this.context = context;
        this.members = members;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==R.id.header){
            return new HeaderHolder(layoutInflater.inflate(R.layout.item_header,parent,false));
        }
        else{
            return new RowHolder(layoutInflater.inflate(R.layout.item_member,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderHolder){
            String header = null;
            if(position==0){
                header = "Administrator";
            }
            else{
                header = "Članovi";
            }
            ((HeaderHolder) holder).bindModel(header);
        }
        else{
            ((RowHolder)holder).bindModel(members.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return members.size()+2;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0 || position==2){
            return R.id.header;
        }
        else{
            return R.id.row;
        }
    }

    class HeaderHolder extends RecyclerView.ViewHolder{

        private TextView txtHeader;

        public HeaderHolder(View itemView) {
            super(itemView);
            txtHeader = (TextView) itemView.findViewById(R.id.txt_header);
        }

        void bindModel(String header){
            txtHeader.setText(header);
        }
    }

    class RowHolder extends RecyclerView.ViewHolder{

        public RowHolder(View itemView) {
            super(itemView);
        }

        void bindModel(User model){

        }
    }
}
