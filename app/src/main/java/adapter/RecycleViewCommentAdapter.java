package adapter;

import android.content.Context;
import android.content.Intent;
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

import models.CommentModels;
import session.Session;

public class RecycleViewCommentAdapter extends RecyclerView.Adapter<RecycleViewCommentAdapter.Comment> {

    Vector<CommentModels> listComment = new Vector<>();
    Context context;
    public RecycleViewCommentAdapter(Vector<CommentModels> listComment,Context context){
        this.listComment = listComment;
        this.context = context;
    }

    @NonNull
    @Override
    public Comment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activity_layout_comment, parent, false);
        return new Comment(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Comment holder, int position) {
        holder.name.setText(listComment.get(position).getName());
        holder.comment.setText(" : "+listComment.get(position).getComment());
        holder.like.setText(listComment.get(position).getLiked()+R.string.liked);
    }

    @Override
    public int getItemCount() {
        return (listComment != null) ? listComment.size() : 0;
    }

    public class Comment extends RecyclerView.ViewHolder{
        private TextView name,comment,like;
        public Comment(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.name_comment);
            this.comment = itemView.findViewById(R.id.comment_comment);
            this.like = itemView.findViewById(R.id.liked_comment);
        }
    }
}
