package layoutrecycle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weaboo.jurnalotaku.R;

public class FriendRequestHolder extends RecyclerView.ViewHolder{
    public Button addFriend,cancelRequest;
    public ImageView profileImg;
    public TextView username, alreadyFriends,email;
    public FriendRequestHolder(@NonNull View itemView) {
        super(itemView);
        this.addFriend=itemView.findViewById(R.id.req_add_friend_btn);
        this.cancelRequest=itemView.findViewById(R.id.cancel_add_friend_btn);
        this.profileImg=itemView.findViewById(R.id.friend_req_profile_img);
        this.username=itemView.findViewById(R.id.friend_username);
        this.alreadyFriends=itemView.findViewById(R.id.already_friend);
        this.email=itemView.findViewById(R.id.friend_email);
    }

}
