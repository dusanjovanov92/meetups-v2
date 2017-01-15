package com.dusanjovanov.meetups3.adapters;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dusanjovanov.meetups3.R;
import com.dusanjovanov.meetups3.models.Group;
import com.dusanjovanov.meetups3.models.User;
import com.dusanjovanov.meetups3.util.InterfaceUtil;

import java.util.ArrayList;

/**
 * Created by duca on 4/1/2017.
 */
public class GroupsRecyclerAdapter extends RecyclerView.Adapter<GroupsRecyclerAdapter.GroupViewHolder> {

    private ArrayList<Group> groups;
    private Context context;
    private User currentUser;
    private InterfaceUtil.OnRowClickListener listener;

    public interface RowClickListener{
        void onRowClick(Group group);
    }

    public GroupsRecyclerAdapter(ArrayList<Group> groups, Context context, User currentUser, InterfaceUtil.OnRowClickListener listener) {
        this.groups = groups;
        this.context = context;
        this.currentUser = currentUser;
        this.listener = listener;
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new GroupViewHolder(inflater.inflate(R.layout.item_group, parent, false));
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {
        holder.bindModel(groups.get(position));
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView ivIcon;
        private TextView txtName;
        private TextView txtMemberCount;
        private TextView txtAdmin;

        public GroupViewHolder(View itemView) {
            super(itemView);
            ivIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
            txtName = (TextView) itemView.findViewById(R.id.txt_name);
            txtMemberCount = (TextView) itemView.findViewById(R.id.txt_num_members);
            txtAdmin = (TextView) itemView.findViewById(R.id.txt_admin);

            itemView.setOnClickListener(this);
        }

        void bindModel(Group model) {
            ivIcon.setImageDrawable(InterfaceUtil.getTextDrawable(model.getName()));
            txtName.setText(model.getName());
            txtMemberCount.setText(String.format(context.getString(R.string.item_group_num_members), model.getMemberCount()));
            if (model.getAdmin().getId()== currentUser.getId()) {
                txtAdmin.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View view) {
            listener.onRowClick(groups.get(getAdapterPosition()));
        }
    }

    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
        }
    }
}
