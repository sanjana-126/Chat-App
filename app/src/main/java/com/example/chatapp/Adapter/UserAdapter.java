package com.example.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.MessageActivity;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUser;
    private boolean isChat;


    public UserAdapter(Context mContext, List<User> mUser, boolean isChat)
    {
        this.mContext = mContext;
        this.mUser = mUser;
        this.isChat = isChat;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_single_layout, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        User user = mUser.get(position);
        holder.mName.setText(user.getName());
        holder.mStatus.setText(user.getStatus());
        if(isChat)
            holder.mStatus.setVisibility(View.GONE);
        else
            holder.mStatus.setVisibility(View.VISIBLE);
        if (user.getImage().equals("default"))
            holder.mImage.setImageResource(R.drawable.profilepic);
        else
            Picasso.with(mContext).load(user.getImage()).placeholder(R.drawable.profilepic).into(holder.mImage);

        if(isChat){
            if(user.getCurrentStatus().equals("online")) {
                holder.mOn.setVisibility(View.VISIBLE);
                holder.mOf.setVisibility(View.GONE);
            }
            else {
                holder.mOn.setVisibility(View.GONE);
                holder.mOf.setVisibility(View.VISIBLE);
            }
        }
        else{
            holder.mOn.setVisibility(View.GONE);
            holder.mOf.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid",user.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mName,mStatus;
        public CircleImageView mImage;
        private ImageView mOn,mOf;

        public ViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.user_single_username);
            mStatus = itemView.findViewById(R.id.user_single_status);
            mImage = itemView.findViewById(R.id.user_single_profilePic);
            mOn = itemView.findViewById(R.id.img_on);
            mOf = itemView.findViewById(R.id.img_off);
        }
    }
}
