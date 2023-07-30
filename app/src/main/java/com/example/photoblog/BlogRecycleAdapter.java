package com.example.photoblog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecycleAdapter extends RecyclerView.Adapter<BlogRecycleAdapter.ViewHolder> {
    public List<BlogPost> blog_list;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public BlogRecycleAdapter(List<BlogPost> blog_list)
    {
        this.blog_list=blog_list;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list,parent,false);
        context=parent.getContext();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String currentUserId=firebaseAuth.getCurrentUser().getUid();
        String desc_data = blog_list.get(position).getDesc();
        holder.setDescText(desc_data);
        String uri=blog_list.get(position).getImage_url();
        holder.setBlog(uri);
        String uid=blog_list.get(position).getUser_id();
        String blogPostId=blog_list.get(position).BlogPostId;
        firebaseFirestore.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    String username=task.getResult().getString("name");
                    String userimage=task.getResult().getString("image");
                    holder.setUserData(username,userimage);

                }
                else
                {

                }
            }
        });
        String pattern="MM/dd/yyyy";
        DateFormat df = new SimpleDateFormat(pattern);
        if(blog_list.get(position).getTimestamp()!=null) {
            long milliseconds = blog_list.get(position).getTimestamp().getTime();
            Date date = new Date(milliseconds);
            String formattedDate = df.format(date);
            //String formattedDate = DateFormat.format("MM/dd/yyyy", utilDate, Locale.getDefault()).toString();
            holder.setDate(formattedDate);
            firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(!value.isEmpty())
                    {
                        int n=value.size();
                        holder.updatelikescount(n);

                    }
                    else
                    {
                        holder.updatelikescount(0);
                    }
                }
            });
            firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(value.exists())
                    {
                        holder.bloglikeimage.setImageDrawable(context.getDrawable(R.mipmap.action_like_accent));
                    }
                    else
                    {
                        holder.bloglikeimage.setImageDrawable(context.getDrawable(R.mipmap.action_like_gray));
                    }

                }
            });
        }
        holder.bloglikeimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists())
                        {
                            Map<String,Object> likeMap=new HashMap<>();
                            likeMap.put("Timestamp", FieldValue.serverTimestamp());
                            firebaseFirestore.collection("Posts").document(blogPostId).collection("Likes").document(currentUserId).set(likeMap);

                        }
                        else
                        {
                            firebaseFirestore.collection("Posts").document(blogPostId).collection("Likes").document(currentUserId).delete();
                        }


                    }
                });


            }
        });


    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView descV;
        private View mView;
        private ImageView blogImageView;
        private TextView blogdate;
        private TextView uname;
        private CircleImageView blog_user_image;
        private ImageView bloglikeimage;
        private TextView likecount;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            bloglikeimage=mView.findViewById(R.id.like_btn);
            likecount=mView.findViewById(R.id.like_count);
        }
        public void setDescText(String text)
        {
            descV=mView.findViewById(R.id.post_des);
            descV.setText(text);
        }
        public void setBlog(String downloaduri)
        {
//            RequestOptions requestOptions=new RequestOptions();
//            requestOptions.placeholder(R.drawable.)
            blogImageView=mView.findViewById(R.id.post_img);
            Glide.with(context).load(downloaduri).into(blogImageView);
        }
        public void setDate(String date)
        {
            blogdate=mView.findViewById(R.id.post_date);
            blogdate.setText(date);

        }
        public void setUserData(String name,String image)
        {
            blog_user_image=mView.findViewById(R.id.post_image);
            uname=mView.findViewById(R.id.post_usr);
            RequestOptions placeholderoption=new RequestOptions();
            placeholderoption.placeholder(R.mipmap.user_icon);
            Glide.with(context).applyDefaultRequestOptions(placeholderoption).load(image).into(blog_user_image);
            uname.setText(name);


        }
        public void updatelikescount(int n)
        {
            likecount=mView.findViewById(R.id.like_count);
            likecount.setText(n+"Likes");
        }

    }
}
