package layoutrecycle;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weaboo.jurnalotaku.R;

public class AllFriendHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    MenuClickListener menuClickListener;
    public ImageView profileImg;
    public TextView username,message,email;

    public AllFriendHolder(@NonNull View itemView) {
        super(itemView);
        this.profileImg=itemView.findViewById(R.id.all_friend_profile_img);
        this.username=itemView.findViewById(R.id.friend_username);
        this.message=itemView.findViewById(R.id.last_message);
        this.email=itemView.findViewById(R.id.friend_email);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        this.menuClickListener.onMenuClickListener(v, getLayoutPosition());
    }
    public void setMenuClickListener(MenuClickListener mc){
        this.menuClickListener=mc;
    }
}
