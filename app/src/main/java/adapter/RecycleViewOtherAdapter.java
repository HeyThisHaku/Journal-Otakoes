package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.weaboo.jurnalotaku.R;

import java.util.Vector;

import models.OtherPost;


public class RecycleViewOtherAdapter extends RecyclerView.Adapter<RecycleViewOtherAdapter.Other> {

    Vector<OtherPost> listOtherPost = new Vector<>();
    Context context;
    public RecycleViewOtherAdapter(Vector<OtherPost> listOtherPost,Context context){
        this.context = context;
        this.listOtherPost = listOtherPost;
    }
    @NonNull
    @Override
    public Other onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activity_other_post_layout, parent, false);
        return new RecycleViewOtherAdapter.Other(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Other holder, final int position) {
        holder.title.setText(listOtherPost.get(position).getTitle());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoId = listOtherPost.get(position).getVideo_url();
                playVideo(videoId, holder.player);
            }
        });
    }

    public void playVideo(final String videoId, YouTubePlayerView youTubePlayerView) {
        //initialize youtube player view
        youTubePlayerView.initialize(videoId,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {

                        youTubePlayer.cueVideo(videoId);
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });
    }


    @Override
    public int getItemCount() {
        return (listOtherPost != null) ? listOtherPost.size() : 0;
    }

    public class Other extends RecyclerView.ViewHolder{
        private TextView title;
        private YouTubePlayerView player;
        private ImageView button;
        public Other(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.other_title);
            this.player = itemView.findViewById(R.id.other_player);
            this.button = itemView.findViewById(R.id.play_btn);
        }
    }
}
