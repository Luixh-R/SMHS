package com.fyp.smhs.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fyp.smhs.Models.Comment;
import com.fyp.smhs.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mContext;
    private List<Comment> mData;

    public CommentAdapter(Context mContext, List<Comment> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.row_comment,parent,false);
        return new CommentViewHolder(row);

    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {

       // Glide.with(mContext).load(mData.get(position).getUid()).into(holder.img_user);
      //  holder.tv_name.setText(mData.get(position).getUname());
        holder.tv_name.setText("Anonymous");
        holder.tv_content.setText(mData.get(position).getContent());
        Glide.with(mContext).load(R.drawable.default_user_photo).into(holder.img_user);

        Object timestamp = (Object) mData.get(position).getTimestamp();
        String convertedToString = String.valueOf(timestamp);
        String milisecoonds = convertedToString.substring(18,28);
        int mili = Integer.parseInt(milisecoonds);
        Date date = new Date(mili * 1000L);
        DateFormat format = new SimpleDateFormat("dd-MM-yy hh:mm");
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String formatted = format.format(date);

        holder.tv_date.setText(formatted);



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        ImageView img_user;
        TextView tv_name,tv_content,tv_date;



        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            img_user = itemView.findViewById(R.id.comment_user_img);
            tv_name = itemView.findViewById(R.id.comment_username);
            tv_content = itemView.findViewById(R.id.comment_content);
            tv_date = itemView.findViewById(R.id.comment_date);
        }
    }



}


