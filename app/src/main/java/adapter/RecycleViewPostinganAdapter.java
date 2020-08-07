package adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weaboo.jurnalotaku.DetailPost;
import com.weaboo.jurnalotaku.R;

import java.util.Vector;

import models.Post;
import session.Session;

public class RecycleViewPostinganAdapter extends RecyclerView.Adapter<RecycleViewPostinganAdapter.Postingan> {


    private Vector<Post> dataList;
    private Context parentContext;
    public RecycleViewPostinganAdapter(Vector<Post> listPostingan,Context context) {
        this.dataList = listPostingan;
        this.parentContext = context;
    }


    @NonNull
    @Override
    public Postingan onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activity_postingan_recycleview, parent, false);
        return new Postingan(view);
    }

    public void setDataList(Vector<Post> dataList) {
        this.dataList = dataList;
    }

    @Override
    public void onBindViewHolder(@NonNull Postingan holder, final int position) {
        holder.title.setText(dataList.get(position).getTitle());
        holder.author.setText(R.string.posted_by+dataList.get(position).getAuthor());
        ImageAdapter.loadImageUrl(dataList.get(position).getImageUrl(),parentContext,holder.imageUrl);
        holder.imageUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parentContext, DetailPost.class);
                Session.setSession("id_postingan",dataList.get(position).getId());
                parentContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class Postingan extends RecyclerView.ViewHolder{
        private TextView title,author;
        private ImageView imageUrl;
        public Postingan(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.title_post);
            this.author = itemView.findViewById(R.id.author_post);
            this.imageUrl = itemView.findViewById(R.id.image_post);
        }
    }
}