package com.fyp.smhs.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fyp.smhs.Activities.PostDetailActivity;
import com.fyp.smhs.Models.Post;
import com.fyp.smhs.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    private static final String TAG = "hi" ;
    Context mContext;
    List<Post> mData;


    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item,parent,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tvTitle.setText(mData.get(position).getTitle());
        holder.tvDesc.setText(mData.get(position).getDescription());
       // Glide.with(mContext).load(mData.get(position).getPicture()).into(holder.imgPost);
        Glide.with(mContext).load(R.drawable.default_user_photo).into(holder.imgPostProfile);
       // String userImg = mData.get(position).getUserPhoto();

       // Glide.with(mContext).load(R.drawable.userphoto).into(holder.imgPostProfile);

      /*  if (userImg != null){
            Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(holder.imgPostProfile);


        }else
            Glide.with(mContext).load(R.drawable.userphoto).into(holder.imgPostProfile);


       */
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvDesc;
        ImageView imgPost;
        ImageView imgPostProfile;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.row_post_title);
            tvDesc = itemView.findViewById(R.id.row_post_desc);
            imgPost = itemView.findViewById(R.id.row_post_img);
            imgPostProfile = itemView.findViewById(R.id.row_post_profile_img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent postDetailActivity = new Intent(mContext, PostDetailActivity.class);
                    int position = getAdapterPosition();


                    postDetailActivity.putExtra("title",mData.get(position).getTitle());
                    postDetailActivity.putExtra("postImage",mData.get(position).getPicture());
                    postDetailActivity.putExtra("description",mData.get(position).getDescription());
                    postDetailActivity.putExtra("postKey",mData.get(position).getPostKey());
                    postDetailActivity.putExtra("userPhoto",mData.get(position).getUserPhoto());

                   // will fix this later i forgot to add user name to post object
                   // postDetailActivity.putExtra("userName",mData.get(position).getUsername);
                   Object timestamp = (Object) mData.get(position).getTimeStamp();

                    String convertedToString = String.valueOf(timestamp);
                    String milisecoonds = convertedToString.substring(18,28);
                    int mili = Integer.parseInt(milisecoonds);

                  //  Date date = new Date(mili * 1000);
                    //String str = new SimpleDateFormat("dd/MM/yyyy").format(date);
                    //DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    //format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
                    //String formatted = format.format(date);

                    Date date = new Date(mili * 1000L);
                    DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                    format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
                    String formatted = format.format(date);
                    //maybe problem


                postDetailActivity.putExtra("postDate",formatted);
                    mContext.startActivity(postDetailActivity);






                }
            });



        }


    }
}
