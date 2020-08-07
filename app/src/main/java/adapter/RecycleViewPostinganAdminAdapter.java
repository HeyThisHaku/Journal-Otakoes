package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weaboo.jurnalotaku.DetailPost;
import com.weaboo.jurnalotaku.FragmentManageJournal;
import com.weaboo.jurnalotaku.R;

import java.util.Vector;

import models.Post;
import session.Session;

public class RecycleViewPostinganAdminAdapter extends RecyclerView.Adapter<RecycleViewPostinganAdminAdapter.Postingan> {


    private Vector<Post> dataList;
    private Context parentContext;
    public RecycleViewPostinganAdminAdapter(Vector<Post> listPostingan, Context context) {
        this.dataList = listPostingan;
        this.parentContext = context;
    }


    @NonNull
    @Override
    public Postingan onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activity_manage_list_post, parent, false);
        return new Postingan(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Postingan holder, final int position) {
        holder.title.setText(dataList.get(position).getTitle());
        ImageAdapter.loadImageUrl(dataList.get(position).getImageUrl(),parentContext,holder.imageUrl);
        holder.imageUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parentContext, DetailPost.class);
                Session.setSession("id_postingan",dataList.get(position).getId());
                parentContext.startActivity(intent);
            }
        });
        holder.action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.action.isChecked())
                Session.addJournalQueue(dataList.get(position));
                else
                    Session.deleteJournalQueue(dataList.get(position));
//                String journal = FragmentManageJournal.journalHint.getText().toString();
//                FragmentManageJournal.journalHint.setText(journal+dataList.get(position).getId());
            }
        });
    }


    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class Postingan extends RecyclerView.ViewHolder{
        private TextView title;
        private ImageView imageUrl;
        private CheckBox action;
        public Postingan(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.title_post);
            this.imageUrl = itemView.findViewById(R.id.image_post);
            this.action = itemView.findViewById(R.id.simpleCheckBox);
        }
    }
}