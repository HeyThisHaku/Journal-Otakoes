package adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weaboo.jurnalotaku.CatalogJournal;
import com.weaboo.jurnalotaku.R;

import java.util.Vector;

import env.Env;
import models.CommentModels;
import models.JournalModel;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.Journal> {

    Vector<JournalModel> listJournal = new Vector<>();
    Context context;
    public JournalAdapter(Vector<JournalModel> listJournal,Context context){
        this.listJournal = listJournal;
        this.context = context;
    }

    @NonNull
    @Override
    public JournalAdapter.Journal onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activity_journal_layout, parent, false);
        return new JournalAdapter.Journal(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final JournalAdapter.Journal holder, final int position) {
        holder.title.setText(listJournal.get(position).getId());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //download link
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(Env.jurnalZip+listJournal.get(position).getLink(), Env.jurnalZip+listJournal.get(position).getLink());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context.getApplicationContext(),R.string.copied_from_clipboard,Toast.LENGTH_SHORT).show();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Env.jurnalZip+listJournal.get(position).getLink()));
                context.startActivity(browserIntent);
//                new DownloadTask(context, Env.jurnalZip+"/"+listJournal.get(position).getLink());

            }
        });
        holder.link.setText("");
//        holder.link.setText(Env.jurnalZip+listJournal.get(position).getLink());
    }



    @Override
    public int getItemCount() {
        return (listJournal != null) ? listJournal.size() : 0;
    }

    public class Journal extends RecyclerView.ViewHolder{
        private TextView title;
        private Button button;
        private TextView link;
        public Journal(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.journal_title);
            this.button = itemView.findViewById(R.id.journal_download);
            this.link = itemView.findViewById(R.id.link_journal);
        }
    }
}
