package layoutrecycle;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weaboo.jurnalotaku.R;

public class ChatBoxHolder extends RecyclerView.ViewHolder{
    public TextView text,time;
    public ImageView profileImg;
    public ChatBoxHolder(@NonNull View itemView) {
        super(itemView);
        this.profileImg=itemView.findViewById(R.id.chat_profile_picture);
        this.text=itemView.findViewById(R.id.chat_message);
        this.time=itemView.findViewById(R.id.chat_date);
    }
}
